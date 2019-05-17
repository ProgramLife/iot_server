package com.xxx.iot.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chen on 2019/1/3
 */
public class TimeUtil {
    public static String getDateStr() {
        Calendar calendar = Calendar.getInstance();

        String year = calendar.get(Calendar.YEAR) + "";
        String month = calendar.get(Calendar.MONTH) + 1 + "";
        String day = calendar.get(Calendar.DATE) + "";

        return year + "-" + month + "-" + day;
    }

    /**
     * 获取时间
     *
     * @return 时间, 年月日时分秒,
     */
    public static String getTime() {
        try {
            // 年月日时分秒
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // 获取当前时间
            Date date = new Date(System.currentTimeMillis());
            String strTime = sdf.format(date);

            return strTime;
        } catch (Exception e) {
        }

        return "";
    }

    public static String getOrder() {
        try {
            // 年月日时分秒
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

            // 获取当前时间
            Date date = new Date(System.currentTimeMillis());
            String strTime = sdf.format(date);

            return strTime;
        } catch (Exception e) {
        }

        return "";
    }
}
