package top.getawaycar.rpc.spring.boot.bean;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class GetawayCarProtocol implements Serializable {

    /**
     * 交流类型
     */
    private int communicateType;

    /**
     * 内容
     */
    private BodyContent content;

    /**
     * Id 生成器
     */
    private AtomicLong idGenerator = new AtomicLong(0L);

    private Long invokeId = idGenerator.incrementAndGet();

    private Flag flag = new Flag();

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }

    public GetawayCarProtocol(int communicateType, BodyContent content) {
        this.communicateType = communicateType;
        this.content = content;
    }

    public GetawayCarProtocol() {
    }

    public GetawayCarProtocol(BodyContent content) {
        this.content = content;
    }

    public int getCommunicateType() {
        return communicateType;
    }

    public void setCommunicateType(int communicateType) {
        this.communicateType = communicateType;
    }

    public BodyContent getContent() {
        return content;
    }

    public void setContent(BodyContent content) {
        this.content = content;
    }

    public AtomicLong getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(AtomicLong idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Long getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(Long invokeId) {
        this.invokeId = invokeId;
    }

    @Override
    public String toString() {
        return "GetawayCarProtocol{" +
                "communicateType=" + communicateType +
                ", content=" + content +
                ", invokeId=" + invokeId +
                ", flag=" + flag +
                '}';
    }
}
