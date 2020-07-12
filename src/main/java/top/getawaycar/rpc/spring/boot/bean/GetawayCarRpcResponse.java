package top.getawaycar.rpc.spring.boot.bean;

public class GetawayCarRpcResponse implements BodyContent {

    private Exception exception;

    private Object result;

    public GetawayCarRpcResponse() {
    }

    public GetawayCarRpcResponse(Object result) {
        this.result = result;
    }

    public GetawayCarRpcResponse(Exception exception, Object result) {
        this.exception = exception;
        this.result = result;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "GetawayCarRpcResponse{" +
                "exception=" + exception +
                ", result=" + result +
                '}';
    }
}
