package com.wuzl.im.server.service;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wuzl.im.common.exception.ImException;
import com.wuzl.im.common.message.boby.SendMessageRequest;
import com.wuzl.im.common.message.boby.SendMessageResponse;
import com.wuzl.im.common.model.UserPlantformModel;
import com.wuzl.im.common.util.XssUtil;
import com.wuzl.im.server.service.param.SendMsgRequest;

import io.netty.channel.Channel;

public class TcpRequestHandlerService {

    private static final Logger errorLogger = LoggerFactory.getLogger("PROJECT_ERROR");
    private static final Logger infoLogger = LoggerFactory.getLogger("PROJECT_INFO");
    AtomicLong messageId = new AtomicLong(1);

    public SendMessageResponse sendMessage(UserPlantformModel userPlantformModel, SendMessageRequest request) {
        if (request == null) {
            throw new ImException("请求消息不可以为空");
        }
        if (request.getMsgType() == null) {
            throw new ImException("发送消息的类型不可以为空");
        }
        if (StringUtils.isEmpty(request.getToId())) {
            throw new ImException("发送消息的接收人不可以为空");
        }
        if (StringUtils.isEmpty(request.getContent())) {
            throw new ImException("发送消息的内容不可以为空");
        }
        Long clientMessageId = request.getClientMessageId();
        if (clientMessageId == null) {
            throw new ImException("发送消息的id不可以为空");
        }
        // 处理xss攻击
        String messageContent = request.getContent();
        if (request.getMsgType() == 1) {
            try {
                JSONObject content = JSON.parseObject(messageContent);
                content.put("pre_url", XssUtil.xssFilter(content.getString("pre_url")));
                messageContent = content.toJSONString().replace("/", "\\/");// 适配h5和app的
            } catch (Exception e) {
                errorLogger.error("处理图片json失败", e);
            }
        }
        SendMsgRequest sendMsgRequest = new SendMsgRequest();
        sendMsgRequest.setTag(2);
        sendMsgRequest.setMsgType(request.getMsgType());
        sendMsgRequest.setSendMemberId(userPlantformModel.getMemberId());
        sendMsgRequest.setReceiveMemberId(request.getToId());
        sendMsgRequest.setClientMessageId(clientMessageId);
        sendMsgRequest.setExtension("");
        sendMsgRequest.setContent(messageContent);
        return mockSend(sendMsgRequest);
    }

    private SendMessageResponse mockSend(SendMsgRequest sendMsgRequest) {
        // TODO 模拟im发送消息
        SendMessageResponse reponse = new SendMessageResponse();
        reponse.setClientMessageId(sendMsgRequest.getClientMessageId());
        reponse.setMessageId(messageId.decrementAndGet());
        reponse.setTimestamp(1l);
        reponse.setConversationId("1:2");
        try {
            infoLogger.info("收到消息:" + sendMsgRequest.getContent());
            Thread.sleep(200l);// TODO 模拟入库时间
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return reponse;
    }

    public void disconnection(Channel channel) {
        channel.close();
    }
}
