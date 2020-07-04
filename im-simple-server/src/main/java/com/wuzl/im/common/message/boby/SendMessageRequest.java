package com.wuzl.im.common.message.boby;

import com.wuzl.im.common.PlantformEnum;

public class SendMessageRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;
    private Integer           msgType;              // 消息类型， 0文本 1图片 2语音
    private String            toId;                 // 接收方id
    private String            content;
    private Long              clientMessageId;      // 客户端请求id
    private Integer           ackCode;              // 是否需要回执，0，1
    private PlantformEnum     plantform;            // 来源平台

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(Long clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public Integer getAckCode() {
        return ackCode;
    }

    public void setAckCode(Integer ackCode) {
        this.ackCode = ackCode;
    }

    public PlantformEnum getPlantform() {
        return plantform;
    }

    public void setPlantform(PlantformEnum plantform) {
        this.plantform = plantform;
    }

}
