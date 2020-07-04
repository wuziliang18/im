package com.wuzl.im.server.processer;

import java.security.KeyPair;

import com.wuzl.im.common.exception.ImException;
import com.wuzl.im.common.message.AckMessage;
import com.wuzl.im.common.message.Message;
import com.wuzl.im.common.message.Message.Header;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.common.message.boby.PublicKeyExchangeRequest;
import com.wuzl.im.common.message.boby.PublicKeyExchangeResponse;
import com.wuzl.im.common.security.ECCkeyUtil;
import com.wuzl.im.server.manager.TcpClientManager;
import com.wuzl.im.server.processer.support.AbstactProcesser;

import io.netty.channel.ChannelHandlerContext;

public class PublicKeyExchageProcesser extends AbstactProcesser {

    @Override
    public OutMessage process(AckMessage msg, ChannelHandlerContext ctx) {
        byte[] data = msg.getData();
        if (data == null || data.length == 0) {
            throw new ImException("请求的数据为空");
        }
        KeyPair keyPair = ECCkeyUtil.generateKeyPair();
        PublicKeyExchangeRequest request = PublicKeyExchangeRequest.parseObject(data, PublicKeyExchangeRequest.class);
        TcpClientManager.saveSecretKey(
                ECCkeyUtil.generateSecret(
                        new KeyPair(ECCkeyUtil.deserializePublicKey(request.getPubkey()), keyPair.getPrivate())),
                ctx.channel());
        String localPubkey = ECCkeyUtil.serializePublicKey(keyPair.getPublic());
        PublicKeyExchangeResponse reponse = new PublicKeyExchangeResponse();
        reponse.setPubkey(localPubkey);
        Header header = Message.Header.getHeader(Message.Type.PUBLIC_KEY_RESPONSE);
        header.setVersion(msg.getHeader().getVersion());
        header.setEncrypt(false);
        OutMessage outMessage = new OutMessage(header, msg.getRequestId(), reponse);
        return outMessage;
    }

}
