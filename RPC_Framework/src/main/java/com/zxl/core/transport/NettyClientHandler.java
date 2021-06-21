package com.zxl.core.transport;

import com.zxl.commons.entity.RpcResponse;
import com.zxl.core.transport.netty.UnprocessedRequests;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final UnprocessedRequests unprocessedRequests = UnprocessedRequests.getNewInstance();

    //客户端业务处理类
    public class ResultHandler extends ChannelInboundHandlerAdapter {

        private RpcResponse rpcResponse = null;

        @Override //读取服务器端返回的数据并异步传输到completefuture
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            //读取反序列化后的数据
            if(msg!=null&&msg instanceof RpcResponse){
                rpcResponse = (RpcResponse)msg;
                unprocessedRequests.complete(rpcResponse);
            }
            //关闭netty
            //ctx.close();
        }
    }
}
