package com.zxl.core.server;

import com.zxl.commons.entity.RpcServiceProperties;
import com.zxl.commons.enums.ServerConfigEnum;
import com.zxl.commons.util.PropertiesFileUtil;
import com.zxl.core.register.zk.util.CuratorUtils;
import com.zxl.core.serialize.RpcMessageDecoder;
import com.zxl.core.serialize.RpcMessageEncoder;
import com.zxl.core.server.handler.NettyRpcServerHandler;
import com.zxl.core.server.handler.SocketRpcRequestHandlerRunnable;
import com.zxl.core.server.impl.ServiceProviderImpl;
import com.zxl.core.server.threadpool.ThreadPoolConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.curator.framework.CuratorFramework;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class RpcServerNetty {

    private static int port = 2181;

    static{
        //初始化收首先要先从配置文件中获取服务器端口号，如果没有则采用默认端口号
        Properties properties = PropertiesFileUtil.readPropertiesFile(ServerConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        String ServerPort = properties != null && properties.getProperty(ServerConfigEnum.SERVER_PORT.getPropertyValue()) != null ? properties.getProperty(ServerConfigEnum.SERVER_PORT.getPropertyValue()) : null;
        try{
            if(ServerPort!=null){
                int temPort = Integer.valueOf(ServerPort);
                if(temPort>=1&&temPort<=65535) port = temPort;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static int getServerPort(){
        return port;
    }


    //需要一个线程池用于执行参数
    ExecutorService threadPool = ThreadPoolConfig.newThreadPool();

    //需要一个类总负责服务的注册、提供等功能
    ServiceProvider serviceProvider = ServiceProviderImpl.newSingletonInstance();

    //组测并服务,service是待发布的实例，clazz是需要发布的接口类型,将接口名称装到rpcServiceProperties用于注册
    public void registerService(Object service, Class<?> clazz,RpcServiceProperties rpcServiceProperties){
        if(!clazz.isAssignableFrom(service.getClass())){
            log.printf("服务发布失败,待发布实例 %s未实现接口%s",service.getClass().getName(),clazz.getName());
        }
        rpcServiceProperties.setInterfaceName(clazz.getName());
        serviceProvider.publishService(service,rpcServiceProperties);
    }

    //组测并服务,service是待发布的实例，clazz是需要发布的接口类型,将接口名称装到rpcServiceProperties用于注册
    public void registerService(Object service, Class<?> clazz){
        this.registerService(service,clazz,RpcServiceProperties.builder().group("").version("").build());
    }

    public void start(){
        //建立两个线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //为了让同一个socketChannel通道的事件处理可以用一个线程池来运行，适用于那些会阻塞线程IO的处理方法
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                Runtime.getRuntime().availableProcessors() * 2//采用2倍的cpu数的线程
        );
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。这样会产生延时
            .childOption(ChannelOption.TCP_NODELAY, true)
            //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
            .option(ChannelOption.SO_BACKLOG, 128)
            //开启心跳参数后，如果2个小时连接都没有发送消息，系统会发送消息确认客户端状态，不对就关闭
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .localAddress(port).childHandler(
            new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    //编码器
                    pipeline.addLast("encoder", new RpcMessageEncoder());
                    //解码器
                    pipeline.addLast("decoder", new RpcMessageDecoder());
                    //服务器端业务处理类
                    pipeline.addLast(serviceHandlerGroup, new NettyRpcServerHandler());
                }
            });
            // 绑定端口，同步等待绑定成功
            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("......server is ready......");
            // 等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            //最后一定要调用一次zkClient，保证与zookeeper的连接关闭，从而清除临时节点
            CuratorFramework zkClient = CuratorUtils.getZkClient();
            zkClient.close();
        }
    }
}
