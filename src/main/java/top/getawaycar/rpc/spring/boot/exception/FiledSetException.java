package top.getawaycar.rpc.spring.boot.exception;

public class FiledSetException extends RuntimeException {

    public FiledSetException() {
        super("填充属性时错误！");
    }
}
