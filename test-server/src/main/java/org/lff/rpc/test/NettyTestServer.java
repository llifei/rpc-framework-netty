package org.lff.rpc.test;

import org.lff.api.HelloService;
import org.lff.rpc.annotation.ServiceScan;
import org.lff.rpc.transport.RpcServer;
import org.lff.rpc.transport.server.NettyServer;


@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        RpcServer rpcServer = new NettyServer("127.0.0.1", 8080, 2);
        System.out.println("serviceName: " + HelloService.class.getCanonicalName());
        rpcServer.start();
    }
}
