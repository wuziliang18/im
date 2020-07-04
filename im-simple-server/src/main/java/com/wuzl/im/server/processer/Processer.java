package com.wuzl.im.server.processer;

import com.wuzl.im.common.message.AckMessage;
import com.wuzl.im.common.message.OutMessage;

import io.netty.channel.ChannelHandlerContext;

/**
 * 类Processer.java的实现描述：请求的处理
 * 
 * @author ziliang.wu 2017年2月27日 上午10:23:48
 */
public interface Processer {

    /**
     * 处理请求
     * 
     * @param msg
     * @param ctx
     * @return
     */
    public OutMessage process(AckMessage msg, ChannelHandlerContext ctx);
}
