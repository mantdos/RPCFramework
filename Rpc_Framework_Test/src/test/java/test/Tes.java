package test;

import com.zxl.core.register.zk.util.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Tes {
    @Test
    public void Test() throws Exception {
        //System.out.println(zkClient.getChildren().forPath("/zxl-rpc/com.zxl.rpc.StudentTest1V1.0"));
        System.out.println(CuratorUtils.getChildrenNodes("com.zxl.rpc.StudentTest1V1.0"));
        CuratorUtils.createEphemeralNode("/tem1");
        CuratorUtils.createEphemeralNode("/cos");
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        System.out.println(zkClient.checkExists().forPath("tem2"));
        zkClient.close();
        System.out.println("成功关闭");
    }

    @Test
    public void test2() throws IOException {
        Socket socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.106.1", 1998);
        socket.connect(inetSocketAddress);
        System.out.println("成功");
    }
}
