package flink1_14.common.resource;

import lombok.Data;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 *
 * @author yangbin216
 * @date 2021/7/30 11:06
 * @version 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class FileInfo extends ResourceBaseInfo {

    private String outputPath;
    private long rolloverInterval = TimeUnit.MINUTES.toMillis(60);
    private long inactivityInterval = TimeUnit.MINUTES.toMillis(60);
    private long maxPartSize = 1024 * 1024 * 1024;

    public FileInfo(String outputPath) {
        this.outputPath = outputPath;
    }
}