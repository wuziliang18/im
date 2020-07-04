package com.wuzl.im.common.message;

import java.util.HashMap;
import java.util.Map;

import com.wuzl.im.common.ImConstants;
import com.wuzl.im.common.util.Bytes;

/**
 * 类Message.java的实现描述：消息
 * 
 * @author ziliang.wu 2017年2月27日 上午9:54:23
 */
public abstract class Message {

    protected final Header header;

    public Message(Header header) {
        this.header = header;
    }

    public enum Type {
        PUBLIC_KEY_REQUEST(1), PUBLIC_KEY_RESPONSE(2), LOGIN_REQUEST(3), LOGIN_RESPONSE(4), HEARTBEAT(5), DISCONNECTION(
                7), SEND_MESSAGE_REQUEST(9), SEND_MESSAGE_RESPONSE(10), MESSAGE_NOTIFICATION(11), COMMON_RESPONSE(
                        13), CONFIRM_READ_RESQUEST(15), CONFIRM_READ_REPONSE(16), FRIEND_STATUS_NOTIFICATION(20);

        final private int val;
        private static Map<Integer, Type> codeLookup = new HashMap<Integer, Type>();

        Type(int val) {
            this.val = val;
        }

        static {
            for (Type t : Type.values()) {
                codeLookup.put(t.val, t);
            }
        }

        public int getVal() {
            return val;
        }

        public static Type valueOf(int i) {
            return codeLookup.get(i);
        }
    }

    public static class Header {

        private String version;
        private boolean encrypt;
        private boolean compress;
        private Type type;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public boolean isEncrypt() {
            return encrypt;
        }

        public void setEncrypt(boolean encrypt) {
            this.encrypt = encrypt;
        }

        public boolean isCompress() {
            return compress;
        }

        public void setCompress(boolean compress) {
            this.compress = compress;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public byte[] encode() {
            short b = 0;
            b |= encrypt ? 1 : 0;
            b |= compress ? 2 : 0;
            return Bytes.short2bytes(b);
        }

        public static Header getHeader(Type type) {
            Header header = new Header();
            header.setVersion(ImConstants.IM_MAGIC_NOW_VERSION);
            header.setEncrypt(true);
            header.setCompress(false);
            header.setType(type);
            return header;
        }
    }

    public Type getType() {
        return header.getType();
    }

    public Header getHeader() {
        return header;
    }

}
