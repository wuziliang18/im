package com.wuzl.im.server.processer.support;

import com.wuzl.im.server.processer.Processer;

/**
 * 类FilterProcesser.java的实现描述：包装拦截器
 * 
 * @author ziliang.wu 2017年2月27日 上午11:19:19
 */
public class FilterProcesser extends WrappedProcesser {

    public FilterProcesser(Processer processer) {
        super(buildInvokerChain(processer));
    }

    private static Processer buildInvokerChain(final Processer processer) {
        Processer last = processer;
        // TODO
        // List<Filter> filters = Filters.getFilters();
        // if (filters != null) {
        // for (final Filter filter : filters) {
        // final Processer next = last;
        // last = new Processer() {
        //
        // @Override
        // public OutMessage process(final AckMessage msg, final ChannelHandlerContext ctx) {
        // return filter.process(next, msg, ctx);
        // }
        // };
        //
        // }
        // }
        return last;
    }

}
