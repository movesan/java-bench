package ratelimit;

/**
 * @description: 令牌桶算法
 * @author: movesan
 * @create: 2020-10-04 17:17
 **/
public class TokenBucketLimiter implements Limiter {

    /**
     * 时间
     */
    private long time;
    /**
     * 总量
     */
    private Double total;
    /**
     * token 放入速度（每秒多少个token ）
     */
    private Double rate;
    /**
     * 当前总量
     */
    private Double nowSize = 0D;

    public TokenBucketLimiter() {
    }

    public TokenBucketLimiter(Double total, Double rate) {
        this.time = System.currentTimeMillis();
        this.total = total;
        this.rate = rate;
    }

    @Override
    public boolean limit() {
        long now = System.currentTimeMillis();
        nowSize = Math.min(total, nowSize + (now - time) / 1000L * rate);
        time = now;
        if (nowSize < 1) {
            // 桶里没有token
            return false;
        } else {
            // 存在token
            nowSize -= 1;
            return true;
        }
    }
}
