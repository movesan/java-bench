package slidingwindow;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Data;

/**
 * @description: 滑动窗口实现
 * @author: movesan
 * @create: 2020-09-06 14:49
 **/
@Data
public class SlidingWindow {

    /**
     * 窗口任务队列
     */
    private Queue<Integer> windowTaskQueue;
    /**
     * 窗口大小
     */
    private int windowSize;
    /**
     * 窗口中第一个任务
     */
    private int firstWindowTask;
    /**
     * 主线程
     */
    private Lock executeLock;
    private Condition executeCondition;


//    private Lock windowLock;
//    private Condition windowCondition;
    private TaskHandler taskHandler;
    private WindowTaskThreadGroup windowTaskThreadGroup;

    public SlidingWindow(int windowSize, TaskHandler taskHandler) {
        this.windowSize = windowSize;
        this.windowTaskQueue = new LinkedBlockingQueue<>(windowSize);
        this.windowTaskThreadGroup = new WindowTaskThreadGroup(windowSize, this);
        this.executeLock = new ReentrantLock();
        this.executeCondition = executeLock.newCondition();
//        this.windowLock = new ReentrantLock();
//        this.windowCondition = windowLock.newCondition();
        this.taskHandler = taskHandler;
    }

    /**
     * 执行滑动窗口处理任务
     * @param windowTaskList
     */
    public void execute(List<WindowTask> windowTaskList) {
        for (WindowTask windowTask : windowTaskList) {
            enqWindow(windowTask);
        }
        windowTaskThreadGroup.getExecutorService().shutdown();
    }

    /**
     * 窗口入队
     * @param windowTask
     */
    private void enqWindow(WindowTask windowTask) {
        while (!windowTaskQueue.offer(windowTask.getTaskNo())) {
            executeLock.lock();
            try {
                executeCondition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                executeLock.unlock();
            }
        }
        windowTaskThreadGroup.addTask(windowTask);
    }

    /**
     * 窗口出队
     * @return
     */
    private Integer deqWindow() {
        windowTaskQueue.poll();
        return windowTaskQueue.peek();
    }

    /**
     * 释放窗口
     * @return
     */
    public boolean releaseWindow() {
        Integer firstWindowTask = deqWindow();
        if (firstWindowTask != null) {
            setFirstWindowTask(firstWindowTask);
            executeLock.lock();
            try {
                executeCondition.signal();
            } finally {
                executeLock.unlock();
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isFirstWindowTask(int taskNo) {
        if (windowTaskQueue.isEmpty()) {
            return true;
        }
        return firstWindowTask == taskNo;
    }
}
