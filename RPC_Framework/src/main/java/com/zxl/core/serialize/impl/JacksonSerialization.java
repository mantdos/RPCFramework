package com.zxl.core.serialize.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxl.core.serialize.SerializeUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class JacksonSerialization implements SerializeUtil {

    //Holder模式的单例创建方式
    private static class SingletonHolder{
        public static JacksonSerialization jacksonSerilization = new JacksonSerialization();
        private SingletonHolder(){}
    }

    private JacksonSerialization(){

    }

    public static JacksonSerialization newSingletonInstance(){
        return SingletonHolder.jacksonSerilization;
    }

    @Override
    public <T> void serializeAndSend(BufferedOutputStream bos, T obj, Class<T> clazz) {
        this.serializeAndSend(bos,obj);
    }

    /**
     * 注意两点：
     * 1、被序列化的对象一定是需要实现get方法的，因为json是通过get方法获取属性的
     * 2、在序列化的时候json是完全没有保存类型信息的，也不需要指定类型信息
     */
    public void serializeAndSend(BufferedOutputStream bos, Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            //序列化数据
            byte[] json = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(obj);
            bos.write(json);
            bos.flush();
        } catch (Exception e) {
            log.printf("序列化发送失败：%s",e.getMessage());
        }
    }


    @Override
    /**
     * 在反序列化时注意两点：
     * 1、对象一定要有一个无参构造方法，方便json类反射生成pojo对象
     * 2、因为序列化时是没有保存类型信息的，所以需要指定类型信息
     */
    public <T> Object recieveAndSerialize(BufferedInputStream bis, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            byte[] bytes = new byte[1024];
            int len = -1;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while((len = bis.read(bytes))!=-1){
                byteArrayOutputStream.write(bytes,0,len);
                byteArrayOutputStream.flush();
                if(len<bytes.length) break;
            }
            Object obj = mapper.readValue(byteArrayOutputStream.toByteArray(),clazz);
            return obj;
        } catch (IOException e) {
            log.printf("反序列化发送失败：%s",e.getMessage());
            return null;
        }
    }

}
