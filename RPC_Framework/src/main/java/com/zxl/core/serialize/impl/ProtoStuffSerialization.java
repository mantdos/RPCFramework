package com.zxl.core.serialize.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxl.commons.util.KryoUtil;
import com.zxl.core.serialize.SerializeUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.runtime.RuntimeSchema;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class ProtoStuffSerialization implements SerializeUtil {


    //Holder模式的单例创建方式
    private static class SingletonHolder{
        public static ProtoStuffSerialization protoStuffSerialization = new ProtoStuffSerialization();
        private SingletonHolder(){}
    }

    private ProtoStuffSerialization(){

    }

    public static ProtoStuffSerialization newSingletonInstance(){
        return SingletonHolder.protoStuffSerialization;
    }



    @Override
    /**
     * 在序列化的时候需要指定pojo类的类型，反序列化亦然
     */
    public <T> void serializeAndSend(BufferedOutputStream bos, T obj, Class<T> clazz) {
        try {
            //序列化数据
            byte[] data = ProtobufIOUtil.toByteArray(obj, RuntimeSchema.createFrom(clazz),
                    LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));;
            bos.write(data);
            bos.flush();
        } catch (Exception e) {
            log.printf("序列化发送失败：%s",e.getMessage());
        }
    }

    @Override
    public <T> Object recieveAndSerialize(BufferedInputStream bis, Class<T> clazz) {
        try {
            byte[] bytes = new byte[1024];
            int len = -1;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while((len = bis.read(bytes))!=-1){
                byteArrayOutputStream.write(bytes,0,len);
                byteArrayOutputStream.flush();
                if(len<bytes.length) break;
            }
            RuntimeSchema<T> runtimeSchema = RuntimeSchema.createFrom(clazz);
            T t = runtimeSchema.newMessage();
            ProtobufIOUtil.mergeFrom(byteArrayOutputStream.toByteArray(), t, runtimeSchema);
            return t;
        } catch (IOException e) {
            log.printf("反序列化发送失败：%s",e.getMessage());
            return null;
        }
    }
}
