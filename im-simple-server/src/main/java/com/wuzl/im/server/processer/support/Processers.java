package com.wuzl.im.server.processer.support;

import com.wuzl.im.common.message.Message;
import com.wuzl.im.server.processer.Processer;

public class Processers {

    private static Processer heartbeatProcesser;
    private static Processer publicKeyExchageProcesser;
    private static Processer loginProcesser;
    private static Processer defaultProcesser;

    public static void setPublicKeyExchageProcesser(Processer publicKeyExchageProcesser) {
        Processers.publicKeyExchageProcesser = getWrappedProcesser(publicKeyExchageProcesser);
    }

    public static void setHeartbeatProcesser(Processer heartbeatProcesser) {
        Processers.heartbeatProcesser = getWrappedProcesser(heartbeatProcesser);
    }

    public static void setLoginProcesser(Processer loginProcesser) {
        Processers.loginProcesser = getWrappedProcesser(loginProcesser);
    }

    public static void setDefaultProcesser(Processer defaultProcesser) {
        Processers.defaultProcesser = getWrappedProcesser(defaultProcesser);
    }

    public static Processer getHeartbeatProcesser() {
        return heartbeatProcesser;
    }

    public static Processer getLoginProcesser() {
        return loginProcesser;
    }

    public static Processer getDefaultProcesser() {
        return defaultProcesser;
    }

    public static Processer getWrappedProcesser(Processer processer) {
        return new DispatcherProcesser(new WriteProcesser(new ExceptionProcesser(new FilterProcesser(processer))));
    }

    public static Processer getProcesser(Message.Type type) {
        switch (type) {
            case HEARTBEAT:
                return heartbeatProcesser;
            case PUBLIC_KEY_REQUEST:
                return publicKeyExchageProcesser;
            case LOGIN_REQUEST:
                return loginProcesser;
            default:
                return defaultProcesser;
        }
    }

    /**
     * 获取同步的processer 给service之类使用
     * 
     * @param type
     * @return
     */
    public static Processer getSyncProcesser(Message.Type type) {
        Processer processer = getProcesser(type);
        if (processer == null) {
            return processer;
        }
        if (processer instanceof DispatcherProcesser) {
            return ((DispatcherProcesser) processer).getProcesser();
        }
        return processer;
    }
}
