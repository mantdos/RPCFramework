package com.zxl.core.transport.nettyUtil;
import com.zxl.commons.entity.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

//用于消费方异步获取返回数据
//定义一个concurrentHashMap存储每个requestId对应的CompletableFuture
public class UnprocessedRequests {
    private static final Map<String, CompletableFuture<RpcResponse>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    private static class Singleton{
        private Singleton(){}
        public static UnprocessedRequests unprocessedRequests = new UnprocessedRequests();
    }

    public static UnprocessedRequests getNewInstance(){
        return Singleton.unprocessedRequests;
    }


    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestID());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
