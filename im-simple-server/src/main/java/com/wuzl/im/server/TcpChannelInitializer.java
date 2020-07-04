package com.wuzl.im.server;

import com.wuzl.im.server.handler.ImMessageHandler;
import com.wuzl.im.server.handler.coder.ImMessageDecoder;
import com.wuzl.im.server.handler.coder.ImMessageEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * 类TcpChannelInitializer.java的实现描述：netty编码器初始化
 * 
 * @author ziliang.wu 2017年2月27日 上午11:08:33
 */
public class TcpChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("encoder", new ImMessageEncoder());
        pipeline.addLast("decoder", new ImMessageDecoder());
        pipeline.addLast("imHandler", new ImMessageHandler());
    }
}
