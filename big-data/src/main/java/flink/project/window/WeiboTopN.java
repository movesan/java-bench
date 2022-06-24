package flink.project.window;

import flink.common.RandomUtil;
import flink.common.mock.MockFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static flink.common.RandomUtil.intervalRandom;

/**
 * @author: yangbin216
 * @create: 2022-02-24 10:37
 * @description: 微博热搜列表1小时 top K，5分钟刷新一次
 **/
public class WeiboTopN {
    public static void main(String[] args) throws Exception {
        // 1.建立环境
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        // 2.设置watermark及定义事件时间
        WatermarkStrategy<UserBehavior> wms = WatermarkStrategy
                .<UserBehavior>forBoundedOutOfOrderness(Duration.ofMinutes(1))
                .withTimestampAssigner((event, timestamp) -> event.getTimestamp());

        // 3.mock 微博访问数据
        SingleOutputStreamOperator<UserBehavior> blogLogsDS = MockFactory.addSource(env, "blog_logs", new UserBehaviorMock(), 2)
                .assignTimestampsAndWatermarks(wms);

        // 4.第一次聚合，按微博id分组开窗聚合,使用aggregate进行增量计算,将微博id用tuple2抽离出来提高效率
        SingleOutputStreamOperator<BlogCount> aggregateDS = blogLogsDS
                .map(new MapFunction<UserBehavior, Tuple2<Long, Integer>>() {
                    @Override
                    public Tuple2<Long, Integer> map(UserBehavior userBehavior) throws Exception {
                        return new Tuple2<>(userBehavior.getBlogId(), 1);
                    }
                })
                .keyBy(data -> data.f0)
                .window(SlidingEventTimeWindows.of(Time.hours(1), Time.minutes(5)))
                .aggregate(new BlogCountAggFunc(), new BlogCountWindowFunc()).setParallelism(8);

        //4.第二次聚合，按窗口聚合，基于状态编程实现窗口内有序
        SingleOutputStreamOperator<String> processDS = aggregateDS.keyBy(BlogCount::getTime)
                .process(new BlogCountProcessFunc(10)).setParallelism(8);

        //5.打印结果并执行
        processDS.print();
        env.execute();
    }

    public static class BlogCountAggFunc implements AggregateFunction<Tuple2<Long, Integer>, Integer, Integer> {
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

    public static class BlogCountWindowFunc implements WindowFunction<Integer, BlogCount, Long, TimeWindow> {
        @Override
        public void apply(Long key, TimeWindow window, Iterable<Integer> input, Collector<BlogCount> out) throws Exception {
            Integer next = input.iterator().next();
            out.collect(new BlogCount(key, new Timestamp(window.getEnd()).toString(), next));
        }
    }

    public static class BlogCountProcessFunc extends KeyedProcessFunction<String, BlogCount, String> {

        //定义构造器可以按入参取排名
        private final int topN;

        // 求 top N，定义小顶堆
//        transient PriorityQueue<BlogCount> queue;

        public BlogCountProcessFunc(int topN) {
            this.topN = topN;
        }

        private ValueState<PriorityQueue<BlogCount>> valueState;

        @Override
        public void open(Configuration parameters) throws Exception {
            valueState = getRuntimeContext()
                    .getState(new ValueStateDescriptor<PriorityQueue<BlogCount>>("value-state", TypeInformation.of(new TypeHint<PriorityQueue<BlogCount>>() {
                    })));

        }

        //定时器
        @Override
        public void processElement(BlogCount value, Context ctx, Collector<String> out) throws Exception {
            PriorityQueue<BlogCount> queue = valueState.value();
            if (queue == null) {
                queue = new PriorityQueue<>(topN, Comparator.comparingInt(BlogCount::getCount));
            }
            queue.add(value);

            valueState.update(queue);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 窗口结束时间一秒之后触发 topN 计算
            ctx.timerService().registerEventTimeTimer(sdf.parse(value.getTime()).getTime() + 1000L);
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
            ArrayList<BlogCount> blogCounts = new ArrayList<>();
            PriorityQueue<BlogCount> queue = valueState.value();

            while (!queue.isEmpty()) {
                blogCounts.add(queue.poll());
            }

            //3.获取前n
            StringBuilder sb = new StringBuilder();
            sb.append("==============")
                    .append(new Timestamp(timestamp - 1000L))
                    .append("==============")
                    .append("\n");

            int size = Math.min(topN, blogCounts.size());
            for (int i = size - 1; i >= 0; i--) {
                BlogCount blogCount = blogCounts.get(i);
                sb.append("Top").append(i + 1);
                sb.append(" BlogId:").append(blogCount.getBlogId());
                sb.append(" Counts:").append(blogCount.getCount());
                sb.append("\n");
            }
            sb.append("==============")
                    .append(new Timestamp(timestamp - 1000L))
                    .append("==============")
                    .append("\n")
                    .append("\n");
            out.collect(sb.toString());

            valueState.clear();
            Thread.sleep(200);//方便查看结果时间休眠
        }
    }

    static class UserBehaviorMock extends RichParallelSourceFunction<UserBehavior> {

        private static final long serialVersionUID = -7286937645300388040L;

        private transient volatile boolean isRunning;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            this.isRunning = true;
        }

        @Override
        public void run(SourceContext<UserBehavior> ctx) throws Exception {
            // Sleep for 1 seconds on start to allow time to copy jobid
            Thread.sleep(1000L);

            while (isRunning) {

                synchronized (ctx.getCheckpointLock()) {
                    UserBehavior userBehavior = new UserBehavior(
                            RandomUtil.intervalRandom(1000, 1100),
                            RandomUtil.getRandomElement(Stream.of("view", "comment", "share").collect(Collectors.toList())),
                            System.currentTimeMillis()
                    );

                    ctx.collect(userBehavior);
                }

                Thread.sleep(intervalRandom(100, 500));
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBehavior {

        private long blogId;
        private String behavior;
        private long timestamp;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BlogCount {
        private long blogId;
        private String time;
        private int count;

    }
}