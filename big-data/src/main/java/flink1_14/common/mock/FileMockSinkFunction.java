package flink1_14.common.mock;

import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.operators.StreamSink;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2021/7/29 17:20
 * @version 1.0
 */
public abstract class FileMockSinkFunction extends RichSinkFunction<StreamSink> {

    @Override
    public void invoke(StreamSink value, Context context) throws Exception {

    }
}