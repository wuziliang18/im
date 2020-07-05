package com.wuzl.im.client;

import java.net.InetSocketAddress;

import com.wuzl.im.common.PlantformEnum;
import com.wuzl.im.common.exception.ImException;
import com.wuzl.im.common.message.OutMessage;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private final ClientHandler handler;
    private String hostname;
    private int port;
    private PlantformEnum plantform;
    private String loginKey;
    private volatile Channel channel;

    public NettyClient(String hostname, int port, PlantformEnum plantform, String loginKey) {
        super();
        this.hostname = hostname;
        this.port = port;
        this.plantform = plantform;
        this.loginKey = loginKey;
        handler = new ClientHandler(plantform, loginKey);
    }

    public void connect() {
        Bootstrap client = getBootstrap();
        ChannelFuture f;
        try {
            f = client.connect().sync();
            Channel channel = f.channel();
            this.channel = channel;
        } catch (Exception e) {
            throw new ImException("连接失败", e);
        }

    }

    public void sendMsg(OutMessage outMessage) {
        if (!handler.hasLogin) {
            throw new ImException("没有登录或者登录失败");
        }
        this.channel.writeAndFlush(outMessage);
    }

    public void destroy() {
        channel.close();
        eventLoopGroup.shutdownGracefully();
    }

    private Bootstrap getBootstrap() {

        Bootstrap client = new Bootstrap();
        client.group(eventLoopGroup).channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(hostname, port)).handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("decoder", new ClientDecoder());
                        ch.pipeline().addLast("encoder", new ClientEncoder());
                        ch.pipeline().addLast(handler);
                    }

                });
        client.option(ChannelOption.SO_KEEPALIVE, true);// 通道选项 tcp的
        return client;
    }

}
