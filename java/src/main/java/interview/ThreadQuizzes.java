package interview;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description: 多线程测验
 * @author: movesan
 * @create: 2020-04-25 22:01
 **/
public class ThreadQuizzes {

    private static volatile boolean flag = false;
    private static volatile int i = 1;

    /**
     * 两个线程交替打印 1-100
     * synchronized，wait 实现
     */
    private static void alternateExecuteA() {
        final Object object = new Object();
        new Thread(() -> {
            int i = 1;
            while (i <= 100) {
                synchronized (object) {
                    try {
                        while (flag) {
                            object.wait();
                        }
                        flag = true;
                        System.out.println(Thread.currentThread().getName() + i);
                        i = i + 2;
                        object.notifyAll();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "ThreadA -- 打印奇数").start();

        new Thread(() -> {
            int i = 2;
            while (i <= 100) {
                synchronized (object) {
                    try {
                        while (!flag) {
                            object.wait();
                        }
                        flag = false;
                        System.out.println(Thread.currentThread().getName() + i);
                        i = i + 2;
                        object.notifyAll();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "ThreadB -- 打印偶数").start();
    }

    /**
     * 两个线程交替打印 1-100
     * Lock，Condition 方式
     */
    private static void alternateExecuteB() {
        Lock lock = new ReentrantLock();
        Condition conditionA = lock.newCondition();
        Condition conditionB = lock.newCondition();
        new Thread(() -> {
            int i = 1;
            while (i <= 100) {
                lock.lock();
                try {
                    while (flag) {
                        conditionA.await();
                    }
                    flag = true;
                    System.out.println(Thread.currentThread().getName() + i);
                    i = i + 2;
                    conditionB.signalAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }

            }
        }, "ThreadA -- 打印奇数").start();

        new Thread(() -> {
            int i = 2;
            while (i <= 100) {
                lock.lock();
                try {
                    while (!flag) {
                        conditionB.await();
                    }
                    flag = false;
                    System.out.println(Thread.currentThread().getName() + i);
                    i = i + 2;
                    conditionA.signalAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }

            }
        }, "ThreadB -- 打印偶数").start();
    }

    /**
     * 两个线程交替打印 1-100
     * Runnable 实现
     */
    private static void alternateExecuteC() {
        Object obj = new Object();
        Runnable runnable = () -> {
            while (i <= 100) {
                synchronized (obj) {
                    System.out.println(Thread.currentThread().getName() + i++);
                    try {
                        obj.notifyAll();
                        obj.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread threadA = new Thread(runnable, "ThreadA -- 打印奇数");
        Thread threadB = new Thread(runnable, "ThreadB -- 打印偶数");
        threadA.start();
        threadB.start();
    }


    public static void main(String[] args) {
//        alternateExecuteA();
        alternateExecuteB();
//        alternateExecuteC();
    }
}
