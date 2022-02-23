package org.lff.rpc.transport;

import org.lff.rpc.entity.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {

    private final RpcClient client;

    public RpcClientProxy(RpcClient client){
        this.client = client;
    }

    /**
     * 通过反射获取代理客户端类
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * 执行动态代理方法时，会执行 invoke 方法
     * @param proxy  被代理的类，即真实委托对象
     * @param method  被调用的方法
     * @param args    被调用的方法参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(), method.getName()
                , method.getParameterTypes(), args);
        return client.sendRequest(rpcRequest);
    }


}
