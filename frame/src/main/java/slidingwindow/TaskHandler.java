package slidingwindow;

/**
 * @description: 任务处理类
 * @author: movesan
 * @create: 2020-09-06 15:29
 **/
public interface TaskHandler {

    public void doWork(WindowTask windowTask);

    public void commitWork(WindowTask windowTask);
}
