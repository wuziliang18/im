package com.wuzl.im.common.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.wuzl.im.common.ImConstants;
import com.wuzl.im.common.SystemConfig;
import com.wuzl.im.common.util.NamedThreadFactory;

/**
 * 类ImThreadPool.java的实现描述： im使用的线程池
 * 
 * @author ziliang.wu 2017年2月27日 上午10:01:46
 */
public class ImThreadPool {

    public static ThreadPoolExecutor newExecutor(String name, int threadCount) {
        return new ThreadPoolExecutor(threadCount, threadCount, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(name, true));
    }

    public static ThreadPoolExecutor newExecutor() {
        return newExecutor(ImConstants.SYSTEM_NAME, SystemConfig.SERVER_THREAD_POOL_COUNT);
    }
}
