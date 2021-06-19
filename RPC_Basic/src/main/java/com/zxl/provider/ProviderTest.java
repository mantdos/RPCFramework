package com.zxl.provider;

import com.zxl.interface1.pojo.RpcRequestPojo;
import com.zxl.provider.Impl.CalculateImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;


public class ProviderTest {
    public static void main(String[] args) throws IOException {
        new ProviderTest().run();
    }
    public void run() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(9999);
            System.out.println("提供放已就绪，正在监听9999端口");
            Socket socket = serverSocket.accept();
            System.out.println("监听到远程调用");
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Object object = objectInputStream.readObject();
                if(object instanceof RpcRequestPojo){
                    RpcRequestPojo requestPojo = (RpcRequestPojo)object;
                    if("add".equals(requestPojo.getMethod())){
                        //计算结果
                        System.out.println("调用Calculate.add方法");
                        Integer ret = new CalculateImpl().add(requestPojo.getA(),requestPojo.getB());
                        //返回结果
                        System.out.println("结果返回");
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject(ret);
                    }
                }
            }finally {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            serverSocket.close();
        }
    }
}
