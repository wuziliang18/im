package com.wuzl.im.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wuzl.im.common.ImConstants;
import com.wuzl.im.common.exception.ImException;
import com.wuzl.im.common.exception.ImRequestException;
import com.wuzl.im.common.message.AckMessage;
import com.wuzl.im.common.message.Message;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.common.security.AesUtil;
import com.wuzl.im.common.util.Bytes;
import com.wuzl.im.common.util.ZipUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ClientDecoder extends ByteToMessageDecoder {

    private static final int    delimLength = 1;                                       // 分隔符长度
    private static final Logger logger      = LoggerFactory.getLogger("PROJECT_ERROR");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        if (buffer.readableBytes() < ImConstants.HEADER_FIXED_LENGTH) {
            // 包小于给的头大小，半包现象，直接返回。
            return;
        }
        buffer.markReaderIndex();
        byte[] header = new byte[ImConstants.HEADER_FIXED_LENGTH];
        buffer.readBytes(header);
        // 验证magic
        if (header[0] != ImConstants.MAGIC_HIGH || header[1] != ImConstants.MAGIC_LOW) {
            throw new ImException("消息体错误，magic不正确");
        }
        short flag = Bytes.bytes2short(new byte[] { header[2], header[3] });
        Message.Header requestHeader = new Message.Header();
        requestHeader.setEncrypt((flag & 1) > 0);
        requestHeader.setCompress((flag & 2) > 0);
        // 类型
        short type = Bytes.bytes2short(new byte[] { header[4], header[5] });
        Message.Type messageType = Message.Type.valueOf(type);
        if (messageType == null) {
            throw new ImException(String.format("消息的类型【%d】不存在", type));
        }
        requestHeader.setType(messageType);
        int requestId = Bytes.bytes2int(new byte[] { header[6], header[7], header[8], header[9] });
        // body length
        int length = Bytes.bytes2int(new byte[] { header[10], header[11], header[12], header[13] });
        if (messageType == Message.Type.HEARTBEAT) {
            if (length > 0) {// 丢弃
                buffer.skipBytes(length);
            }
            out.add(new OutMessage(requestHeader, requestId, null));
            return;
        }
        if (length == 0) {
            throw new ImRequestException(requestId, true, String.format("消息内容不可以为空", type), 1);
        }
        if (length > buffer.readableBytes()) {
            // 长度不够,此时也是半包，头部分已经读取，消息体不全
            buffer.resetReaderIndex();// 还原到上次mark的地方
            return;
        }
        byte[] bodyBytes = new byte[length];
        buffer.readBytes(bodyBytes);
        // 先解密
        if (requestHeader.isEncrypt()) {
            byte[] secretKey = ctx.channel().attr(ClientHandler.SECRET_INFO_KEY).get();
            if (secretKey == null) {
                logger.error("没有对应的公钥和私钥，关闭连接，对方ip：" + getRemoteIp(ctx));
                ctx.channel().close();
                return;
            }
            bodyBytes = AesUtil.decrypt(bodyBytes, secretKey);
        } else {
            if (messageType != Message.Type.PUBLIC_KEY_RESPONSE) {
                throw new ImException("消息必须加密");
            }
        }
        // 再解压
        if (requestHeader.isCompress()) {
            bodyBytes = ZipUtils.unGZip(bodyBytes);
        }
        // 处理消息体
        out.add(this.decodeBoby(requestHeader, requestId, Unpooled.wrappedBuffer(bodyBytes)));
    }

    private AckMessage decodeBoby(Message.Header requestHeader, int requestId, ByteBuf bodyBuf) {
        int eol = findEndOfLine(bodyBuf);
        if (eol == -1) {// 没有分隔符
            throw new ImException("消息体错误，没有分割符号");
        }
        int lineLength = eol - bodyBuf.readerIndex();
        if (lineLength < 5) {
            throw new ImException("消息体错误，magic不正确");
        }
        // 解析magic
        ByteBuf magicBuf = bodyBuf.readBytes(lineLength);
        String version = checkMagicAndGetVersion(magicBuf.toString(ImConstants.CHARSET));
        if (version == null) {
            throw new ImException("消息体错误，magic不正确");
        }
        requestHeader.setVersion(version);
        bodyBuf.skipBytes(delimLength);
        // 解析头
        int contentLength = -1;
        while (bodyBuf.isReadable()) {
            eol = findEndOfLine(bodyBuf);
            if (eol < 0 || (eol == bodyBuf.readerIndex())) {// 读取到消息体或者结尾
                bodyBuf.skipBytes(delimLength);
                break;
            }
            ByteBuf buf = bodyBuf.readBytes(eol - bodyBuf.readerIndex());
            String headerString = buf.toString(ImConstants.CHARSET);
            String[] headerValueArray = headerString.split(":");
            if (headerValueArray.length < 2) {
                throw new ImException("消息的格式错误");
            }
            try {
                switch (headerValueArray[0]) {
                    case "content-length":
                        contentLength = Integer.parseInt(headerValueArray[1]);
                        break;
                    default:
                        // 不做处理
                        break;
                }
            } catch (Exception e) {
                throw new ImRequestException(requestId, true, "消息的格式错误", 1);
            }
            bodyBuf.skipBytes(delimLength);
        }

        if (contentLength == -1 || bodyBuf.readableBytes() == 0) {
            throw new ImRequestException(requestId, true, "消息的内容长度不可以为空", 1);
        }
        // 消息体的协议头解析完后，剩下的是消息体json内容
        byte[] bodyBytes = new byte[bodyBuf.readableBytes()];
        bodyBuf.readBytes(bodyBytes);
        return new AckMessage(requestHeader, requestId, bodyBytes);
    }

    private String checkMagicAndGetVersion(String magic) {
        if (magic == null) {
            return null;
        }
        String[] magicArray = magic.split(" ");
        if (magicArray.length < 2) {
            return null;
        }
        if (magicArray[0].equals(ImConstants.IM_HEADER_MAGIC)) {
            return magicArray[1];
        }
        return null;
    }

    private String getRemoteIp(ChannelHandlerContext ctx) {
        String ip = "";
        Channel channel = ctx.channel();
        if (channel instanceof NioSocketChannel) {
            ip = ((NioSocketChannel) channel).remoteAddress().toString();
        }
        return ip;
    }

    private static int findEndOfLine(final ByteBuf buffer) {
        final int n = buffer.writerIndex();
        for (int i = buffer.readerIndex(); i < n; i++) {
            final byte b = buffer.getByte(i);
            if (b == ImConstants.LINESEPARATOR) {
                return i;
            }
        }
        return -1;
    }

}
