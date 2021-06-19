package com.zxl.customer;


import com.zxl.interfacePojo.Calculate;

import java.io.IOException;

//服务消费方
public class CustomerTest {
    public static void main(String[] args) throws IOException {
        //根据要求为某个接口创建其代理实现类，传入接口类
        Calculate target = (Calculate) RPCProxy.newProxyInstance(Calculate.class);
        System.out.println(target.add(1,2));
    }
}
