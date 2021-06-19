package com.zxl.core.register.zk;

import com.zxl.commons.entity.RpcServiceProperties;
import com.zxl.core.loadbalance.LoadBanlance;
import com.zxl.core.loadbalance.impl.RandomLoadBanlance;
import com.zxl.core.register.ServiceDiscovery;
import com.zxl.core.register.zk.util.CuratorUtils;

import java.net.InetSocketAddress;
import java.util.List;

public class ZookeeperDiscovery implements ServiceDiscovery {

    //组合一个复杂均衡模块进来,负责负载均衡
    final LoadBanlance loadBanlance = new RandomLoadBanlance();

    public InetSocketAddress lookUpService(RpcServiceProperties rpcServiceProperties) {
        //1、先获取到所有的目标地址
        List<String> targetNodes = CuratorUtils.getChildrenNodes(rpcServiceProperties.toServerName());
        if(targetNodes==null||targetNodes.size()==0){
            throw new RuntimeException("未发现相关服务！");
        }
        //2、通过复杂均衡策略选择一个地址
        String s = loadBanlance.selectServiceAddrass(targetNodes);

        //InetSocketAddress inetSocketAddress;
        //3、格式转化为地址
        int i = s.lastIndexOf(':');
        String inetAddress = s.substring(0,i);
        int port = Integer.valueOf(s.substring(i+1,s.length()));
        InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress,port);

        return inetSocketAddress;
    }
}
