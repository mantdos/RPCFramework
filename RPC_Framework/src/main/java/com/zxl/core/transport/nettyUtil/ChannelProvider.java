package com.zxl.core.transport.nettyUtil;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class ChannelProvider {

    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    private ChannelProvider(){

    }

    private static class SingletonHolder{
        public static ChannelProvider channelProvider = new ChannelProvider();
        private SingletonHolder(){}
    }

    public static ChannelProvider newSingletonInstance(){
        return ChannelProvider.SingletonHolder.channelProvider;
    }


    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            // 当前存在该连接  直接返回
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(key);
            }
        }
        return null;
    }


    public void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
        log.printf("Channel map size :[{}]", channelMap.size());
    }
}