package com.wuzl.im.common.message.boby;

import java.io.Serializable;

public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer           msgType;
    private String            fromId;
    private String            toId;
    private String            receiveDisplayName;   // 接受者的显示名称
    private Integer           userGroup;            // 用户所属组 0:普通用户 1:服务号组 2:订阅组
    private String            conversationId;
    private String            content;
    private Long              messageId;
    private Long              clientMessageId;
    private Long              timestamp;
    private Integer           status;
    private Integer           ackCode;              // 是否需要回执，0，1

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getReceiveDisplayName() {
        return receiveDisplayName;
    }

    public void setReceiveDisplayName(String receiveDisplayName) {
        this.receiveDisplayName = receiveDisplayName;
    }

    public Integer getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(Integer userGroup) {
        this.userGroup = userGroup;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(Long clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAckCode() {
        return ackCode;
    }

    public void setAckCode(Integer ackCode) {
        this.ackCode = ackCode;
    }

}
