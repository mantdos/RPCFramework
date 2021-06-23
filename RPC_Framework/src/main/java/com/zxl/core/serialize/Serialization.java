package com.zxl.core.serialize;

import com.zxl.commons.entity.RpcRequest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

//序列化工具类F
public interface Serialization {
    //序列化并发送对象
    public <T> void serializeAndSend(BufferedOutputStream bos, T obj,Class<T> clazz);


    //序列化对象
    public <T> byte[] serialize(T obj,Class<T> clazz);

    //接收并反序列化
    public <T> Object recieveAndSerialize(BufferedInputStream bis, Class<T> clazz);


    //反序列化对象
    public <T> Object unSerialize(byte[] bytes,Class<T> clazz);
}
