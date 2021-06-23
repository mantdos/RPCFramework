package com.zxl.core.transport.impl;

import com.zxl.commons.entity.RpcRequest;
import com.zxl.commons.entity.RpcResponse;
import com.zxl.commons.entity.RpcServiceProperties;
import com.zxl.core.register.ServiceDiscovery;
import com.zxl.core.register.zk.ZookeeperDiscovery;
import com.zxl.core.serialize.Serialization;
import com.zxl.core.serialize.impl.ProtoStuffSerialization;
import com.zxl.core.transport.RpcRequestTransport;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketRpcClient implements RpcRequestTransport {

    //利用zookeeper来实现服务发现
    final ServiceDiscovery serviceDiscovery = new ZookeeperDiscovery();

    //组合一个序列化工具类
    final Serialization serializeUtil = ProtoStuffSerialization.newSingletonInstance();

    //传输Rpc请求数据
    public RpcResponse sendRpcRequest(RpcRequest rpcRequest) {
        //1、根据rpcRequest发现服务
        RpcServiceProperties rpcServiceProperties = rpcRequest.toServerProperties();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookUpService(rpcServiceProperties);
        System.out.println(inetSocketAddress);
        //2、根据返回的地址请求Rpc参数，暂时采用JDK的序列化方式
        try(Socket socket = new Socket()){
            //连接服务器
            socket.connect(inetSocketAddress);
            //获取输入输出流
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            //序列化并发送请求信息数据
            serializeUtil.serializeAndSend(bos,rpcRequest,RpcRequest.class);
            //读取返回数据并发序列化
            Object object = serializeUtil.recieveAndSerialize(bis,RpcResponse.class);
            RpcResponse rpcResponse = null;
            if(object!=null&&object instanceof RpcResponse){
                rpcResponse = (RpcResponse)object;
            }
            return rpcResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
