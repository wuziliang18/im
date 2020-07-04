package com.wuzl.im.common.model;

import java.io.Serializable;

/**
 * 类UserOnlineTimeModel.java的实现描述：用户的在线时长
 * 
 * @author ziliang.wu 2017年6月14日 下午2:40:53
 */
/**
 * 类UserOnlineTimeModel.java的实现描述：TODO 类实现描述
 * 
 * @author ziliang.wu 2017年6月14日 下午2:42:04
 */
public class UserOnlineTimeModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String            memberId;
    private Long              onlineTime;           // 上线时间
    private Long              offineTime;           // 下线时间
    private Integer           plantform;            // 平台 IOS(1), Android(2"), H5(3), Window(4);
    private String            ip;                   // 对方ip地址

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Long getOffineTime() {
        return offineTime;
    }

    public void setOffineTime(Long offineTime) {
        this.offineTime = offineTime;
    }

    public Integer getPlantform() {
        return plantform;
    }

    public void setPlantform(Integer plantform) {
        this.plantform = plantform;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
