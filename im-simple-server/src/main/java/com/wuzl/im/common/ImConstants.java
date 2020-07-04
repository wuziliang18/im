package com.wuzl.im.common;

import java.nio.charset.Charset;

import com.wuzl.im.common.util.Bytes;

public class ImConstants {

    public static final String SYSTEM_NAME = "wuzlIm";
    public static final int DEFAULT_PORT = 10010;
    public static final String IM_HEADER_MAGIC = "WUZL";
    public static final int HEADER_FIXED_LENGTH = 14; // 固定头
    public static final short IM_MAGIC = (short) 0x55ff;
    public static final byte MAGIC_HIGH = Bytes.short2bytes(IM_MAGIC)[0];

    public static final byte MAGIC_LOW = Bytes.short2bytes(IM_MAGIC)[1];
    public static final Charset CHARSET = Charset.forName("UTF-8"); // 文本编码
    public static final byte LINESEPARATOR = '\n'; // 分隔符
    public static final String IM_MAGIC_NOW_VERSION = "1.0";
    public static final String IM_H5_DEFAULT_VERSION = "1.0";
    public static final int TIME_OUT = 600; // 秒

}
