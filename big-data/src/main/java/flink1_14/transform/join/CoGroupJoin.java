package flink1_14.transform.join;

import flink1_14.common.StreamData;
import flink1_14.common.StreamExecutionEnvironmentBuilder;
import flink1_14.common.mock.MockFactory;
import flink1_14.common.mock.OrderMock;
import flink1_14.common.mock.PackageMock;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.CoGroupFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.junit.Test;

import java.time.Duration;

/**
 * @description:
 *
 * stream.coGroup(otherStream)
 * 		.where(<KeySelector>)
 * 		.equalTo(<KeySelector>)
 * 		.window(<WindowAssigner>)
 * 		.apply(<CoGroupFunction>);
 *
 * @author yangbin216
 * @date 2022/2/28 17:15
 * @version 1.0
 */
public class CoGroupJoin {

    public static final String JOIN_KEY = "order_no";

    @Test
    public void innerJoin() throws Exception {
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

        DataStream<StreamData> result = orderStream.coGroup(packageStream)
                .where(e -> e.getFields().get(JOIN_KEY))
                .equalTo(e -> e.getFields().get(JOIN_KEY))
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .apply(new CoGroupFunction<StreamData, StreamData, StreamData>() {
                    @Override
                    public void coGroup(Iterable<StreamData> iterable, Iterable<StreamData> iterable1, Collector<StreamData> collector) throws Exception {
                        iterable.forEach(e -> {
                            iterable1.forEach(e1 -> {
                                e.join(e1);
                                collector.collect(e);
                            });
                        });
                    }
                });

        result.print();
        env.execute("CoGroup Inner Join Example");
    }

    @Test
    public void leftJoin() throws Exception {
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

        DataStream<StreamData> result = orderStream.coGroup(packageStream)
                .where(e -> e.getFields().get(JOIN_KEY))
                .equalTo(e -> e.getFields().get(JOIN_KEY))
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .apply(new CoGroupFunction<StreamData, StreamData, StreamData>() {
                    @Override
                    public void coGroup(Iterable<StreamData> iterable, Iterable<StreamData> iterable1, Collector<StreamData> collector) throws Exception {
                        iterable.forEach(e -> {
                            iterable1.forEach(e::join);
                            collector.collect(e);
                        });
                    }
                });

        result.print();
        env.execute("CoGroup Left Join Example");
    }

    @Test
    public void rightJoin() throws Exception {
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

        DataStream<StreamData> result = orderStream.coGroup(packageStream)
                .where(e -> e.getFields().get(JOIN_KEY))
                .equalTo(e -> e.getFields().get(JOIN_KEY))
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .apply(new CoGroupFunction<StreamData, StreamData, StreamData>() {
                    @Override
                    public void coGroup(Iterable<StreamData> iterable, Iterable<StreamData> iterable1, Collector<StreamData> collector) throws Exception {
                        iterable1.forEach(e1 -> {
                            iterable.forEach(e1::join);
                            collector.collect(e1);
                        });
                    }
                });

        result.print();
        env.execute("CoGroup Right Join Example");
    }
}