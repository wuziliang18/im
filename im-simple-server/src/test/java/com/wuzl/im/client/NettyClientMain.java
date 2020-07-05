package com.wuzl.im.client;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.wuzl.im.common.PlantformEnum;
import com.wuzl.im.common.message.MessageFactory;
import com.wuzl.im.common.message.OutMessage;

public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException {
        NettyClient client = new NettyClient("localhost", 10010, PlantformEnum.IOS, "10010");
        client.connect();
        Thread.sleep(1000);
        // 发送消息
        String memberId = "10086";
        for (int i = 0; i < 10000; i++) {
            Map<String, Object> message = new HashMap<>();
            message.put("content", "我是联通你是移动吗? 发几个好看的番号给你 番号id:" + i);
            OutMessage sendMessage = MessageFactory.getSendMessage(memberId, 0, JSON.toJSONString(message), 1l);
            client.sendMsg(sendMessage);
        }

    }
}
