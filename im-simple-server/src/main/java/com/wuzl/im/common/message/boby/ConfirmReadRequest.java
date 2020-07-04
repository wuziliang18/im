package com.wuzl.im.common.message.boby;

public class ConfirmReadRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;
    private String            conversationId;

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

}
