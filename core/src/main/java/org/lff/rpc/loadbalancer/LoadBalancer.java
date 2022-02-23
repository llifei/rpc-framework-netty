package org.lff.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public interface LoadBalancer {

    Instance select(List<Instance> instances);

    static LoadBalancer getLoadBalancerByCode(int loadBalancerCode){
        switch (loadBalancerCode){
            case 0:
                return new RandomLoadBalance();
            case 1:
                return new RoundRobinLoadBalancer();
            default:
                return null;
        }
    }
}
