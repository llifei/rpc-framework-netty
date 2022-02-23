package org.lff.rpc.register;

import java.net.InetSocketAddress;

public interface ServiceRegistry {

    /**
     * 注册服务到远程注册中心
     * @param serviceName
     * @param inetSocketAddress
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);

}
