package com.zxl.core.server.impl;

import com.zxl.commons.entity.RpcServiceProperties;
import com.zxl.core.register.zk.util.CuratorUtils;
import com.zxl.core.server.RpcServer;
import com.zxl.core.server.ServiceProvider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class ServiceProviderImpl implements ServiceProvider {

    //保存当前已经发布了的服务
    private final Map<String,Object> PUBLISHED_SERVICE_MAP = new ConcurrentHashMap<>();

    private final Set<String> PUBLISHED_SERVICE_SET = PUBLISHED_SERVICE_MAP.keySet();


    //Holder模式的单例创建方式
    private static class SingletonHolder{
        public static ServiceProviderImpl serviceProvider = new ServiceProviderImpl();
        private SingletonHolder(){}
    }

    private ServiceProviderImpl(){

    }

    public static ServiceProviderImpl newSingletonInstance(){
        return SingletonHolder.serviceProvider;
    }

    @Override
    /**
     * 将service类添加到服务字典中
     */
    public void addService(Object service, RpcServiceProperties rpcServiceProperties) {
        String serviceName = rpcServiceProperties.toServerName();
        if(PUBLISHED_SERVICE_SET.contains(serviceName)){
            log.printf("服务重复发布：%s\r\n"+serviceName);
            return;
        }
        PUBLISHED_SERVICE_MAP.put(serviceName,service);
    }

    @Override
    /**
     *
     */
    public Object getService(RpcServiceProperties rpcServiceProperties) {
        return PUBLISHED_SERVICE_MAP.getOrDefault(rpcServiceProperties.toString(),null);
    }

    @Override
    //发布服务,rpcServiceProperties中已经添加了其接口名称，不需要利用service来获取名称
    public void publishService(Object service, RpcServiceProperties rpcServiceProperties) {
        try {
            String serverName = rpcServiceProperties.toServerName();
            CuratorUtils.createPersistentNode(serverName);//先创建到接口全限定名的永久节点
            String host = InetAddress.getLocalHost().getHostAddress();
            //基于本身地址创建临时节点
            CuratorUtils.createEphemeralNode(serverName+"/"+host+":"+ String.valueOf(RpcServer.getServerPort()));
            //将发布了的服务添加到容器中
            addService(service,rpcServiceProperties);
            log.printf("服务发布成功：%s\r\n",serverName+"/"+host+":"+ String.valueOf(RpcServer.getServerPort()));
        } catch (Exception e) {
            log.printf("服务发布失败：%s\r\n",e.getMessage());
        }
    }
}
