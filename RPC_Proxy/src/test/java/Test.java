import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class Test {
    //测试用于Zookeeper增删改查的curator框架
    @org.junit.Test
    public void test(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(2000, 3);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString("192.168.106.129:2181")
                .retryPolicy(retryPolicy)
                .build();
        try {
            zkClient.start();
            zkClient.create().withMode(CreateMode.PERSISTENT).forPath("/node1","123".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            zkClient.close();
        }
    }


    @org.junit.Test
    public void test1(){

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(2000, 3);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString("192.168.106.130:2181")
                .retryPolicy(retryPolicy)
                .build();
        try {
            zkClient.start();
            zkClient.create().withMode(CreateMode.EPHEMERAL).forPath("/nodeTem","linshi".getBytes());
            System.out.println("/node1:"+new String(zkClient.getData().forPath("/node1")));
            System.out.println("/node1:"+new String(zkClient.getData().forPath("/nodeTem")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            zkClient.close();
        }
    }
}
