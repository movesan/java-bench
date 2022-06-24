package flink.transform.window;

import flink.common.StreamData;
import flink.common.StreamExecutionEnvironmentBuilder;
import flink.common.mock.DataMock;
import flink.common.mock.MockFactory;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.delta.DeltaFunction;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.triggers.DeltaTrigger;
import org.apache.flink.streaming.api.windowing.triggers.PurgingTrigger;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;
import org.junit.Test;

import java.time.Duration;

/**
 * @description: 用来确定窗口什么时候触发计算
 *
 * @author yangbin216
 * @date 2022/3/7 18:22
 * @version 1.0
 */
public class Trigger {

    public static final String JOIN_KEY = "order_no";

    /**
     * 场景：
     *      有这样一个车辆区间测试的需求，车辆每分钟上报当前位置与车速，每行进10公里，计算区间内最高车速。
     *
     *      我们可以自定义 WindowAssigner 来实现一些复杂场景，但是不一定简单，可以用 GlobalWindow + Trigger 来实现同样的效果
     *      KeyStream.countWindow 就是基于 GlobalWindow 和 Trigger 来实现的
     *
     * @throws Exception
     */
    @Test
    public void deltaTrigger() throws Exception {
        String[] args = {};
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = new StreamExecutionEnvironmentBuilder()
                .setConfiguration(conf)
                .buildWithWebUI(args);

        SingleOutputStreamOperator<StreamData> orderSource = MockFactory.addSource(env, "order", new DataMock(), 2);

        DataStream<StreamData> orderStream = orderSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<StreamData>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));

        DataStream<StreamData> result = orderStream
                .keyBy(e -> e.getFields().get(JOIN_KEY))
                .window(GlobalWindows.create())
                .trigger(DeltaTrigger.of(100000,
                        new DeltaFunction<StreamData>() {
                            @Override
                            public double getDelta(StreamData oldDataPoint, StreamData newDataPoint) {
                                return Double.parseDouble(newDataPoint.getFields().get("km")) - Double.parseDouble(newDataPoint.getFields().get("km"));
                            }
                        }, orderStream.getType().createSerializer(env.getConfig())))
                .max("speed");

        result.print();
        env.execute("deltaTrigger Example");
    }

    /**
     * 基于 GlobalWindow 和 Trigger 来实现 countWindow 效果
     * @throws Exception
     */
    @Test
    public void countTrigger() throws Exception {
        String[] args = {};
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = new StreamExecutionEnvironmentBuilder()
                .setConfiguration(conf)
                .buildWithWebUI(args);

        SingleOutputStreamOperator<StreamData> orderSource = MockFactory.addSource(env, "order", new DataMock(), 2);

        DataStream<StreamData> orderStream = orderSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<StreamData>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));

        DataStream<StreamData> result = orderStream
                .keyBy(e -> e.getFields().get(JOIN_KEY))
                .window(GlobalWindows.create())
                .trigger(PurgingTrigger.of(CountTrigger.of(100)))
                .process(new ProcessWindowFunction<StreamData, StreamData, String, GlobalWindow>() {
                    @Override
                    public void process(String s, Context context, Iterable<StreamData> iterable, Collector<StreamData> collector) throws Exception {
                        iterable.forEach(collector::collect);
                    }
                });

        result.print();
        env.execute("countTrigger Example");
    }
}