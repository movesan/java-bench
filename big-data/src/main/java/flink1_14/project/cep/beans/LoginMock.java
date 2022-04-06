package flink1_14.project.cep.beans;

import flink1_14.common.RandomUtil;
import flink1_14.project.window.WeiboTopN;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static flink1_14.common.RandomUtil.intervalRandom;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/3/22 13:28
 * @version 1.0
 */
public class LoginMock extends RichParallelSourceFunction<LoginEvent> {

    private transient volatile boolean isRunning;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.isRunning = true;
    }

    @Override
    public void run(SourceFunction.SourceContext<LoginEvent> ctx) throws Exception {
        // Sleep for 1 seconds on start to allow time to copy jobid
        Thread.sleep(1000L);

        while (isRunning) {

            synchronized (ctx.getCheckpointLock()) {
                LoginEvent loginEvent = new LoginEvent(
                        (long) RandomUtil.intervalRandom(1000, 1100),
                        "127.0.0.1",
                        RandomUtil.getRandomElement(Stream.of("success", "fail").collect(Collectors.toList())),
                        System.currentTimeMillis()
                );

                ctx.collect(loginEvent);
            }

            Thread.sleep(intervalRandom(100, 500));
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}