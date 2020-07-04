package com.wuzl.im.server.util;

import com.wuzl.im.common.message.AckMessage;
import com.wuzl.im.common.message.Message;
import com.wuzl.im.common.message.Message.Header;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.common.message.boby.ExceptionReponse;

/**
 * 类OutMessageUtil.java的实现描述：获取消息的工具类
 * 
 * @author ziliang.wu 2017年2月27日 上午10:41:05
 */
public class OutMessageUtil {

    public static OutMessage getExceptionOut(AckMessage msg, String exceptionMsg) {
        return getExceptionOut(msg, 1, exceptionMsg);
    }

    public static OutMessage getExceptionOut(AckMessage msg, int errorCode, String exceptionMsg) {
        ExceptionReponse reponse = new ExceptionReponse();
        reponse.setCode(errorCode);
        reponse.setMsg(exceptionMsg);
        reponse.setTimestamp(System.currentTimeMillis());
        Header header = Message.Header.getHeader(Message.Type.COMMON_RESPONSE);
        header.setVersion(msg.getHeader().getVersion());
        OutMessage outMessage = new OutMessage(header, msg.getRequestId(), reponse);
        return outMessage;
    }

    public static OutMessage getExceptionOut(int requestId, int errorCode, String exceptionMsg) {
        ExceptionReponse reponse = new ExceptionReponse();
        reponse.setCode(errorCode);
        reponse.setMsg(exceptionMsg);
        reponse.setTimestamp(System.currentTimeMillis());
        Header header = Message.Header.getHeader(Message.Type.COMMON_RESPONSE);
        OutMessage outMessage = new OutMessage(header, requestId, reponse);
        return outMessage;
    }

    public static OutMessage getExceptionOut(int requestId, int errorCode, int bodyCode, String exceptionMsg) {
        ExceptionReponse reponse = new ExceptionReponse();
        reponse.setCode(errorCode);
        reponse.setMsg(exceptionMsg);
        reponse.setTimestamp(System.currentTimeMillis());
        Header header = Message.Header.getHeader(Message.Type.COMMON_RESPONSE);
        OutMessage outMessage = new OutMessage(header, requestId, bodyCode, reponse);
        return outMessage;
    }
    
    
}
