package netty.reactor.mainsub;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: movesan
 * @create: 2020-08-19 17:19
 **/
public class SubReactorThreadGroup {

    private static final AtomicInteger requestCounter = new AtomicInteger();  //请求计数器

    private final int nioThreadCount;  // 线程池IO线程的数量
    private static final int DEFAULT_NIO_THREAD_COUNT;
    private SubReactorThread[] nioThreads;
    private ExecutorService businessExecutePool; //业务线程池

    static {
        //      DEFAULT_NIO_THREAD_COUNT = Runtime.getRuntime().availableProcessors() > 1
        //              ? 2 * (Runtime.getRuntime().availableProcessors() - 1 ) : 2;

        DEFAULT_NIO_THREAD_COUNT = 4;
    }

    public SubReactorThreadGroup() {
        this(DEFAULT_NIO_THREAD_COUNT);
    }

    public SubReactorThreadGroup(int threadCount) {

        if(threadCount < 1) {
            threadCount = DEFAULT_NIO_THREAD_COUNT;
        }

        businessExecutePool = Executors.newFixedThreadPool(threadCount);

        this.nioThreadCount = threadCount;
        this.nioThreads = new SubReactorThread[threadCount];
        for(int i = 0; i < threadCount; i ++ ) {
            this.nioThreads[i] = new SubReactorThread(businessExecutePool);
            this.nioThreads[i].start(); //构造方法中启动线程，由于nioThreads不会对外暴露，故不会引起线程逃逸
        }

        System.out.println("Nio 线程数量：" + threadCount);
    }

    public void dispatch(SocketChannel socketChannel) {
        if(socketChannel != null ) {
            next().register(new NioTask(socketChannel, SelectionKey.OP_READ));
        }
    }

    protected SubReactorThread next() {
        return this.nioThreads[ requestCounter.getAndIncrement() %  nioThreadCount ];
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
}
