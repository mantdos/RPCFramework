package com.zxl.core.register;

import com.zxl.commons.entity.RpcServiceProperties;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    //最小知道原则，只需要知道rpcServiceProperties就好了
    public InetSocketAddress lookUpService(RpcServiceProperties rpcServiceProperties);
}
