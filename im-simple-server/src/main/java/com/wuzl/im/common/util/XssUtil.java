package com.wuzl.im.common.util;

public class XssUtil {

    public static String xssFilter(String input) {
        if (input == null) {
            return input;
        }
        return input.replace("%22", "").replace("\"", "＂").replace("\'", "＇");
    }
}
