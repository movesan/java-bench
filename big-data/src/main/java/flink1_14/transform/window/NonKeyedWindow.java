package flink1_14.transform.window;

import flink1_14.common.StreamData;
import flink1_14.common.StreamExecutionEnvironmentBuilder;
import flink1_14.common.mock.DataMock;
import flink1_14.common.mock.MockFactory;
import flink1_14.common.mock.OrderMock;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;
import org.junit.Test;

import java.time.Duration;

/**
 * @description:
 *
 * stream
 *        .windowAll(...)           <-  required: "assigner"
 *       [.trigger(...)]            <-  optional: "trigger" (else default trigger)
 *       [.evictor(...)]            <-  optional: "evictor" (else no evictor)
 *       [.allowedLateness(...)]    <-  optional: "lateness" (else zero)
 *       [.sideOutputLateData(...)] <-  optional: "output tag" (else no side output for late data)
 *        .reduce/aggregate/apply()      <-  required: "function"
 *       [.getSideOutput(...)]      <-  optional: "output tag"
 *
 * @author yangbin216
 * @date 2022/3/1 15:49
 * @version 1.0
 */
public class NonKeyedWindow {

    @Test
    public void windowAll() throws Exception {
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

                // -------------------------- 1. Window Assigners --------------------------
//                .timeWindowAll(Time.seconds(1000)) // 时间窗口已废弃
//                .countWindowAll(10, 5) // 数量窗口大小以及滑动步长
                .countWindowAll(10) // 数量窗口大小
//                .windowAll(GlobalWindows.create()) // 全局窗口
//                .windowAll(new WindowAssigner<StreamData, Window>() {
//                    @Override
//                    public Collection<Window> assignWindows(StreamData streamData, long l, WindowAssignerContext windowAssignerContext) {
//                        return null;
//                    }
//
//                    @Override
//                    public Trigger<StreamData, Window> getDefaultTrigger(StreamExecutionEnvironment streamExecutionEnvironment) {
//                        return null;
//                    }
//
//                    @Override
//                    public TypeSerializer<Window> getWindowSerializer(ExecutionConfig executionConfig) {
//                        return null;
//                    }
//
//                    @Override
//                    public boolean isEventTime() {
//                        return false;
//                    }
//                })

                // -------------------------- 2. Window Function --------------------------
                .apply(new AllWindowFunction<StreamData, StreamData, GlobalWindow>() {
                    @Override
                    public void apply(GlobalWindow window, Iterable<StreamData> iterable, Collector<StreamData> collector) throws Exception {
                        iterable.forEach(collector::collect);
                    }
                }).setParallelism(1); // 只能为 1

        result.forward();


        result.print();
        env.execute("NonKeyed Window Example");
    }
}