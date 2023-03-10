package flinksql.stream.examples.udf;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@FunctionHint(
        output = @DataTypeHint("MAP<STRING, INT>"),
        input = @DataTypeHint("ARRAY<ROW<metricName STRING, calcType STRING, calcMember INT>>")
)
public class CalcMetricsTableFunction extends TableAggregateFunction<Tuple2<String, String>, MetricsAccumulator> {

    @Override
    public MetricsAccumulator createAccumulator() {
        return new MetricsAccumulator();
    }

    public void accumulate(MetricsAccumulator acc, Row[] metricRowArray) {
        System.out.println("accumulate param:" + Arrays.toString(metricRowArray));
        for (Row metricRow : metricRowArray) {
            acc.accumulate(metricRow);
        }
    }

    public void merge(MetricsAccumulator acc, Iterable<MetricsAccumulator> it) {

    }

    public void emitValue(MetricsAccumulator acc, Collector<Map<String, Integer>> out) {
        System.out.println("accumulate result:" + acc.getMetricsMap());
        out.collect(acc.getMetricsMap());
    }
}
