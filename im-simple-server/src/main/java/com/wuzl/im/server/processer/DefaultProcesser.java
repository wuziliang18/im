package com.wuzl.im.server.processer;

import org.apache.commons.lang3.StringUtils;

import com.wuzl.im.common.exception.ImException;
import com.wuzl.im.common.message.AckMessage;
import com.wuzl.im.common.message.Message;
import com.wuzl.im.common.message.Message.Header;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.common.message.boby.SendMessageRequest;
import com.wuzl.im.common.message.boby.SendMessageResponse;
import com.wuzl.im.common.model.UserPlantformModel;
import com.wuzl.im.server.manager.TcpClientManager;
import com.wuzl.im.server.processer.support.AbstactProcesser;
import com.wuzl.im.server.service.TcpRequestHandlerService;

import io.netty.channel.ChannelHandlerContext;

/**
 * 类DefaultProcesser.java的实现描述： 业务级别的processor，功能：消息转发
 * 
 * @author macun 2017年4月8日 下午3:45:30
 */
public class DefaultProcesser extends AbstactProcesser {

    private TcpRequestHandlerService tcpRequestHandlerService = new TcpRequestHandlerService();

    @Override
    public OutMessage process(AckMessage msg, ChannelHandlerContext ctx) {
        String memberId = TcpClientManager.getMemberId(ctx.channel());
        if (StringUtils.isEmpty(memberId)) {
            throw new ImException("没有在线用户信息");
        }
        byte[] data = msg.getData();

        switch (msg.getType()) {
        // 发送信息类型
        case SEND_MESSAGE_REQUEST:
            SendMessageRequest sendMessageRequest = SendMessageRequest.parseObject(data, SendMessageRequest.class);
            UserPlantformModel userPlantformModel = TcpClientManager.getUserInfo(ctx.channel());
            SendMessageResponse sendMessageResponse = tcpRequestHandlerService.sendMessage(userPlantformModel,
                    sendMessageRequest);
            return returnMessage(msg, sendMessageResponse, Message.Type.SEND_MESSAGE_RESPONSE, false);
        // 确认已读消息类型
        case CONFIRM_READ_RESQUEST:

            // TODO
            return null;
        // 断开链接，注销功能
        case DISCONNECTION:
            // TODO
            return null;
        default:
            throw new ImException("暂不支持的操作");
        }
    }

    private OutMessage returnMessage(AckMessage msg, Object reponse, Message.Type type, boolean compress) {
        Header header = Message.Header.getHeader(type);
        header.setCompress(compress);
        header.setVersion(msg.getHeader().getVersion());
        OutMessage outMessage = new OutMessage(header, msg.getRequestId(), reponse);
        return outMessage;
    }
}
