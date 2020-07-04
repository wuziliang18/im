package com.wuzl.im.common;

/**
 * 类SystemConfig.java的实现描述：系统的配置信息
 * 
 * @author ziliang.wu 2017年2月24日 下午5:09:00
 */
public class SystemConfig {

    public static int SERVER_THREAD_POOL_COUNT = 500; // 服务端线程数
    public static int SERVER_MAX_CONNECTION = 600000;// 服务端最大连接数
    public static int SERVER_WARN_CONNECTION = 500000;// 服务端警告连接数
    public static String PUSH_TOPIC; // push topic

    public static void setSERVER_MAX_CONNECTION(int sERVER_MAX_CONNECTION) {
        SERVER_MAX_CONNECTION = sERVER_MAX_CONNECTION;
    }

    public static void setSERVER_WARN_CONNECTION(int sERVER_WARN_CONNECTION) {
        SERVER_WARN_CONNECTION = sERVER_WARN_CONNECTION;
    }

    public static void setSERVER_THREAD_POOL_COUNT(int sERVER_THREAD_POOL_COUNT) {
        SERVER_THREAD_POOL_COUNT = sERVER_THREAD_POOL_COUNT;
    }

    public static void setPUSH_TOPIC(String pUSH_TOPIC) {
        PUSH_TOPIC = pUSH_TOPIC;
    }

}
