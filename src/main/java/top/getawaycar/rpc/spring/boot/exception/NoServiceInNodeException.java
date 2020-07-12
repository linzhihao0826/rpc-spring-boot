package top.getawaycar.rpc.spring.boot.exception;

public class NoServiceInNodeException extends RuntimeException {

    public NoServiceInNodeException(String registerCenter) {
        super("注册中心" + registerCenter + ",未找到服务!");
    }
}
