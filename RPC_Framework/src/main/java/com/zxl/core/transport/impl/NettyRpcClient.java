package com.zxl.core.transport.impl;

import com.zxl.commons.entity.RpcMessage;
import com.zxl.commons.entity.RpcRequest;
import com.zxl.commons.entity.RpcResponse;
import com.zxl.commons.entity.RpcServiceProperties;
import com.zxl.commons.enums.RpcCodecEnum;
import com.zxl.commons.enums.RpcRequestTypeEnum;
import com.zxl.core.register.ServiceDiscovery;
import com.zxl.core.register.zk.ZookeeperDiscovery;
import com.zxl.core.serialize.RpcMessageDecoder;
import com.zxl.core.serialize.RpcMessageEncoder;
import com.zxl.core.transport.NettyClientHandler;
import com.zxl.core.transport.RpcRequestTransport;
import com.zxl.core.transport.nettyUtil.ChannelProvider;
import com.zxl.core.transport.nettyUtil.UnprocessedRequests;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class NettyRpcClient implements RpcRequestTransport {

    //利用zookeeper来实现服务发现
    private final ServiceDiscovery serviceDiscovery = new ZookeeperDiscovery();

    //在netty中只需要通过枚举指定序列化方式就好了
    private final RpcCodecEnum serializeEnum = RpcCodecEnum.KRYO;

    //用于消费方异步的获取返回数据
    private final UnprocessedRequests unprocessedRequests = UnprocessedRequests.getNewInstance();

    //用于获取通道连接
    private final ChannelProvider channelProvide = ChannelProvider.newSingletonInstance();

    private Bootstrap b = null;

    private EventLoopGroup group = null;

    public NettyRpcClient(){
        //netty初始化
        group = new NioEventLoopGroup();
        try {
            b = new Bootstrap();
            b.group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //编码器
                            pipeline.addLast("encoder", new RpcMessageEncoder());
                            //解码器  构造方法第一个参数设置二进制数据的最大字节数  第二个参数设置具体使用哪个类解析器
                            pipeline.addLast("decoder", new RpcMessageDecoder());
                            //客户端业务处理类
                            pipeline.addLast("handler", new NettyClientHandler());
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
            //表明netty初始化失败了
            b= null;
        }
    }

    //传输Rpc请求数据
    public RpcResponse sendRpcRequest(RpcRequest rpcRequest) {
        //1、根据rpcRequest发现服务
        RpcServiceProperties rpcServiceProperties = rpcRequest.toServerProperties();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookUpService(rpcServiceProperties);
        System.out.println(inetSocketAddress);
        //2、根据返回的地址请求Rpc参数，采用netty框架通讯,先定义一个异步future类CompletableFuture,返回的类为RpcResponse
        CompletableFuture<RpcResponse> completableFuture = new CompletableFuture<>();
        //获取连接到指定服务器的channelsocket通道
        try {
            Channel channel = channelProvide.get(inetSocketAddress);
            if(channel==null){
                ChannelFuture future = b.connect(inetSocketAddress).sync();
                channel = future.channel();
            }
            //3、获得服务器地址后包装成RpcMessage数据并发送数据给服务器
            RpcMessage rpcMessage = RpcMessage.builder()
                    .body(rpcRequest)
                    .requsetType(RpcRequestTypeEnum.RPC_REQUEST_TYPE.getTpye())
                    .codec(serializeEnum.getCodec()).build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.print("消费方端发送RpcRequest成功\r\n");
                    //将completableFuture添加到异步进程获取的map中，然后netty会在handler中根据requestId异步注入数据
                    unprocessedRequests.put(rpcRequest.getRequestID(),completableFuture);
                } else {
                    future.channel().close();
                    //关闭异步future资源
                    completableFuture.completeExceptionally(future.cause());
                    log.print("消费方端发送RpcRequest失败\r\n");
                }
            });
            //4、最后异步获得返回数据
            return completableFuture.get();
            } catch (Exception e){
                e.printStackTrace();
                log.printf("连接服务器失败: %s\r\n",inetSocketAddress.toString());
            }finally {
                group.shutdownGracefully();//关闭线程池资源
            }

            return null;
    }
}
