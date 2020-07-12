package top.getawaycar.rpc.spring.boot.discover.impl;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import top.getawaycar.rpc.spring.boot.config.ZkConfiguration;
import top.getawaycar.rpc.spring.boot.discover.IDiscover;
import top.getawaycar.rpc.spring.boot.exception.NoServiceInNodeException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@EnableConfigurationProperties(ZkConfiguration.class)
public class ZkDiscoverImpl implements IDiscover {

    private final ZkConfiguration zkConfiguration;

    private ZkClient zkClient;

    private ConcurrentMap<String, IZkChildListener> zkChildListener = new ConcurrentHashMap<String, IZkChildListener>(8);
    private ConcurrentMap<String, List<String>> zkChildAddress = new ConcurrentHashMap<String, List<String>>(8);

    public ZkDiscoverImpl(ZkConfiguration zkConfiguration) {
        this.zkConfiguration = zkConfiguration;
    }


    @PostConstruct
    public void init() {
        zkClient = new ZkClient(zkConfiguration.getAddress(), zkConfiguration.getSessionTimeOut(), zkConfiguration.getConnectionTimeOut());
    }

    @Override
    public void discover(String serviceName) {
        String rootPath = zkConfiguration.getRegistryPath();
        if (rootPath.indexOf("/") != 0) {
            rootPath = "/" + rootPath;
        }
        String servicePath = rootPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            throw new NoServiceInNodeException("Zookeeper");
        }
        List<String> children = zkClient.getChildren(servicePath);
        List<String> address = new ArrayList<>();
        children.forEach(item -> {
            address.add(zkClient.readData(servicePath + "/" + item));
        });


        zkChildAddress.put(serviceName, address);

        if (!zkChildListener.containsKey(serviceName)) {
            IZkChildListener listener = new IZkChildListener() {
                @Override
                public void handleChildChange(String s, List<String> list) throws Exception {
                    discover(serviceName);
                }

            };
            //订阅子节点是否删除
            zkClient.subscribeChildChanges(servicePath, listener);
            zkChildListener.put(servicePath, listener);
        }
    }

    @Override
    public List<String> getServiceAddress(String serviceName) {
        List<String> addresses = zkChildAddress.get(serviceName);
        if (addresses == null || addresses.size() <= 0) {
            this.discover(serviceName);
        }
        return zkChildAddress.get(serviceName);
    }
}
