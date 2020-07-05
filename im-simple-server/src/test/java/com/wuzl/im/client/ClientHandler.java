package com.wuzl.im.client;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wuzl.im.common.PlantformEnum;
import com.wuzl.im.common.message.AckMessage;
import com.wuzl.im.common.message.MessageFactory;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.common.message.boby.LoginResponse;
import com.wuzl.im.common.message.boby.PublicKeyExchangeResponse;
import com.wuzl.im.common.message.boby.SendMessageResponse;
import com.wuzl.im.common.security.ECCkeyUtil;
import com.wuzl.im.common.threadpool.ImThreadPool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final PlantformEnum plantform;
    private final String loginKey;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    public volatile boolean hasLogin = false;
    public static final AttributeKey<byte[]> SECRET_INFO_KEY = AttributeKey.valueOf("secretKey");
    public static final ThreadPoolExecutor threadPool = ImThreadPool.newExecutor("handler", 100);
    private AtomicInteger count = new AtomicInteger(5000);

    public ClientHandler(PlantformEnum plantform, String loginKey) {
        this.plantform = plantform;
        this.loginKey = loginKey;
        KeyPair keyPair = ECCkeyUtil.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        logger.error("IM异常处理失败", e);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (!(obj instanceof AckMessage)) {
            super.channelRead(ctx, obj);
            return;
        }
        AckMessage msg = (AckMessage) obj;
        threadPool.submit(new Runnable() {

            @Override
            public void run() {
                // 具体的处理
                switch (msg.getType()) {
                case HEARTBEAT:
                    ctx.write(MessageFactory.getHeartbeatMessage());
                    ctx.flush();
                    return;
                case PUBLIC_KEY_RESPONSE:
                    PublicKeyExchangeResponse reponse = JSONObject.parseObject(msg.getData(),
                            PublicKeyExchangeResponse.class);
                    byte[] secretKey = ECCkeyUtil.generateSecret(
                            new KeyPair(ECCkeyUtil.deserializePublicKey(reponse.getPubkey()), privateKey));
                    ctx.channel().attr(SECRET_INFO_KEY).set(secretKey);
                    // 登录
                    OutMessage loginMessage = MessageFactory.getLoginMessage(plantform.getValue(), loginKey);
                    ctx.write(loginMessage);
                    ctx.flush();
                    return;
                case LOGIN_RESPONSE:
                    LoginResponse loginResponse = JSONObject.parseObject(msg.getData(), LoginResponse.class);
                    if (loginResponse.getNick() != null) {
                        hasLogin = true;
                        logger.info("登录成功，返回信息:" + JSON.toJSONString(loginResponse));
                    } else {
                        logger.error("登录失败");
                    }

                    return;
                case COMMON_RESPONSE:
                    logger.error("出现错误:" + JSONObject.parseObject(msg.getData(), Map.class));
                    return;
                case MESSAGE_NOTIFICATION:
                    logger.info("收到信息:" + JSONObject.parse(new String(msg.getData())));
                    return;
                case SEND_MESSAGE_RESPONSE:
                    SendMessageResponse sendMessageResponse = JSONObject.parseObject(msg.getData(),
                            SendMessageResponse.class);

                    logger.info("收到消息时间:" + System.currentTimeMillis());
                    return;
                default:
                    logger.info("收到:" + JSONObject.parse(new String(msg.getData())));
                    break;

                }

            }
        });

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 交换公钥
        String key = ECCkeyUtil.serializePublicKey(publicKey);
        OutMessage exchangeKey = MessageFactory.getPublicKeyExchangeRequestMessage(key);
        ctx.write(exchangeKey);
        ctx.flush();
    }
}
