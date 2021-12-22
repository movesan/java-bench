package netty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @description: netty 服务端
 * @author: Mr.Move
 * @create: 2018-11-08 14:14
 **/
public class NettyServer {

    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap(); // 辅助工具类（引导类），用于服务器通道的一系列配置

        NioEventLoopGroup boss = new NioEventLoopGroup(); // 用于处理服务器端接收客户端连接，表示监听端口，accept 新连接的线程组
        NioEventLoopGroup worker = new NioEventLoopGroup(); // 表示处理每一条连接的数据读写的线程组
        serverBootstrap
                .group(boss, worker) // 绑定两个线程组
                .channel(NioServerSocketChannel.class)  // 指定NIO的模式
                .childHandler(new ChannelInitializer<NioSocketChannel>() { // 配置具体的数据处理方式，ChannelInitializer 其实也是一个 handler，作为 pipeline 第一个

                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                .bind(8000);
    }
}
