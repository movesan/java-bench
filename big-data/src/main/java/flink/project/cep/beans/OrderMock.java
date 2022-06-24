package flink.project.cep.beans;

import flink.common.RandomUtil;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static flink.common.RandomUtil.intervalRandom;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/3/22 13:28
 * @version 1.0
 */
public class OrderMock extends RichParallelSourceFunction<OrderEvent> {

    private transient volatile boolean isRunning;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.isRunning = true;
    }

    @Override
    public void run(SourceContext<OrderEvent> ctx) throws Exception {
        // Sleep for 1 seconds on start to allow time to copy jobid
        Thread.sleep(1000L);

        while (isRunning) {

            synchronized (ctx.getCheckpointLock()) {
                OrderEvent orderEvent = new OrderEvent(
                        RandomUtil.intervalRandom(1000, 1100) + "",
                        RandomUtil.getRandomElement(Stream.of("1", "2", "3", "4").collect(Collectors.toList())),
                        System.currentTimeMillis(),
                        Double.NaN
                );

                ctx.collect(orderEvent);
            }

            Thread.sleep(intervalRandom(100, 500));
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}