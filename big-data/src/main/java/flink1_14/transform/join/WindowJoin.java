package flink1_14.transform.join;

import flink1_14.common.StreamData;
import flink1_14.common.StreamExecutionEnvironmentBuilder;
import flink1_14.common.mock.MockFactory;
import flink1_14.common.mock.OrderMock;
import flink1_14.common.mock.PackageMock;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.Test;

import java.time.Duration;

/**
 * @description:
 *
 * stream.join(otherStream)
 *     .where(<KeySelector>) // 左流
 *     .equalTo(<KeySelector>) // 右流
 *     .window(<WindowAssigner>)
 *     .apply(<JoinFunction>)
 *
 * @author yangbin216
 * @date 2022/2/28 17:09
 * @version 1.0
 */
public class WindowJoin {

    public static final String JOIN_KEY = "order_no";

    @Test
    public void tumblingWindow() throws Exception {
        String[] args = {};
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = new StreamExecutionEnvironmentBuilder()
                .setConfiguration(conf)
                .buildWithWebUI(args);

        SingleOutputStreamOperator<StreamData> orderSource = MockFactory.addSource(env, "order", new OrderMock());
        SingleOutputStreamOperator<StreamData> packageSource = MockFactory.addSource(env, "package", new PackageMock());

        DataStream<StreamData> orderStream = orderSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<StreamData>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));

        DataStream<StreamData> packageStream = packageSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<StreamData>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));

        DataStream<StreamData> result = orderStream.join(packageStream)
                .where(e -> e.getFields().get(JOIN_KEY))
                .equalTo(e -> e.getFields().get(JOIN_KEY))
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                // JoinFunction
                .apply((JoinFunction<StreamData, StreamData, StreamData>) StreamData::join);
//                .apply((FlatJoinFunction<StreamData, StreamData, Object>) (streamData, streamData2, collector) -> {
//                    collector.collect(streamData.join(streamData2));
//                })

        result.print();
        env.execute("Tumbling Window Example");
    }

    @Test
    public void slidingWindow() throws Exception {
        String[] args = {};
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = new StreamExecutionEnvironmentBuilder()
                .setConfiguration(conf)
                .buildWithWebUI(args);

        SingleOutputStreamOperator<StreamData> orderSource = MockFactory.addSource(env, "order", new OrderMock());
        SingleOutputStreamOperator<StreamData> packageSource = MockFactory.addSource(env, "package", new PackageMock());

        DataStream<StreamData> orderStream = orderSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<StreamData>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));

        DataStream<StreamData> packageStream = packageSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<StreamData>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));

        DataStream<StreamData> result = orderStream.join(packageStream)
                .where(e -> e.getFields().get(JOIN_KEY))
                .equalTo(e -> e.getFields().get(JOIN_KEY))
                .window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))
                // JoinFunction
                .apply((JoinFunction<StreamData, StreamData, StreamData>) StreamData::join);
//                .apply((FlatJoinFunction<StreamData, StreamData, Object>) (streamData, streamData2, collector) -> {
//                    collector.collect(streamData.join(streamData2));
//                })

        result.print();
        env.execute("Sliding Window Example");
    }

    @Test
    public void sessionWindow() throws Exception {
        String[] args = {};
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = new StreamExecutionEnvironmentBuilder()
                .setConfiguration(conf)
                .buildWithWebUI(args);

        SingleOutputStreamOperator<StreamData> orderSource = MockFactory.addSource(env, "order", new OrderMock());
        SingleOutputStreamOperator<StreamData> packageSource = MockFactory.addSource(env, "package", new PackageMock());

        DataStream<StreamData> orderStream = orderSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<StreamData>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));

        DataStream<StreamData> packageStream = packageSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<StreamData>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));

        DataStream<StreamData> result = orderStream.join(packageStream)
                .where(e -> e.getFields().get(JOIN_KEY))
                .equalTo(e -> e.getFields().get(JOIN_KEY))
                .window(EventTimeSessionWindows.withGap(Time.seconds(1)))
                // JoinFunction
                .apply((JoinFunction<StreamData, StreamData, StreamData>) StreamData::join);
//                .apply((FlatJoinFunction<StreamData, StreamData, Object>) (streamData, streamData2, collector) -> {
//                    collector.collect(streamData.join(streamData2));
//                })

        result.print();
        env.execute("Session Window Example");
    }


}