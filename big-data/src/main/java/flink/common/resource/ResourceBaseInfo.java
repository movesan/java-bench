package flink.common.resource;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yangbin216
 * @version 1.0
 * @description:
 * @date 2021/7/26 17:58
 */
@Data
public class ResourceBaseInfo implements Serializable {
    private Integer parallelism = 1;
}
