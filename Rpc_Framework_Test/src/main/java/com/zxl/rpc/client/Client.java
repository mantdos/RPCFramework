package com.zxl.rpc.client;

import com.zxl.commons.entity.RpcServiceProperties;
import com.zxl.core.proxy.RpcClientProxy;
import com.zxl.rpc1.interfacee.Student;

public class Client {
    public static void main(String[] args) {
        //定义Rpc的版本信息等等
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder().group("Test1").version("V1.0").build();
        //创建代理处理类
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcServiceProperties);
        //获得动态代理类
        Student student = rpcClientProxy.getProxy(Student.class);
        //代理执行
        student.eat();
        System.out.println(student.getName());
    }
}
