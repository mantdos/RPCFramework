package com.zxl.provider;

import com.zxl.interfacePojo.SayHello;

public class SayHelloImpl implements SayHello {
    public boolean sayHello() {
        System.out.println("hello~");
        return true;
    }
}
