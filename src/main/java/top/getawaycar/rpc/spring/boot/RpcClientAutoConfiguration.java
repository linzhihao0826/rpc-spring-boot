package top.getawaycar.rpc.spring.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.getawaycar.rpc.spring.boot.annotation.GetawayCarRpcClient;
import top.getawaycar.rpc.spring.boot.annotation.GetawayCarRpcService;
import top.getawaycar.rpc.spring.boot.communicate.NettyClient;
import top.getawaycar.rpc.spring.boot.communicate.NettyService;
import top.getawaycar.rpc.spring.boot.config.RpcProperties;
import top.getawaycar.rpc.spring.boot.config.ZkConfiguration;
import top.getawaycar.rpc.spring.boot.discover.IDiscover;
import top.getawaycar.rpc.spring.boot.discover.impl.ZkDiscoverImpl;
import top.getawaycar.rpc.spring.boot.processor.RpcClientBeanPostProcessor;
import top.getawaycar.rpc.spring.boot.registery.IRegister;
import top.getawaycar.rpc.spring.boot.registery.impl.ZKRegisterImpl;

/**
 * @version V1.0
 * @author: lin_shen
 * @date: 2018/11/2
 * @Description: TODO
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnClass({GetawayCarRpcClient.class, GetawayCarRpcService.class})
public class RpcClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public RpcProperties defaultRpcServerProperties() {
        return new RpcProperties();
    }

    @ConditionalOnMissingBean
    @Bean
    public ZkConfiguration defaultZKProperties() {
        return new ZkConfiguration();
    }

    @ConditionalOnMissingBean
    @Bean
    public IDiscover zkServiceDiscovery(ZkConfiguration zkConfiguration) {
        return new ZkDiscoverImpl(zkConfiguration);
    }

    @ConditionalOnMissingBean
    @Bean
    public IRegister zkServiceRegister(ZkConfiguration zkConfiguration) {
        return new ZKRegisterImpl(zkConfiguration);
    }

    @Bean
    public NettyClient rpcClient(IDiscover discover) {
        return new NettyClient(discover);
    }

    @Bean
    public NettyService rpcService(IRegister register, RpcProperties rpcProperties) {
        return new NettyService(register, rpcProperties);
    }

    @ConditionalOnMissingBean
    @Bean
    public RpcClientBeanPostProcessor rpcClientBeanPostProcessor(NettyClient nettyClient) {
        return new RpcClientBeanPostProcessor(nettyClient);
    }
}
