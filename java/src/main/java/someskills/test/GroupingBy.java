package someskills.test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @description: 自定义 collector，功能同 Collectors.groupingBy
 *
 * @author yangbin216
 * @date 2022/1/26 15:42
 * @version 1.0
 */
public class GroupingBy<T, K> implements Collector<T, Map<K, List<T>>, Map<K, List<T>>> {
    @Override
    public Supplier<Map<K, List<T>>> supplier() {
        return null;
    }

    @Override
    public BiConsumer<Map<K, List<T>>, T> accumulator() {
        return null;
    }

    @Override
    public BinaryOperator<Map<K, List<T>>> combiner() {
        return null;
    }

    @Override
    public Function<Map<K, List<T>>, Map<K, List<T>>> finisher() {
        return null;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return null;
    }
}