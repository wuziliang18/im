package com.wuzl.im.server.manager.support;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.wuzl.im.common.PlantformEnum;
import com.wuzl.im.common.model.UserOnlineTimeModel;
import com.wuzl.im.common.util.DateUtil;
import com.wuzl.im.server.util.OutMessageUtil;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 类UserConnection.java的实现描述：用户tcp连接信息，包含两个链接 android和ios的 后期加入window客户端的
 * 
 * @author ziliang.wu 2017年2月27日 下午2:19:19
 */
public class UserConnection {

    private static final String FORMAT_MSG = "您的帐号于%s 在其他设备上登录。如果这不是您的操作，请及时修改您的密码。";
    private final String memberId;
    // private final AtomicReference<ChannelHelper> androidChannelHelper = new AtomicReference<ChannelHelper>(null);
    // private final AtomicReference<ChannelHelper> iosChannelHelper = new AtomicReference<ChannelHelper>(null);
    private final ConcurrentHashMap<PlantformEnum, ChannelHelper> channelHelperMap = new ConcurrentHashMap<>();

    public UserConnection(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }

    public Collection<ChannelHelper> getChannels() {
        return channelHelperMap.values();
    }

    /**
     * 获取约束的通道集合
     * 
     * @param excludePlantform
     * @return
     */
    public Collection<ChannelHelper> getConstraintChannels(PlantformEnum excludePlantform) {
        if (excludePlantform == null) {
            return getChannels();
        }
        Set<ChannelHelper> channels = new HashSet<>();
        for (Map.Entry<PlantformEnum, ChannelHelper> entry : channelHelperMap.entrySet()) {
            if (!(excludePlantform == entry.getKey())) {
                channels.add(entry.getValue());
            }
        }
        return channels;
    }

    public boolean isEmpty() {
        return channelHelperMap.isEmpty();
    }

    public boolean addConnection(PlantformEnum plantForm, ChannelHelper helper) {
        if (helper == null) {
            return false;
        }
        ChannelHelper oldChannelHelper = channelHelperMap.put(plantForm, helper);
        if (oldChannelHelper != null && oldChannelHelper == helper) {// 同一通道
            return false;
        }
        // 如果是多个链接关闭之前的 保证每个平台就一个链接
        try {
            if (oldChannelHelper != null) {
                sendKickMessage(oldChannelHelper);
                oldChannelHelper.close();
                // 保存登录时间
                this.saveOnlineTime(oldChannelHelper, plantForm);
            }
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * 发送互踢信息
     * 
     * @param plantform
     */
    public void sendKickMessage(PlantformEnum plantform) {
        ChannelHelper channelHelper = getByPlantform(plantform);
        if (channelHelper != null) {
            this.sendKickMessage(channelHelper);
        }
    }

    public boolean removeConnection(PlantformEnum plantForm, Channel channel) {
        if (channel == null) {
            return false;
        }
        ChannelHelper oldChannelHelper = channelHelperMap.get(plantForm);
        if (oldChannelHelper != null && oldChannelHelper.getChannel() == channel) {
            channelHelperMap.remove(plantForm);// TODO 此处极端情况下会误删
            // 保存登录时间
            this.saveOnlineTime(oldChannelHelper, plantForm);
            return true;
        }
        return false;
    }

    public boolean closeConnection(PlantformEnum plantForm) {
        ChannelHelper oldChannelHelper = channelHelperMap.get(plantForm);
        if (oldChannelHelper != null) {
            oldChannelHelper.close();
            return true;
        }
        return false;
    }

    /**
     * 获取用户指定平台的连接
     * 
     * @param plantform
     * @return
     */
    public ChannelHelper getByPlantform(PlantformEnum plantform) {
        ChannelHelper channelHelper = channelHelperMap.get(plantform);
        return channelHelper;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("memberId:").append(memberId);
        sb.append(",connecitons:");
        for (Map.Entry<PlantformEnum, ChannelHelper> entry : channelHelperMap.entrySet()) {
            sb.append(entry.getKey().getName()).append(":");
            sb.append(entry.getValue().getChannel().toString());
            sb.append(",");
        }
        return sb.toString();
    }

    private void sendKickMessage(ChannelHelper channelHelper) {
        try {
            channelHelper.write(
                    OutMessageUtil.getExceptionOut(0, 1000401, String.format(FORMAT_MSG, DateUtil.format(new Date()))));
        } catch (Exception e) {
        }
    }

    /**
     * 保存连接存在的时间
     * 
     * @param helper
     * @param plantform
     */
    private void saveOnlineTime(ChannelHelper helper, PlantformEnum plantForm) {

        UserOnlineTimeModel model = new UserOnlineTimeModel();
        model.setMemberId(memberId);
        model.setOnlineTime(helper.getDateline());
        model.setOffineTime(System.currentTimeMillis());
        model.setPlantform(plantForm.getValue());
        Channel channel = helper.getChannel();
        if (channel instanceof NioSocketChannel) {
            model.setIp(((NioSocketChannel) channel).remoteAddress().getAddress().getHostAddress());
        } else {
            model.setIp("");
        }
        // TODO
    }
}
