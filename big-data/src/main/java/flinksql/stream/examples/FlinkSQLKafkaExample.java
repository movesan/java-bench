package flinksql.stream.examples;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author yangbin216
 * @version 1.0
 * @description:
 * @date 2022/6/15 11:08
 */
public class FlinkSQLKafkaExample {

    public static void main(String[] args) throws Exception {

        // 1. ---------------------------------- 创建执行环境 ----------------------------------
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .build();
//        TableEnvironment tableEnv = TableEnvironment.create(settings);
//        tableEnv.useCatalog("custom_catalog"); // catalog 名
//        tableEnv.useDatabase("custom_database"); // 数据库名

        Configuration conf = new Configuration();
        conf.setInteger(RestOptions.PORT, 8050);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        // ---------- checkpoint config ---------
        // start a checkpoint every 1000 ms
        env.enableCheckpointing(60000);

        // advanced options:
        // set mode to exactly-once (this is the default)
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // make sure 500 ms of progress happen between checkpoints
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(60000);
        // checkpoints have to complete within one minute, or are discarded
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        // only two consecutive checkpoint failures are tolerated
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(2);
        // allow only one checkpoint to be in progress at the same time
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        // enable externalized checkpoints which are retained after job cancellation
        env.getCheckpointConfig().enableExternalizedCheckpoints(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        // enables the unaligned checkpoints
        env.getCheckpointConfig().enableUnalignedCheckpoints();
        // sets the checkpoint storage where checkpoint snapshots will be written
//        env.getCheckpointConfig().setCheckpointStorage("hdfs:///my/checkpoint/dir");

        // 初始化 table evn
        TableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // access flink configuration
        Configuration configuration = tableEnv.getConfig().getConfiguration();
        // set low-level key-value options
        configuration.setString("table.exec.mini-batch.enabled", "true"); // enable mini-batch optimization
        configuration.setString("table.exec.mini-batch.allow-latency", "5 s"); // use 5 seconds to buffer input records
        configuration.setString("table.exec.mini-batch.size", "5000"); // the maximum number of records can be buffered by each aggregate operator task



        // 2. ---------------------------------- 创建表 ----------------------------------
        // Kafka Table DDL
        String kafkaTableDDL = "CREATE TABLE employees (\n" +
                "  `emp_no` INT,\n" +
                "  `birth_date` BIGINT,\n" +
                "  `first_name` STRING,\n" +
                "  `last_name` STRING,\n" +
                "  `hire_date` BIGINT\n" +
                ") WITH (\n" +
                "  'connector' = 'kafka',\n" +
                "  'topic' = 'employees',\n" +
                "  'properties.bootstrap.servers' = 'localhost:9092',\n" +
                "  'properties.group.id' = 'testGroup',\n" +
                "  'scan.startup.mode' = 'earliest-offset',\n" +
                "  'format' = 'json'\n" +
                ")";
        // 2.3 使用 SQL 创建，Using SQL DDL
        tableEnv.executeSql(kafkaTableDDL);


        // 3. ---------------------------------- 扩展表标识符 ----------------------------------
//        Table table = tableEnv.from("X").select("...");
//        // register the view named 'exampleView' in the catalog named 'custom_catalog'
//        // in the database named 'custom_database'
//        tableEnv.createTemporaryView("exampleView", table);
//
//        // register the view named 'exampleView' in the catalog named 'custom_catalog'
//        // in the database named 'other_database'
//        tableEnv.createTemporaryView("other_database.exampleView", table);
//
//        // register the view named 'example.View' in the catalog named 'custom_catalog'
//        // in the database named 'custom_database'
//        tableEnv.createTemporaryView("`example.View`", table);
//
//        // register the view named 'exampleView' in the catalog named 'other_catalog'
//        // in the database named 'other_database'
//        tableEnv.createTemporaryView("other_catalog.other_database.exampleView", table);


        // 4. ---------------------------------- 查询表 ----------------------------------
//        Table revenueSql = tableEnv.sqlQuery(
//                "SELECT cID, cName, SUM(revenue) AS revSum " +
//                        "FROM Orders " +
//                        "WHERE cCountry = 'FRANCE' " +
//                        "GROUP BY cID, cName"
//        );

//        String querySql = "SELECT * FROM employees";
//        TableResult queryResult = tableEnv.executeSql(querySql);
//        queryResult.print();

        String aggSql = "CREATE VIEW aggregate_view AS\n" +
                "SELECT\n" +
                "count(*) AS cnt \n" +
                "FROM\n" +
                "employees";
        TableResult queryResult = tableEnv.executeSql(aggSql);

        // 5. ---------------------------------- 输出表 ----------------------------------
        // compute revenue for all customers from France and emit to "RevenueFrance"
        tableEnv.executeSql(
                "CREATE TABLE print_table (\n" +
                        "  cnt BIGINT\n" +
                        ") WITH (\n" +
                        "  'connector' = 'print'\n" +
//                        "  'sink.parallelism\n' = '10'\n" +
                        ")"
        );

        tableEnv.executeSql(
                "INSERT INTO print_table\n" +
                        "SELECT *\n" +
                        "FROM aggregate_view"
        );
//        tableResult.print();


    }
}