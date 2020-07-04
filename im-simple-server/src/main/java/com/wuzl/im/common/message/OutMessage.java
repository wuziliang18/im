package com.wuzl.im.common.message;

public class OutMessage extends AckMessage {

    private final Object obj;
    private final int    code;// 返回码 正常是200 异常是500

    public OutMessage(Header header, int requestId, Object obj){
        this(header, requestId, 200, obj);
    }

    public OutMessage(Header header, int requestId, int code, Object obj){
        super(header, requestId, null);
        this.code = code;
        this.obj = obj;
    }

    public Object getObj() {
        return obj;
    }

    public int getCode() {
        return code;
    }

}
