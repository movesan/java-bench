package flink.common.mock;

import flink.common.resource.FileInfo;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.Map;

public class MockFactory {

    private static final int PARALLELISM = 1;

    public static <T> SingleOutputStreamOperator<T> addSource(StreamExecutionEnvironment env, String source, RichParallelSourceFunction<T> function, int parallelism) {
        return env.addSource(function)
                .setParallelism(parallelism)
                .name(source)
                .uid(source);
    }

    public static <T> SingleOutputStreamOperator<T> addSource(StreamExecutionEnvironment env, String source, RichParallelSourceFunction<T> function) {
        return env.addSource(function)
                .setParallelism(PARALLELISM)
                .name(source)
                .uid(source);
    }

    public static void addSink(DataStream<Map<String, String>> dataStream, FileInfo fileInfo) {
        final StreamingFileSink<Map<String, String>> sink = StreamingFileSink
                .forRowFormat(new Path(fileInfo.getOutputPath()), new SimpleStringEncoder<Map<String, String>>("UTF-8"))
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withRolloverInterval(fileInfo.getRolloverInterval())
                                .withInactivityInterval(fileInfo.getInactivityInterval())
                                .withMaxPartSize(fileInfo.getMaxPartSize())
                                .build())
                .build();

        dataStream.addSink(sink)
                .name("file_sink")
                .uid("file_sink")
                .setParallelism(PARALLELISM);
    }


}
