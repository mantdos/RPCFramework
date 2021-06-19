package com.zxl.core.server;

import com.zxl.commons.entity.RpcServiceProperties;
import com.zxl.commons.enums.ServerConfigEnum;
import com.zxl.commons.enums.zkConfigEnum;
import com.zxl.commons.util.PropertiesFileUtil;
import com.zxl.core.register.zk.util.CuratorUtils;
import com.zxl.core.server.handler.SocketRpcRequestHandlerRunnable;
import com.zxl.core.server.impl.ServiceProviderImpl;
import com.zxl.core.server.threadpool.ThreadPoolConfig;
import javafx.animation.ScaleTransition;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class RpcServer {

    private static int port = 2181;

    static{
        //初始化收首先要先从配置文件中获取服务器端口号，如果没有则采用默认端口号
        Properties properties = PropertiesFileUtil.readPropertiesFile(ServerConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        String ServerPort = properties != null && properties.getProperty(ServerConfigEnum.SERVER_PORT.getPropertyValue()) != null ? properties.getProperty(ServerConfigEnum.SERVER_PORT.getPropertyValue()) : null;
        try{
            if(ServerPort!=null){
                int temPort = Integer.valueOf(ServerPort);
                if(temPort>=1&&temPort<=65535) port = temPort;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static int getServerPort(){
        return port;
    }
    //需要一个线程池用于执行参数
    ExecutorService threadPool = ThreadPoolConfig.newThreadPool();

    //需要一个类总负责服务的注册、提供等功能
    ServiceProvider serviceProvider = ServiceProviderImpl.newSingletonInstance();

    //组测并服务,service是待发布的实例，clazz是需要发布的接口类型,将接口名称装到rpcServiceProperties用于注册
    public void registerService(Object service, Class<?> clazz,RpcServiceProperties rpcServiceProperties){
        if(!clazz.isAssignableFrom(service.getClass())){
            log.printf("服务发布失败,待发布实例 %s未实现接口%s",service.getClass().getName(),clazz.getName());
        }
        rpcServiceProperties.setInterfaceName(clazz.getName());
        serviceProvider.publishService(service,rpcServiceProperties);
    }

    //组测并服务,service是待发布的实例，clazz是需要发布的接口类型,将接口名称装到rpcServiceProperties用于注册
    public void registerService(Object service, Class<?> clazz){
        this.registerService(service,clazz,RpcServiceProperties.builder().group("").version("").build());
    }

    public void start(){
        try(ServerSocket socket = new ServerSocket(port)) {
            //2、初始化套接字并监听端口
            Socket client;
            while((client = socket.accept())!=null){
                log.printf("客户端连接： %s\r\n", socket.getInetAddress());
                threadPool.execute(new SocketRpcRequestHandlerRunnable(client));//从线程池中抽调一个线程来执行该客户端的处理操作
            }
            threadPool.shutdown();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {//最后一定要调用一次zkClient，保证与zookeeper的连接关闭，从而清除临时节点
            CuratorFramework zkClient = CuratorUtils.getZkClient();
            zkClient.close();
        }
    }
}
