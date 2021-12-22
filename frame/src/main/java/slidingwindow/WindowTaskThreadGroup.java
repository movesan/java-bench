package slidingwindow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Data;

/**
 * @description: 窗口工作线程组
 * @author: movesan
 * @create: 2020-09-06 15:07
 **/
@Data
public class WindowTaskThreadGroup {

    private int taskThreadCount;

    private ExecutorService executorService;

    private SlidingWindow slidingWindow;

    private List<Thread> threads = new ArrayList<>();

    public WindowTaskThreadGroup(int taskThreadCount, SlidingWindow slidingWindow) {
        this.taskThreadCount = taskThreadCount;
        this.executorService = Executors.newFixedThreadPool(taskThreadCount);
        this.slidingWindow = slidingWindow;
    }

    public void addTask(WindowTask windowTask) {
        WindowTaskThread windowTaskThread = new WindowTaskThread(windowTask, slidingWindow, this);
        executorService.submit(windowTaskThread);
    }
}
