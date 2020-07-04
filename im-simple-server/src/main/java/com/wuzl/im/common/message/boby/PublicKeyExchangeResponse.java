package com.wuzl.im.common.message.boby;

import java.io.Serializable;

/**
 * 类PublicKeyExchangeRequest.java的实现描述：公钥交换返回
 * 
 * @author ziliang.wu 2017年3月16日 下午2:40:28
 */
public class PublicKeyExchangeResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private String            pubkey;

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

}
