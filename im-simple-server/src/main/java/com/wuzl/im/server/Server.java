package com.wuzl.im.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wuzl.im.common.ImConstants;
import com.wuzl.im.common.util.NetUtils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

    // private final EventLoopGroup bossGroup = new EpollEventLoopGroup(1);
    // private final EventLoopGroup workerGroup = new EpollEventLoopGroup();
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private static final Logger logger = LoggerFactory.getLogger("PROJECT_INFO");
    private final int port;
    private final String localHost;
    private static String serverUrl; // host:port
    private Channel channle;

    public Server() {
        this(ImConstants.DEFAULT_PORT);
    }

    public Server(int port) {
        this.port = port;
        this.localHost = NetUtils.getLocalIpNetFirst();
        serverUrl = this.localHost + ":" + port;
    }

    public static String getServerUrl() {
        return serverUrl;
    }

    private ServerBootstrap getDefaultServerBootstrap() {
        ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup, workerGroup).option(ChannelOption.SO_BACKLOG, 1000)
                // 接收和发送缓冲区大小
                .option(ChannelOption.SO_SNDBUF, 32 * 1024).option(ChannelOption.SO_RCVBUF, 32 * 1024)
                .option(ChannelOption.TCP_NODELAY, true)
                // 容量动态调整的接收缓冲区分配器
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                // .channel(EpollServerSocketChannel.class)//
                .channel(NioServerSocketChannel.class)//
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        return server;
    }

    public ChannelFuture start() throws InterruptedException {
        // 此处可以考虑只绑定外网ip
        ChannelFuture future = getDefaultServerBootstrap().childHandler(new TcpChannelInitializer()).bind(port).sync();
        // ServerManager.pubServer(serverUrl);
        logger.info("Im服务启动成功,端口号：" + port + '.');
        channle = future.channel();
        return future;
    }

    public void destroy() {
        logger.info("Im服务开始关闭");
        if (channle != null) {
            channle.close();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info("Im服务成功关闭");
    }
}
