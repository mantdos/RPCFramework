package com.zxl.core.proxy;

import com.zxl.commons.entity.RpcRequest;
import com.zxl.commons.entity.RpcResponse;
import com.zxl.commons.entity.RpcServiceProperties;
import com.zxl.commons.enums.RpcResponseCodeEnum;
import com.zxl.core.transport.RpcRequestTransport;
import com.zxl.core.transport.impl.NettyRpcClient;
import com.zxl.core.transport.impl.SocketRpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

//消费方的动态代理处理类
public class RpcClientProxy implements InvocationHandler {
    //用来告诉代理类当前使用的是什么分组和什么版本
    final RpcServiceProperties rpcServiceProperties;
    final RpcRequestTransport rpcRequestTransport = new NettyRpcClient();

    public RpcClientProxy(RpcServiceProperties rpcServiceProperties) {
        this.rpcServiceProperties = rpcServiceProperties;
        if(this.rpcServiceProperties.getGroup()==null)
            this.rpcServiceProperties.setGroup("");
        if(this.rpcServiceProperties.getVersion()==null)
            this.rpcServiceProperties.setVersion("");
    }

    public RpcClientProxy() {
        this.rpcServiceProperties = RpcServiceProperties.builder().version("").group("").build();
    }

    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);//生成的代理对象在调用方法时会直接调用该类的invoke方法
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //1、通过method获得类的信息、方法信息、和输入参数类型信息
        RpcRequest rpcRequest = RpcRequest.builder()
                                .requestID(UUID.randomUUID().toString())
                                .parameters(args)
                                .interfaceName(method.getDeclaringClass().getName())
                                .methodName(method.getName())
                                .parameterType(method.getParameterTypes())
                                .group(rpcServiceProperties.getGroup())
                                .version(rpcServiceProperties.getVersion()).build();
        //2、直接通过发送类rpcRequestTransport发送rpcRequest
        RpcResponse response = rpcRequestTransport.sendRpcRequest(rpcRequest);

        //3、判断响应是否正确，正确才返回，不正确则抛出异常，那么执行的方法在来处理
        if(response!=null&&response.getCode()!=500&&response.getRequestID().equals(rpcRequest.getRequestID()))
            return response.getObject();
        if(response==null)
            throw new RuntimeException(RpcResponseCodeEnum.FAIL.getMessage());
        else
            throw new RuntimeException(response.getMessage());
    }

}
