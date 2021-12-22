package ratelimit;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 计数器
 * @author: movesan
 * @create: 2020-10-04 17:13
 **/
public class CounterLimiter implements Limiter {
    /**
     * 最大访问数量
     */
    private int limit = 10;
    /**
     * 访问时间差（单位：ms）
     */
    private long timeout = 1000;
    /**
     * 请求时间
     */
    private long time;
    /**
     * 当前计数器
     */
    private final AtomicInteger reqCount = new AtomicInteger(0);

    public CounterLimiter() {
    }

    public CounterLimiter(int limit, long timeout) {
        this.limit = limit;
        this.timeout = timeout;
    }

    @Override
    public boolean limit() {
        long now = System.currentTimeMillis();
        if (now < time + timeout) {
            // 单位时间内
            return reqCount.incrementAndGet() <= limit;
        } else {
            // 超出单位时间
            time = now;
            int current = reqCount.get();

            // 需要初始化
            if (current != 0) {
                // 保证只有一个线程能初始化成功
                if (reqCount.compareAndSet(current, 0)) {
                    System.out.println("初始化成功，已初始化：0");
                    return true;
                } else {
                    System.out.println("初始化失败，自增");
                    return reqCount.incrementAndGet() <= limit;
                }
            }
            // 无需初始化
            else {
                System.out.println("无需初始化");
                return reqCount.incrementAndGet() <= limit;
            }
        }
    }
}
