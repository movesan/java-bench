package flinksql.stream.env;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/6/15 11:01
 * @version 1.0
 */
public class TableEnv {

    public static void main(String[] args) {
        // 1. 通过静态方法 TableEnvironment.create() 创建
        EnvironmentSettings fsSettings = EnvironmentSettings
                .newInstance()
//                .useBlinkPlanner() // Blink 执行器
//                .useOldPlanner() // Flink 执行器
//                .inBatchMode() // 批模式
                .inStreamingMode() // 流模式
                .build();
        TableEnvironment tableEnv = TableEnvironment.create(fsSettings);

        // 2. 从现有的 StreamExecutionEnvironment 创建
        StreamExecutionEnvironment fsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment fsTableEnv = StreamTableEnvironment.create(fsEnv);
    }
}