package org.lff.rpc.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.lff.rpc.enumeration.RpcError;
import org.lff.rpc.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NacosUtil {

    private static final String NACOS_SERVER_ADDR = "127.0.0.1:8848";

    //提供nacos操作的实例对象
    private static final NamingService namingService;

    //保存注册过的服务名称
    private static final Set<String> serviceNames;

    //服务提供者的地址
    private static  InetSocketAddress address;

    static{
        try {
            // 连接 nacos 服务器
            namingService = NamingFactory.createNamingService(NACOS_SERVER_ADDR);

            //初始化注册表
            serviceNames = new HashSet<>();

        } catch (NacosException e) {
            System.err.println("连接Nacos服务器时发生错误：" + e.getMessage());
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    // 在nacos服务器中注册服务，注册信息是： 服务名称、服务提供者的 ip、端口号
    public static void registerService(String serviceName, InetSocketAddress inetSocketAddress) throws NacosException {
        namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        address = inetSocketAddress;
        serviceNames.add(serviceName);
    }

    // 获取所有在 nacos 中注册的服务
    public static List<Instance> getAllInstances(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    /**
     * 注销所有服务
     */
    public static void clearRegistry(){
        if(!serviceNames.isEmpty() && address != null){
            String host = address.getHostName();
            int port = address.getPort();
            for(String serviceName : serviceNames){
                try {
                    namingService.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    System.err.println("注销服务：" + serviceName + " 失败：" + e.getMessage());
                }
            }
        }
    }

}
