package org.lff.rpc.handler;

import org.lff.rpc.entity.RpcRequest;
import org.lff.rpc.entity.RpcResponse;
import org.lff.rpc.enumeration.ResponseCode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {

    public Object handle(RpcRequest request, Object service){
        Object result = null;
        try{
            result = invokeTargetMethod(request, service);
            System.out.println("服务：" + request.getInterfaceName() + " 成功调用方法：" + request.getMethodName());
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("调用或发送时发生错误：" + e.getMessage());
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest request, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try{
            method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD);
        }
        return method.invoke(service, request.getParamters());
    }
}
