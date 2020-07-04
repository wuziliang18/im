package com.wuzl.im.server.service.param;

import java.io.Serializable;
import java.util.Map;

import com.wuzl.im.server.service.enums.RequestSourceEnum;

public class SendMsgRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private String sendMemberId;
    private String receiveMemberId;
    private int tag; // 会话类型
    private String content;
    private String extension;
    private String conversationExtension; // 会话扩展信息
    private int msgType; // 0文本 1图片 2语音
    private boolean notification = true; // 是否通知
    private boolean sendMySelf = true; // 是否通知自己
    private boolean notificationMyself = true; // 是否push通知自己
    private Long clientMessageId; // 客户端请求id
    private String notificationText = "您收到了一条新的通知";// 通知
    private String pushText = null; // push标题 notificationText 作废
    private Map<String, Object> pushExtra = null; // 推送扩展字段
    private RequestSourceEnum requestSourceEnum; // 请求来源

    public String getSendMemberId() {
        return sendMemberId;
    }

    public void setSendMemberId(String sendMemberId) {
        this.sendMemberId = sendMemberId;
    }

    public String getReceiveMemberId() {
        return receiveMemberId;
    }

    public void setReceiveMemberId(String receiveMemberId) {
        this.receiveMemberId = receiveMemberId;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getConversationExtension() {
        return conversationExtension;
    }

    public void setConversationExtension(String conversationExtension) {
        this.conversationExtension = conversationExtension;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public Long getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(Long clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public boolean isSendMySelf() {
        return sendMySelf;
    }

    public void setSendMySelf(boolean sendMySelf) {
        this.sendMySelf = sendMySelf;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public boolean isNotificationMyself() {
        return notificationMyself;
    }

    public void setNotificationMyself(boolean notificationMyself) {
        this.notificationMyself = notificationMyself;
    }

    public String getPushText() {
        return pushText;
    }

    public void setPushText(String pushText) {
        this.pushText = pushText;
    }

    public Map<String, Object> getPushExtra() {
        return pushExtra;
    }

    public void setPushExtra(Map<String, Object> pushExtra) {
        this.pushExtra = pushExtra;
    }

    public RequestSourceEnum getRequestSourceEnum() {
        return requestSourceEnum;
    }

    public void setRequestSourceEnum(RequestSourceEnum requestSourceEnum) {
        this.requestSourceEnum = requestSourceEnum;
    }

}
