package org.lff.rpc.provider;

public interface ServiceProvider {

    /**
     * 将一个服务添加到本地服务提供中心
     * @param service  待添加服务
     * @param <T>  待添加服务类型
     */
    <T> void addServiceProvider(T service);

    /**
     * 通过服务名称获取指定服务
     * @param serviceName  指定的服务名称
     * @return
     */
    Object getService(String serviceName);
}
