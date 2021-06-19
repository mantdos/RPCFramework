package com.zxl.core.serialize;

import com.zxl.commons.entity.RpcRequest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

//序列化工具类F
public interface SerializeUtil {
    //序列化并发送对象
    public <T> void serializeAndSend(BufferedOutputStream bos, T obj,Class<T> clazz);

    //接收并反序列化
    public <T> Object recieveAndSerialize(BufferedInputStream bis, Class<T> clazz);
}
