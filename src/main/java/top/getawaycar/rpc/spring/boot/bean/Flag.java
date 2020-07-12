package top.getawaycar.rpc.spring.boot.bean;

public class Flag {
    /**
     * 状态
     * 0表示发送
     * 1表示接收
     */
    private int status = 0;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
