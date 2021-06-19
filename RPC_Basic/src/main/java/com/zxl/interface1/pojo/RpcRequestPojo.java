package com.zxl.interface1.pojo;

import java.io.Serializable;

public class RpcRequestPojo implements Serializable {
    private Integer a;
    private Integer b;
    private String method;

    public RpcRequestPojo(Integer a, Integer b,String method) {
        this.a = a;
        this.b = b;
        this.method = method;
    }

    public Integer getA() {
        return a;
    }

    public Integer getB() {
        return b;
    }

    public String getMethod() {
        return method;
    }
}
