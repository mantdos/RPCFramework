package com.zxl.core.serialize.impl;

import com.zxl.core.serialize.Serialization;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.runtime.RuntimeSchema;

import java.io.*;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class JdkSerialization implements Serialization {

    //Holder模式的单例创建方式
    private static class SingletonHolder{
        public static JdkSerialization jdkSerialization = new JdkSerialization();
        private SingletonHolder(){}
    }

    private JdkSerialization(){

    }

    public static JdkSerialization newSingletonInstance(){
        return SingletonHolder.jdkSerialization;
    }

    /**
     * 因为是jdk自带的，所以他会自动保存对象原本类型，并且不需要指定对象的类型，优点就是方便
     */
    public void serializeAndSend(BufferedOutputStream bos,Object obj) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            log.printf("序列化发送失败：%s",e.getMessage());
        }
    }

    @Override
    public <T> void serializeAndSend(BufferedOutputStream bos, T obj, Class<T> clazz) {
        this.serializeAndSend(bos,obj);
    }

    /**
     * 什么都不需要，只需要二进制文件即可
     */
    public Object recieveAndSerialize(BufferedInputStream bis){
        ObjectInputStream ois = null;
        Object obj = null;
        try {
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } catch (Exception e) {
            log.printf("反序列化失败：%s",e.getMessage());
        }
        return obj;
    }

    @Override
    public <T> Object recieveAndSerialize(BufferedInputStream bis, Class<T> clazz) {
        return this.recieveAndSerialize(bis);
    }

    @Override
    public <T> byte[] serialize(T obj, Class<T> clazz) {
        log.printf("JDK序列化失败,netty下不支持JDK序列化方式");
        return null;
    }
    @Override
    public <T> Object unSerialize(byte[] bytes, Class<T> clazz) {
        log.printf("JDK反序列化失败,netty下不支持JDK反序列化方式");
        return null;
    }
}
