package org.lff.rpc.transport.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.lff.rpc.codec.CommonDecoder;
import org.lff.rpc.codec.CommonEncoder;
import org.lff.rpc.entity.RpcRequest;
import org.lff.rpc.entity.RpcResponse;
import org.lff.rpc.enumeration.LoadBalanceCode;
import org.lff.rpc.enumeration.SerializerCode;
import org.lff.rpc.loadbalancer.LoadBalancer;
import org.lff.rpc.register.NacosServiceDiscovery;
import org.lff.rpc.register.ServiceDiscovery;
import org.lff.rpc.serializer.CommonSerializer;
import org.lff.rpc.transport.RpcClient;

import java.net.InetSocketAddress;

public class NettyClient implements RpcClient {

    // 服务发现器
    private final ServiceDiscovery serviceDiscovery;

    private static final Bootstrap bootstrap;

    private CommonSerializer serializer;


    /**
     * 无参构造，默认使用 kryo 序列化器、随机负载均衡算法
     */
    public NettyClient() {
        this(SerializerCode.KRYO.getCode(), LoadBalanceCode.RANDOM.getCode());
    }

    /**
     * 默认负载均衡算法的构造器
     * @param serializerCode
     */
    public NettyClient(int serializerCode){
        // 默认使用随机负载均衡算法
        this(serializerCode, LoadBalanceCode.RANDOM.getCode());
    }


    /**
     * 全参构造器
     * @param serializerCode 序列化器  0-Kryo   1-Jackson   2-Hessian
     * @param loadBalancerCode 负载均衡  0-随机算法  1-轮询算法
     */
    public NettyClient(int serializerCode, int loadBalancerCode){
        serviceDiscovery = new NacosServiceDiscovery(
                LoadBalancer.getLoadBalancerByCode(loadBalancerCode));
        this.serializer = CommonSerializer.getSerializerByCode(serializerCode);
    }

    static{
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                //Socket参数，连接保活。启用时TCP会主动探测空闲连接有效性，可视为TCP的心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try{

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();

                    /**
                     *  CommonDecoder 是 ChannelInboundHandler 的实现类
                     *  CommonEncoder 是 ChannelOuntboundHandler 的实现类
                     *  注意 pipeline 中 的执行顺序：
                     *      1. ChannelInboundHandler 顺序执行，ChannelOutboundHandler 逆序执行
                     *      2. ChannelOutboundHandler 的后面必须有 ChannelInboundHandler，否则不会执行
                     *
                     *  编写程序时要注意：
                     *      客户端：先 Out，后 In；
                     *      服务端：先 In，后 Out
                     *
                     *  pipeline 维护一个 双链表（保存 InboundHandler 和 OutboundHandler） 结构
                     */
                    pipeline.addLast(new CommonDecoder())
                            .addLast(new CommonEncoder(serializer))
                            .addLast(new NettyClientHandler());
                }
            });


            InetSocketAddress address = serviceDiscovery.findService(rpcRequest.getInterfaceName());
            ChannelFuture future = bootstrap.connect(address.getHostName(), address.getPort()).sync();
            System.out.println("客户端连接到服务器 " + address.getHostName() + ":" + address.getPort());
            Channel channel = future.channel();
            if(channel != null){
                //写入（发送）请求，并设置监听

                // channel.writeAndFlush 会使消息从 piprline 的末尾结点开始向前（逆序）寻找 outboundHandler 流动处理，
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess())
                        System.out.println("客户端发送消息成功：" + rpcRequest.toString());
                    else
                        System.err.println("客户端发送消息失败：" + future1.cause());
                });
                //关闭通道
                 channel.closeFuture().sync();

                //因为 rpcRequest的发送是非阻塞的，所以发送后会立刻返回，而无法得到结果
                //这里通过 AttributeKey 的方式阻塞获得返回结果。
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("RpcResponse");
                RpcResponse response = channel.attr(key).get();
                return response.getData();
            }
        } catch (InterruptedException e) {
            System.err.println("发送消息时发生错误：" + e.getMessage());
        }
        return null;
    }
}
