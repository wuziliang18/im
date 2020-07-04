package com.wuzl.im.common.model;

import com.wuzl.im.common.PlantformEnum;
import com.wuzl.im.common.exception.ImException;

/**
 * 类UserPlantformModel.java的实现描述：用户平台的信息
 * 
 * @author ziliang.wu 2017年3月8日 下午1:53:18
 */
public class UserPlantformModel {

    public UserPlantformModel(String memberId, int plantform, String version){
        this(memberId, PlantformEnum.valueOfInt(plantform), version);
    }

    public UserPlantformModel(String memberId, PlantformEnum plantform, String version){
        if (plantform == null) {
            throw new ImException("平台值不正确");
        }
        this.memberId = memberId;
        this.plantform = plantform;
        this.version = version;
    }

    private final String        memberId;
    private final PlantformEnum plantform;// 平台
    private final String        version;  // 版本号

    public String getMemberId() {
        return memberId;
    }

    public PlantformEnum getPlantform() {
        return plantform;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("memberId:").append(memberId);
        sb.append(",plantform:").append(plantform);
        return sb.toString();
    }

}
