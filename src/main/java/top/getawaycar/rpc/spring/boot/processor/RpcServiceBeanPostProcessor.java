package top.getawaycar.rpc.spring.boot.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class RpcServiceBeanPostProcessor implements BeanPostProcessor {




    /**
     * 在初始化完成后调用 该方法
     * 该方法主要是为了将@GetawayCarRpcService下面的类注册到Zookeeper
     * 并且初始化Netty，提供给Client调用的接口
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
