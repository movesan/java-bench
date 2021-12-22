package ratelimit;

import java.util.Random;
import java.util.stream.IntStream;
import org.junit.Test;

/**
 * @description: 限流测试
 * @author: movesan
 * @create: 2020-10-04 17:21
 **/
public class LimiterTest {

    @Test
    public void counterTest() {
        CounterLimiter counterLimiter = new CounterLimiter(10, 1000);

        // 测试10个线程
        IntStream.range(0, 30).forEach((i) -> {
            new Thread(() -> {

                while (true) {

                    try {
                        Thread.sleep(new Random().nextInt(20) * 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (counterLimiter.limit()) {
                        System.out.println("线程：" + i + "通行！");
                    } else {
                        System.out.println("线程：" + i + "等待！===========");
                    }
                }

            }).start();

        });

        while (true) {

        }
    }

    @Test
    public void TimeWindowTest() {

    }

    @Test
    public void LeakBucketTest() {

    }

    @Test
    public void TokenBucketTest() {
        TokenBucketLimiter tokenBucketLimiter = new TokenBucketLimiter(100D, 10D);
        // 测试10个线程
        IntStream.range(0, 10).forEach((i) -> {
            new Thread(() -> {

                while (true) {

                    try {
                        Thread.sleep(new Random().nextInt(20) * 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (tokenBucketLimiter.limit()) {
                        System.out.println("线程：" + i + "通行！");
                    } else {
                        System.out.println("线程：" + i + "等待！===========");
                    }
                }

            }).start();

        });

        while (true) {

        }
    }
}
