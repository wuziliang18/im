package com.wuzl.im.common.message;

import com.wuzl.im.common.message.Message.Header;
import com.wuzl.im.common.message.boby.LoginRequest;
import com.wuzl.im.common.message.boby.PublicKeyExchangeRequest;
import com.wuzl.im.common.message.boby.SendMessageRequest;

public class MessageFactory {

    private final static OutMessage DISCONNECTION_MESSAGE = new OutMessage(Message.Header.getHeader(Message.Type.DISCONNECTION),
                                                                           0, null);
    private final static OutMessage HEARTBEAT_MESSAGE     = new OutMessage(Message.Header.getHeader(Message.Type.HEARTBEAT),
                                                                           0, null);

    public static OutMessage getDisconnnectionMessage() {
        return DISCONNECTION_MESSAGE;
    }

    public static OutMessage getHeartbeatMessage() {
        return HEARTBEAT_MESSAGE;
    }

    public static OutMessage getPublicKeyExchangeRequestMessage(String pubkey) {
        Header header = Message.Header.getHeader(Message.Type.PUBLIC_KEY_REQUEST);
        header.setEncrypt(false);
        PublicKeyExchangeRequest request = new PublicKeyExchangeRequest();
        request.setPubkey(pubkey);
        OutMessage outMessage = new OutMessage(header, AckMessage.getNewRequestId(), request);
        return outMessage;
    }

    /**
     * 获取登录的请求
     * 
     * @param platform
     * @param loginKey
     * @return
     */
    public static OutMessage getLoginMessage(int platform, String loginKey) {
        Header header = Message.Header.getHeader(Message.Type.LOGIN_REQUEST);
        LoginRequest request = new LoginRequest();
        request.setPlatform(platform);
        request.setLoginKey(loginKey);
        OutMessage outMessage = new OutMessage(header, AckMessage.getNewRequestId(), request);
        return outMessage;
    }

    public static OutMessage getSendMessage(String receiveMemberId, int type, String content, Long clientMessageId) {
        Header header = Message.Header.getHeader(Message.Type.SEND_MESSAGE_REQUEST);
        SendMessageRequest request = new SendMessageRequest();
        request.setMsgType(type);
        request.setToId(receiveMemberId);
        request.setContent(content);
        request.setClientMessageId(clientMessageId);
        OutMessage outMessage = new OutMessage(header, AckMessage.getNewRequestId(), request);
        return outMessage;
    }
}
