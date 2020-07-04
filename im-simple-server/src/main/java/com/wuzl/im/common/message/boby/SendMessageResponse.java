package com.wuzl.im.common.message.boby;

import java.io.Serializable;

public class SendMessageResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long              messageId;            // 服务端产生的id
    private String            conversationId;       // 会话id
    private Long              clientMessageId;      // 客户端请求id

    private Long              timestamp;            // 时间戳

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
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

}
