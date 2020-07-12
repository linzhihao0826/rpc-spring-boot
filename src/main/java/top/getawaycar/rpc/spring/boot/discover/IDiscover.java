package top.getawaycar.rpc.spring.boot.discover;


import java.util.List;

public interface IDiscover {

    void discover(String serviceName);

    List<String> getServiceAddress(String serviceName);


}
