package flink.transform.window;

import flink.common.StreamData;
import flink.common.StreamExecutionEnvironmentBuilder;
import flink.common.mock.DataMock;
import flink.common.mock.MockFactory;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.Test;

import java.time.Duration;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/3/17 10:00
 * @version 1.0
 */
public class Watermark {

    @Test
    public void watermark() throws Exception {
        String[] args = {};
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = new StreamExecutionEnvironmentBuilder()
                .setConfiguration(conf)
                .buildWithWebUI(args);

        // 设置水位线生成间隔
        env.getConfig().setAutoWatermarkInterval(500);

        SingleOutputStreamOperator<StreamData> orderSource = MockFactory.addSource(env, "order", new DataMock(), 2);

        /* ----- Periodic 周期性生成 TimestampsAndWatermarks ----- */
        // 1.1 Flink 内置基于时间戳最大值的场景，支持乱序
        orderSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<StreamData>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));
        // 1.2 Flink 内置，假设Event Time时间戳单调递增，默认有序
        orderSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .<StreamData>forMonotonousTimestamps()
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));

        // 2 自定义生成 watermarks, timestamp
        orderSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .forGenerator(context -> new BoundedOutOfOrdernessGenerator())
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));

        /* ----- Punctuated 根据标记生成 TimestampsAndWatermarks ----- */
        orderSource.assignTimestampsAndWatermarks(
                WatermarkStrategy
                        .forGenerator(context -> new PunctuatedAssigner())
                        .withTimestampAssigner((event, timestamp) -> event.getTimeStamp()));


        orderSource.print();
        env.execute("Watermark Example");
    }

    /**
     * @description: （自定义周期性 Watermark 生成器） 同 {@link org.apache.flink.api.common.eventtime.BoundedOutOfOrdernessWatermarks}
     *               该 watermark 生成器可以覆盖的场景是：数据源在一定程度上乱序。
     *               即某个最新到达的时间戳为 t 的元素将在最早到达的时间戳为 t 的元素之后最多 n 毫秒到达。
     *
     * @author yangbin216
     * @date 2021/9/17 14:34
     * @version 1.0
     */
    public static class BoundedOutOfOrdernessGenerator implements WatermarkGenerator<StreamData> {

        private final long maxOutOfOrderness = 3500; // 3.5 秒

        private long currentMaxTimestamp;

        @Override
        public void onEvent(StreamData event, long eventTimestamp, WatermarkOutput output) {
            currentMaxTimestamp = Math.max(currentMaxTimestamp, eventTimestamp);
        }

        @Override
        public void onPeriodicEmit(WatermarkOutput output) {
            // 发出的 watermark = 当前最大时间戳 - 最大乱序时间
            output.emitWatermark(new org.apache.flink.api.common.eventtime.Watermark(currentMaxTimestamp - maxOutOfOrderness));
        }
    }

    /**
     * @description: （自定义标记 Watermark 生成器）
     *                标记 watermark 生成器观察流事件数据并在获取到带有 watermark 信息的特殊事件元素时发出 watermark。
     *
     * @author yangbin216
     * @date 2021/9/17 14:56
     * @version 1.0
     */
    public static class PunctuatedAssigner implements WatermarkGenerator<StreamData> {

        @Override
        public void onEvent(StreamData event, long eventTimestamp, WatermarkOutput output) {
            boolean hasWatermarkMarker = true; // event.hasWatermarkMarker()
            long watermarkTimestamp = 0L; // event.getWatermarkTimestamp()
            if (hasWatermarkMarker) {
                output.emitWatermark(new org.apache.flink.api.common.eventtime.Watermark(watermarkTimestamp));
            }
        }

        @Override
        public void onPeriodicEmit(WatermarkOutput output) {
            // onEvent 中已经实现
        }
    }

    /**
     * @description: （自定义周期性 Watermark 生成器）
     *                该生成器生成的 watermark 滞后于处理时间固定量。它假定元素会在有限延迟后到达 Flink。
     *
     * @author yangbin216
     * @date 2021/9/17 14:34
     * @version 1.0
     */
    public static class TimeLagWatermarkGenerator implements WatermarkGenerator<StreamData> {

        private final long maxTimeLag = 5000; // 5 秒

        @Override
        public void onEvent(StreamData event, long eventTimestamp, WatermarkOutput output) {
            // 处理时间场景下不需要实现
        }

        @Override
        public void onPeriodicEmit(WatermarkOutput output) {
            output.emitWatermark(new org.apache.flink.api.common.eventtime.Watermark(System.currentTimeMillis() - maxTimeLag));
        }
    }
}