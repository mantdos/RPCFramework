package com.zxl.core.transport;

import com.zxl.commons.entity.RpcRequest;
import com.zxl.commons.entity.RpcResponse;

public interface RpcRequestTransport {
    //独立负责RpcRequest的发送部分
    public RpcResponse sendRpcRequest(RpcRequest rpcRequest);
}
