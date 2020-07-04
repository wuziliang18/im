package com.wuzl.im.common.util;

import com.wuzl.im.common.exception.ImException;

public class Assert {

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new ImException(message);
        }
    }

    public static void notEmpty(String s, String message) {
        if (s == null || s.equals("")) {
            throw new ImException(message);
        }
    }
}
