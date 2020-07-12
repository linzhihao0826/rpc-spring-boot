package top.getawaycar.rpc.spring.boot.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version V1.0
 * @author: lin_shen
 * @date: 18-12-1
 * @Description: 目前仅起标识作用
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface GetawayCarRpcClient {

}