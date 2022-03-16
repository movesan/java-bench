package someskills;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Fibonacci {

    private final Map<Integer, Long> cache;

    public Fibonacci() {
        cache = new ConcurrentHashMap<>();
        cache.put(0, 0L);
        cache.put(1, 1L);
    }

    public long fibonacci(int x) {
        return cache.computeIfAbsent(x, n -> fibonacci(n - 1) + fibonacci(n - 2));
    }

    @Override
    public String toString() {
        return cache.values().toString();
    }
}
