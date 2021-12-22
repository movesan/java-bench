package ratelimit;

/**
 * @description: 限流器
 * @author: bin.yang05
 * @create: 2020-10-04 17:18
 **/
public interface Limiter {

    /**
     * 限制流量
     * @return
     */
    boolean limit();
}
