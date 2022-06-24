package flink.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangbin216
 * @version 1.0
 * @description: 流数据
 * @date 2021/7/27 11:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamData {

    private String tableName;

    private long timeStamp;

    private Map<String, String> fields = new HashMap<>();

    public StreamData(String tableName) {
        this.tableName = tableName;
    }

    public StreamData join(StreamData otherData) {
        this.getFields().putAll(otherData.getFields());
        return this;
    }
}