package com.zxl.core.serialize.impl;

import com.zxl.commons.util.KryoUtil;
import com.zxl.core.serialize.Serialization;

import java.io.*;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class KryoSerialization implements Serialization {

    //Holder模式的单例创建方式
    private static class SingletonHolder{
        public static KryoSerialization kryoSerialization = new KryoSerialization();
        private SingletonHolder(){}
    }

    private KryoSerialization(){

    }

    public static KryoSerialization newSingletonInstance(){
        return SingletonHolder.kryoSerialization;
    }

    /**
     * 因为是也是针对java语言的，所以他会自动保存对象原本类型，并且不需要指定对象的类型
     */
    public void serializeAndSend(BufferedOutputStream bos, Object obj) {
        try {
            //采用這種方式會同時保存對象的類型信息，這樣在讀取時是不需要填寫類型信息的
            byte[] bytes = KryoUtil.writeToByteArray(obj);
            bos.write(bytes);
            bos.flush();
        } catch (Exception e) {
            log.printf("序列化发送失败：%s",e.getMessage());
        }
    }

    @Override
    public <T> void serializeAndSend(BufferedOutputStream bos, T obj, Class<T> clazz) {
        this.serializeAndSend(bos,obj);
    }



    /**
     * 和jdk方式一样，在反序列化的时候什么都不需要，只需要二进制文件即可
     */
    public Object recieveAndSerialize(BufferedInputStream bis) {
        try {
            byte[] bytes = new byte[1024];
            int len = -1;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while((len = bis.read(bytes))!=-1){
                byteArrayOutputStream.write(bytes,0,len);
                byteArrayOutputStream.flush();
                if(len<bytes.length) break;
            }
            Object obj = KryoUtil.readFromByteArray(byteArrayOutputStream.toByteArray());
            return obj;
        } catch (IOException e) {
            log.printf("反序列化发送失败：%s",e.getMessage());
            return null;
        }
    }

    @Override
    public <T> Object recieveAndSerialize(BufferedInputStream bis, Class<T> clazz) {
        return recieveAndSerialize(bis);
    }

    @Override
    public <T> byte[] serialize(T obj, Class<T> clazz) {
        try {
            //采用這種方式會同時保存對象的類型信息，這樣在讀取時是不需要填寫類型信息的
            return KryoUtil.writeToByteArray(obj);
        } catch (Exception e) {
            log.printf("Kryo序列化失败：%s",e.getMessage());
        }
        return null;
    }
    @Override
    public <T> Object unSerialize(byte[] bytes, Class<T> clazz) {
        try {
            return KryoUtil.readFromByteArray(bytes);
        }catch (Exception e){
            log.printf("Kryo反序列化发送失败：%s",e.getMessage());
        }
        return null;
    }

}
