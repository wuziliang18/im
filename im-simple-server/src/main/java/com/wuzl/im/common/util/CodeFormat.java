package com.wuzl.im.common.util;

public class CodeFormat {

    /**
     * 去下划线和驼峰并且小写
     * 
     * @param srcCode
     * @return
     */
    public static String delUnderlineHumpAndToLower(String srcCode) {
        return srcCode.replace("_", "").toLowerCase();
    }
}
