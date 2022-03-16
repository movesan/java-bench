package flink1_14.common;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @description: StreamExecutionEnvironment Builder
 *
 * @author yangbin216
 * @date 2021/7/23 15:57
 * @version 1.0
 */
public class StreamExecutionEnvironmentBuilder {

    /* Flink 参数默认值 */
    private static final int DEFAULT_MAX_PARALLELISM = 1000;
    private static final long DEFAULT_CHECKPOINT_INTERVAL = 120000L;
    private static final long DEFAULT_MIN_PAUSE_BETWEEN_CHECKPOINTS = 600000L;
    private static final long DEFAULT_CHECKPOINT_TIMEOUT = 600000L;
    private static final int DEFAULT_MAX_CONCURRENT_CHECKPOINTS = 1;
    private static final boolean DEFAULT_DISABLE_CHAINING = false;

    private Configuration conf;
    private CheckpointingMode checkpointingMode = CheckpointingMode.EXACTLY_ONCE;
    private int maxParallelism = DEFAULT_MAX_PARALLELISM; // 最大并行度
    private long checkpointInterval = DEFAULT_CHECKPOINT_INTERVAL; // 开启 checkpoint 执行频率
    private long minPauseBetweenCheckpoints = DEFAULT_MIN_PAUSE_BETWEEN_CHECKPOINTS; // checkpoint 的最小时间间隔，checkpointInterval 应大于此值
    private long checkpointTimeout = DEFAULT_CHECKPOINT_TIMEOUT; // checkpoint 超时时间
    private int maxConcurrentCheckpoints = DEFAULT_MAX_CONCURRENT_CHECKPOINTS; // 最大并行 checkpoint 个数
    private boolean disableOperatorChaining = DEFAULT_DISABLE_CHAINING;

    public StreamExecutionEnvironmentBuilder setCheckpointingMode(CheckpointingMode checkpointingMode) {
        this.checkpointingMode = checkpointingMode;
        return this;
    }

    public StreamExecutionEnvironmentBuilder setMaxParallelism(int maxParallelism) {
        this.maxParallelism = maxParallelism;
        return this;
    }

    public StreamExecutionEnvironmentBuilder setCheckpointInterval(long checkpointInterval) {
        this.checkpointInterval = checkpointInterval;
        return this;
    }

    public StreamExecutionEnvironmentBuilder setMinPauseBetweenCheckpoints(long minPauseBetweenCheckpoints) {
        this.minPauseBetweenCheckpoints = minPauseBetweenCheckpoints;
        return this;
    }

    public StreamExecutionEnvironmentBuilder setCheckpointTimeout(long checkpointTimeout) {
        this.checkpointTimeout = checkpointTimeout;
        return this;
    }

    public StreamExecutionEnvironmentBuilder setMaxConcurrentCheckpoints(int maxConcurrentCheckpoints) {
        this.maxConcurrentCheckpoints = maxConcurrentCheckpoints;
        return this;
    }

    public StreamExecutionEnvironmentBuilder setDisableOperatorChaining(boolean disableOperatorChaining) {
        this.disableOperatorChaining = disableOperatorChaining;
        return this;
    }

    public StreamExecutionEnvironmentBuilder setConfiguration(Configuration conf) {
        this.conf = conf;
        return this;
    }

    private boolean checkNotEmpty(int param) {
        return param != 0;
    }

    public StreamExecutionEnvironment build(String[] args) {
        ParameterTool parameterFile = ParameterTool.fromArgs(args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(parameterFile);

        env.setMaxParallelism(maxParallelism);
        env.enableCheckpointing(checkpointInterval, checkpointingMode);
        if (disableOperatorChaining) {
            env.disableOperatorChaining();
        }

        CheckpointConfig config = env.getCheckpointConfig();
        config.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        config.setMinPauseBetweenCheckpoints(minPauseBetweenCheckpoints);
        config.setCheckpointTimeout(checkpointTimeout);
        config.setMaxConcurrentCheckpoints(maxConcurrentCheckpoints);

        return env;
    }

    public StreamExecutionEnvironment buildWithWebUI(String[] args) {
        ParameterTool parameterFile = ParameterTool.fromArgs(args);

        if (conf == null) {
            conf = new Configuration();
            conf.setInteger(RestOptions.PORT, 8050);
//            conf.setString("taskmanager.memory.network.min", "512mb");
//            conf.setString("taskmanager.memory.network.max", "512mb");
//            conf.setDouble("taskmanager.memory.network.fraction", 0.2);
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.getConfig().setGlobalJobParameters(parameterFile);

        env.setMaxParallelism(maxParallelism);
        env.enableCheckpointing(checkpointInterval, checkpointingMode);
        if (disableOperatorChaining) {
            env.disableOperatorChaining();
        }

        CheckpointConfig config = env.getCheckpointConfig();
        config.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        config.setMinPauseBetweenCheckpoints(minPauseBetweenCheckpoints);
        config.setCheckpointTimeout(checkpointTimeout);
        config.setMaxConcurrentCheckpoints(maxConcurrentCheckpoints);

        return env;
    }
}