package flink1_14.common.mock;

import flink1_14.common.StreamData;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2021/7/29 13:12
 * @version 1.0
 */
public abstract class KafkaMockSourceFunction extends RichParallelSourceFunction<StreamData> {
    private static final long serialVersionUID = -7286937645300388040L;

    private transient volatile boolean isRunning;
    private transient Random random;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.random = new Random();
        this.isRunning = true;
    }

    @Override
    public void run(SourceContext<StreamData> ctx) throws Exception {
        // Sleep for 1 seconds on start to allow time to copy jobid
        Thread.sleep(1000L);

        while (isRunning) {
            Map<String, String> otherFields = generateFields();
            Map<String, String> keyFields = new HashMap<>();
            for (Map.Entry<String, String> entry : generateKeys().entrySet()) {
                keyFields.put(entry.getKey(), entry.getValue() + intervalRandom(1000, 1020));
            }

            Map<String, String> fieldsRandom =
                    splitFieldsRandom(Stream.concat(keyFields.entrySet().stream(), otherFields.entrySet().stream())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (v1, v2) -> v1)));

            synchronized (ctx.getCheckpointLock()) {
                StreamData streamData = new StreamData();
                streamData.setFields(fieldsRandom);
                streamData.setTableName(generateTableName());
                streamData.setTimeStamp(System.currentTimeMillis());

                ctx.collect(streamData);
            }

            Thread.sleep(intervalRandom(100, 500));
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }

    protected abstract String generateTableName();

    protected abstract Map<String, String> generateKeys();

    protected abstract Map<String, String> generateFields();

    private Map<String, String> splitFieldsRandom(Map<String, String> fields) {
        Map<String, String> fieldsRes = new HashMap<>();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            fieldsRes.put(key, value);
        }

        return fieldsRes;
    }

    /**
     * 生成 [m,n] 的数字
     * int randNumber =rand.nextInt(MAX - MIN + 1) + MIN;
     * */
    private int intervalRandom(int min, int max) {
        return random.nextInt((max - min + 1)) + min;
    }

    /**
     * 随机返回集合中某个元素
     * @param list
     * @return
     */
    protected String getRandomElement(List<String> list) {
        Collections.shuffle(list);
        return list.get(0);
    }

}