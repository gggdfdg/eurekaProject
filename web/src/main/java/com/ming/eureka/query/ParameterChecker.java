package com.ming.eureka.query;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class ParameterChecker {

    /**
     * 判断是否含有汉字
     *
     * @param string
     */
    public static boolean containChinese(String string) {
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        return pattern.matcher(string).find();
    }

    /**
     * 判断是否含有空格
     */
    public static boolean containBlank(String string) {
        Pattern pattern = Pattern.compile("[\\s]");
        return pattern.matcher(string).find();
    }

    /**
     * 判断是否为电话号码或手机号码
     */
    public static boolean isPhone(String string) {
        return ParameterChecker.isMobile(string) || ParameterChecker.isTelephone(string);
    }

    /**
     * 判断是否为手机号码
     */
    public static boolean isMobile(String string) {
        Pattern pattern = Pattern.compile("1[3,4,5,7,8]\\d{9}");
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是否为固定电话
     */
    public static boolean isTelephone(String string) {
        Pattern pattern = Pattern.compile("^((\\+86)|(86))?1[3,4,5,7,8]\\d{9}$");
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是否为邮箱
     */
    public static boolean isEmail(String string) {
        Pattern pattern = Pattern.compile("[&~#$*%\\u4e00-\\u9fa5_0-9a-z\\-\\.\\/\\\\]+@([\\u4e00-\\u9fa5-a-z0-9]+\\.){1,5}[\\u4e00-\\u9fa5a-z]+", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是否为链接地址
     */
    public static boolean isUrl(String string) {
        Pattern pattern = Pattern.compile("(((http|https|ftp):\\/\\/)?([\\w\\u4e00-\\u9fa5\\-]+\\.)+[\\w\\u4e00-\\u9fa5\\-]+(:\\d+)?(\\/[\\w\\u4e00-\\u9fa5\\-\\.\\/?\\@\\%\\!\\&=\\+\\~\\:\\#\\;\\,]*)?)", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(string).matches();
    }

    /**
     * 判断连接地址是否加协议
     */
    public static boolean startWithProtocol(String string) {
        Pattern pattern = Pattern.compile("(http|https|ftp):\\/\\/.*", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是否为域名
     */
    public static boolean isDomain(String string) {
        Pattern pattern = Pattern.compile("^([\\x{4e00}-\\x{9fa5}-a-z0-9]+\\.){1,5}[\\x{4e00}-\\x{9fa5}a-z]+$", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(string).matches();
    }

    /**
     * 判断字符串为null or 空
     */
    public static boolean isNullOrEmpty(String string) {
        return StringUtils.isNotBlank(string);
    }

    /**
     * 检测密码强度
     */
    public static int checkStrength(String string) {
        int strength = 0;

        Pattern pattern = Pattern.compile("[a-z]+", Pattern.CASE_INSENSITIVE);
        if (pattern.matcher(string).find()) {
            strength++;
        }
        pattern = Pattern.compile("[0-9]+", Pattern.CASE_INSENSITIVE);
        if (pattern.matcher(string).find()) {
            strength++;
        }
        pattern = Pattern.compile("[\\/,.~!@#$%^&*()\\[\\]_+\\-=\\:\";'\\{\\}\\|\\\\><\\?]+", Pattern.CASE_INSENSITIVE);
        if (pattern.matcher(string).find()) {
            strength++;
        }
        return strength;
    }

    /**
     * 判断是否为数字
     */
    public static boolean isNumber(String string) {
        Pattern pattern = Pattern.compile("^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$", Pattern.CASE_INSENSITIVE);
        return "0".equals(string) || pattern.matcher(string).matches();
    }

    /**
     * 判断是否为营业时间（小时：分钟）
     */
    public static boolean isBusinessHours(String string) {
        Pattern pattern = Pattern.compile("^\\d{2}\\:\\d{2}$");
        if (pattern.matcher(string).matches()) {
            String[] array = string.split(":");
            if (Integer.valueOf(array[0]) <= 24 && Integer.valueOf(array[1]) <= 60) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否金额
     */
    public static boolean isMoney(String string) {
        Pattern pattern = Pattern.compile("^(0|[1-9]\\d*)(\\.\\d{1,2})+$");
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是否是否为使用密码（数字和字母组成）
     */
    public static boolean isUsePassword(String string) {
        Pattern pattern = Pattern.compile("^\\w*$");
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是否为中文开头
     */
    public static boolean initialIsChinese(String string) {
        Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5]");
        return pattern.matcher(string).find();
    }

    /**
     * 检测密码
     */
    public static boolean checkPassword(String password, int minLength, int maxLength) {
        if (isNullOrEmpty(password)) {
            return false;
        }
        if (containChinese(password)) {
            return false;
        }
        if (containBlank(password)) {
            return false;
        }
        if (minLength > 0 && password.length() < minLength) {
            return false;
        }
        if (maxLength > 0 && password.length() > maxLength) {
            return false;
        }
        return true;

    }

    /**
     * 检测密码
     */
    public static boolean checkPassword(String password) {
        return checkPassword(password, 0, 0);
    }

    /**
     * 判断字符串是否为时间格式
     */
    public static boolean isDate(String string) {
        Pattern pattern = Pattern.compile("^([1-9]\\d{3})-([0-1]\\d)-([0-3]\\d)$", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(string).matches();
    }

    /**
     * 判断字符串是否为年月格式
     */
    public static boolean isYearMonth(String string) {
        Pattern pattern = Pattern.compile("^([1-9]\\d{3})-([0-1]\\d)$", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(string).matches();
    }

    /**
     * 判断字符串是否为年格式
     */
    public static boolean isYear(String string) {
        Pattern pattern = Pattern.compile("^([1-9]\\d{3})$", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是非为 0 或 1
     */
    public static boolean isBooleanNumber(String string) {
        return "0".equals(string) || "1".equals(string);
    }
}
