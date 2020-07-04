package com.wuzl.im.server.service.enums;

public enum RequestSourceEnum {

    Unknown(0, "Unknown"), IOS(1, "IOS"), Android(2, "Android"), H5(3, "H5"), Window(4, "window"), HSF(5,
            "Hsf"), INNER(6, "Inner");

    final private String name;
    final private int value;

    RequestSourceEnum(int value, String name) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static RequestSourceEnum getRequestSourceEnumByValue(Integer value) {
        if (value == null) {
            return Unknown;
        }
        for (RequestSourceEnum requestSourceEnum : RequestSourceEnum.values()) {
            if (requestSourceEnum.value == value)
                return requestSourceEnum;
        }
        return Unknown;
    }
}
