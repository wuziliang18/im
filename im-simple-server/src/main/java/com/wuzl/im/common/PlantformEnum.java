package com.wuzl.im.common;

/**
 * 类ClientEnum.java的实现描述：平台分类枚举
 * 
 * @author ziliang.wu 2017年2月27日 下午1:49:58
 */
public enum PlantformEnum {
                           IOS(1, "IOS"), Android(2, "Android"), H5(3, "H5"), Window(4, "window");

    final private String name;
    final private int    value;

    PlantformEnum(int value, String name){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static PlantformEnum valueOfInt(int value) {
        for (PlantformEnum plantformEnum : PlantformEnum.values()) {
            if (plantformEnum.value == value) return plantformEnum;
        }
        return null;
    }

}
