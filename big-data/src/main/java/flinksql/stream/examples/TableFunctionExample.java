package flinksql.stream.examples;

import flinksql.stream.common.Order;
import flinksql.stream.common.TableEnvironmentBuilder;
import flinksql.stream.examples.udf.SplitFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.types.Row;

import java.util.Arrays;

import static org.apache.flink.table.api.Expressions.*;

import static org.apache.flink.table.api.Expressions.$;

public class TableFunctionExample {

    public static void main(String[] args) {
        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        // set up the Java Table API
        final StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        final DataStream<Order> order =
                env.fromCollection(
                        Arrays.asList(
                                new Order(1L, "beer, qwe", 3),
                                new Order(1L, "diaper, 4567743, rtyy", 4),
                                new Order(3L, "", 2)));

//        // call function "inline" without registration in Table API
//        tableEnv.from("MyTable")
//                .joinLateral(call(SplitFunction.class, $("myField")))
//                .select($("myField"), $("word"), $("length"));
//        tableEnv.from("MyTable")
//                .leftOuterJoinLateral(call(SplitFunction.class, $("myField")))
//                .select($("myField"), $("word"), $("length"));
//
//        // rename fields of the function in Table API
//        tableEnv.from("MyTable")
//                .leftOuterJoinLateral(call(SplitFunction.class, $("myField")).as("newWord", "newLength"))
//                .select($("myField"), $("newWord"), $("newLength"));
//
//        // register function
        tableEnv.createTemporarySystemFunction("SplitFunction", SplitFunction.class);
        tableEnv.createTemporaryView("order_table", order);
//
//        // call registered function in Table API
//        tableEnv.from("MyTable")
//                .joinLateral(call("SplitFunction", $("myField")))
//                .select($("myField"), $("word"), $("length"));
//        tableEnv.from("MyTable")
//                .leftOuterJoinLateral(call("SplitFunction", $("myField")))
//                .select($("myField"), $("word"), $("length"));

        // call registered function in SQL
        TableResult result = tableEnv.executeSql(
                "SELECT user, product, word, length " +
                        "FROM order_table, LATERAL TABLE(SplitFunction(product))");
//        tableEnv.sqlQuery(
//                "SELECT myField, word, length " +
//                        "FROM order_table " +
//                        "LEFT JOIN LATERAL TABLE(SplitFunction(myField)) ON TRUE");

        // rename fields of the function in SQL
//        TableResult result = tableEnv.executeSql(
//                "SELECT user, product, newWord, newLength " +
//                        "FROM order_table " +
//                        "LEFT JOIN LATERAL TABLE(SplitFunction(product)) AS T(newWord, newLength) ON TRUE");

        result.print();
    }
}
