package org.lff.rpc.register;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {

    /**
     * 根据服务名称在注册中心查找服务
     * @param serviceName
     * @return
     */
    InetSocketAddress findService(String serviceName);
}
