package slidingwindow;

import lombok.Data;

/**
 * @description: 单个窗口任务
 * @author: movesan
 * @create: 2020-09-06 14:51
 **/
@Data
public class WindowTask {
    /**
     * 任务号
     */
    private int taskNo;
    /**
     * 任务数据
     */
    private Object taskValue;

}
