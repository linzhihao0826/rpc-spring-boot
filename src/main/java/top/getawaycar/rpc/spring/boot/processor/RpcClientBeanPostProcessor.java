package top.getawaycar.rpc.spring.boot.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import top.getawaycar.rpc.spring.boot.annotation.GetawayCarRpcClient;
import top.getawaycar.rpc.spring.boot.communicate.NettyClient;
import top.getawaycar.rpc.spring.boot.exception.FiledSetException;

import java.lang.reflect.Field;

@Component
public class RpcClientBeanPostProcessor implements BeanPostProcessor {


    private final NettyClient nettyClient;

    public RpcClientBeanPostProcessor(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        resolveGetawayCarRpcServiceAnnotation(bean);
        return bean;
    }

    /**
     * 将每个由Spring管理的Bean中有@GetawayCarRpcClient的列注入参数
     *
     * @param object
     */
    public void resolveGetawayCarRpcServiceAnnotation(Object object) {
        Class<?> beanClass = object.getClass();
        //获取所有属性
        while (beanClass != null) {
            Field[] declaredFields = beanClass.getDeclaredFields();

            for (Field declaredField : declaredFields) {
                //判断是否有GetawayCarRpcClient的注解
                if (declaredField.getAnnotation(GetawayCarRpcClient.class) != null) {
                    //强制访问对象属性
                    declaredField.setAccessible(true);
                    //将通讯类放入到Bean中
                    try {
                        declaredField.set(object, nettyClient.crate(declaredField.getType()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        throw new FiledSetException();
                    }
                }
            }
            //获取父类Class
            beanClass = beanClass.getSuperclass();
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
