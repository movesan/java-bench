package flinksql.stream;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.FormatDescriptor;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/6/15 11:08
 * @version 1.0
 */
public class FlinkSQLDemo {

    public static void main(String[] args) {

        // 1. ---------------------------------- 创建执行环境 ----------------------------------
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                //.inBatchMode()
                .build();

        TableEnvironment tableEnv = TableEnvironment.create(settings);
        tableEnv.useCatalog("custom_catalog"); // catalog 名
        tableEnv.useDatabase("custom_database"); // 数据库名


        // 2. ---------------------------------- 创建表 ----------------------------------
        // 2.1 通过 Table API 在 catalog 中创建
        Table projTable = tableEnv.from("X").select("...");

        // register the Table projTable as table "projectedTable"
        tableEnv.createTemporaryView("projectedTable", projTable);

        // 2.2 使用 table 描述器， Using table descriptors
        final TableDescriptor sourceDescriptor = TableDescriptor.forConnector("datagen")
                .schema(Schema.newBuilder()
                        .column("f0", DataTypes.STRING())
                        .build())
                .build();

        tableEnv.createTable("SourceTableA", sourceDescriptor);
        tableEnv.createTemporaryTable("SourceTableB", sourceDescriptor);

        // 2.3 使用 SQL 创建，Using SQL DDL
        tableEnv.executeSql("CREATE [TEMPORARY] TABLE MyTable (...) WITH (...)");


        // 3. ---------------------------------- 扩展表标识符 ----------------------------------
        Table table = tableEnv.from("X").select("...");
        // register the view named 'exampleView' in the catalog named 'custom_catalog'
        // in the database named 'custom_database'
        tableEnv.createTemporaryView("exampleView", table);

        // register the view named 'exampleView' in the catalog named 'custom_catalog'
        // in the database named 'other_database'
        tableEnv.createTemporaryView("other_database.exampleView", table);

        // register the view named 'example.View' in the catalog named 'custom_catalog'
        // in the database named 'custom_database'
        tableEnv.createTemporaryView("`example.View`", table);

        // register the view named 'exampleView' in the catalog named 'other_catalog'
        // in the database named 'other_database'
        tableEnv.createTemporaryView("other_catalog.other_database.exampleView", table);


        // 4. ---------------------------------- 查询表 ----------------------------------
        // 4.1 Table API
        // scan registered Orders table
        Table orders = tableEnv.from("Orders");
        Table revenue = orders
                .filter($("cCountry").isEqual("FRANCE"))
                .groupBy($("cID"), $("cName"))
                .select($("cID"), $("cName"), $("revenue").sum().as("revSum"));

        // 4.2 Flink SQL
        Table revenueSql = tableEnv.sqlQuery(
                "SELECT cID, cName, SUM(revenue) AS revSum " +
                        "FROM Orders " +
                        "WHERE cCountry = 'FRANCE' " +
                        "GROUP BY cID, cName"
        );

        // compute revenue for all customers from France and emit to "RevenueFrance"
        tableEnv.executeSql(
                "INSERT INTO RevenueFrance " +
                        "SELECT cID, cName, SUM(revenue) AS revSum " +
                        "FROM Orders " +
                        "WHERE cCountry = 'FRANCE' " +
                        "GROUP BY cID, cName"
        );


        // 5. ---------------------------------- 输出表 ----------------------------------
        // create an output Table
        final Schema schema = Schema.newBuilder()
                .column("a", DataTypes.INT())
                .column("b", DataTypes.STRING())
                .column("c", DataTypes.BIGINT())
                .build();

        tableEnv.createTemporaryTable("CsvSinkTable", TableDescriptor.forConnector("filesystem")
                .schema(schema)
                .option("path", "/path/to/file")
                .format(FormatDescriptor.forFormat("csv")
                        .option("field-delimiter", "|")
                        .build())
                .build());
        // compute a result Table using Table API operators and/or SQL queries
        Table result = tableEnv.sqlQuery(
                "SELECT a FROM CsvSinkTable "
        );

        TableResult tableResult = result.executeInsert("CsvSinkTable");
        tableResult.print();

    }
}