package top.getawaycar.rpc.spring.boot.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.getawaycar.rpc.spring.boot.bean.GetawayCarProtocol;
import top.getawaycar.rpc.spring.boot.bean.GetawayCarRpcResponse;

import java.util.concurrent.ConcurrentMap;

public class GetawayCarRpcClientHandler extends SimpleChannelInboundHandler<GetawayCarProtocol> {

    ConcurrentMap<Long, GetawayCarProtocol> map;

    public GetawayCarRpcClientHandler(ConcurrentMap<Long, GetawayCarProtocol> map) {
        //将所有的请求注入进去
        this.map = map;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GetawayCarProtocol msg) throws Exception {
        //将服务端的数据回写到Map中
        System.out.println("接收到数据:" + msg.toString());
        map.put(msg.getInvokeId(), msg);

        ctx.close();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("服务出现异常：" + cause.getMessage());
        ctx.close();
    }
}
