package com.wuzl.im.common.util;

import org.apache.commons.lang3.StringUtils;

public class EmojiFilterUtils {

    /**
     * 将emoji表情替换
     * 
     * @param source
     * @param replacement
     * @return
     */
    public static String filterEmoji(String source, String replacement) {
        if (replacement == null) {
            replacement = "";
        }
        if (StringUtils.isNotBlank(source)) {
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", replacement);
        } else {
            return source;
        }
    }

    /**
     * 替换emoji为标签
     * 
     * @param source
     * @return
     */
    public static String emoji2Tag(String source) {
        return filterEmoji(source, "[emoji]");
    }

    /**
     * 去除emoji
     * 
     * @param source
     * @return
     */
    public static String removeEmoji(String source) {
        return filterEmoji(source, "");
    }
}
