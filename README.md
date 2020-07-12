# rpc-spring-boot
用于学习Spring，Netty，Zookeeper，Proxy 和 反射机制 的一个RPC框架

请先用mvn install 安装

引入
<dependency>
    <groupId>top.getawaycar.rpc.spring.boot</groupId>
    <artifactId>rpc-spring-boot</artifactId>
    <version>1.0.0</version>
</dependency>

配置application.properties

#Zookeeper地址

zk.address=

#连接超时

zk.connection-time-out=

#session过期失效

zk.session-time-out=

#Netty 的端口

rpc.port=1999

需要在启动类添加@ComponentScan配置扫描的全限定名
basePackages = {"com.**"}


在Server上添加注解
@GetawayCarRpcServer(value=IXXXServer.class)

在Client引用的字段上添加注解
@GetawayCarRpcClient
