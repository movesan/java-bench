package slidingwindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @description:
 * @author: movesan
 * @create: 2020-09-06 14:53
 **/
public class SlidingWindowTest {

    public static void main(String[] args) throws InterruptedException {
        int listSize = 100;
        List<WindowTask> taskList = new ArrayList<>(listSize);
        List<Integer> taskDoneList = new ArrayList<>(listSize);
        for (int i=0; i<listSize; i++) {
            WindowTask windowTask = new WindowTask();
            windowTask.setTaskNo(i);
            windowTask.setTaskValue("我是任务：" + i);
            taskList.add(windowTask);
        }

        SlidingWindow slidingWindow = new SlidingWindow(4, new TaskHandler() {
            @Override
            public void doWork(WindowTask windowTask) {
                System.out.println("处理任务：" + windowTask.getTaskNo());
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void commitWork(WindowTask windowTask) {
                System.out.println("任务：" + windowTask.getTaskNo() + " 处理完成，准备提交");
                // 也可以提交到队列中，开启一个提交线程统一提交
                taskDoneList.add(windowTask.getTaskNo());
            }
        });
        // 执行滑动窗口任务
        slidingWindow.execute(taskList);

        Thread.sleep(1000);
        System.out.println(taskDoneList);

    }
}
