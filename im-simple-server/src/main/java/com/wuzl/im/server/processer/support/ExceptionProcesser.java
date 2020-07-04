package com.wuzl.im.server.processer.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wuzl.im.common.exception.ImException;
import com.wuzl.im.common.message.AckMessage;
import com.wuzl.im.common.message.Message;
import com.wuzl.im.common.message.Message.Header;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.common.message.boby.ExceptionReponse;
import com.wuzl.im.server.manager.TcpClientManager;
import com.wuzl.im.server.processer.Processer;
import com.wuzl.im.server.util.OutMessageUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 类ExceptionProcesser.java的实现描述：异常处理
 * 
 * @author ziliang.wu 2017年2月27日 上午11:18:36
 */
public class ExceptionProcesser extends WrappedProcesser {

    private static final Logger logger = LoggerFactory.getLogger("PROJECT_ERROR");

    public ExceptionProcesser(Processer processer) {
        super(processer);
    }

    @Override
    public OutMessage process(AckMessage msg, ChannelHandlerContext ctx) {
        OutMessage message = null;
        try {
            message = super.process(msg, ctx);
        } catch (ImException e) {
            message = getExceptionOutMessage(msg, e.getErrCode(), e.getErrMsg());
        } catch (Exception e) {
            String ip = "";
            Channel channel = ctx.channel();
            if (channel instanceof NioSocketChannel) {
                ip = ((NioSocketChannel) channel).remoteAddress().toString();
            }
            String memberId = TcpClientManager.getMemberId(channel);
            if (memberId == null) {
                memberId = "未知";

            }
            logger.error("Im处理异常,对方ip:" + ip + ",对方memberId:" + memberId, e);
            message = getExceptionOutMessage(msg, 1, "服务器异常");
        }
        return message;
    }

    private OutMessage getExceptionOutMessage(AckMessage msg, int errrorCode, String errorMsg) {
        Message.Type type = msg.getType();
        Message.Type reponseType = Message.Type.valueOf(type.getVal() + 1);
        if (reponseType != null) {
            ExceptionReponse reponse = new ExceptionReponse();
            reponse.setCode(errrorCode);
            reponse.setMsg(errorMsg);
            reponse.setTimestamp(System.currentTimeMillis());
            Header header = Message.Header.getHeader(reponseType);
            header.setVersion(msg.getHeader().getVersion());
            return new OutMessage(header, msg.getRequestId(), 500, reponse);
        } else {
            return OutMessageUtil.getExceptionOut(msg, errrorCode, errorMsg);
        }
    }
}
