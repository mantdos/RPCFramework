package com.zxl.rpc.provider;

import com.zxl.commons.entity.RpcServiceProperties;
import com.zxl.core.server.RpcServerNetty;
import com.zxl.core.server.RpcServerSocket;
import com.zxl.rpc1.interfacee.Student;
import com.zxl.rpc1.interfacee.ZhangShan;

//基于netty
public class ServerNetty {
    public static void main(String[] args) {
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder().group("Test1").version("V1.0").build();
        RpcServerNetty rpcServer = new RpcServerNetty();
        Student zhangSan = new ZhangShan();
        rpcServer.registerService(zhangSan,Student.class,rpcServiceProperties);
        rpcServer.start();
    }
}
