package flink.common.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2022/2/28 10:52
 * @version 1.0
 */
public class DataMock extends KafkaMockSourceFunction{

    @Override
    protected String generateTableName() {
        return "order";
    }

    @Override
    protected Map<String, String> generateKeys() {
        Map<String, String> map = new HashMap<>();
        map.put("key_no", "NO");
        return map;
    }

    @Override
    protected Map<String, String> generateFields() {
        Random random = new Random();
        Map<String, String> map = new HashMap<>();
        map.put("user", String.valueOf(random.nextInt(100)));
        map.put("gender", String.valueOf(random.nextInt(100)));
        map.put("phone", String.valueOf(random.nextInt(100)));
        return map;
    }
}