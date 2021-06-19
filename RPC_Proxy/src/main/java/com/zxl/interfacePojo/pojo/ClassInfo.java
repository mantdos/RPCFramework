package com.zxl.interfacePojo.pojo;

import java.io.Serializable;

public class ClassInfo implements Serializable {
    public final long serilizebleUID = 3L;
    public String className;//类名
    public String methodName;//方法名
    public Class<?>[] types; //参数类型有那些
    public Object[] objects;//参数的值有哪些
}
