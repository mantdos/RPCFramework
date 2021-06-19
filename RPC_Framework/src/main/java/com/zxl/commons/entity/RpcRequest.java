package com.zxl.commons.entity;

import lombok.*;

import java.io.Serializable;
import java.lang.reflect.Method;

@Data
@Builder
//使用后添加一个无参构造器
@NoArgsConstructor(access = AccessLevel.PRIVATE)
//使用后添加一个全参构造器
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcRequest implements Serializable {
    private String requestID;//用于确保同一次传输的正确性，发送方和返回方应该是同一个ID
    private String interfaceName;//调用接口的全限定名，通过接口类的全限定名就可以锁定服务器ip了
    private String methodName;//方法名称
    private Class<?>[] parameterType;//输入参数类型
    private Object[] parameters;//输入参数
    private String group;//服务分组，用于处理对于同一个接口类有多个提供方提供的情况
    private String version;//
    

    //生成用于服务发现的接口属性
    public RpcServiceProperties toServerProperties(){
        return RpcServiceProperties.builder().group(this.getGroup()).version(this.getVersion()).interfaceName(this.getInterfaceName()).build();
    }
}
