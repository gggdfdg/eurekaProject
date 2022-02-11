/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 *
 * @author anderson
 * @version 1.0
 */

import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期工具类
 */
public class DateUtil {

    public static final String TIME_FORMATE = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMATE = "yyyy-MM-dd";
    public static final String MONTH_FORMATE = "yyyy-MM";
    public static final String MINUTES_FORMATE = "yyyy-MM-dd HH:mm";

    /**
     * 获取日期当天的凌晨0点0分0秒的日期
     * @param time 例如2022-2-9
     * @return
     */
    public static Date getStartDateTimeFromStr(String time) {
        try {
            return DateUtils.parseDate(time, DATE_FORMATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取日期当天的凌晨23:59:59的日期
     * @param time 例如2022-2-9
     * @return
     */
    public static Date getEndDateTimeFromStr(String time) {
        time = time + " 23:59:59";
        try {
            return DateUtils.parseDate(time, TIME_FORMATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取得指定月份的第一天
     *
     * @param strdate String
     * @return String
     */
    public static String getMonthBegin(String strdate) {
        java.util.Date date = StringToDate(strdate);
        return formatDateByFormat(date, MONTH_FORMATE) + "-01";
    }

    /**
     * 取得指定月份的第一天
     *
     * @return String
     */
    public static String getMonthBegin(Date date) {
        return formatDateByFormat(date, MONTH_FORMATE) + "-01";
    }

    /**
     * 取得指定月份的最后一天
     *
     * @param strdate String
     * @return String
     */
    public static String getMonthEnd(String strdate) {
        //获取当月的第一天
        java.util.Date date = StringToDate(getMonthBegin(strdate));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //月份加一，往下走
        calendar.add(Calendar.MONTH, 1);
        //天数减一就是最后一天
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return formatDate(calendar.getTime());
    }

    /**
     * 取得指定月份的最后一天
     *
     * @return String
     */
    public static String getMonthEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //将日子设置为当前日期第一天
        calendar.set(Calendar.DAY_OF_MONTH,1);
        //月份加一，往下走
        calendar.add(Calendar.MONTH, 1);
        //天数减一就是最后一天
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return formatDate(calendar.getTime());
    }

    /**
     * 取得指定月份的总天数
     *
     * @param strdate String
     * @return String
     */
    public static int getMonthDaynum(String strdate) {
        String enddate = getMonthEnd(strdate);
        return Integer.parseInt(enddate.substring(enddate.length() - 2,
                enddate.length()));
    }

    /**
     * 常用的格式化日期
     *
     * @param date Date
     * @return String
     */
    public static String formatDate(java.util.Date date) {
        return formatDateByFormat(date, "yyyy-MM-dd");
    }

    /**
     * time是否在当前时间的前N天
     *
     * @return
     */
    public static boolean isPastDay(long time) {
        Calendar current = Calendar.getInstance();
        current.set(Calendar.HOUR, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        Calendar old = Calendar.getInstance();
        current.setTime(new java.util.Date(time));
        return current.getTime().getTime() < old.getTime().getTime();
    }

    /**
     * 是否在同一天
     *
     * @return
     */
    public static boolean isToday(Date time) {
        Calendar current = Calendar.getInstance();
        current.set(Calendar.HOUR, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);
        Calendar old = Calendar.getInstance();
        old.setTime(time);
        old.set(Calendar.HOUR, 0);
        old.set(Calendar.MINUTE, 0);
        old.set(Calendar.SECOND, 0);
        old.set(Calendar.MILLISECOND, 0);
        return current.getTime().getTime() == old.getTime().getTime();
    }

    /**
     * 判断是否比当前时间小
     *
     * @return
     */
    public static boolean isBeforToday(Date time) {
        Calendar current = Calendar.getInstance();
        current.set(Calendar.HOUR, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);
        Calendar old = Calendar.getInstance();
        old.setTime(time);
        old.set(Calendar.HOUR, 0);
        old.set(Calendar.MINUTE, 0);
        old.set(Calendar.SECOND, 0);
        old.set(Calendar.MILLISECOND, 0);
        return current.getTime().getTime() > old.getTime().getTime();
    }

    /**
     * 是否在同一天
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean isTheSameDay(long milliseconds1, long milliseconds2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(milliseconds1);
        c2.setTimeInMillis(milliseconds2);
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
                && (c1.get(Calendar.DAY_OF_MONTH) == c2
                .get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 是否在同一年
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean isTheSameYear(long milliseconds1, long milliseconds2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(milliseconds1);
        c2.setTimeInMillis(milliseconds2);
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR));
    }

    /**
     * 是否在同一天
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean isSameToday(long time) {
        Calendar current = Calendar.getInstance();
        current.set(Calendar.HOUR, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.MILLISECOND, 0);
        Calendar old = Calendar.getInstance();
        old.setTime(new java.util.Date(time));
        old.set(Calendar.HOUR, 0);
        old.set(Calendar.MINUTE, 0);
        old.set(Calendar.SECOND, 0);
        old.set(Calendar.MILLISECOND, 0);
        return current.getTime().getTime() == old.getTime().getTime();
    }

    /**
     * 常用的格式化日期 yyyy-MM-dd HH:mm:ss
     *
     * @param date Date
     * @return String
     */
    public static String formatDate2(java.util.Date date) {
        return formatDateByFormat(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 以指定的格式来格式化日期
     *
     * @param date   Date
     * @param format String
     * @return String
     */
    public static String formatDateByFormat(java.util.Date date, String format) {
        String result = "";
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                result = sdf.format(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 取得系统当前时间,类型为Timestamp
     *
     * @return Timestamp
     */
    public static Timestamp getNowTimestamp() {
        java.util.Date d = new java.util.Date();
        Timestamp numTime = new Timestamp(d.getTime());
        return numTime;
    }

    /**
     * 取得系统当前时间,类型为String
     *
     * @return String
     */
    public static String getNowTimeString() {
        return TimestampToString(getNowTimestamp());
    }

    /**
     * 取得系统的当前时间,类型为String
     *
     * @return String
     */
    public static String getNowMonth() {
        return getNowTimeString().substring(0, 7);
    }

    /**
     * 取得系统的当前年份,类型为String
     *
     * @return String
     */
    public static String getYearNow() {
        java.util.Date now = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(now);
    }

    /**
     * 取得系统的当前月份,类型为String
     *
     * @return String
     */
    public static String getMonthNow() {
        java.util.Date now = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        return sdf.format(now);
    }

    public static String getTimeName() {
        java.util.Date now = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String hour = sdf.format(now);
        if ("00".equals(hour) || "0".equals(hour) || "01".equals(hour)
                || "1".equals(hour) || "02".equals(hour) || "2".equals(hour)
                || "03".equals(hour) || "3".equals(hour) || "04".equals(hour)
                || "4".equals(hour)) {
            return "凌晨";
        } else if ("05".equals(hour) || "5".equals(hour) || "06".equals(hour)
                || "6".equals(hour) || "07".equals(hour) || "7".equals(hour)
                || "08".equals(hour) || "8".equals(hour)) {
            return "早上";
        } else if ("09".equals(hour) || "9".equals(hour) || "10".equals(hour)
                || "11".equals(hour)) {
            return "上午";
        } else if ("12".equals(hour) || "13".equals(hour)) {
            return "中午";
        } else if ("14".equals(hour) || "15".equals(hour) || "16".equals(hour)
                || "17".equals(hour)) {
            return "下午";
        } else {
            return "晚上";
        }
    }

    /**
     * 取得工资年月,类型为String
     *
     * @return String
     */
    public static String getSalaryMonth() {
        java.util.Date now = getNowDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MONTH, -1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        return formatter.format(calendar.getTime());
    }

    /**
     * 取得系统的当前时间,类型为java.sql.Date
     *
     * @return java.sql.Date
     */
    public static java.sql.Date getNowDate() {
        java.util.Date d = new java.util.Date();
        return new java.sql.Date(d.getTime());
    }

    /**
     * 从Timestamp类型转化为yyyy/mm/dd类型的字符串
     *
     * @param date
     * @param strDefault
     * @return
     */
    public static String TimestampToString(Timestamp date, String strDefault) {
        String strTemp = strDefault;
        if (date != null) {
            // SimpleDateFormat formatter= new SimpleDateFormat ("yyyy/MM/dd");
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            strTemp = formatter.format(date);
        }
        return strTemp;
    }

    /**
     * 从Timestamp类型转化为yyyy/mm/dd类型的字符串,如果为null,侧放回""
     *
     * @param date
     * @return
     */
    public static String TimestampToString(Timestamp date) {
        return TimestampToString(date, null);
    }

    /**
     * date型转化为String 格式为yyyy/MM/dd
     *
     * @param date
     * @param strDefault
     * @return
     */
    public static String DateToString(java.sql.Date date, String strDefault) {
        String strTemp = strDefault;
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            strTemp = formatter.format(date);
        }
        return strTemp;
    }

    /**
     * date型转化为String 格式为yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String DateToString(java.util.Date date) {
        String strTemp = "";
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            strTemp = formatter.format(date);
        }
        return strTemp;
    }

    /**
     * date型转化为String 格式为yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String DateToStringYearMonth(java.util.Date date) {
        String strTemp = "";
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
            strTemp = formatter.format(date);
        }
        return strTemp;
    }

    /**
     * yyyy-MM-dd HH:mm
     *
     * @param date
     * @return
     */
    public static String DateToString4(java.util.Date date) {
        String strTemp = "";
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm");
            strTemp = formatter.format(date);
        }
        return strTemp;
    }

    /**
     * date型转化为String 格式为MM-dd
     *
     * @param date
     * @return
     */
    public static String DateToStringDay(java.util.Date date) {
        String strTemp = "";
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
            strTemp = formatter.format(date);
        }
        return strTemp;
    }

    /**
     * date型转化为String 格式为hh:mm
     *
     * @param date
     * @param strDefault
     * @return
     */
    public static String DateToString2(java.util.Date date) {
        String strTemp = "";
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            strTemp = formatter.format(date);
        }
        return strTemp;
    }

    public static String DateToString2(java.sql.Time date) {
        String strTemp = "";
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            strTemp = formatter.format(date);
        }
        return strTemp;
    }

    /**
     * date型转化为String 格式为hh:mm:ss
     *
     * @param date
     * @param strDefault
     * @return
     */
    public static String DateToString3(java.util.Date date) {
        String strTemp = "";
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            strTemp = formatter.format(date);
        }
        return strTemp;
    }

    /**
     * date型转化为String 格式为yyyy-MM
     *
     * @param date
     * @param strDefault
     * @return
     */
    public static String DateToString3(java.sql.Date date, String strDefault) {
        String strTemp = strDefault;
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
            strTemp = formatter.format(date);
        }
        return strTemp;
    }

    public static String DateToString2(java.sql.Date date, String strDefault) {
        String strTemp = strDefault;
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            strTemp = formatter.format(date);
        }
        return strTemp;
    }

    public static String DateToString2(java.sql.Date date) {
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
            String strTemp = formatter.format(date);
            return strTemp;
        }
        return "";
    }

    public static String DateToString(java.sql.Date date) {
        return DateToString(date, null);
    }

    /**
     * String转化为Timestamp类型
     *
     * @param strDefault
     * @param date
     * @return
     */
    public static Timestamp StringToTimestamp(String strDate) {
        if (strDate != null && !strDate.equals("")) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date d = formatter.parse(strDate);
                Timestamp numTime = new Timestamp(d.getTime());
                return numTime;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * String转化为java.sql.date类型，
     *
     * @param strDate
     * @return
     */
    public static java.sql.Date StringToDate(String strDate) {
        if (strDate != null && !strDate.equals("")) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date d = formatter.parse(strDate);
                java.sql.Date numTime = new java.sql.Date(d.getTime());
                return numTime;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * String转化为java.sql.date类型，
     *
     * @param strDate
     * @return
     */
    public static java.sql.Date StringToDateLong(String strDate) {
        if (strDate != null && !strDate.equals("")) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                java.util.Date d = formatter.parse(strDate);
                java.sql.Date numTime = new java.sql.Date(d.getTime());
                return numTime;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * String转化为java.util.date类型，
     *
     * @param strDate
     * @return
     */
    public static java.util.Date StringToUtilDate(String strDate) {
        if (strDate != null && !strDate.equals("")) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                java.util.Date d = formatter.parse(strDate);
                return d;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * String("yyyy-MM-dd)转化为java.util.date类型，
     */
    public static java.util.Date StringToUtilDate2(String strDate) {
        if (strDate != null && !strDate.equals("")) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date d = formatter.parse(strDate);
                return d;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static java.util.Date StringToUtilDate(String strDate, String fmt) {
        if (strDate != null && !strDate.equals("")) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(fmt);
                java.util.Date d = formatter.parse(strDate);
                return d;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * String("yyyy-MM-dd)转化为java.util.date类型，
     */
    public static java.util.Date StringToUtilDate3(String strDate) {
        if (strDate != null && !strDate.equals("")) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
                java.util.Date d = formatter.parse(strDate);
                return d;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * String("hh:mm:ss)转化为java.util.date类型，
     *
     * @param strDate
     * @return
     */
    public static java.util.Date StringToUtilDate4(String strDate) {
        if (strDate != null && !strDate.equals("")) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                java.util.Date d = formatter.parse(strDate);
                return d;
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 得到上个月的日期
     *
     * @param date
     * @return
     */
    public static Date getPreDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        return new Date(calendar.getTime().getTime());
    }

    /**
     * 获取30天之前的毫秒数
     *
     * @return
     */
    public static long getThirtyDaysBefore() {
        long month = 30 * 24 * 60 * 60 * 1000l;
        return System.currentTimeMillis() - month;
    }

    /**
     * 获取前一天的日期
     *
     * @param date
     * @return
     */
    public static Date getPreDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return new Date(calendar.getTime().getTime());
    }

    /**
     * 得到下一个月的字符串
     *
     * @param strDate
     * @return
     * @throws ParseException
     */
    public static String getPreMonthStr(String strDate) {
        Date fromDate = StringToDate(strDate);
        Date toDate = getPreDate(fromDate);
        return DateToString(toDate);
    }

    /**
     * 得到当前日期的字符串
     *
     * @return
     */
    public static String getNowDateString() {
        return DateToString(getNowDate());
    }

    /**
     * 得到当前日期的字符串
     *
     * @return
     */
    public static String getNowDateBeginString() {
        return getNowDateString() + " 00:00:00";
    }

    public static String getYesterdayBeginString() {
        return DateToString(getPreDay(new Date())) + " 00:00:00";
    }

    public static String getNowDateEndString() {
        return getNowDateString() + " 23:59:59";
    }

    public static String getYesterdayEndString() {
        return DateToString(getPreDay(new Date())) + " 23:59:59";
    }

    public static long getLast7dayTime() {
        long time = StringToDateLong(getNowDateBeginString()).getTime();
        return (time - 6 * 24 * 60 * 60 * 1000);
    }

    public static String getEndString(java.util.Date date) {
        return DateToString(date) + " 23:59:59";
    }

    public static String getEndString2(java.util.Date date) {
        String strTemp = "";
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            strTemp = formatter.format(date);
        }
        return strTemp + " 23:59:59";
    }

    public static String getStartString(java.util.Date date) {
        return DateToString(date) + " 00:00:00";
    }

    public static Date getEndDate(java.util.Date date) {
        return StringToDateLong(DateToString(date) + " 23:59:59");
    }

    public static Date getStartDate(java.util.Date date) {
        return StringToDateLong(DateToString(date) + " 00:00:00");
    }

    /**
     * 得到日期在月份的第几周
     *
     * @param date
     * @return
     */
    public static int getWeekOfMonth(String strDate) {
        Date date = StringToDate(strDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);

        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * 传入日期加1
     *
     * @param date
     * @return (date + 1)
     */
    public static String getQueryDate(String date) {
        java.util.Date d = StringToDate(date);
        return formatDate(getDateAfter(d, 1));
    }

    /**
     * 给指定日期加减天数，返回结果日期
     *
     * @param date
     * @param day
     * @return
     */
    public static java.util.Date getDateAfter(java.util.Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, day);
        java.util.Date newdate = calendar.getTime();
        return newdate;
    }

    /**
     * 给指定日期加减天数，返回结果日期
     *
     * @param date
     * @param day
     * @return
     */
    public static java.util.Date getDateAfterOrBefor(java.util.Date date,
                                                     int day, String timeStr) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, day);
        java.util.Date newdate = calendar.getTime();
        String dateStr = DateToString(newdate) + timeStr;
        return StringToUtilDate(dateStr);
    }

    /**
     * 给指定日期加减分钟，返回结果日期
     *
     * @param date
     * @param day
     * @return
     */
    public static java.util.Date getDateAfterMin(java.util.Date date, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, min);
        java.util.Date newdate = calendar.getTime();
        return newdate;
    }

    /**
     * 将字符串时间转换为java.util.date时间格式(字符串格式：yyyy-MM-dd hh:mm:ss)
     *
     * @param str
     * @return
     */
    public static java.util.Date getDateByLongFormat(String str) {
        if (str == null || str.length() < 1)
            return null;
        java.util.Date result = null;
        try {
            result = DateUtils.parseDate(str, TIME_FORMATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 返回当前小时数(24小时)
     *
     * @return
     */
    public static int getHour() {
        Calendar calendars = Calendar.getInstance();
        return calendars.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 返回当前小时数(24小时)
     *
     * @return
     */
    public static int getMinute() {
        Calendar calendars = Calendar.getInstance();
        return calendars.get(Calendar.MINUTE);
    }

    /**
     * 获得昨天的日期
     */
    public static String getYesterday() {
        java.util.Date d = new java.util.Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    /**
     * 获得昨天的月份
     */
    public static String getYesterdayMonth() {
        java.util.Date d = new java.util.Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(calendar.getTime());
    }

    /**
     * 获得一个员工在公司的完整年数
     *
     * @param date
     * @return
     */
    public static int getYearCount(java.util.Date date, java.util.Date date2) {
        int count = 0;
        // 获得员工在公司的完整年数
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        int year = Integer.parseInt(sdf.format(date));
        int cyear = Integer.parseInt(sdf.format(date2));
        count = cyear - year - 1;
        count = count < 0 ? 0 : count;
        return count;
    }

    public static String getChineseDateStr2(String strdate) {
        String datearr[] = strdate.split("-");
        if (datearr[1].startsWith("0"))
            datearr[1] = datearr[1].substring(1, datearr[1].length());
        String s = datearr[1] + "月" + datearr[2] + "日 ";
        return s;
    }

    public static String getChineseDateStr3(String strdate) {
        String datearr[] = strdate.split("-");
        String s = datearr[0] + "年" + datearr[1] + "月" + datearr[2] + "日 ";
        return s;
    }

    /**
     * 获得年
     */
    public static String getYear(Date date) {
        String strdate = DateToString(date);
        String datearr[] = strdate.split("-");
        return datearr[0];
    }

    /**
     * 获得月
     */
    public static String getMonth(Date date) {
        String strdate = DateToString(date);
        String datearr[] = strdate.split("-");
        return datearr[1];
    }

    /**
     * 获得天
     */
    public static String getDay(Date date) {
        String strdate = DateToString(date);
        String datearr[] = strdate.split("-");
        return datearr[2];
    }

    /**
     * 得到当前日期年月日字符串 result[0]:year result[1]:month result[2]:day
     */
    public static String[] getYearMonthDay() {
        String[] result = new String[3];
        Calendar c = Calendar.getInstance();
        c.setTime(new java.util.Date());
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (month < 10) {
            result[1] = String.valueOf("0" + month);
        } else {
            result[1] = String.valueOf(month);
        }
        if (day < 10) {
            result[2] = String.valueOf("0" + day);
        } else {
            result[2] = String.valueOf(day);
        }
        result[0] = String.valueOf(c.get(Calendar.YEAR));
        return result;
    }

    /**
     * 返回10位的int型当前时间
     *
     * @return
     */
    public static int getCurrTime() {
        long nowTime = System.currentTimeMillis();
        String nowTimeStr = String.valueOf(nowTime).substring(0, 10);
        return Integer.parseInt(nowTimeStr);
    }

    /**
     * 根据date获得邮件显示用准确时间
     *
     * @param date 源date
     * @return 邮件显示用准确时间
     */
    public static String getMailDate(java.util.Date date) {
        String mailDateStr = formatDate(date);
        String curDateStr = getNowDateString();
        if (curDateStr.equals(mailDateStr)) {
            // 当天的邮件，只显示小时和分钟
            return new SimpleDateFormat("HH:mm").format(date);
        } else {
            // 不是当天的邮件，则只显示年、月、日
            return mailDateStr;
        }
    }

    public static String convertTimeToStr(int time) {
        return time < 10 ? "0" + time : time + "";
    }

    /**
     * 转换毫秒数成“分、秒”，如“01:53”。若超过60分钟则显示“时、分、秒”，如“01:01:30
     *
     * @param 待转换的毫秒数
     */
    public static String converLongTimeToStr(long startTime, long endTime) {
        long time = endTime - startTime;
        if (startTime == 0 || endTime == 0) {
            time = 0;
        }
        if (time <= 0) {
            time = 0;
        }
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;

        long hour = (time) / hh;
        long minute = (time - hour * hh) / mi;
        long second = (time - hour * hh - minute * mi) / ss;

        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        if (hour > 0) {
            return strHour + ":" + strMinute + ":" + strSecond;
        } else {
            return strMinute + ":" + strSecond;
        }
    }

    /**
     * 语音通话时间转换函数
     * <p>
     * 转换毫秒数成“分、秒”，如“01:53”
     * <p>
     * 当通话时间大于1分钟时，显示格式为：“XX分XX秒”，如通话2小时则显示120分00秒；<br>
     * 当通话时间小于或等于1分钟时，显示格式为：“XX秒”。
     *
     * @param 待转换的毫秒数
     */
    public static String converLongTimeToStrForCall(long startTime, long endTime) {
        long time = endTime - startTime;
        if (startTime == 0 || endTime == 0) {
            time = 0;
        }
        if (time <= 0) {
            time = 0;
        }
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;

        long hour = (time) / hh;
        long minute = (time - hour * hh) / mi;
        long second = (time - hour * hh - minute * mi) / ss;

        @SuppressWarnings("unused")
        // String strHour = hour < 10 ? "0" + hour : "" + hour;
                // String strMinute = minute < 10 ? "0" + minute : "" + minute;
                // String strSecond = second < 10 ? "0" + second : "" + second;
                String strHour = String.valueOf(hour);
        String strMinute = String.valueOf(minute);
        String strSecond = String.valueOf(second);
        if (hour > 0) {
            return (hour * 60 + minute) + "分" + strSecond + "秒";
        } else if (minute > 0) {
            return strMinute + "分" + strSecond + "秒";
        } else {
            return strSecond + "秒";
        }
    }

    /**
     * 视频通话时间转换函数
     * <p>
     * 转换毫秒数成“分、秒”，如“01:53”
     * <p>
     * 当通话时间大于1分钟时，显示格式为：“XX:XX”，如通话2小时则显示120:00；<br>
     * 当通话时间小于或等于1分钟时，显示格式为：“00:XX”。
     *
     * @param 待转换的毫秒数
     */
    public static String converLongTimeToStrForInCall(long startTime,
                                                      long endTime) {
        long time = endTime - startTime;
        if (startTime == 0 || endTime == 0) {
            time = 0;
        }
        if (time <= 0) {
            time = 0;
        }
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;

        long hour = (time) / hh;
        long minute = (time - hour * hh) / mi;
        long second = (time - hour * hh - minute * mi) / ss;

        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        // String strHour = String.valueOf(hour);
        // String strMinute = String.valueOf(minute);
        // String strSecond = String.valueOf(second);
        if (hour > 0) {
            return strHour + ":" + strMinute + ":" + strSecond;
        } else if (minute > 0) {
            return strMinute + ":" + strSecond;
        } else {
            return "00" + ":" + strSecond;
        }
    }

    // public static boolean isValidLong(String str) {
    // try {
    // long _v = Long.parseLong(str);
    // return true;
    // } catch (NumberFormatException e) {
    // return false;
    // }
    // }

    /**
     * 判断是否在时间 范围内
     *
     * @param startTimeHour   起始时间小时
     * @param startTimeMinute 起始时间分钟
     * @param endTimeHour     结束时间小时
     * @param endTimeMinute   结束时间分钟
     * @return 是否在时间范围内
     */
    public static boolean isBetweenDate(int startTimeHour, int startTimeMinute,
                                        int endTimeHour, int endTimeMinute) {
        Calendar cal = Calendar.getInstance();// 当前日期
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
        int minute = cal.get(Calendar.MINUTE);// 获取分钟
        int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
        final int start = startTimeHour * 60 + startTimeMinute;// 起始时间 的分钟数
        final int end = endTimeHour * 60 + endTimeMinute;// 结束时间的分钟数
        if (minuteOfDay >= start && minuteOfDay <= end) {
            // Log.e("isBetweenDate()", "在时间 范围内");
            return true;
        } else {
            // Log.e("isBetweenDate()", "在时间 范围外");
            return false;
        }
    }

    /**
     * 给指定日期加减天数，返回结果日期
     *
     * @param date
     * @param day
     * @return
     */
    public static java.util.Date getDateAfterOrBefor2(java.util.Date date,
                                                      int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, day);
        java.util.Date newdate = calendar.getTime();
        return newdate;
    }

    /**
     * 给指定日期加减时、分，返回结果日期
     *
     * @param date
     * @param day
     * @return
     */
    public static java.util.Date getDateUpdateHourAndMin(java.util.Date date,
                                                         int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        calendar.add(Calendar.MINUTE, min);
        java.util.Date newdate = calendar.getTime();
        return newdate;
    }

    /**
     * 转换毫秒数成“分、秒”，如“01分53秒”。若超过60分钟则显示“时、分、秒”，如“01小时01分30秒
     *
     * @param 待转换的毫秒数
     */
    public static String converLongTimeToStr2(long startTime, long endTime) {
        long time = endTime - startTime;
        if (startTime == 0 || endTime == 0) {
            time = 0;
        }
        if (time <= 0) {
            time = 0;
        }
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;

        long hour = (time) / hh;
        long minute = (time - hour * hh) / mi;
        long second = (time - hour * hh - minute * mi) / ss;

        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        if (hour > 0) {
            return strHour + "小时" + strMinute + "分" + strSecond + "秒";
        } else {
            return strMinute + "分" + strSecond + "秒";
        }
    }

    public static long getTimebyString(String time) {

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMATE);
        Date date;
        try {
            date = format.parse(time);

            return date.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static String long2Date(long time) {

        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMATE);
        Date date;
        date = new Date(time);
        return format.format(date);
    }

    /**
     * 获取时间段，存入数组
     * 格式：yyyy-MM-dd
     *
     * @param startTimeDate 开始时间
     * @param endTimeDate   结束时间
     * @return List<String>
     */
    public static List<String> getTimeArray(Date startTimeDate, Date endTimeDate) {

        List<String> times = new ArrayList<>();
        Calendar start = Calendar.getInstance();
        start.setTime(startTimeDate);
        Long startTime = start.getTimeInMillis();

        Calendar end = Calendar.getInstance();
        end.setTime(endTimeDate);
        Long endTime = end.getTimeInMillis();

        Long oneDay = 1000 * 60 * 60 * 24l;

        Long time = startTime;
        while (time <= endTime) {
            Date d = new Date(time);
            DateFormat df = new SimpleDateFormat(DATE_FORMATE);
            times.add(df.format(d));
            time += oneDay;
        }
        return times;
    }

    /**
     * 时间区间内随机一个时间
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static Date randomDate(Date beginDate, Date endDate) {
        try {
            if (beginDate.getTime() >= endDate.getTime()) {
                return null;
            }
            long date = random(beginDate.getTime(), endDate.getTime());
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long random(long begin, long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }




    public static void main(String args[])throws Exception{
        //输出月份的最后一天  2021-10-31
//        System.out.println(getMonthEnd("2021-10-7"));
//        System.out.println(getMonthEnd(DateUtils.parseDate("2021-10-3", DATE_FORMATE)));
        //输出月份的第一天  2021-10-01
//        System.out.println(getMonthBegin("2021-10-7"));
//        System.out.println(getMonthBegin(DateUtils.parseDate("2021-10-3", DATE_FORMATE)));
        //输出日期的最晚时间 Thu Oct 07 23:59:59 CST 2021
//        System.out.println(getEndDateTimeFromStr("2021-10-7"));
        //输出日期的最早时间 Thu Oct 07 00:00:00 CST 2021
//        System.out.println(getStartDateTimeFromStr("2021-10-7"));
        //输出月份天数 28
//        System.out.println(getMonthDaynum("2021-2-7"));
        //输出日期的格式化 2022-02-10
//        System.out.println(formatDate(new Date()));
        //是否日期是否是过去 true
//        System.out.println(isPastDay(DateUtils.parseDate("2021-10-3", DATE_FORMATE).getTime()));
        //是否日期是否是同一天 false
//        System.out.println(isTheSameDay(DateUtils.parseDate("2022-1-10", DATE_FORMATE).getTime(),new Date().getTime()));
        //是否日期是否是同一年 false
        System.out.println(isTheSameYear(DateUtils.parseDate("2021-1-10", DATE_FORMATE).getTime(),new Date().getTime()));
    }
}
