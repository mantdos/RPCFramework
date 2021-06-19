package com.zxl.rpc1.provider;

import com.zxl.commons.entity.RpcServiceProperties;
import com.zxl.core.server.RpcServer;
import com.zxl.rpc1.interfacee.Student;
import com.zxl.rpc1.interfacee.ZhangShan;

public class Server {
    public static void main(String[] args) {
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder().group("Test1").version("V1.0").build();
        RpcServer rpcServer = new RpcServer();
        Student zhangSan = new ZhangShan();
        rpcServer.registerService(zhangSan,Student.class,rpcServiceProperties);
        rpcServer.start();
    }
}
