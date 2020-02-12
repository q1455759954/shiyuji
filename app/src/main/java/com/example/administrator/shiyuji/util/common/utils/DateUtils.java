package com.example.administrator.shiyuji.util.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2019/7/7.
 */

public class DateUtils {
    public static final String TYPE_01 = "yyyy-MM-dd HH:mm:ss";
    public static final String TYPE_02 = "yyyy-MM-dd";
    public static final String TYPE_03 = "HH:mm:ss";
    public static final String TYPE_04 = "yyyy年MM月dd日";

    public DateUtils() {
    }

    public static String formatDate(long time, String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return (new SimpleDateFormat(format)).format(cal.getTime());
    }

    public static String formatDate(String longStr, String format) {
        try {
            return formatDate(Long.parseLong(longStr), format);
        } catch (Exception var3) {
            return "";
        }
    }

    public static long formatStr(String timeStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            return sdf.parse(timeStr).getTime();
        } catch (ParseException var4) {
            var4.printStackTrace();
            return 0L;
        }
    }
}
