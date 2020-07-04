package com.wuzl.im.server.handler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wuzl.im.common.exception.ImException;
import com.wuzl.im.common.exception.ImRequestException;
import com.wuzl.im.common.message.AckMessage;
import com.wuzl.im.common.message.MessageFactory;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.server.manager.TcpClientManager;
import com.wuzl.im.server.processer.Processer;
import com.wuzl.im.server.processer.support.Processers;
import com.wuzl.im.server.util.OutMessageUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * 类ImMessageHandler.java的实现描述：连接，请求与异常处理
 * 
 * @author ziliang.wu 2017年2月27日 上午10:44:34
 */
public class ImMessageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger("PROJECT_ERROR");
    private static final Logger infoLogger = LoggerFactory.getLogger("PROJECT_INFO");
    private static final OutMessage HEARTBEAT_MESSAGE = MessageFactory.getHeartbeatMessage();
    public static final String CONNECTION_LOGIN_TIMEOUT_HANDLER = "connectionLoginTimeoutHandler";

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        Channel channel = ctx.channel();
        String ip = "";
        if (channel instanceof NioSocketChannel) {
            ip = ((NioSocketChannel) channel).remoteAddress().toString();
        }
        // TODO调试用
        logger.error("im异常", e);
        try {
            if (e instanceof ReadTimeoutException) {
                // 发送心跳
                ctx.write(HEARTBEAT_MESSAGE).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else if (e instanceof ImRequestException) {// 请求异常
                ImRequestException requestException = (ImRequestException) e;
                if (requestException.isAck()) {
                    OutMessage message = OutMessageUtil.getExceptionOut(requestException.getRequestId(),
                            requestException.getErrCode(), requestException.getErrMsg());
                    ctx.write(message);
                }

            } else if (e instanceof DecoderException) {// 请求异常
                DecoderException decoderException = (DecoderException) e;
                Throwable throwable = (Throwable) decoderException.getCause();
                if (throwable instanceof ImRequestException) {
                    ImRequestException imException = (ImRequestException) throwable;
                    OutMessage message = OutMessageUtil.getExceptionOut(imException.getRequestId(),
                            imException.getErrCode(), imException.getErrMsg());
                    ctx.write(message);
                } else if (throwable instanceof ImException) {
                    ImException imException = (ImException) throwable;
                    OutMessage message = OutMessageUtil.getExceptionOut(0, imException.getErrCode(),
                            imException.getErrMsg());
                    ctx.write(message);
                } else {
                    OutMessage message = OutMessageUtil.getExceptionOut(0, 1, "系统异常");
                    ctx.write(message);
                    logger.error("IM异常处理失败,非框架异常", e);
                }
            } else {
                try {
                    TcpClientManager.removeChannel(ctx.channel());
                } catch (Exception e1) {
                } finally {
                    try {
                        ctx.channel().close();
                    } catch (Exception e1) {
                    }
                }
            }
        } catch (Throwable t) {
            logger.error("IM异常处理失败", t);
            ctx.channel().close();
        }
        if (!(e instanceof ReadTimeoutException)) {
            if (e instanceof IOException && (ip.startsWith("/100.109.") || ip.startsWith("/100.116."))) {// 吃掉异常
                                                                                                         // 怀疑是内网ping接口的错误

            } else {
                infoLogger.info("IM出现异常，" + e.getMessage() + ",对方ip:" + ip);
            }

        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (!(obj instanceof AckMessage)) {
            super.channelRead(ctx, obj);
            return;
        }
        AckMessage msg = (AckMessage) obj;
        Processer p = Processers.getProcesser(msg.getType());
        if (p == null) {
            return;
        }
        p.process(msg, ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().addFirst(CONNECTION_LOGIN_TIMEOUT_HANDLER, new ReadTimeoutHandler(60, TimeUnit.SECONDS));
    }
}
