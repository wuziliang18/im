package com.wuzl.im.server.processer.support;

import com.wuzl.im.common.message.Message;
import com.wuzl.im.server.processer.DefaultProcesser;
import com.wuzl.im.server.processer.HeartbeatProcesser;
import com.wuzl.im.server.processer.LoginProcesser;
import com.wuzl.im.server.processer.Processer;
import com.wuzl.im.server.processer.PublicKeyExchageProcesser;

public class Processers {

    private static Processer heartbeatProcesser = getWrappedProcesser(new HeartbeatProcesser());
    private static Processer publicKeyExchageProcesser = getWrappedProcesser(new PublicKeyExchageProcesser());
    private static Processer loginProcesser = getWrappedProcesser(new LoginProcesser());
    private static Processer defaultProcesser = getWrappedProcesser(new DefaultProcesser());

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
