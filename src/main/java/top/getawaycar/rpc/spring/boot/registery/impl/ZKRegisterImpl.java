package top.getawaycar.rpc.spring.boot.registery.impl;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import top.getawaycar.rpc.spring.boot.config.ZkConfiguration;
import top.getawaycar.rpc.spring.boot.registery.IRegister;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@EnableConfigurationProperties(ZkConfiguration.class)
public class ZKRegisterImpl implements IRegister {


    private final ZkConfiguration zkConfiguration;

    private ZkClient zkClient;

    public ZKRegisterImpl(ZkConfiguration zkConfiguration) {
        this.zkConfiguration = zkConfiguration;
    }

    @PostConstruct
    void init() {
        zkClient = new ZkClient(zkConfiguration.getAddress(), zkConfiguration.getSessionTimeOut(), zkConfiguration.getConnectionTimeOut());
    }


    @Override
    public void register(String serviceName, String serviceAddress, int port) {
        String rootPath = zkConfiguration.getRegistryPath();
        if (rootPath.indexOf("/") != 0) {
            rootPath = "/" + rootPath;
        }
        if (!zkClient.exists(rootPath)) {
            //持久化储存 根节点
            zkClient.createPersistent(rootPath);
        }
        String servicePath = rootPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            //持久化存储 服务节点
            zkClient.createPersistent(servicePath);
        }

        zkClient.createEphemeral(servicePath + "/service-" + System.currentTimeMillis(), serviceAddress + ":" + port);

    }

    public String convertToString(String[] array) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                str.append(",");
            }
            str.append(array[i]);
        }
        return str.toString();
    }

}
