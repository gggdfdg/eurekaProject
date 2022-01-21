package com.ming.eureka;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 常用日历操作辅助类
 */
public class CalendarUtil {

    /**
     * 获得当前年份
     *
     * @return
     */
    public static int getYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获得当前月份
     *
     * @return
     */
    public static int getMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获得当前日期
     *
     * @return
     */
    public static int getDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE);
    }

    /**
     * 获得当前小時
     *
     * @return
     */
    public static int getHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获得当前分钟
     *
     * @return
     */
    public static int getMinute() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获得当前秒数
     *
     * @return
     */
    public static int getSecond() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 获得今天在本年的第几天
     *
     * @return
     */
    public static int getDayOfYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获得今天在本月的第几天(获得当前日)
     *
     * @return
     */
    public static int getDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /****
     *
     * 系统当前时间
     * */
    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.format(calendar.getTime());
    }

    public static void main(String args[]){
        System.out.println(getYear());
        System.out.println(getMonth());
        System.out.println(getDay());
        System.out.println(getHour());
        System.out.println(getMinute());
        System.out.println(getSecond());
        System.out.println(getDayOfYear());
        System.out.println(getDayOfMonth());
        System.out.println(getCurrentDate());
    }
}