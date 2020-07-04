package com.wuzl.im.common.exception;

/**
 * 类ImRequestException.java的实现描述：im系统的客户单请求异常
 * 
 * @author ziliang.wu 2017年2月24日 下午5:11:54
 */
public class ImRequestException extends ImException {

    private static final long serialVersionUID = 1L;
    private final int requestId;
    private final boolean ack;

    public ImRequestException(int requestId, boolean ack, String errMsg, int errorNum) {
        super(errMsg, errorNum);
        this.requestId = requestId;
        this.ack = ack;
    }

    public boolean isAck() {
        return ack;
    }

    public int getRequestId() {
        return requestId;
    }

}
