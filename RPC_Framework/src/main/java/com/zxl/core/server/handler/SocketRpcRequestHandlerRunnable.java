package com.zxl.core.server.handler;

import com.zxl.commons.entity.RpcRequest;
import com.zxl.commons.entity.RpcResponse;
import com.zxl.commons.entity.RpcServiceProperties;
import com.zxl.core.serialize.Serialization;
import com.zxl.core.serialize.impl.ProtoStuffSerialization;
import com.zxl.core.server.impl.ServiceProviderImpl;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * 被RpcServer调用，用于完成对输入RpcRequest的处理
 * 包括RpcRequest的识别处理
 * 服务的本地调用
 * 返回的传输
 */

public class SocketRpcRequestHandlerRunnable implements Runnable{

    private Socket client;

    //组合一个序列化工具类
    final Serialization serializeUtil = ProtoStuffSerialization.newSingletonInstance();

    private  SocketRpcRequestHandlerRunnable(){}

    public SocketRpcRequestHandlerRunnable(Socket client){
        this.client = client;
    }

    @Override
    public void run() {
        //1、等待读取client的发送请求并验证
        RpcResponse rpcResponse = null;
        try(BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
            BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
        ) {
            //获取请求体信息并反序列化
            Object o = serializeUtil.recieveAndSerialize(bis,RpcRequest.class);
            RpcRequest request = null;
            if( null != o && RpcRequest.class.isAssignableFrom(o.getClass())){
                request = (RpcRequest) o;
            }
            request = (RpcRequest) o;
            //2、根据请求类获取服务实例
            RpcServiceProperties rpcServiceProperties = request.toServerProperties();
            Object service = ServiceProviderImpl.newSingletonInstance().getService(rpcServiceProperties);
            //3、根据服务对象和方法名、方法参数获取方法类
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterType());
            //4、反射调用方法
            Object response = method.invoke(service, request.getParameters());
            //5、封装为响应体返回
            rpcResponse = RpcResponse.success(response,request.getRequestID());
            serializeUtil.serializeAndSend(bos,rpcResponse,RpcResponse.class);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse = RpcResponse.fail(e.getMessage());
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
            serializeUtil.serializeAndSend(bos,rpcResponse,RpcResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
