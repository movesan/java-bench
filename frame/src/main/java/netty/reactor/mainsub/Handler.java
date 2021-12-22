package netty.reactor.mainsub;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @description: 业务线程
 * 该handler的功能就是在收到的请求信息，后面加上 hello,服务器收到了你的信息，然后返回给客户端
 *
 * @author: movesan
 * @create: 2020-08-19 17:23
 **/
public class Handler implements Runnable {

    private static final byte[] b = "hello,服务器收到了你的信息。".getBytes(); // 服务端给客户端的响应

    private SocketChannel sc;
    private ByteBuffer reqBuffer;
    private SubReactorThread parent;

    public Handler(SocketChannel sc, ByteBuffer reqBuffer,
                   SubReactorThread parent) {
        super();
        this.sc = sc;
        this.reqBuffer = reqBuffer;
        this.parent = parent;
    }

    @Override
    public void run() {
        System.out.println("业务在handler中开始执行。。。");
        // TODO Auto-generated method stub
        //业务处理
        reqBuffer.put(b);
        parent.register(new NioTask(sc, SelectionKey.OP_WRITE, reqBuffer));
        System.out.println("业务在handler中执行结束。。。");
    }
}
