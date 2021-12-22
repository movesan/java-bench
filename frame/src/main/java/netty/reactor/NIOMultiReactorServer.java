package netty.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description: nio 多线程多reactor 模型（server）
 * @author: movesan
 * @create: 2020-08-19 16:29
 **/
public class NIOMultiReactorServer {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        new Thread(new Acceptor()).start();

    }

    /**
     * 连接线程模型，反应堆，转发器 Acceptor
     *
     */
    private static final class Acceptor implements Runnable {

        private NioReactorThreadGroup nioReactorThreadGroup;

        public Acceptor() {
            nioReactorThreadGroup = new NioReactorThreadGroup();
        }

        public void run() {
            // TODO Auto-generated method stub
            System.out.println("服务端启动成功，等待客户端接入");
            ServerSocketChannel ssc = null;
            Selector selector = null;
            try {
                ssc = ServerSocketChannel.open();
                ssc.configureBlocking(false);
                ssc.bind(new InetSocketAddress("127.0.0.1", 9080));

                selector = Selector.open();
                ssc.register(selector, SelectionKey.OP_ACCEPT);

                Set<SelectionKey> ops = null;
                while (true) {
                    try {
                        selector.select(); // 如果没有感兴趣的事件到达，阻塞等待
                        ops = selector.selectedKeys();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        break;
                    }

                    // 处理相关事件
                    for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext();) {
                        SelectionKey key = it.next();
                        it.remove();

                        try {
                            if (key.isAcceptable()) { // 客户端建立连接
                                System.out.println("收到客户端的连接请求。。。");
                                ServerSocketChannel serverSc = (ServerSocketChannel) key.channel();// 这里其实，可以直接使用ssl这个变量
                                SocketChannel clientChannel = serverSc.accept();
                                clientChannel.configureBlocking(false);
                                nioReactorThreadGroup.dispatch(clientChannel); // 转发该请求
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            System.out.println("客户端主动断开连接。。。。。。。");
                        }

                    }
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    /**
     * nio 线程组;简易的NIO线程组
     * @author dingwei2
     *
     */
    public static class NioReactorThreadGroup {

        private final AtomicInteger requestCounter = new AtomicInteger();  //请求计数器

        private final int nioThreadCount;  // 线程池IO线程的数量
        private static final int DEFAULT_NIO_THREAD_COUNT;
        private NioReactorThread[] nioThreads;

        static {
            //      DEFAULT_NIO_THREAD_COUNT = Runtime.getRuntime().availableProcessors() > 1
            //              ? 2 * (Runtime.getRuntime().availableProcessors() - 1 ) : 2;

            DEFAULT_NIO_THREAD_COUNT = 4;
        }

        public NioReactorThreadGroup() {
            this(DEFAULT_NIO_THREAD_COUNT);
        }

        public NioReactorThreadGroup(int threadCount) {
            if (threadCount < 1) {
                threadCount = DEFAULT_NIO_THREAD_COUNT;
            }
            this.nioThreadCount = threadCount;
            this.nioThreads = new NioReactorThread[threadCount];
            for (int i = 0; i < threadCount; i++) {
                this.nioThreads[i] = new NioReactorThread();
                this.nioThreads[i].start(); //构造方法中启动线程，由于nioThreads不会对外暴露，故不会引起线程逃逸
            }

            System.out.println("Nio 线程数量：" + threadCount);
        }

        public void dispatch(SocketChannel socketChannel) {
            if (socketChannel != null) {
                next().register(socketChannel);
            }
        }

        protected NioReactorThread next() {
            return this.nioThreads[requestCounter.getAndIncrement() % nioThreadCount];
        }

        public void main(String[] args) {
            // TODO Auto-generated method stub

        }
    }

    /**
     * Nio 线程，专门负责nio read,write
     * 本类是实例行代码，不会对nio,断线重连，写半包等场景进行处理,旨在理解 Reactor模型（多线程版本）
     * @author dingwei2
     *
     */
    public static class NioReactorThread extends Thread {

        private final byte[] b = "hello,服务器收到了你的信息。".getBytes(); //服务端给客户端的响应

        private Selector selector;
        private List<SocketChannel> waitRegisterList = new ArrayList<SocketChannel>(512);
        private ReentrantLock registerLock = new ReentrantLock();

        public NioReactorThread() {
            try {
                this.selector = Selector.open();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /**
         * socket channel
         *
         * @param socketChannel
         */
        public void register(SocketChannel socketChannel) {
            if (socketChannel != null) {
                try {
                    registerLock.lock();
                    waitRegisterList.add(socketChannel);
                } finally {
                    registerLock.unlock();
                }
            }
        }

        //private

        public void run() {
            while (true) {
                Set<SelectionKey> ops = null;
                try {
                    selector.select(1000);
                    ops = selector.selectedKeys();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    continue;
                }

                //处理相关事件
                for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext(); ) {
                    SelectionKey key = it.next();
                    it.remove();

                    try {
                        if (key.isWritable()) { //向客户端发送请求
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            ByteBuffer buf = (ByteBuffer) key.attachment();
                            buf.flip();
                            clientChannel.write(buf);
                            System.out.println("服务端向客户端发送数据。。。");
                            //重新注册读事件
                            clientChannel.register(selector, SelectionKey.OP_READ);
                        } else if (key.isReadable()) {  //接受客户端请求
                            System.out.println("服务端接收客户端连接请求。。。");
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            ByteBuffer buf = ByteBuffer.allocate(1024);
                            System.out.println(buf.capacity());
                            clientChannel.read(buf);//
                            buf.put(b);
                            clientChannel.register(selector, SelectionKey.OP_WRITE, buf);//注册写事件
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        System.out.println("客户端主动断开连接。。。。。。。");
                    }

                }

                //注册事件
                if (!waitRegisterList.isEmpty()) {
                    try {
                        registerLock.lock();
                        for (Iterator<SocketChannel> it = waitRegisterList.iterator(); it.hasNext(); ) {
                            SocketChannel sc = it.next();
                            try {
                                sc.register(selector, SelectionKey.OP_READ);
                            } catch (Throwable e) {
                                e.printStackTrace();//ignore
                            }
                            it.remove();
                        }

                    } finally {
                        registerLock.unlock();
                    }
                }

            }
        }
    }
}
