package others;

import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description: 一些小测验
 * @author: movesan
 * @create: 2020-05-06 15:23
 **/
public class SomeProblemQuizzes {

    static Vector v = new Vector(10);

    private static void deadLoopTest() {
        System.out.println("请求cpu死循环");
        Thread.currentThread().setName("loop-thread-cpu");
        int num = 0;
        while (true) {
            num++;
            System.out.println(num);
            if (num == Integer.MAX_VALUE) {
                System.out.println("reset");
            }
//            num = 0;
        }
    }

    private static void deadLockTest() {
        ExecutorService service = new ThreadPoolExecutor(4, 10,
                0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1024),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        System.out.println("请求cpu");
        Object lock1 = new Object();
        Object lock2 = new Object();
        service.submit(new DeadLockThread(lock1, lock2), "deadLookThread-" + new Random().nextInt());
        service.submit(new DeadLockThread(lock2, lock1), "deadLookThread-" + new Random().nextInt());

    }

    public static class DeadLockThread implements Runnable {
        private Object lock1;
        private Object lock2;

        public DeadLockThread(Object lock1, Object lock2) {
            this.lock1 = lock1;
            this.lock2 = lock2;
        }

        @Override
        public void run() {
            synchronized (lock2) {
                System.out.println(Thread.currentThread().getName()+"get lock2 and wait lock1");
                try {
                    TimeUnit.MILLISECONDS.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock1) {
                    System.out.println(Thread.currentThread().getName()+"get lock1 and lock2 ");
                }
            }
        }
    }

    /**
     * 模拟内存泄漏
     * 问题原因：
     *        由于 ThreadLocalMap 的key 维护的是弱引用，所以会在gc 时回收，但是 value 却不会释放，导致内存泄漏
     *        （threadLocal已做优化，在 set，get，remove 时进行了对 key=null 的删除）
     */
    public static void leakTest() {
        int i = 0;
        while (i < 500) {
//            System.out.println("模拟内存泄漏");
            ThreadLocal<Byte[]> localVariable = new ThreadLocal<Byte[]>();
            localVariable.set(new Byte[4096 * 1024]);// 为线程添加变量
            i++;
        }
        // 为了避免让进程停止，观察内存情况
        while (true) {

        }
    }

    /**
     * 这些静态变量的生命周期和应用程序一致，他们所引用的所有的对象Object也不能被释放，因为他们也将一直被Vector等引用着。
     */
    public static void heapLeakTest() {
        for (int i = 1; i<100; i++){
            Object o = new Object();
            v.add(o);
            o = null;
        }
    }

    public static void main(String[] args) {
//        deadLoopTest();
        deadLockTest();
    }
}
