package top.getawaycar.rpc.spring.boot.communicate;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;
import top.getawaycar.rpc.spring.boot.bean.GetawayCarProtocol;
import top.getawaycar.rpc.spring.boot.bean.GetawayCarRpcRequest;
import top.getawaycar.rpc.spring.boot.bean.GetawayCarRpcResponse;
import top.getawaycar.rpc.spring.boot.codec.GetawayCarRpcDecoder;
import top.getawaycar.rpc.spring.boot.codec.GetawayCarRpcEncoder;
import top.getawaycar.rpc.spring.boot.discover.IDiscover;
import top.getawaycar.rpc.spring.boot.handler.GetawayCarRpcClientHandler;
import top.getawaycar.rpc.spring.boot.handler.GetawayCarRpcServerHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class NettyClient {


    /**
     * 存放请求编号与响应对象的映射关系
     */
    private ConcurrentMap<Long, GetawayCarProtocol> data = new ConcurrentHashMap<>();

    private final IDiscover discover;

    public NettyClient(IDiscover discover) {
        this.discover = discover;
    }


    /**
     * 创建代理类
     *
     * @param interfaceClazz
     * @param <T>
     * @return
     */
    public <T> T crate(final Class<?> interfaceClazz) {
        Object bean = Proxy.newProxyInstance(interfaceClazz.getClassLoader(), new Class<?>[]{interfaceClazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                GetawayCarRpcRequest getawayCarRpcRequest = new GetawayCarRpcRequest();
                getawayCarRpcRequest.setClazzName(method.getDeclaringClass().getName());
                getawayCarRpcRequest.setMethodName(method.getName());
                getawayCarRpcRequest.setParametersType(method.getParameterTypes());
                getawayCarRpcRequest.setParameters(args);
                //获取服务列表
                List<String> serviceAddress = discover.getServiceAddress(interfaceClazz.getName());

                System.out.println();
                //获取第一个地址
                //格式为192.168.0.1:1111
                String addresses = serviceAddress.get(0);
                String[] split = addresses.split(":");


                GetawayCarProtocol getawayCarProtocol = new GetawayCarProtocol();
                getawayCarProtocol.setCommunicateType(1);
                getawayCarProtocol.setContent(getawayCarRpcRequest);
                System.out.println("----------------------->发送数据:" + getawayCarProtocol);
                GetawayCarRpcResponse response = send(getawayCarProtocol, split[0], Integer.parseInt(split[1]));
                System.out.println("------------------------>返回结果:" + response.toString());
                return response.getResult();
            }
        });

        return (T) bean;
    }

    public GetawayCarRpcResponse send(GetawayCarProtocol getawayCarProtocol, String host, int port) throws InterruptedException {
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        ConcurrentMap<Long, GetawayCarProtocol> map = new ConcurrentHashMap<>(8);
        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new GetawayCarRpcEncoder());
                            pipeline.addLast(new GetawayCarRpcDecoder());

                            //自己处理Handler
                            pipeline.addLast(new GetawayCarRpcClientHandler(map));

                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();

            Channel channel = channelFuture.channel();

            System.out.println("---------------------------------->发送数据:" + getawayCarProtocol.toString());
            channel.writeAndFlush(getawayCarProtocol).sync();
            channel.closeFuture().sync();
            System.out.println("---------------------------------->发送完成:" + getawayCarProtocol.toString());

            //返回数据
            return (GetawayCarRpcResponse) map.get(getawayCarProtocol.getInvokeId()).getContent();

        } finally {
            loopGroup.shutdownGracefully();
        }

    }

}
