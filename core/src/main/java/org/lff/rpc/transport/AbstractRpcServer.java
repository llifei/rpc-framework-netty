package org.lff.rpc.transport;

import org.lff.rpc.annotation.Service;
import org.lff.rpc.annotation.ServiceScan;
import org.lff.rpc.enumeration.RpcError;
import org.lff.rpc.exception.RpcException;
import org.lff.rpc.provider.ServiceProvider;
import org.lff.rpc.register.ServiceRegistry;
import org.lff.rpc.util.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Set;

public abstract class AbstractRpcServer implements RpcServer{

    protected  String host;                             // 待注册服务的地址
    protected  int port;                                // 待注册服务的端口号

    protected ServiceProvider serviceProvider;          // 本地的服务提供中心
    protected ServiceRegistry serviceRegistry;          // 远程的服务注册中心


    /**
     * 扫描出所有的服务并注册
     */
    public void scanServices(){
        //获取启动类名称
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try{
            //获取启动类实例
            startClass = Class.forName(mainClassName);
            //检查启动类是否标注 @ServiceScan注解
            if(!startClass.isAnnotationPresent(ServiceScan.class)){
                System.err.println("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        }catch (ClassNotFoundException e){
            System.err.println("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }

        //获取启动类的 @ServiceScan 注解中的 value 值，也就是要扫描的包名
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();

        //如果没有设置 @ServiceScan 的包值，就默认使用启动类的包名
        if("".equals(basePackage)){
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }

        //获取该包下的所有类
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);

        //遍历该包下的类集合进行注册
        for(Class<?> clazz : classSet){
            //检查是否被标注了 @Service 注解
            if(clazz.isAnnotationPresent(Service.class)){

                //获取 @Service 注解中设置的 name 属性
                String serviceName = clazz.getAnnotation(Service.class).name();

                Object obj;
                try {
                    obj = clazz.getDeclaredConstructor().newInstance();
                }catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e){
                    System.err.println("创建 " + clazz + " 时失败");
                    continue;
                }

                //如果该类的注解 @Service 中的 name 属性值没有设置，就将该类的所有接口都进行注册
                if("".equals(serviceName)){
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> oneInterface : interfaces){
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                }else{  //如果在 @Service 中指定了 name 属性，就注册指定的服务
                    publishService(obj, serviceName);
                }
            }
        }
    }


    @Override
    public <T> void publishService(Object service, String serviceName) {

        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}
