package flinksql.stream.examples;

import flinksql.stream.common.Order;
import flinksql.stream.examples.udf.CalcMetricsTableFunction;
import flinksql.stream.examples.udf.SplitFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.Arrays;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.call;

public class TableAggFunctionExample {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        // set up the Java Table API
        final StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        final DataStream<Order> order =
                env.fromCollection(
                        Arrays.asList(
                                new Order(1L, "beer", 3),
                                new Order(1L, "diaper", 4),
                                new Order(2L, "diaper", 4),
                                new Order(2L, "diaper", 4),
                                new Order(3L, "diaper", 4),
                                new Order(3L, "diaper", 4),
                                new Order(4L, "diaper", 4),
                                new Order(4L, "fruit", 2)));

        // register function
        tableEnv.createTemporarySystemFunction("CalcMetricsTableFunction", CalcMetricsTableFunction.class);
        tableEnv.createTemporaryView("order_table", order);

        // call registered function in SQL
        Table metricsView = tableEnv.sqlQuery(
                "SELECT\n" +
                        "\t`user`,\n" +
                        "\tARRAY [\n" +
                        "\t\tROW (\n" +
                        "\t\t\tCAST('ORDER_AMT' AS VARCHAR),\n" +
                        "\t\t\tCAST('SUM' AS VARCHAR),\n" +
                        "\t\t\tamount\n" +
                        "\t\t)\n" +
                        "\t] AS metrics \n" +
                        "FROM order_table");

        tableEnv.createTemporaryView("metricsView", metricsView);

//        String resSql = "SELECT user, metricsMap['ORDER_AMT']\n" +
//                "FROM order_table, LATERAL TABLE(CalcMetricsTableFunction(metrics)) AS metricsMap";
//        TableResult result = tableEnv.executeSql(resSql);

        Table result = tableEnv
                .from("metricsView")
                .groupBy($("user"))
                .flatAggregate(call("CalcMetricsTableFunction", $("metrics")).as("metricsMap"))
                .select($("user"), $("metricsMap"));

//        env.execute();
//        result.print();
    }
}
