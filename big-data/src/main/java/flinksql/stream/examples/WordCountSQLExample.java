package flinksql.stream.examples;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

/** The famous word count example that shows a minimal Flink SQL job in batch execution mode. */
public final class WordCountSQLExample {

    public static void main(String[] args) throws Exception {

        // set up the Table API
        final EnvironmentSettings settings =
                EnvironmentSettings.newInstance().inBatchMode().build();
        final TableEnvironment tableEnv = TableEnvironment.create(settings);

        // execute a Flink SQL job and print the result locally
        tableEnv.executeSql(
                        // define the aggregation
                        "SELECT word, SUM(frequency) AS `count`\n"
                                // read from an artificial fixed-size table with rows and columns
                                + "FROM (\n"
                                + "  VALUES ('Hello', 1), ('Ciao', 1), ('Hello', 2)\n"
                                + ")\n"
                                // name the table and its columns
                                + "AS WordTable(word, frequency)\n"
                                // group for aggregation
                                + "GROUP BY word")
                .print();
    }
}
