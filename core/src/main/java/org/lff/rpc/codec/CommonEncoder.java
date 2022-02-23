package org.lff.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.lff.rpc.entity.RpcRequest;
import org.lff.rpc.enumeration.PackageType;
import org.lff.rpc.serializer.CommonSerializer;

public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xCAFABABE;
    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    // 编码格式：MAGIC_NUMBER + REQUEST/RESPONSE.code + serializer.code + msg_bytes.length + msg_bytes
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC_NUMBER);
        if(msg instanceof RpcRequest)
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        else
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
