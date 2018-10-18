package com.jeffrey.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期格式化类
 * Created by Jun.wang on 2018/10/18.
 */
public class DateFormatHelper {
    public enum DateFormat {
        DATE_1("MM-dd"),
        DATE_2("MM月dd日"),
        DATE_3("MM月dd日 HH:mm"),
        DATE_4("yyyy-MM-dd HH:mm"),
        DATE_5("yyyy-MM-dd"),
        DATE_6("EEEE"),
        DATE_7("yyyy-MM-dd"),
        DATE_8("yyyy-MM-dd hh:mm:ss"),
        DATE_9("MM-dd HH:mm"),
        DATE_10("yyyy/MM/dd"),
        DATE_11("HH:mm"),
        DATE_12("yyyy年MM月dd日");

        private String value;
        private DateFormat(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static String formatDate(String timeSpace, DateFormat dateFormat) {
        // 默认时区改为东八区
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = null;
        Long time = Long.parseLong(timeSpace);
        Date date = new Date(time);
        format = new SimpleDateFormat(dateFormat.getValue());
        return format.format(date);
    }
}














