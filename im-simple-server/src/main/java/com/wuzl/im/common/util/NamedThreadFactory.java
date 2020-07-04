package com.wuzl.im.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 
 * 类NamedThreadFactory.java的实现描述：TODO 类实现描述 
 * @author ziliang.wu 2017年2月24日 下午5:05:21
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_SEQ   = new AtomicInteger(1);// 为了区分多个没有设置连接池名字的一个字段
                                                                         // 自增长安全

    private final AtomicInteger        mThreadNum = new AtomicInteger(1);// 区分每个县城
                                                                         // 自增长安全

    private final String               mPrefix;

    private final boolean              mDaemo;                           // 线程是否后台运行

    private final ThreadGroup          mGroup;                           // 线程组

    public NamedThreadFactory(){
        this("pool-" + POOL_SEQ.getAndIncrement(), false);
    }

    public NamedThreadFactory(String prefix){
        this(prefix, false);
    }

    public NamedThreadFactory(String prefix, boolean daemo){
        mPrefix = prefix + "-thread-";
        mDaemo = daemo;
        SecurityManager s = System.getSecurityManager();
        mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();// 保存线程组到变量
    }

    public Thread newThread(Runnable runnable) {
        String name = mPrefix + mThreadNum.getAndIncrement();
        Thread ret = new Thread(mGroup, runnable, name, 0);// 新创建一个线程 初始化包含组
        ret.setDaemon(mDaemo);
        return ret;
    }

    public ThreadGroup getThreadGroup() {
        return mGroup;
    }
}
