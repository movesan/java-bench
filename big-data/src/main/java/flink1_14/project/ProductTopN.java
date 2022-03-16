package flink1_14.project;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/3/9 15:38
 * @version 1.0
 */

import org.apache.commons.compress.utils.Lists;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author: Rango
 * @create: 2021-05-24 10:37
 * @description: 每隔5分钟输出最近1小时内点击量最多的前N个商品
 **/
public class ProductTopN {
    public static void main(String[] args) throws Exception {
        //1.建立环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);

        //2.设置watermark及定义事件时间，从socket获取数据并对应到JavaBean,筛选只取pv数据
        WatermarkStrategy<UserBehavior> wms = WatermarkStrategy
                .<UserBehavior>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                .withTimestampAssigner(new SerializableTimestampAssigner<UserBehavior>() {
                    @Override
                    public long extractTimestamp(UserBehavior element, long recordTimestamp) {
                        return element.getTimestamp() * 1000L;
                    }
                });
        String filePath = Objects.requireNonNull(ProductTopN.class.getClassLoader().getResource("UserBehavior.csv")).getPath();
        SingleOutputStreamOperator<UserBehavior> userBehaviorDS = env
                .readTextFile(filePath)
                .map(new MapFunction<String, UserBehavior>() {
                    @Override
                    public UserBehavior map(String value) throws Exception {
                        String[] split = value.split(",");
                        return new UserBehavior(Long.parseLong(split[0]),
                                Long.parseLong(split[1]),
                                Integer.parseInt(split[2]),
                                split[3],
                                Long.parseLong(split[4]));
                    }
                })
                .filter(data -> "pv".equals(data.getBehavior()))
                .assignTimestampsAndWatermarks(wms);

        //3.第一次聚合，按商品id分组开窗聚合,使用aggregate进行增量计算,将商品id用tuple2抽离出来提高效率
        SingleOutputStreamOperator<ItemCount> aggregateDS = userBehaviorDS
                .map(new MapFunction<UserBehavior, Tuple2<Long, Integer>>() {
                    @Override
                    public Tuple2<Long, Integer> map(UserBehavior value) throws Exception {
                        return new Tuple2<>(value.getItemId(), 1);
                    }
                })
                .keyBy(data -> data.f0)
                .window(SlidingEventTimeWindows.of(Time.hours(1), Time.minutes(5)))
                .aggregate(new ItemCountAggFunc(), new ItemCountWindowFunc());

        //4.第二次聚合，按窗口聚合，基于状态编程实现窗口内有序
        SingleOutputStreamOperator<String> processDS = aggregateDS.keyBy(ItemCount::getTime)
                .process(new ItemCountProcessFunc(5));

        //5.打印结果并执行
        processDS.print();
        env.execute();
    }

    public static class ItemCountAggFunc implements AggregateFunction<Tuple2<Long, Integer>, Integer, Integer> {
        @Override
        public Integer createAccumulator() {
            return 0;
        }

        @Override
        public Integer add(Tuple2<Long, Integer> value, Integer accumulator) {
            return accumulator + 1;
        }

        @Override
        public Integer getResult(Integer accumulator) {
            return accumulator;
        }

        @Override
        public Integer merge(Integer a, Integer b) {
            return a + b;
        }
    }

    public static class ItemCountWindowFunc implements WindowFunction<Integer, ItemCount, Long, TimeWindow> {
        @Override
        public void apply(Long key, TimeWindow window, Iterable<Integer> input, Collector<ItemCount> out) throws Exception {
            Integer next = input.iterator().next();
            out.collect(new ItemCount(key, new Timestamp(window.getEnd()).toString(), next));
        }
    }

    public static class ItemCountProcessFunc extends KeyedProcessFunction<String, ItemCount, String> {
        //定义构造器可以按入参取排名
        private Integer topN;

        public ItemCountProcessFunc(Integer topN) {
            this.topN = topN;
        }

        //使用liststatus并初始化
        private ListState<ItemCount> listState;

        @Override
        public void open(Configuration parameters) throws Exception {
            listState = getRuntimeContext()
                    .getListState(new ListStateDescriptor<ItemCount>("list-state", ItemCount.class));
        }

        //定时器
        @Override
        public void processElement(ItemCount value, Context ctx, Collector<String> out) throws Exception {
            listState.add(value);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 窗口结束时间一秒之后触发 topN 计算
            ctx.timerService().registerEventTimeTimer(sdf.parse(value.getTime()).getTime() + 1000L);
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
            //1.获取状态的数据并转为List
            Iterator<ItemCount> iterator = listState.get().iterator();
            ArrayList<ItemCount> itemCounts = Lists.newArrayList(iterator);

            //2.排序
            itemCounts.sort(((o1, o2) -> o2.getCount() - o1.getCount()));

            //3.获取前n
            StringBuilder sb = new StringBuilder();
            sb.append("==============")
                    .append(new Timestamp(timestamp - 1000L))
                    .append("==============")
                    .append("\n");
            for (int i = 0; i < Math.min(topN, itemCounts.size()); i++) {
                ItemCount itemCount = itemCounts.get(i);
                sb.append("Top").append(i + 1);
                sb.append(" ItemId:").append(itemCount.getItem());
                sb.append(" Counts:").append(itemCount.getCount());
                sb.append("\n");
            }
            sb.append("==============")
                    .append(new Timestamp(timestamp - 1000L))
                    .append("==============")
                    .append("\n")
                    .append("\n");
            listState.clear();
            out.collect(sb.toString());
            Thread.sleep(200);//方便查看结果时间休眠
        }
    }

    public static class UserBehavior {

        private long itemId;
        private long a;
        private String behavior;
        private int b;
        private long timestamp;

        public UserBehavior(long itemId, long a, int b, String behavior, long timestamp) {
            this.itemId = itemId;
            this.a = a;
            this.behavior = behavior;
            this.b = b;
            this.timestamp = timestamp;
        }

        public long getItemId() {
            return itemId;
        }

        public void setItemId(long itemId) {
            this.itemId = itemId;
        }

        public long getA() {
            return a;
        }

        public void setA(long a) {
            this.a = a;
        }

        public String getBehavior() {
            return behavior;
        }

        public void setBehavior(String behavior) {
            this.behavior = behavior;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public static class ItemCount {
        private long item;
        private String time;
        private int count;

        public ItemCount(long item, String time, int count) {
            this.item = item;
            this.time = time;
            this.count = count;
        }

        public long getItem() {
            return item;
        }

        public void setItem(long item) {
            this.item = item;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}