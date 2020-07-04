package com.wuzl.im.server.processer.support;

import com.wuzl.im.common.message.AckMessage;
import com.wuzl.im.common.message.Message;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.server.processer.Processer;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * 类WriteProcesser.java的实现描述： 发送信息的处理
 * 
 * @author ziliang.wu 2017年2月27日 上午11:19:45
 */
public class WriteProcesser extends WrappedProcesser {

    public WriteProcesser(Processer processer) {
        super(processer);
    }

    @Override
    public OutMessage process(AckMessage msg, ChannelHandlerContext ctx) {
        OutMessage message = super.process(msg, ctx);
        if (message == null) {
            return null;
        }
        if (message.getType() == Message.Type.DISCONNECTION) {
            ctx.write(message).addListener(ChannelFutureListener.CLOSE);
            try {
                ctx.channel().close();
            } catch (Exception e) {
            }
        } else {
            // 可以考虑几次失败后在关闭
            ctx.write(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
        ctx.flush();
        return null;
    }
}
