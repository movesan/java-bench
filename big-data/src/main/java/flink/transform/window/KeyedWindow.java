package flink.transform.window;

import flink.common.StreamData;
import flink.common.StreamExecutionEnvironmentBuilder;
import flink.common.mock.DataMock;
import flink.common.mock.MockFactory;
import org.apache.flink.api.common.accumulators.IntCounter;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.evictors.CountEvictor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.junit.Test;

import java.time.Duration;

/**
 * @description:
 *
 * stream
 *        .keyBy(...)               <-  keyed versus non-keyed windows
 *        .window(...)              <-  required: "assigner"
 *       [.trigger(...)]            <-  optional: "trigger" (else default trigger)
 *       [.evictor(...)]            <-  optional: "evictor" (else no evictor)
 *       [.allowedLateness(...)]    <-  optional: "lateness" (else zero)
 *       [.sideOutputLateData(...)] <-  optional: "output tag" (else no side output for late data)
 *        .reduce/aggregate/apply()      <-  required: "function"
 *       [.getSideOutput(...)]      <-  optional: "output tag"
 *
 * @author yangbin216
 * @date 2022/3/2 10:10
 * @version 1.0
 */
public class KeyedWindow {

    public static final String JOIN_KEY = "order_no";

//    public static final OutputTag<StreamData> outputTag = new OutputTag<>("tag");

    @Test
    public void window() throws Exception {
        String[] args = {};
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = new StreamExecutionEnvironmentBuilder()
                .setConfiguration(conf)
                .buildWithWebUI(args);

        SingleOutputStreamOperator<StreamData> orderSource = MockFactory.addSource(env, "order", new DataMock(), 2);

        DataStream<StreamData> orderStream = orderSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<StreamData>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));

        SingleOutputStreamOperator<Tuple2<String, Integer>> result = orderStream
                .keyBy(e -> e.getFields().get(JOIN_KEY))

                // -------------------------- 1. Window Assigners --------------------------
                .window(TumblingEventTimeWindows.of(Time.seconds(10))) // ???????????? - ????????????
//                .window(TumblingProcessingTimeWindows.of(Time.seconds(10))) // ???????????? - ????????????
//                .window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5))) // ???????????? - ????????????
//                .window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5))) // ???????????? - ????????????
//                .window(EventTimeSessionWindows.withGap(Time.seconds(3)) // ???????????? - ????????????
//                .window(EventTimeSessionWindows.withDynamicGap((element) -> { // ???????????? - ????????????
//                    // ???????????????????????????
//                }))
//                .window(ProcessingTimeSessionWindows.withGap(Time.seconds(3)) // ???????????? - ????????????
//                .window(ProcessingTimeSessionWindows.withDynamicGap((element) -> { // ???????????? - ????????????
//                    // ???????????????????????????
//                }))
//                .timeWindow(Time.minutes(10)) // ??????????????????
//                .timeWindow(Time.minutes(10), Time.minutes(5)) // ??????????????????
//                .countWindow(10) // ????????????
//                .countWindow(10, 10) // ????????????
//                .window(GlobalWindows.create()) // ????????????
//                .window(new WindowAssigner<StreamData, Window>() {
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

                // -------------------------- 1.1 Window Triggers --------------------------
                .trigger(CountTrigger.of(100))
//                .trigger(PurgingTrigger.of(CountTrigger.of(100)))
//                .trigger(EventTimeTrigger.create())
//                .trigger(ContinuousEventTimeTrigger.of(Time.minutes(2)))
//                .trigger(new Trigger<StreamData, TimeWindow>() {
//                    @Override
//                    public TriggerResult onElement(StreamData streamData, long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
//                        return null;
//                    }
//
//                    @Override
//                    public TriggerResult onProcessingTime(long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
//                        return null;
//                    }
//
//                    @Override
//                    public TriggerResult onEventTime(long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
//                        return null;
//                    }
//
//                    @Override
//                    public void clear(TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
//
//                    }
//                })

                // -------------------------- 1.2 Window Evictors --------------------------
                .evictor(CountEvictor.of(5))
//                .evictor(TimeEvictor.of(Time.of(5, TimeUnit.SECONDS)))
//                .evictor(new Evictor<StreamData, TimeWindow>() {
//                    @Override
//                    public void evictBefore(Iterable<TimestampedValue<StreamData>> iterable, int i, TimeWindow timeWindow, EvictorContext evictorContext) {
//
//                    }
//
//                    @Override
//                    public void evictAfter(Iterable<TimestampedValue<StreamData>> iterable, int i, TimeWindow timeWindow, EvictorContext evictorContext) {
//
//                    }
//                })

                // -------------------------- 1.3 LateData --------------------------
//                .allowedLateness(Time.seconds(5))
//                .sideOutputLateData(outputTag)

                // -------------------------- 2. Window Function --------------------------
//                .aggregate(new AggregateFunction<StreamData, MyIntegerAccumulator, Tuple2<String, Integer>>() {
//                    @Override
//                    public MyIntegerAccumulator createAccumulator() {
//                        return new MyIntegerAccumulator();
//                    }
//
//                    @Override
//                    public MyIntegerAccumulator add(StreamData streamData, MyIntegerAccumulator acc) {
//                        acc.setOrderNo(streamData.getFields().get(JOIN_KEY));
//                        acc.add(1);
//                        return acc;
//                    }
//
//                    @Override
//                    public Tuple2<String, Integer> getResult(MyIntegerAccumulator acc) {
//                        return new Tuple2<>(acc.getOrderNo(), acc.getLocalValue());
//                    }
//
//                    @Override
//                    public MyIntegerAccumulator merge(MyIntegerAccumulator acc, MyIntegerAccumulator acc1) {
//                        acc.merge(acc1);
//                        return acc;
//                    }
//                })
//                .reduce(new ReduceFunction<StreamData>() {
//                    @Override
//                    public StreamData reduce(StreamData streamData, StreamData t1) throws Exception {
//                        return null;
//                    }
//                })
//                .reduce(new ReduceFunction<StreamData>() {
//                    @Override
//                    public StreamData reduce(StreamData streamData, StreamData t1) throws Exception {
//                        return null;
//                    }
//                }, new ProcessWindowFunction<StreamData, Tuple2<String, Integer>, String, TimeWindow>() {
//                    @Override
//                    public void process(String s, Context context, Iterable<StreamData> iterable, Collector<Tuple2<String, Integer>> collector) throws Exception {
//
//                    }
//                })
                .process(new ProcessWindowFunction<StreamData, Tuple2<String, Integer>, String, TimeWindow>() {
                    @Override
                    public void process(String s, Context context, Iterable<StreamData> iterable, Collector<Tuple2<String, Integer>> collector) throws Exception {

                    }
                })
//                .apply(new WindowFunction<StreamData, StreamData, String, Window>() {
//                    @Override
//                    public void apply(String s, Window timeWindow, Iterable<StreamData> iterable, Collector<StreamData> collector) throws Exception {
//                        iterable.forEach(collector::collect);
//                    }
//                })
                ;

//        DataStream<StreamData> outputStream = result.getSideOutput(outputTag);

        result.print();
        env.execute("Keyed Window Example");
    }

    public static class MyIntegerAccumulator extends IntCounter {
        private String orderNo;

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getOrderNo() {
            return orderNo;
        }
    }
}