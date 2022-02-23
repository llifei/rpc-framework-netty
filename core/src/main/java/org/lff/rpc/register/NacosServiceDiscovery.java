package org.lff.rpc.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.lff.rpc.enumeration.RpcError;
import org.lff.rpc.exception.RpcException;
import org.lff.rpc.loadbalancer.LoadBalancer;
import org.lff.rpc.util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceDiscovery implements ServiceDiscovery{

    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress findService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstances(serviceName);
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            System.err.println("从Nacos中获取服务失败：" + e.getMessage());
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
    }
}
