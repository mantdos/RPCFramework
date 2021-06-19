package com.zxl.provider;

import com.zxl.interfacePojo.pojo.ClassInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

//服务提供方，负责不断监听本端口并且提供服务
public class ProviderTest {
    public static void main(String[] args) throws IOException {
        new ProviderTest().run();
    }
    public void run() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(9999);
            System.out.println("提供放已就绪，正在监听9999端口");
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("监听到远程调用");
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    Object object = objectInputStream.readObject();//在这个过程中已经完成了jdk的序列化实现
                    if(object instanceof ClassInfo){//来了一个服务请求，开始解析
                        ClassInfo classInfo = (ClassInfo)object;
                        String interfacePath = "com.zxl.interfacePojo";//提供方接口的包文件名
                        String lastName = classInfo.className.substring(classInfo.className.lastIndexOf("."));//获得被调用服务的类名称
                        String interfaceName = interfacePath+lastName;//得到被调用服务在提供方对应的真实接口全限定名
                        ResourceBundle rb = ResourceBundle.getBundle("rpc");//用于读取properties文件
                        String className = null;
                        Object target = null;
                        try{
                            className = rb.getString(interfaceName);//获取实现类的全限定名
                            if(className!=null){//说明存在该实现类
                                target = Class.forName(className).newInstance();//加载该类
                            }
                        }finally {}
                        if(target!=null){//如果实现类不为空
                            Method method = target.getClass().getMethod(classInfo.methodName,classInfo.types);//获取该类中被调用的方法类
                            Object result = method.invoke(target,classInfo.objects);//基于方法对象、类的实例对象和输入参数执行方法
                            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());//获得输出流
                            oos.writeObject(result);
                            oos.flush();
                        }else{
                            System.out.println("远程请求解析错误！");
                        }
                    }
                }finally {
                    System.out.println("serverSocker：我本中断关闭了");
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            System.out.println("serverSocker：我本中断关闭了");
            serverSocket.close();
        }

    }
}
