package org.lff.rpc.register;

import com.alibaba.nacos.api.exception.NacosException;
import org.lff.rpc.enumeration.RpcError;
import org.lff.rpc.exception.RpcException;
import org.lff.rpc.util.NacosUtil;

import java.net.InetSocketAddress;

public class NacosServiceRegistry implements ServiceRegistry{


    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            System.err.println("Nacos注册服务失败：" + e.getMessage());
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
}
