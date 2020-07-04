package com.wuzl.im.server.processer.support;

import com.wuzl.im.common.message.MessageFactory;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.server.processer.Processer;

public abstract class AbstactProcesser implements Processer {

    public static final OutMessage HEARTBEAT  = MessageFactory.getHeartbeatMessage();

    public static final OutMessage DISCONNECT = MessageFactory.getDisconnnectionMessage();

}
