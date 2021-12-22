package others;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;
import org.junit.Test;

/**
 * @description:
 * @author: movesan
 * @create: 2020-09-06 18:33
 **/
public class ThreadTest {

    @Test
    public void lockSupportTest() throws InterruptedException {
        Thread t = new Thread(()->{
            System.out.println("start");
            LockSupport.park(); //一直wait
            System.out.println("continue");
        });
        t.start();

        Thread.sleep(1000);
        LockSupport.unpark(t); //指定t线程解除wait态
    }

    @Test
    public void threadPoolTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            System.out.println("我是一个线程");
        });
    }
}
