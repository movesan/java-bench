package flink1_14.common.mock;

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
public class PackageMock extends KafkaMockSourceFunction{

    @Override
    protected String generateTableName() {
        return "package";
    }

    @Override
    protected Map<String, String> generateKeys() {
        Map<String, String> map = new HashMap<>();
        map.put("order_no", "ORDER");
        return map;
    }

    @Override
    protected Map<String, String> generateFields() {
        Random random = new Random();
        Map<String, String> map = new HashMap<>();
        map.put("goods", String.valueOf(random.nextInt(100)));
        map.put("address", String.valueOf(random.nextInt(100)));
        return map;
    }
}