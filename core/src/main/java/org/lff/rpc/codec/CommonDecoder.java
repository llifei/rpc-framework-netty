package org.lff.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.lff.rpc.entity.RpcRequest;
import org.lff.rpc.entity.RpcResponse;
import org.lff.rpc.enumeration.PackageType;
import org.lff.rpc.enumeration.RpcError;
import org.lff.rpc.exception.RpcException;
import org.lff.rpc.serializer.CommonSerializer;

import java.util.List;

public class CommonDecoder extends ReplayingDecoder {

    private static final int MAGIC_NUMBER = 0xCAFABABE;

    // 编码格式：MAGIC_NUMBER + REQUEST/RESPONSE.code + serializer.code + msg_bytes.length + msg_bytes
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        int magic = in.readInt();
        if(magic != MAGIC_NUMBER){
            System.err.println("解码错误，不识别的协议包：" + magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int packageCode =  in.readInt();
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK.getCode())
            packageClass = RpcRequest.class;
        else if(packageCode == PackageType.RESPONSE_PACK.getCode())
            packageClass = RpcResponse.class;
        else{
            System.err.println("不识别的数据包：" + packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        int serizlizeCode = in.readInt();
        CommonSerializer serializer = CommonSerializer.getSerializerByCode(serizlizeCode);
        if(serializer == null){
            System.err.println("不是别的反序列化器：" + serizlizeCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        int byte_length = in.readInt();
        byte[] bytes = new byte[byte_length];
        in.readBytes(bytes);
        Object o = serializer.deserialize(bytes, packageClass);
        list.add(o);
    }
}
