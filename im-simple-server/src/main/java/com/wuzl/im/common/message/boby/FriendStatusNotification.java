package com.wuzl.im.common.message.boby;

import java.io.Serializable;

/**
 * 类FriendStatusNotification.java的实现描述：好友状态的通知
 * 
 * @author ziliang.wu 2017年5月10日 下午2:03:16
 */
/**
 * 类FriendStatusNotification.java的实现描述：TODO 类实现描述
 * 
 * @author ziliang.wu 2017年5月10日 下午2:05:46
 */
public class FriendStatusNotification implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 1 收到添加请求 info: 验证信息。 弹出验证

       2 收到添加同意。 添加到联系人列表

       3 收到添加拒绝 info: 可能为拒绝理由。

       4 收到被删除。从联系人列表删除

      5 收到其他端加好友。添加到联系人列表

      6 收到其他端删除好友。从联系人列表删除

     */
    private Integer           opType;
    private String            memberId;             // 对方的memberId
    private String            info;                 // 附加信息。验证信息
    private Long              timestamp;

    public Integer getOpType() {
        return opType;
    }

    public void setOpType(Integer opType) {
        this.opType = opType;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

}
