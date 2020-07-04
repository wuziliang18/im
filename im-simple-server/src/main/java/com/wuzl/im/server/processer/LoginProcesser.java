package com.wuzl.im.server.processer;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.wuzl.im.common.ImConstants;
import com.wuzl.im.common.PlantformEnum;
import com.wuzl.im.common.exception.ImException;
import com.wuzl.im.common.message.AckMessage;
import com.wuzl.im.common.message.Message;
import com.wuzl.im.common.message.Message.Header;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.common.message.boby.LoginRequest;
import com.wuzl.im.common.message.boby.LoginResponse;
import com.wuzl.im.common.model.UserPlantformModel;
import com.wuzl.im.common.util.Assert;
import com.wuzl.im.server.handler.ImMessageHandler;
import com.wuzl.im.server.manager.TcpClientManager;
import com.wuzl.im.server.processer.support.AbstactProcesser;
import com.wuzl.im.server.util.OutMessageUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class LoginProcesser extends AbstactProcesser {

    @Override
    public OutMessage process(AckMessage msg, ChannelHandlerContext ctx) {
        byte[] data = msg.getData();
        if (data == null || data.length == 0) {
            throw new ImException("请求的登陆数据为空");
        }
        LoginRequest request = LoginRequest.parseObject(data, LoginRequest.class);
        String loginKey = request.getLoginKey();
        Assert.notEmpty(loginKey, "登陆loginKey不可以为空");
        String memberId = getMemberIdFromLoginKey(loginKey);
        if (StringUtils.isEmpty(memberId)) {
            // throw new ImException("用户验证失败,缺少参数");
            // loginkey无效或者已经失效
            return OutMessageUtil.getExceptionOut(0, 1000401, 500, "登录信息已过期，请重新登录");
        }
        int timeout = ImConstants.TIME_OUT * 2;
        try {
            ctx.pipeline().remove(ImMessageHandler.CONNECTION_LOGIN_TIMEOUT_HANDLER);
        } catch (Exception e1) {
        }
        try {
            ctx.pipeline().addFirst("readTimeOutHandler", new ReadTimeoutHandler(timeout, TimeUnit.SECONDS));
        } catch (Exception e1) {
        }
        // 获取平台信息
        int plantform = request.getPlatform();
        PlantformEnum plantformEnum = PlantformEnum.valueOfInt(plantform);
        if (plantformEnum == null) {
            throw new ImException("平台值不正确");
        }
        String version = msg.getHeader().getVersion();
        TcpClientManager.getClientManager().registerClient(new UserPlantformModel(memberId, plantformEnum, version),
                ctx.channel());
        Header header = Message.Header.getHeader(Message.Type.LOGIN_RESPONSE);
        header.setVersion(msg.getHeader().getVersion());
        LoginResponse reponse = new LoginResponse();
        reponse.setNick("用户" + memberId);
        reponse.setAvatar("http://img05.tooopen.com/images/20150201/sl_109938035874.jpg");
        reponse.setMobile("");
        reponse.setTimestamp(System.currentTimeMillis());
        OutMessage outMessage = new OutMessage(header, msg.getRequestId(), reponse);
        return outMessage;
    }

    private String getMemberIdFromLoginKey(String loginKey) {
        // TODO验证用户
        return loginKey;
    }

}
