package com.wuzl.im.common.exception;

public class ImException extends RuntimeException {

    private static final long serialVersionUID = -2084401215148116454L;
    private final String errMsg;
    private final int errCode;
    private boolean havException = false;
    private Exception exception;

    public ImException(String errMsg, int errCode) {
        super(errMsg);
        this.errMsg = errMsg;
        this.errCode = errCode;
    }

    public ImException(String errMsg) {
        this(errMsg, 1);
    }

    public ImException(String errMsg, Exception exception) {
        this(errMsg);
        this.exception = exception;
        if (exception != null) {
            havException = true;
        }
    }

    public String getErrMsg() {
        return errMsg;
    }

    public boolean havException() {
        return havException;
    }

    public int getErrCode() {
        return errCode;
    }

    public Exception getExcetion() {
        return exception;
    }
}
