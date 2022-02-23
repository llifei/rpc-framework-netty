package org.lff.rpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.lff.rpc.enumeration.SerializerCode;
import org.lff.rpc.exception.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements CommonSerializer{
    @Override
    public byte[] serialize(Object obj) {
        HessianOutput hessianOutput = null;
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            hessianOutput = new HessianOutput(outputStream);
            hessianOutput.writeObject(obj);
            return outputStream.toByteArray();
        }catch (IOException e){
            System.err.println("hessian序列化时发生错误：" + e.getMessage());
            throw new SerializeException("hessian序列化时发生错误");
        }finally {
            if(hessianOutput != null){
                try {
                    hessianOutput.close();
                } catch (IOException e) {
                    System.err.println("关闭输出流时错误");
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        HessianInput hessianInput = null;
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)){
            hessianInput = new HessianInput(byteArrayInputStream);
            return hessianInput.readObject();
        } catch (IOException e) {
            System.err.println("hessian反序列化时发生错误：" + e.getMessage());
            throw new SerializeException("hessian反序列化时发生错误");
        }finally {
            if(hessianInput != null){
                hessianInput.close();
            }
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.Hessian.getCode();
    }
}
