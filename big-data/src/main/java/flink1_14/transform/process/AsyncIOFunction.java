package flink1_14.transform.process;

import flink1_14.common.StreamData;
import flink1_14.common.StreamExecutionEnvironmentBuilder;
import flink1_14.common.mock.MockFactory;
import flink1_14.common.mock.OrderMock;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.async.AsyncFunction;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.util.ExecutorUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/3/7 15:03
 * @version 1.0
 */
public class AsyncIOFunction {

    @Test
    public void test() throws Exception {
        String[] args = {};
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = new StreamExecutionEnvironmentBuilder()
                .setConfiguration(conf)
                .buildWithWebUI(args);

        SingleOutputStreamOperator<StreamData> orderSource = MockFactory.addSource(env, "order", new OrderMock(), 2);

        DataStream<StreamData> result = AsyncDataStream.orderedWait(
                orderSource, new SampleAsyncFunction(100, 5, 100), 1000, TimeUnit.MILLISECONDS, 100);

        result.print();
        env.execute("Async IO Example");
    }

    /**
     * An sample of {@link AsyncFunction} using a thread pool and executing working threads
     * to simulate multiple async operations.
     *
     * <p>For the real use case in production environment, the thread pool may stay in the
     * async client.
     */
    private static class SampleAsyncFunction extends RichAsyncFunction<StreamData, StreamData> {
        private static final long serialVersionUID = 2098635244857937717L;

        private transient ExecutorService executorService;

        /**
         * The result of multiplying sleepFactor with a random float is used to pause
         * the working thread in the thread pool, simulating a time consuming async operation.
         */
        private final long sleepFactor;

        /**
         * The ratio to generate an exception to simulate an async error. For example, the error
         * may be a TimeoutException while visiting HBase.
         */
        private final float failRatio;

        private final long shutdownWaitTS;

        SampleAsyncFunction(long sleepFactor, float failRatio, long shutdownWaitTS) {
            this.sleepFactor = sleepFactor;
            this.failRatio = failRatio;
            this.shutdownWaitTS = shutdownWaitTS;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);

            executorService = Executors.newFixedThreadPool(30);
        }

        @Override
        public void close() throws Exception {
            super.close();
            ExecutorUtils.gracefulShutdown(shutdownWaitTS, TimeUnit.MILLISECONDS, executorService);
        }

        @Override
        public void asyncInvoke(final StreamData input, final ResultFuture<StreamData> resultFuture) {
            executorService.submit(() -> {
                // wait for while to simulate async operation here
                long sleep = (long) (ThreadLocalRandom.current().nextFloat() * sleepFactor);
                try {
                    Thread.sleep(sleep);

                    if (ThreadLocalRandom.current().nextFloat() < failRatio) {
//                        resultFuture.completeExceptionally(new Exception("wahahahaha..."));
                    } else {
//                        resultFuture.complete(
//                                Collections.singletonList("key-" + (input % 10)));
                    }
                } catch (InterruptedException e) {
                    resultFuture.complete(new ArrayList<>(0));
                }
            });
        }
    }
}