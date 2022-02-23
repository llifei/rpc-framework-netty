package org.lff.rpc.test;

import org.lff.api.HelloObject;
import org.lff.api.HelloService;
import org.lff.rpc.transport.RpcClient;
import org.lff.rpc.transport.RpcClientProxy;
import org.lff.rpc.transport.client.NettyClient;

public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(2, 0);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(261, "你好！");
        String res = helloService.hello(helloObject);
        System.out.println(res);
    }
}
