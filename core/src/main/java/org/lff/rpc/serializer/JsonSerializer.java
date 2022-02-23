package org.lff.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lff.rpc.entity.RpcRequest;
import org.lff.rpc.enumeration.SerializerCode;

import java.io.IOException;

public class JsonSerializer implements CommonSerializer{

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try{
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            System.err.println("jackson序列化时发生错误：" + e.getMessage());
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try{
            Object obj = objectMapper.readValue(bytes, clazz);
            if(obj instanceof RpcRequest)
                obj = handleRequest(obj);
            return obj;
        } catch (IOException e) {
            System.err.println("反序列化时发生错误：" + e.getMessage());
            return null;
        }
    }

    /**
     * 在反序列化时，会根据字段类型进行反序列化，但是RpcRequest有一个字段是Object数组，
     * Object是一个模糊的类型，会出现反序列化失败的现象，这时就需要RpcRequest中的另一个字段
     * ParamTypes来获取到Object数组中每个实例的实际类，来辅助反序列化
     * @param obj
     * @return
     * @throws IOException
     */
    private Object handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) obj;
        for(int i = 0; i < rpcRequest.getParamTypes().length; i++){
            Class<?> clazz = rpcRequest.getParamTypes()[i];

            // class1.isAssignableFrom(class2) 判断 class1 是否是 class2 的父类
            // 也就是检查 rpcRequest 的字段里是否存在反序列化失败的对象，如果有就单独再进行一次反序列化
            if(!clazz.isAssignableFrom(rpcRequest.getParamTypes()[i].getClass())){
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParamters()[i]);
                rpcRequest.getParamters()[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode() {
        return SerializerCode.JSON.getCode();
    }
}
