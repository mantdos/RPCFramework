package com.zxl.customer;

import com.zxl.interfacePojo.pojo.ClassInfo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.*;

//RPC的动态代理实现类，提供静态方法以实现动态对象的创建
public class RPCProxy {
    public static Object newProxyInstance(final Class target){
            return Proxy.newProxyInstance(target.getClassLoader(), new Class[]{target}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {//target动态代理类在执行方法时会调用这个方法
                //proxy：生成的代理类  method：当前执行的方法的方法对象  args：输入参数
                //我们要干的事情就是将类全限定名、方法名、输入参数、输入参数类型等封装起来就好
                ClassInfo classInfo = new ClassInfo();
                classInfo.methodName = method.getName();
                classInfo.className = target.getName();
                classInfo.objects = args;
                classInfo.types = method.getParameterTypes();

                //作为客户端连接服务端快速编程
//                Socket socket = new Socket();
//                socket.connect(new InetSocketAddress("127.0.0.1",9999));
                Socket socket = new Socket("127.0.0.1",9999);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(classInfo);
                oos.flush();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Object result = ois.readObject();
                socket.close();
                return result;
            }
        });
    }
}
