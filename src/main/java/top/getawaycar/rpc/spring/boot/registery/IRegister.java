package top.getawaycar.rpc.spring.boot.registery;

public interface IRegister {


    /**
     * 服务注册
     *
     * @param serviceName    全限定名
     * @param serviceAddress 地址
     * @param port           端口
     */
    void register(String serviceName, String serviceAddress, int port);


}
