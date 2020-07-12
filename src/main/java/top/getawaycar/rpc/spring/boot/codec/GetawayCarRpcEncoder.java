package top.getawaycar.rpc.spring.boot.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import top.getawaycar.rpc.spring.boot.bean.GetawayCarProtocol;
import top.getawaycar.rpc.spring.boot.serialize.SerializableEngine;

public class GetawayCarRpcEncoder extends MessageToByteEncoder<GetawayCarProtocol> {


    @Override
    protected void encode(ChannelHandlerContext ctx, GetawayCarProtocol msg, ByteBuf out) throws Exception {
        SerializableEngine serializableEngine = new SerializableEngine();
        byte[] body = serializableEngine.translateToSerializable(msg.getContent());
        byte[] flag = serializableEngine.translateToSerializable(msg.getFlag());
        //类型
        out.writeInt(msg.getCommunicateType());
        //返回Id
        out.writeLong(msg.getInvokeId());
        //写Flag长度
        out.writeInt(flag.length);
        //写Flag
        out.writeBytes(flag);
        //内容长度
        out.writeInt(body.length);
        //内容
        out.writeBytes(body);
    }
}
