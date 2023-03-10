package flinksql.stream.examples.udf;

import lombok.Getter;
import org.apache.flink.types.Row;

import java.util.HashMap;
import java.util.Map;

public class MetricsAccumulator {

    @Getter
    public Map<String, Integer> metricsMap;
//    private String metricType;
//    private Integer metricValue;

    public MetricsAccumulator() {
        metricsMap = new HashMap<>();
    }

    public void accumulate(Row metricRow) {
        String metricName = metricRow.getFieldAs(0);
        String calcType = metricRow.getFieldAs(1);
        int calcMember = Integer.parseInt(metricRow.getFieldAs(2));

        int metricValue = metricsMap.get(metricName) != 0 ? metricsMap.get(metricName) : 0;

        if ("COUNT".equals(calcType)) {
            metricsMap.put(metricName, ++metricValue);
        } else if ("SUM".equals(calcType)) {
            metricsMap.put(metricName, metricValue + calcMember);
        }

    }

}
