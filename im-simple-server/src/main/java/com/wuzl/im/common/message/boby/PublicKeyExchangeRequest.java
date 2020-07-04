package com.wuzl.im.common.message.boby;


/**
 * 类PublicKeyExchangeRequest.java的实现描述：公钥交换请求
 * 
 * @author ziliang.wu 2017年3月16日 下午2:40:28
 */
public class PublicKeyExchangeRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;
    private String            pubkey;

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

}
