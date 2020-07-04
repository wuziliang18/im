package com.wuzl.im.common.message.boby;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

public abstract class AbstractRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    public static <T> T parseObject(byte[] data,Class<? extends AbstractRequest> clazz) {
        return JSONObject.parseObject(data, clazz);
    }

}
