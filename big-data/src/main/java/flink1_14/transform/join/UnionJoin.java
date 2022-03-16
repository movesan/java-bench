package flink1_14.transform.join;

import flink1_14.common.StreamData;
import flink1_14.common.StreamExecutionEnvironmentBuilder;
import flink1_14.common.mock.MockFactory;
import flink1_14.common.mock.OrderMock;
import flink1_14.common.mock.PackageMock;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.junit.Test;

import java.time.Duration;
import java.util.Map;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/3/1 9:44
 * @version 1.0
 */
public class UnionJoin {

    public static final String JOIN_KEY = "order_no";

    @Test
    public void union() throws Exception {
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

        DataStream<StreamData> result = orderStream
                .keyBy(e -> e.getFields().get(JOIN_KEY))
                .intervalJoin(packageStream.keyBy(e -> e.getFields().get(JOIN_KEY)))
                .between(Time.seconds(-2), Time.seconds(1))
                .process(new ProcessJoinFunction<StreamData, StreamData, StreamData>() {
                    @Override
                    public void processElement(StreamData streamData, StreamData streamData2, Context context, Collector<StreamData> collector) throws Exception {
                        collector.collect(streamData.join(streamData2));
                    }
                });


        result.print();
        env.execute("Union Example");
    }

    @Test
    public void join() throws Exception {
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

        DataStream<StreamData> result = orderStream.union(packageStream)
                .keyBy(e -> e.getFields().get(JOIN_KEY))
                .flatMap(new RichFlatMapFunction<StreamData, StreamData>() {
                    private transient ValueState<Map<String, String>> state;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        StateTtlConfig ttlConfig = StateTtlConfig.newBuilder(org.apache.flink.api.common.time.Time.days(30)) // TTL 时间
                                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite) // state 时间戳更新策略
                                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired) // 当 state 过期时处理策略
                                .cleanupInRocksdbCompactFilter(60000L) // 过期对象清理策略，每读取若干条记录就执行一次清理操作
                                .build();
                        ValueStateDescriptor<Map<String, String>> stateConfig = new ValueStateDescriptor<>("state", Types.MAP(Types.STRING, Types.STRING));
                        stateConfig.enableTimeToLive(ttlConfig);
                        state = getRuntimeContext().getState(stateConfig);
                    }

                    @Override
                    public void flatMap(StreamData streamData, Collector<StreamData> collector) throws Exception {
                        // join

                    }
                });


        result.print();
        env.execute("Join Example");
    }
}