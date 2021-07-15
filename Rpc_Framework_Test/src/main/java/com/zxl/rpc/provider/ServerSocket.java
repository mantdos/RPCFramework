package com.zxl.rpc.provider;

import com.zxl.commons.entity.RpcServiceProperties;
import com.zxl.core.server.RpcServerSocket;
import com.zxl.rpc.interfacee.Student;
import com.zxl.rpc.interfacee.ZhangShan;

public class ServerSocket {
    public static void main(String[] args) {
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder().group("Test1").version("V1.0").build();
        RpcServerSocket rpcServer = new RpcServerSocket();
        Student zhangSan = new ZhangShan();
        rpcServer.registerService(zhangSan,Student.class,rpcServiceProperties);
        rpcServer.start();
    }
}
