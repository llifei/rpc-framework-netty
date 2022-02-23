package org.lff.rpc.transport.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.lff.rpc.entity.RpcRequest;
import org.lff.rpc.entity.RpcResponse;
import org.lff.rpc.handler.RequestHandler;
import org.lff.rpc.provider.ServiceProviderImpl;
import org.lff.rpc.provider.ServiceProvider;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static RequestHandler requestHandler;       // 请求处理器
    private static ServiceProvider serverProvider;      // 本地的服务提供中心

    static{
        requestHandler = new RequestHandler();
        serverProvider = new ServiceProviderImpl();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try{
            System.out.println("服务器接收到请求：" + msg);
            String interfaceName = msg.getInterfaceName();
            String methodName = msg.getMethodName();
            //从本地服务表中获取需要的服务
            Object service = serverProvider.getService(interfaceName);
            //调用处理器，处理器会通过反射调用服务的指定方法
            Object result = requestHandler.handle(msg, service);

            /**
             *  在通道里写入结果
             *  channel.writeAndFlush 会使消息从 piprline 的当前结点开始向前（逆序）寻找 outboundHandler 流动处理，
             */
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));

            // 写入完成时关闭通道
            future.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("处理过程调用时发生错误");
        cause.printStackTrace();
        ctx.close();
    }
}
