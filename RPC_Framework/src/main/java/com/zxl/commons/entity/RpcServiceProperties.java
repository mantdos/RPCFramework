package com.zxl.commons.entity;

import lombok.*;

@Getter
@Setter
@Builder
//使用后添加一个无参构造器
@NoArgsConstructor(access = AccessLevel.PRIVATE)
//使用后添加一个全参构造器
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcServiceProperties {
    private String group;//服务分组，用于处理对于同一个接口类有多个提供方提供的情况
    private String version;//版本控制
    private String interfaceName;//调用接口的全限定名，通过接口类的全限定名就可以锁定服务器ip了

    //生成用于服务器发现的接口名称
    public String toServerName(){
        return interfaceName+group+version;//接口名称+group+版本
    }

    //生成用于服务器发现的接口名称
    @Override
    public String toString(){
        return interfaceName+group+version;//接口名称+group+版本
    }
}
