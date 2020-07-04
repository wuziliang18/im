package com.wuzl.im.server.processer.support;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wuzl.im.common.exception.ImException;
import com.wuzl.im.common.message.AckMessage;
import com.wuzl.im.common.message.OutMessage;
import com.wuzl.im.common.threadpool.ImThreadPool;
import com.wuzl.im.server.processer.Processer;

import io.netty.channel.ChannelHandlerContext;

/**
 * 类DispatcherProcesser.java的实现描述：线程池处理
 * 
 * @author ziliang.wu 2017年2月27日 上午11:21:41
 */
public class DispatcherProcesser extends WrappedProcesser {

    private static final Logger logger = LoggerFactory.getLogger("PROJECT_ERROR");
    private final static ThreadPoolExecutor THREAD_POOL = ImThreadPool.newExecutor();
    private final Processer processer;

    public DispatcherProcesser(Processer processer) {
        super(processer);
        this.processer = processer;
    }

    @Override
    public OutMessage process(final AckMessage msg, final ChannelHandlerContext ctx) {
        try {
            THREAD_POOL.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        processer.process(msg, ctx);
                    } catch (Exception e) {
                        logger.error(msg.getType() + " 处理出现错误", e);
                    }
                }
            });
        } catch (Exception e) {
            throw new ImException(msg.getType() + " 加入线程池异常", e);
        }
        return null;
    }
}
