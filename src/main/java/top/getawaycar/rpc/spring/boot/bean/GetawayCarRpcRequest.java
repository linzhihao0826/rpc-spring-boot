package top.getawaycar.rpc.spring.boot.bean;

import java.util.Arrays;

public class GetawayCarRpcRequest implements BodyContent {

    private String clazzName;

    private String methodName;

    private Class<?>[] parametersType;

    private Object[] parameters;

    public GetawayCarRpcRequest() {
    }

    public GetawayCarRpcRequest(String clazzName) {
        this.clazzName = clazzName;
    }

    public GetawayCarRpcRequest(String clazzName, String methodName, Class<?>[] parametersType, Object[] parameters) {
        this.clazzName = clazzName;
        this.methodName = methodName;
        this.parametersType = parametersType;
        this.parameters = parameters;
    }


    public GetawayCarRpcRequest(String clazzName, Class<?>[] parametersType, Object[] parameters) {
        this.clazzName = clazzName;
        this.parametersType = parametersType;
        this.parameters = parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public Class<?>[] getParametersType() {
        return parametersType;
    }

    public void setParametersType(Class<?>[] parametersType) {
        this.parametersType = parametersType;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "GetawayCarRpcRequest{" +
                "clazzName='" + clazzName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parametersType=" + Arrays.toString(parametersType) +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
