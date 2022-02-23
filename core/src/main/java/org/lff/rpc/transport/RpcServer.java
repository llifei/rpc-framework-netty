package org.lff.rpc.transport;

public interface RpcServer {

    void start();

    /**
     * 注册服务到远程注册中心
     * @param service   要注册的具体服务实例
     * @param serviceName 服务名称
     * @param <T>
     */
    <T> void publishService(Object service, String serviceName);
}
