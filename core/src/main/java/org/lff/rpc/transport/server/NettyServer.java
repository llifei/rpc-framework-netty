package org.lff.rpc.transport.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.lff.rpc.codec.CommonDecoder;
import org.lff.rpc.codec.CommonEncoder;
import org.lff.rpc.enumeration.SerializerCode;
import org.lff.rpc.hook.ShutdownHook;
import org.lff.rpc.provider.ServiceProviderImpl;
import org.lff.rpc.register.NacosServiceRegistry;
import org.lff.rpc.serializer.CommonSerializer;
import org.lff.rpc.transport.AbstractRpcServer;

public class NettyServer extends AbstractRpcServer {

    private CommonSerializer serializer;

    public NettyServer(String host, int port){
        this(host, port, SerializerCode.KRYO.getCode());
    }

    public NettyServer(String host, int port, int serializerCode) {
        this.host = host;
        this.port = port;
        serviceProvider = new ServiceProviderImpl();
        serviceRegistry = new NacosServiceRegistry();
        this.serializer = CommonSerializer.getSerializerByCode(serializerCode);
        scanServices();
    }


    @Override
    public void start() {
        // bossGroup 用于接收连接，workerGroup 用于具体处理
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建服务端启动引导类 ServerBootstrap
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 给引导类配置两大线程组
            bootstrap.group(bossGroup, workerGroup)
                    //打印日志（非必须）
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //指定 IO 模型
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            pipeline.addLast(new CommonEncoder(serializer))
                                    .addLast(new CommonDecoder())
                                    .addLast(new NettyServerHandler());
                        }
                    });

            //绑定端口，使用sync方法阻塞到绑定成功
            ChannelFuture future = bootstrap.bind(host, port).sync();

            //注册钩子函数（JVM终止时注销服务）
            ShutdownHook.getShutdownHook().addClearAllHook();

            //阻塞直到服务器关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.err.println("启动服务器时发生错误");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
