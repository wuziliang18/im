package com.wuzl.im.common.message.boby;


public class LoginRequest extends AbstractRequest {

    private static final long serialVersionUID = 1L;
    private Integer           platform;             // 平台类型
    private String            loginKey;             // 登录key

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public String getLoginKey() {
        return loginKey;
    }

    public void setLoginKey(String loginKey) {
        this.loginKey = loginKey;
    }

}
