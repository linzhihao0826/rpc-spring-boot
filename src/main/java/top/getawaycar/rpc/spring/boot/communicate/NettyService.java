package top.getawaycar.rpc.spring.boot.communicate;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import top.getawaycar.rpc.spring.boot.annotation.GetawayCarRpcService;
import top.getawaycar.rpc.spring.boot.codec.GetawayCarRpcDecoder;
import top.getawaycar.rpc.spring.boot.codec.GetawayCarRpcEncoder;
import top.getawaycar.rpc.spring.boot.config.RpcProperties;
import top.getawaycar.rpc.spring.boot.config.ZkConfiguration;
import top.getawaycar.rpc.spring.boot.handler.GetawayCarRpcServerHandler;
import top.getawaycar.rpc.spring.boot.registery.IRegister;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@AutoConfigureAfter({ZkConfiguration.class, RpcProperties.class})
@EnableConfigurationProperties(RpcProperties.class)
public class NettyService implements ApplicationContextAware, InitializingBean {


    private final IRegister register;

    private final RpcProperties rpcProperties;

    private ConcurrentMap<String, Object> handler = new ConcurrentHashMap<>(8);

    public NettyService(IRegister register, RpcProperties rpcProperties) {
        this.register = register;
        this.rpcProperties = rpcProperties;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();//默认 CPU核数*2
        try {
            if (!CollectionUtils.isEmpty(handler)) {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new GetawayCarRpcEncoder());
                                pipeline.addLast(new GetawayCarRpcDecoder());

                                pipeline.addLast(new GetawayCarRpcServerHandler(handler));
                            }
                        });

                ChannelFuture channelFuture = serverBootstrap.bind(rpcProperties.getPort()).sync();
                register();
                channelFuture.channel().closeFuture().sync();
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void register() throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        handler.forEach((key, obj) -> {
            register.register(key, hostAddress, rpcProperties.port);
        });

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(GetawayCarRpcService.class);
        if (!CollectionUtils.isEmpty(beansWithAnnotation)) {
            for (Object obj : beansWithAnnotation.values()) {
                GetawayCarRpcService declaredAnnotation = obj.getClass().getDeclaredAnnotation(GetawayCarRpcService.class);
                System.out.println("--------------------->获取带注解的类：" + declaredAnnotation.value().getName());
                handler.put(declaredAnnotation.value().getName(), obj);
            }
        }

    }
}
