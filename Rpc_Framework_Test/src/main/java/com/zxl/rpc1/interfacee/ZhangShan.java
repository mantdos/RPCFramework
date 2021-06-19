package com.zxl.rpc1.interfacee;

public class ZhangShan implements Student {
    public void eat() {
        System.out.println("张三在吃东西");
    }

    @Override
    public String getName() {
        System.out.println("my name is 张三");
        return "张三";
    }
}
