package com.zxl.customer.Impl;

import com.zxl.interface1.Calculate;
import com.zxl.interface1.pojo.RpcRequestPojo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//Calculate的具体实现类
public class CalculateImpl implements Calculate {
    public Integer add(Integer a, Integer b) throws IOException {
        Socket socket = null;
        //建立连接套接字
        try {
            socket = new Socket("127.0.0.1",9999);
            System.out.println("封装输入信息");
            RpcRequestPojo addRpcRequest = new RpcRequestPojo(a,b,"add");
            System.out.println("开始远程调用");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(addRpcRequest);
            System.out.println("等待返回结果");
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Object object = objectInputStream.readObject();
            if(object instanceof Integer){
                System.out.println("结果返回");
                return (Integer)object;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            socket.close();
        }
        System.out.println("服务器实现类异常");
        return null;
    }
}
