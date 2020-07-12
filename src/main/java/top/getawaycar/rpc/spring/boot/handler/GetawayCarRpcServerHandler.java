package top.getawaycar.rpc.spring.boot.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.getawaycar.rpc.spring.boot.bean.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

public class GetawayCarRpcServerHandler extends SimpleChannelInboundHandler<GetawayCarProtocol> {

    ConcurrentMap<String, Object> concurrentMap;

    public GetawayCarRpcServerHandler(ConcurrentMap<String, Object> map) {
        concurrentMap = map;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "上线了");
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GetawayCarProtocol msg) throws Exception {
        System.out.println("收到信息" + msg.getContent().toString());
        GetawayCarRpcRequest content = (GetawayCarRpcRequest) msg.getContent();
        //通过反射调用方法
        Object handler = handler(content);

        //回写数据
        GetawayCarRpcResponse receiveData = new GetawayCarRpcResponse(handler);
        GetawayCarProtocol getawayCarProtocol = new GetawayCarProtocol();
        getawayCarProtocol.setCommunicateType(1);
        getawayCarProtocol.setInvokeId(msg.getInvokeId());
        getawayCarProtocol.setContent(receiveData);
        Flag flag = getawayCarProtocol.getFlag();
        //类型为RpcRequestResponse
        flag.setStatus(1);
        ctx.writeAndFlush(getawayCarProtocol);
    }

    private Object handler(GetawayCarRpcRequest request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //通过反射机制调用
        String clazzName = request.getClazzName();
        Object serviceBean = concurrentMap.get(clazzName);

        Class<?> serviceBeanClazz = serviceBean.getClass();
        String methodName = request.getMethodName();

        Method method = serviceBeanClazz.getMethod(methodName, request.getParametersType());

        //强制访问
        method.setAccessible(true);
        Object[] parameters = request.getParameters();
        return method.invoke(serviceBean, parameters);

    }

}
