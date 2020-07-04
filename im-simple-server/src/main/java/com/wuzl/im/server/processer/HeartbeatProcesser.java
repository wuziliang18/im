package com.wuzl.im.server.processer;

import com.wuzl.im.common.message.AckMessage;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.server.processer.support.AbstactProcesser;

import io.netty.channel.ChannelHandlerContext;

public class HeartbeatProcesser extends AbstactProcesser {

    @Override
    public OutMessage process(AckMessage msg, ChannelHandlerContext ctx) {
        return HEARTBEAT;
    }

}
