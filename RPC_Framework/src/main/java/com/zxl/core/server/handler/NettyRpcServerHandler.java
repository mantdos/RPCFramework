package com.zxl.core.server.handler;

import com.zxl.commons.entity.RpcRequest;
import com.zxl.commons.entity.RpcResponse;
import com.zxl.commons.entity.RpcServiceProperties;
import com.zxl.core.server.impl.ServiceProviderImpl;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    @Override  //读取客户端发来的数据并通过反射调用实现类的方法
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse rpcResponse = null;
        try{
            RpcRequest request = null;
            if( null != msg && RpcRequest.class.isAssignableFrom(msg.getClass())){
                request = (RpcRequest) msg;
            }
            request = (RpcRequest) msg;
            //2、根据请求类获取服务实例
            RpcServiceProperties rpcServiceProperties = request.toServerProperties();
            Object service = ServiceProviderImpl.newSingletonInstance().getService(rpcServiceProperties);
            //3、根据服务对象和方法名、方法参数获取方法类
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterType());
            //4、反射调用方法
            Object response = method.invoke(service, request.getParameters());
            //5、封装为响应体返回
            rpcResponse = RpcResponse.success(response,request.getRequestID());
            ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);//如果不成功则关闭通道
            return;
        }catch (Exception e) {
            e.printStackTrace();
            rpcResponse = RpcResponse.fail(e.getMessage());
        }finally {
            ReferenceCountUtil.release(msg);
        }
        try {
            ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);//如果不成功则关闭通道
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
