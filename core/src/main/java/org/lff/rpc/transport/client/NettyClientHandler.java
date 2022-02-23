package org.lff.rpc.transport.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.lff.rpc.entity.RpcResponse;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try{
            System.out.println("客户端接收到消息：" + msg);

            // 以 AttributeKey 方式保存结果
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RpcResponse");
            ctx.channel().attr(key).set(msg);
            ctx.channel().close();
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("过程调用时发生错误：");
        cause.printStackTrace();
        ctx.close();
    }
}
