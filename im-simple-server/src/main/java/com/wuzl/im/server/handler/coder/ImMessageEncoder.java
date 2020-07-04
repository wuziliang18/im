package com.wuzl.im.server.handler.coder;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.wuzl.im.common.ImConstants;
import com.wuzl.im.common.message.Message;
import com.wuzl.im.common.message.Message.Header;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.common.security.AesUtil;
import com.wuzl.im.common.util.Bytes;
import com.wuzl.im.common.util.ZipUtils;
import com.wuzl.im.server.manager.TcpClientManager;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * 类ImMessageEncoder.java的实现描述：编码器
 * 
 * @author ziliang.wu 2017年3月16日 下午2:23:15
 */
@Sharable
public class ImMessageEncoder extends MessageToMessageEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        OutMessage outMessage = (OutMessage) msg;
        Object obj = outMessage.getObj();
        String json = obj != null ? JSON.toJSONString(obj) : "";
        Header header = outMessage.getHeader();
        StringBuilder sb = new StringBuilder();
        sb.append(ImConstants.IM_HEADER_MAGIC).append(header.getVersion()).append("\n");
        sb.append("code:").append(outMessage.getCode()).append("\n");
        sb.append("content-length:").append(json.length()).append("\n");
        sb.append("\n");
        sb.append(json);
        byte[] bodyBytes = sb.toString().getBytes();
        // 压缩
        if (header.isCompress()) {
            bodyBytes = ZipUtils.gZip(bodyBytes);
        }
        if (header.isEncrypt()) {
            // 加密
            byte[] secretKey = TcpClientManager.getSecretKeyByChannel(ctx.channel());
            if (secretKey != null) {
                bodyBytes = AesUtil.encrypt(bodyBytes, secretKey);
            } else {
                header.setEncrypt(false);
            }

        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bodyBytes.length + ImConstants.HEADER_FIXED_LENGTH);
        // 写入header
        baos.write(Bytes.short2bytes(ImConstants.IM_MAGIC));
        // 标志位
        baos.write(header.encode());
        // 类型
        baos.write(Bytes.short2bytes((short) header.getType().getVal()));
        // 请求id
        baos.write(Bytes.int2bytes(outMessage.getRequestId()));
        // 消息体长度
        if (outMessage.getType() == Message.Type.HEARTBEAT) {
            baos.write(Bytes.int2bytes(0));
        } else {
            baos.write(Bytes.int2bytes(bodyBytes.length));
            baos.write(bodyBytes);
        }
        out.add(Unpooled.wrappedBuffer(baos.toByteArray()));
    }
}
