package com.wuzl.im.common.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 类ExecutorsEx.java的实现描述：TODO 类实现描述
 * 
 * @author ziliang.wu 2017年2月27日 上午10:01:17
 */
public class ExecutorsEx {

    /**
     * 创建一个堵塞队列
     * 
     * @param threadSize
     * @return
     */
    public static ExecutorService newFixedThreadPool(int threadSize) {
        return new ThreadPoolExecutor(threadSize, threadSize, 0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>(1) {

                                          private static final long serialVersionUID = -9028058603126367678L;

                                          @Override
                                          public boolean offer(Runnable e) {
                                              try {
                                                  put(e);
                                                  return true;
                                              } catch (InterruptedException ie) {
                                                  Thread.currentThread().interrupt();
                                              }
                                              return false;
                                          }
                                      });
    }

}
