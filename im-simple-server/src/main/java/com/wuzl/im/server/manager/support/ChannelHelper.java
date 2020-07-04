package com.wuzl.im.server.manager.support;

import com.wuzl.im.common.message.AckMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ChannelHelper {

    private static ChannelFutureListener CLOSE_ON_FAILURE = new ChannelFutureListener() {

                                                              public void operationComplete(ChannelFuture future) {
                                                                  if (!future.isSuccess()) {
                                                                      future.channel().close();
                                                                  }
                                                              }
                                                          };
    private Channel                      channel;
    private final Long                   dateline;

    public ChannelHelper(Channel channel){
        this.channel = channel;
        this.dateline = System.currentTimeMillis();
    }

    public Channel getChannel() {
        return channel;
    }

    public void write(AckMessage message) {
        if (channel.isOpen()) this.channel.writeAndFlush(message).addListener(CLOSE_ON_FAILURE);
    }

    public boolean isOpen() {
        return channel.isOpen();
    }

    public void close() {
        channel.close();
    }

    @Override
    public int hashCode() {
        return getChannel().hashCode();
    }

    public Long getDateline() {
        return dateline;
    }

    @Override
    public String toString() {
        return getChannel().toString();
    }
}
