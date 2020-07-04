package com.wuzl.im.common.message.boby;

import java.io.Serializable;

/**
 * 类ExceptionReponse.java的实现描述：请求有问题的时候统一返回错误信息
 * 
 * @author ziliang.wu 2017年3月16日 下午3:14:11
 */
public class ExceptionReponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private int               code;
    private String            msg;
    private long              timestamp;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
