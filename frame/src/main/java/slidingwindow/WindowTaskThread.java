package slidingwindow;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * @description: 窗口任务线程
 * @author: movesan
 * @create: 2020-09-06 14:50
 **/
public class WindowTaskThread implements Runnable {

    private WindowTask windowTask;
    private SlidingWindow slidingWindow;
    private TaskHandler taskHandler;
    private Condition windowCondition;
    private Lock windowLock;
    private WindowTaskThreadGroup windowTaskThreadGroup;

    public WindowTaskThread(WindowTask windowTask, SlidingWindow slidingWindow, WindowTaskThreadGroup windowTaskThreadGroup) {
        this.windowTask = windowTask;
        this.slidingWindow = slidingWindow;
        this.taskHandler = slidingWindow.getTaskHandler();
        this.windowTaskThreadGroup = windowTaskThreadGroup;
    }

    @Override
    public void run() {
        if (!windowTaskThreadGroup.getThreads().contains(Thread.currentThread())) {
            windowTaskThreadGroup.getThreads().add(Thread.currentThread());
        }
        // 处理任务
        taskHandler.doWork(windowTask);

        while (!slidingWindow.isFirstWindowTask(windowTask.getTaskNo())) {
            LockSupport.park();
//            System.out.println("任务：" + windowTask.getTaskNo() + " park");
        }
        // 提交任务
        taskHandler.commitWork(windowTask);
        // 释放单个窗口
        if (slidingWindow.releaseWindow()) {
            for (Thread thread : windowTaskThreadGroup.getThreads()) {
                LockSupport.unpark(thread);
            }
        }

    }
}
