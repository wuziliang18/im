package com.wuzl.im.server.manager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.wuzl.im.common.PlantformEnum;
import com.wuzl.im.common.model.UserPlantformModel;
import com.wuzl.im.server.manager.support.ChannelHelper;
import com.wuzl.im.server.manager.support.UserConnection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;

/**
 * 类ClientManager.java的实现描述： tcp连接的客户端管理
 * 
 * @author ziliang.wu 2017年2月27日 下午1:58:51
 */
public class TcpClientManager {

    // 简单用户信息
    private static final AttributeKey<UserPlantformModel> USER_INFO_KEY = AttributeKey.valueOf("userInfo");
    // AES 加密key，session级别
    private static final AttributeKey<byte[]> SECRET_INFO_KEY = AttributeKey.valueOf("secretKey");
    private static final TcpClientManager clientManager = new TcpClientManager();
    private static final ChannelFutureListener CLIENT_REMOVER = new ChannelFutureListener() {

        public void operationComplete(ChannelFuture future) throws Exception {
            removeChannel(future.channel());
        }
    };
    // 用户的tcp链接
    private final ConcurrentHashMap<String, UserConnection> cientChannelMap = new ConcurrentHashMap<String, UserConnection>(
            500000, 0.9f, 256);
    // tcp链接总数
    private final AtomicInteger clientCountDomain = new AtomicInteger(); // 单个服务的用户总数

    private TcpClientManager() {

    }

    public static TcpClientManager getClientManager() {
        return clientManager;
    }

    /**
     * 移除用户的某个连接
     * 
     * @param userPlantformModel
     * @param chn
     */
    public void removeChannelByMemberId(UserPlantformModel userPlantformModel, Channel chn) {
        String memberId = userPlantformModel.getMemberId();
        UserConnection userConnection = cientChannelMap.get(memberId);
        if (userConnection != null) {
            if (userConnection.removeConnection(userPlantformModel.getPlantform(), chn)) {
                clientCountDomain.decrementAndGet();
                // 下线TODO
            }
            if (userConnection.isEmpty()) {
                cientChannelMap.remove(memberId);
            }
        }
    }

    /**
     * 注册连接
     * 
     * @param userPlantformModel
     * @param chn
     */
    public void registerClient(UserPlantformModel userPlantformModel, Channel chn) {
        if (chn == null) {
            return;
        }
        if (userPlantformModel == null) {
            return;
        }
        chn.closeFuture().addListener(CLIENT_REMOVER);
        chn.attr(USER_INFO_KEY).set(userPlantformModel);
        // 上线
        UserConnection userConnection = cientChannelMap.get(userPlantformModel.getMemberId());
        boolean add = false;
        if (userConnection != null) {
            add = userConnection.addConnection(userPlantformModel.getPlantform(), new ChannelHelper(chn));
            if (add) {
                clientCountDomain.incrementAndGet();
            }
            return;
        }
        String memberId = userPlantformModel.getMemberId();
        ChannelHelper helpser = new ChannelHelper(chn);
        userConnection = new UserConnection(memberId);
        add = userConnection.addConnection(userPlantformModel.getPlantform(), helpser);
        if (cientChannelMap.putIfAbsent(memberId, userConnection) != null) {
            add = cientChannelMap.get(memberId).addConnection(userPlantformModel.getPlantform(),
                    new ChannelHelper(chn));
        }
        if (add) {
            clientCountDomain.incrementAndGet();
        }
    }

    /**
     * 获取所有用户连接
     * 
     * @return
     */
    public Collection<UserConnection> getAllChannel() {
        return cientChannelMap.values();
    }

    /**
     * 根据memberId获取连接信息
     * 
     * @param memberId
     * @return
     */
    public UserConnection getByMemberId(String memberId) {
        return cientChannelMap.get(memberId);
    }

    /**
     * 获取用户指定的会话类型channel
     * 
     * @param memberId
     * @param plantform
     * @return
     */
    public ChannelHelper getByMemberIdPlantform(String memberId, PlantformEnum plantform) {
        UserConnection userConnection = cientChannelMap.get(memberId);
        if (userConnection != null && userConnection.getChannels().size() > 0) {
            return userConnection.getByPlantform(plantform);
        }
        return null;
    }

    /**
     * 发送互踢信息
     * 
     * @param memberId
     * @param plantform
     */
    public void sendKickMessage(String memberId, PlantformEnum plantform) {
        UserConnection userConnection = cientChannelMap.get(memberId);
        if (userConnection != null && userConnection.getChannels().size() > 0) {
            userConnection.sendKickMessage(plantform);
        }
    }

    /**
     * 是否本地在线
     * 
     * @param memberId
     * @return
     */
    public boolean isLocalOnline(String memberId) {
        UserConnection userConnection = cientChannelMap.get(memberId);
        if (userConnection != null && userConnection.getChannels().size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取服务的连接数
     * 
     * @return
     */
    public int getClientCountDomain() {
        return clientCountDomain.get();
    }

    /**
     * 关闭通道
     * 
     * @param memberId
     * @param plantform
     */
    public void closeChannel(String memberId, PlantformEnum plantform) {
        UserConnection userConnection = cientChannelMap.get(memberId);
        if (userConnection != null && userConnection.getChannels().size() > 0) {
            boolean result = userConnection.closeConnection(plantform);
            if (result) {
                clientCountDomain.decrementAndGet();
            }
        }
    }

    /**
     * 获取连接数
     * 
     * @return
     */
    public static int getClientCount() {
        return getClientManager().getClientCountDomain();
    }

    /**
     * 移除连接
     * 
     * @param chn
     */
    public static void removeChannel(Channel chn) {
        if (chn == null) {
            return;
        }
        UserPlantformModel userPlantformModel = chn.attr(USER_INFO_KEY).get();
        if (userPlantformModel != null) {
            getClientManager().removeChannelByMemberId(userPlantformModel, chn);
        }
        chn.closeFuture().removeListener(CLIENT_REMOVER);
    }

    /**
     * 根据连接获取用户id
     * 
     * @param chn
     * @return
     */
    public static String getMemberId(Channel chn) {
        if (chn == null) {
            return null;
        }
        UserPlantformModel userPlantformModel = chn.attr(USER_INFO_KEY).get();
        if (userPlantformModel != null) {
            return userPlantformModel.getMemberId();
        }
        return null;
    }

    public static UserPlantformModel getUserInfo(Channel chn) {
        if (chn == null) {
            return null;
        }
        UserPlantformModel userPlantformModel = chn.attr(USER_INFO_KEY).get();
        if (userPlantformModel != null) {
            return userPlantformModel;
        }
        return null;
    }

    /**
     * 当前服务器的连接
     * 
     * @param chn
     * @return
     */
    public static boolean isLocalOnlineByChannel(Channel chn) {
        return StringUtils.isNotEmpty(getMemberId(chn));
    }

    /**
     * 保存加密的key
     * 
     * @param secretKey
     * @param chn
     */
    public static void saveSecretKey(byte[] secretKey, Channel chn) {
        if (secretKey != null && !"".equals(secretKey)) {
            chn.attr(SECRET_INFO_KEY).set(secretKey);
        }
    }

    /**
     * 获取channel上的secretKey
     * 
     * @param chn
     * @return
     */
    public static byte[] getSecretKeyByChannel(Channel chn) {
        if (chn == null) {
            return null;
        }
        byte[] secretKey = chn.attr(SECRET_INFO_KEY).get();
        return secretKey;
    }

}
