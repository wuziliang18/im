package com.wuzl.im.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wuzl.im.common.ImConstants;
import com.wuzl.im.common.message.Message;
import com.wuzl.im.common.message.Message.Header;
import com.wuzl.im.common.message.MessageFactory;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.common.message.boby.PublicKeyExchangeResponse;
import com.wuzl.im.common.security.AesUtil;
import com.wuzl.im.common.security.ECCkeyUtil;
import com.wuzl.im.common.util.Bytes;
import com.wuzl.im.common.util.ZipUtils;

public abstract class ReceiveMessage {

    private String              tcpUrl;
    String                      loginKey = "";
    private final SocketChannel socketChannel;
    private Selector            selector;
    private byte[]              secretKey;
    private PrivateKey          privateKey;
    private PublicKey           publicKey;

    public ReceiveMessage(String tcpUrl, String loginKey) throws IOException{
        this.tcpUrl = tcpUrl;
        this.loginKey = loginKey;
        this.socketChannel = SocketChannel.open();
        selector = Selector.open();
        KeyPair keyPair = ECCkeyUtil.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public void startSub() {
        try {
            String[] urlArray = tcpUrl.split(":");
            if (urlArray.length < 2) {
                throw new RuntimeException("tcpUrl地址不正确");
            }
            socketChannel.socket().connect(new InetSocketAddress(urlArray[0], Integer.parseInt(urlArray[1])));
            final ByteBuffer buffer = ByteBuffer.allocate(1024); // 创建Buffer
            final int keepAlive = 600;// 心跳
            // 交换key
            String key = ECCkeyUtil.serializePublicKey(publicKey);
            OutMessage exchangeKey = MessageFactory.getPublicKeyExchangeRequestMessage(key);
            buffer.put(encode(exchangeKey));
            buffer.flip();
            socketChannel.write(buffer);
            Thread.sleep(1000l);
            new Thread() {

                public void run() {
                    try {
                        ByteBuffer buffer = ByteBuffer.allocate(1024 * 100);
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        init();
                        long begin = System.currentTimeMillis() / 1000l;
                        while (true) {
                            int selects = selector.select(keepAlive * 1000);
                            long end = System.currentTimeMillis() / 1000l;
                            if ((end - begin) >= keepAlive) {
                                // 简单心跳
                                OutMessage heartbeat = MessageFactory.getHeartbeatMessage();
                                buffer.clear();
                                buffer.put(encode(heartbeat));
                                buffer.flip();
                                System.out.println("发送心跳");
                                socketChannel.write(buffer);
                                begin = end;
                            }
                            if (selects > 0) {
                                Set<SelectionKey> selectedKeys = selector.selectedKeys(); // 获取发生的事件
                                Iterator<SelectionKey> it = selectedKeys.iterator(); // 依次进行处理
                                while (it.hasNext()) {
                                    SelectionKey selectedKey = it.next();
                                    it.remove();
                                    if (selectedKey.isReadable()) {
                                        selectedKey.interestOps(selectedKey.interestOps() & (~SelectionKey.OP_READ));
                                        buffer.clear();
                                        int count = 0;
                                        try {
                                            while ((count = socketChannel.read(buffer)) > 0) { // 读取接收到的数据
                                                buffer.flip();
                                                while (buffer.hasRemaining()) {
                                                    byte[] dst = new byte[ImConstants.HEADER_FIXED_LENGTH];
                                                    buffer.mark();
                                                    buffer.get(dst);
                                                    // 验证magic
                                                    if (dst[0] != ImConstants.MAGIC_HIGH
                                                        || dst[1] != ImConstants.MAGIC_LOW) {
                                                        return;
                                                    }
                                                    Message.Header requestHeader = new Message.Header();
                                                    // 标志位
                                                    short flag = Bytes.bytes2short(new byte[] { dst[2], dst[3] });
                                                    short messageType = Bytes.bytes2short(new byte[] { dst[4],
                                                                                                       dst[5] });
                                                    ;
                                                    requestHeader.setEncrypt((flag & 1) > 0);
                                                    requestHeader.setCompress((flag & 2) > 0);
                                                    int length = Bytes.bytes2int(new byte[] { dst[10], dst[11], dst[12],
                                                                                              dst[13] });
                                                    if (length <= buffer.remaining()) {
                                                        byte[] bodyBytes = new byte[length];
                                                        buffer.get(bodyBytes);
                                                        // 先解密
                                                        if (requestHeader.isEncrypt()) {
                                                            if (secretKey == null) {
                                                                break;
                                                            }
                                                            bodyBytes = AesUtil.decrypt(bodyBytes, secretKey);
                                                        }
                                                        // 再解压
                                                        if (requestHeader.isCompress()) {
                                                            bodyBytes = ZipUtils.unGZip(bodyBytes);
                                                        }
                                                        String body = new String(bodyBytes, "utf-8");
//                                                        System.out.println(body);
                                                        String[] bodyArray = body.split("\n");
                                                        messageArrived(Message.Type.valueOf(messageType),
                                                                       bodyArray[bodyArray.length - 1], requestHeader);
                                                    } else {
                                                        buffer.reset();
                                                        buffer.compact();
                                                        break;
                                                    }

                                                }
                                                // buffer.clear();
                                            }
                                            if (count < 0) {
                                                try {
                                                    socketChannel.close();// 关闭同道
                                                                          // 最可行的方案
                                                                          // 解决注释上的问题
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            // 没有可用字节,继续监听OP_READ
                                            selectedKey.interestOps(SelectionKey.OP_READ);
                                            selectedKey.selector().wakeup();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e2) {
                        throw new RuntimeException("监听出错");
                    }
                };
            }.start();
        } catch (Exception e) {
            throw new RuntimeException("启动失败", e);
        }
    }

    void sendLoginRequest() {

        try {
            // 认证
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 100);
            OutMessage loginMessage = MessageFactory.getLoginMessage(4, loginKey);
            buffer.put(encode(loginMessage));
            buffer.flip();
            socketChannel.write(buffer);
            Thread.sleep(1000l);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void sendMessage(String receiveMemberId, String content) {

        try {
            // 认证
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 100);
            OutMessage loginMessage = MessageFactory.getSendMessage(receiveMemberId, 0, content, 1l);
            buffer.put(encode(loginMessage));
            buffer.flip();
            socketChannel.write(buffer);
            Thread.sleep(1000l);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    void sendImgMessage(String receiveMemberId, String content) {

        try {
            // 认证
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 100);
            OutMessage loginMessage = MessageFactory.getSendMessage(receiveMemberId, 4, content, 1l);
            buffer.put(encode(loginMessage));
            buffer.flip();
            socketChannel.write(buffer);
            Thread.sleep(1000l);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    void sendAdminMessage(String receiveMemberId, String content) {

        try {
            // 认证
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 100);
            OutMessage loginMessage = MessageFactory.getSendMessage(receiveMemberId, 70, content, 1l);
            buffer.put(encode(loginMessage));
            buffer.flip();
            socketChannel.write(buffer);
            Thread.sleep(1000l);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    void sendMessage(String receiveMemberId, String content,int type) {

        try {
            // 认证
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 100);
            OutMessage loginMessage = MessageFactory.getSendMessage(receiveMemberId, type, content, 1l);
            buffer.put(encode(loginMessage));
            buffer.flip();
            socketChannel.write(buffer);
//            Thread.sleep(1000l);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    void setKey(String json) {
        PublicKeyExchangeResponse reponse = JSONObject.parseObject(json, PublicKeyExchangeResponse.class);
        secretKey = ECCkeyUtil.generateSecret(new KeyPair(ECCkeyUtil.deserializePublicKey(reponse.getPubkey()),
                                                          privateKey));
    }

    private byte[] encode(OutMessage message) throws IOException {
        OutMessage outMessage = (OutMessage) message;
        Object obj = outMessage.getObj();
        String json = obj != null ? JSON.toJSONString(obj) : "";
        Header header = outMessage.getHeader();
        StringBuilder sb = new StringBuilder();
        sb.append(ImConstants.IM_HEADER_MAGIC).append(" ").append(header.getVersion()).append("\n");
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
        baos.write(Bytes.int2bytes(bodyBytes.length));
        baos.write(bodyBytes);
        return baos.toByteArray();
    }

    public void close() {
        try {
            selector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 子类初始化
     */
    protected void init() {

    }

    protected abstract void messageArrived(Message.Type messageType, String json, Message.Header requestHeader);
}
