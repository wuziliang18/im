package com.wuzl.im.server.processer.support;

import com.wuzl.im.common.message.AckMessage;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.server.processer.Processer;

import io.netty.channel.ChannelHandlerContext;

public class WrappedProcesser extends AbstactProcesser {

    private Processer processer;

    public WrappedProcesser(Processer processer){
        this.processer = processer;
    }

    @Override
    public OutMessage process(AckMessage msg, ChannelHandlerContext ctx) {
        return processer.process(msg, ctx);
    }

    public Processer getProcesser() {
        return processer;
    }

}
