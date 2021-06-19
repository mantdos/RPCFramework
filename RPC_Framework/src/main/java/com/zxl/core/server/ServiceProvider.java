package com.zxl.core.server;

import com.zxl.commons.entity.RpcServiceProperties;

//服务提供类，实际负责发布服务、根据请求信息获取具体实现对象等工作
public interface ServiceProvider {

    //addService:将发布好的服务添加到本地，方便以后查找
    void addService(Object service, RpcServiceProperties rpcServiceProperties);

    //getService:根据请求信息获取具体实现对象
    public Object getService(RpcServiceProperties rpcServiceProperties);



    //publishService:发布服务
    void publishService(Object service, RpcServiceProperties rpcServiceProperties);
}
