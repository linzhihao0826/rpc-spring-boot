package top.getawaycar.rpc.spring.boot.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import top.getawaycar.rpc.spring.boot.bean.*;
import top.getawaycar.rpc.spring.boot.serialize.SerializableEngine;

import java.util.List;

public class GetawayCarRpcDecoder extends ReplayingDecoder<CodecStatus> {


    private CodecStatus codecStatus;


    public GetawayCarRpcDecoder() {
        codecStatus = CodecStatus.TYPE;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int bodyLength = 0;
        int type = -1;
        long invokeId = -1;
        int flagLength = 0;
        int cType = 0;
        SerializableEngine serializableEngine = new SerializableEngine();

        switch (state()) {
            case TYPE:
                type = in.readInt();
                if (type != 1) {
                    break;
                }
                checkpoint(CodecStatus.INVOKE_ID);
            case INVOKE_ID:
                invokeId = in.readLong();
                checkpoint(CodecStatus.FLAG_LENGTH);
            case FLAG_LENGTH:
                flagLength = in.readInt();
                checkpoint(CodecStatus.FLAG);
            case FLAG:
                byte[] flagBytes = new byte[flagLength];
                in.readBytes(flagBytes);
                Flag deserializable = serializableEngine.deserializable(flagBytes, Flag.class);
                cType = deserializable.getStatus();
            case BODY_LENGTH:
                bodyLength = in.readInt();
                checkBodyLength(bodyLength);
                checkpoint(CodecStatus.BODY_CONTENT);
            case BODY_CONTENT:
                byte[] bytes = new byte[bodyLength];
                in.readBytes(bytes);

                BodyContent content = null;
                if (cType == 0) {
                    content = serializableEngine.deserializable(bytes, GetawayCarRpcRequest.class);
                } else {
                    content = serializableEngine.deserializable(bytes, GetawayCarRpcResponse.class);
                }
                GetawayCarProtocol getawayCarProtocol = new GetawayCarProtocol();
                getawayCarProtocol.setInvokeId(invokeId);
                getawayCarProtocol.setContent(content);
                getawayCarProtocol.setCommunicateType(type);
                out.add(getawayCarProtocol);
                break;
            default:
                break;

        }
        checkpoint(CodecStatus.TYPE);

    }

    //1byte * 1024 = 1M
    private static final Integer MAX_LENGTH = 20 * 1024;

    private void checkBodyLength(int length) {
        if (length > MAX_LENGTH) {
            //抛出异常
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
