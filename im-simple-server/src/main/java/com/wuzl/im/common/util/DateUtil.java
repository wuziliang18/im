package com.wuzl.im.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static final String                  DATE_FORMAT = "yyyy年MM月dd日 HH:mm:ss";
    private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>() {

                                                                 protected synchronized SimpleDateFormat initialValue() {
                                                                     return new SimpleDateFormat(DATE_FORMAT);
                                                                 }
                                                             };

    public static DateFormat getDateFormat() {
        return (DateFormat) threadLocal.get();
    }

    public static Date parse(String textDate) throws ParseException {
        return getDateFormat().parse(textDate);
    }

    public static String format(Date date) {
        return getDateFormat().format(date);
    }
}
