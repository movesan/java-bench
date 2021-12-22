package concurrent.thread;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

/**
 * @description: 多线程测试 demo
 * @author: movesan
 * @create: 2020-08-31 15:07
 **/
public class MultiThreadTestDemo {

    @Test
    public void joinTest() {
        AtomicInteger num = new AtomicInteger(0);
        int count = 100;
        for (int i = 0; i < count; i++) {
            Thread thread = new Thread(() -> {
//                int a = 5 / 0;
                num.getAndIncrement();
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("子任务执行异常");
                throw new RuntimeException(e);
            }
        }
        System.out.println("总任务执行完成，num：" + num);
    }

    /**
     * 借助 future 可以实现捕获子线程中的异常，并且发生异常后主线程可终止
     *
     * 之所以 future 可以捕获异常，是因为在执行 submit 的时候，其实是通过传进去的 callable 构造了 FutureTask，
     * callable 作为 futureTask 中的属性，执行 futureTask 的 run 方法时是调用了 callable 的 call 方法，此方法支持返回值和抛出异常；
     * 而在 futureTask 的 run 方法中会保存返回结果或者异常信息，如果最后调用 get 方法时，
     *      状态为正常状态：返回结果
     *      状态为非正常状态：new ExecutionException((Throwable)x)，x 即为之前保存的异常信息
     * @see FutureTask#report(int)
     */
    @Test
    public void futureTest() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        // 子任务 futures
        List<Future> futures = Lists.newArrayList();
        int count = 2;
        for (int i = 0; i < count; i++) {
            futures.add(executor.submit(() -> {
                System.out.println("thread id is: " + Thread.currentThread().getId());
                try {
                    int a = 5 / 0;
                } catch (Exception e) {
                    throw new RuntimeException("运行时异常");
                } finally {

                }
            }));
        }
        for (Future f : futures) {
            try {
                // 等待子任务执行完成
                f.get();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted!" + e);
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted!", e);
            } catch (ExecutionException e) {
                System.out.println("子任务执行失败");
                throw new RuntimeException(e);
            }
        }
        System.out.println("总任务执行完成");
    }

    @Test
    public void threadTest() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        int count = 10;
        for (int i = 0; i < count; i++) {
            executor.execute(() -> {
                System.out.println("thread id is: " + Thread.currentThread().getId());
                try {
                    int a = 5 / 0;
                } catch (Exception e) {
                    System.out.println("运行时异常");
                    throw new RuntimeException("运行时异常");
                } finally {
                    System.out.println("\"thread id is: \" + Thread.currentThread().getId()" + "finally");
                }
            });
        }
    }

    @Test
    public void threadExceptionTest() {
        ExecutorService executor = Executors.newFixedThreadPool(2, new HandlerThreadFactory());
        int count = 2;
        for (int i = 0; i < count; i++) {
            executor.execute(() -> {
                System.out.println("thread id is: " + Thread.currentThread().getId());
                try {
                    int a = 5 / 0;
                } catch (Exception e) {
                    System.out.println("运行时异常" + e);
                    throw new RuntimeException("运行时异常" + e);
                } finally {
                    System.out.println("\"thread id is: \" + Thread.currentThread().getId()" + "finally");
                }
            });
        }
        System.out.println("主线程执行完成");
    }

    public static class HandlerThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            System.out.println("创建一个新的线程");
            Thread t = new Thread(r);
            t.setUncaughtExceptionHandler(new MyExceptionHandler());
            return t;
        }

    }

    public static class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println("捕获到异常：" + e);
        }

    }
}
