package com.mes.admin.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 日期时间工具类
 * <p>
 * 功能包括：
 * - 日期时间格式化与解析
 * - 日期时间计算（加减、相差天数/小时数等）
 * - 获取日期时间戳
 * - 判断日期合法性
 * - 常用日期格式常量
 * </p>
 *
 * @author MES Admin
 */
public class DateTimeUtil {

    // ==================== 常用日期格式常量 ====================

    /** 标准日期格式：yyyy-MM-dd */
    public static final String PATTERN_DATE = "yyyy-MM-dd";

    /** 标准时间格式：HH:mm:ss */
    public static final String PATTERN_TIME = "HH:mm:ss";

    /** 标准日期时间格式：yyyy-MM-dd HH:mm:ss */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /** 紧凑日期格式：yyyyMMdd */
    public static final String PATTERN_DATE_COMPACT = "yyyyMMdd";

    /** 紧凑时间格式：HHmmss */
    public static final String PATTERN_TIME_COMPACT = "HHmmss";

    /** 紧凑日期时间格式：yyyyMMddHHmmss */
    public static final String PATTERN_DATETIME_COMPACT = "yyyyMMddHHmmss";

    /** ISO 日期格式：yyyy-MM-dd'T'HH:mm:ss */
    public static final String PATTERN_ISO = "yyyy-MM-dd'T'HH:mm:ss";

    /** 中文日期格式：yyyy年MM月dd日 */
    public static final String PATTERN_DATE_CN = "yyyy年MM月dd日";

    /** 中文日期时间格式：yyyy年MM月dd日 HH时mm分ss秒 */
    public static final String PATTERN_DATETIME_CN = "yyyy年MM月dd日 HH时mm分ss秒";

    /** 时间戳格式（毫秒）：yyyyMMddHHmmssSSS */
    public static final String PATTERN_TIMESTAMP = "yyyyMMddHHmmssSSS";

    // ==================== Formatter 实例（线程安全） ====================

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATE);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATETIME);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_TIMESTAMP);

    // ==================== 格式化（Date -> String） ====================

    /**
     * 格式化日期为字符串
     *
     * @param date    日期
     * @param pattern 格式模式
     * @return 格式化后的字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) return null;
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 格式化日期（默认格式 yyyy-MM-dd）
     */
    public static String formatDate(Date date) {
        return format(date, PATTERN_DATE);
    }

    /**
     * 格式化日期时间（默认格式 yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return format(date, PATTERN_DATETIME);
    }

    /**
     * 格式化日期为紧凑格式（yyyyMMdd）
     */
    public static String formatDateCompact(Date date) {
        return format(date, PATTERN_DATE_COMPACT);
    }

    /**
     * 格式化日期时间为紧凑格式（yyyyMMddHHmmss）
     */
    public static String formatDateTimeCompact(Date date) {
        return format(date, PATTERN_DATETIME_COMPACT);
    }

    /**
     * 格式化时间戳（yyyyMMddHHmmssSSS）
     */
    public static String formatTimestamp(Date date) {
        return format(date, PATTERN_TIMESTAMP);
    }

    /**
     * 格式化日期为中文格式
     */
    public static String formatDateCN(Date date) {
        return format(date, PATTERN_DATE_CN);
    }

    /**
     * 格式化日期时间为中文格式
     */
    public static String formatDateTimeCN(Date date) {
        return format(date, PATTERN_DATETIME_CN);
    }

    // ==================== 解析（String -> Date） ====================

    /**
     * 解析字符串为日期
     *
     * @param str     日期字符串
     * @param pattern 格式模式
     * @return 日期对象
     */
    public static Date parse(String str, String pattern) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            return new SimpleDateFormat(pattern).parse(str);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期解析失败: " + str + ", 期望格式: " + pattern, e);
        }
    }

    /**
     * 解析标准日期格式（yyyy-MM-dd）
     */
    public static Date parseDate(String str) {
        return parse(str, PATTERN_DATE);
    }

    /**
     * 解析标准日期时间格式（yyyy-MM-dd HH:mm:ss）
     */
    public static Date parseDateTime(String str) {
        return parse(str, PATTERN_DATETIME);
    }

    /**
     * 解析紧凑日期格式（yyyyMMdd）
     */
    public static Date parseDateCompact(String str) {
        return parse(str, PATTERN_DATE_COMPACT);
    }

    /**
     * 解析紧凑日期时间格式（yyyyMMddHHmmss）
     */
    public static Date parseDateTimeCompact(String str) {
        return parse(str, PATTERN_DATETIME_COMPACT);
    }

    /**
     * 解析时间戳格式（yyyyMMddHHmmssSSS）
     */
    public static Date parseTimestamp(String str) {
        return parse(str, PATTERN_TIMESTAMP);
    }

    /**
     * 解析 ISO 格式（yyyy-MM-dd'T'HH:mm:ss）
     */
    public static Date parseIso(String str) {
        return parse(str, PATTERN_ISO);
    }

    // ==================== LocalDateTime 转换 ====================

    /**
     * Date 转 LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * LocalDateTime 转 Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDate 转 Date
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime 转 String
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null) return null;
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * String 转 LocalDateTime
     */
    public static LocalDateTime parseLocalDateTime(String str, String pattern) {
        if (str == null || str.trim().isEmpty()) return null;
        return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern));
    }

    // ==================== 日期计算 ====================

    /**
     * 日期加减天数
     *
     * @param date   基准日期
     * @param days   加减天数（正数加，负数减）
     * @return 结果日期
     */
    public static Date addDays(Date date, int days) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     * 日期加减小时
     */
    public static Date addHours(Date date, int hours) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    /**
     * 日期加减分钟
     */
    public static Date addMinutes(Date date, int minutes) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    /**
     * 日期加减秒
     */
    public static Date addSeconds(Date date, int seconds) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    /**
     * 日期加减月
     */
    public static Date addMonths(Date date, int months) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    /**
     * 日期加减年
     */
    public static Date addYears(Date date, int years) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }

    // ==================== 日期差计算 ====================

    /**
     * 计算两个日期相差天数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 相差天数（endDate - startDate）
     */
    public static long diffDays(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return 0;
        long diff = endDate.getTime() - startDate.getTime();
        return diff / (24 * 60 * 60 * 1000);
    }

    /**
     * 计算两个日期相差小时数
     */
    public static long diffHours(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return 0;
        long diff = endDate.getTime() - startDate.getTime();
        return diff / (60 * 60 * 1000);
    }

    /**
     * 计算两个日期相差分钟数
     */
    public static long diffMinutes(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return 0;
        long diff = endDate.getTime() - startDate.getTime();
        return diff / (60 * 1000);
    }

    /**
     * 计算两个日期相差秒数
     */
    public static long diffSeconds(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return 0;
        return (endDate.getTime() - startDate.getTime()) / 1000;
    }

    /**
     * 计算两个日期相差月数
     */
    public static int diffMonths(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return 0;
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        int yearDiff = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
        int monthDiff = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
        return yearDiff * 12 + monthDiff;
    }

    // ==================== 获取日期分量 ====================

    /**
     * 获取年份
     */
    public static int getYear(Date date) {
        if (date == null) return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取月份（1-12）
     */
    public static int getMonth(Date date) {
        if (date == null) return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取日期（1-31）
     */
    public static int getDay(Date date) {
        if (date == null) return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取小时（0-23）
     */
    public static int getHour(Date date) {
        if (date == null) return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取分钟（0-59）
     */
    public static int getMinute(Date date) {
        if (date == null) return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取秒（0-59）
     */
    public static int getSecond(Date date) {
        if (date == null) return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 获取星期几（1=星期日, 2=星期一, ... 7=星期六）
     */
    public static int getDayOfWeek(Date date) {
        if (date == null) return 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取星期几的中文名称
     */
    public static String getDayOfWeekCN(Date date) {
        if (date == null) return null;
        String[] weekDays = {"", "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        return weekDays[getDayOfWeek(date)];
    }

    // ==================== 判断方法 ====================

    /**
     * 判断是否为今天
     */
    public static boolean isToday(Date date) {
        if (date == null) return false;
        String today = formatDate(new Date());
        String target = formatDate(date);
        return today.equals(target);
    }

    /**
     * 判断是否为昨天
     */
    public static boolean isYesterday(Date date) {
        if (date == null) return false;
        Date yesterday = addDays(new Date(), -1);
        return formatDate(yesterday).equals(formatDate(date));
    }

    /**
     * 判断是否为明天
     */
    public static boolean isTomorrow(Date date) {
        if (date == null) return false;
        Date tomorrow = addDays(new Date(), 1);
        return formatDate(tomorrow).equals(formatDate(date));
    }

    /**
     * 判断日期是否在范围内
     */
    public static boolean isBetween(Date date, Date startDate, Date endDate) {
        if (date == null || startDate == null || endDate == null) return false;
        return !date.before(startDate) && !date.after(endDate);
    }

    /**
     * 判断是否为周末
     */
    public static boolean isWeekend(Date date) {
        int dayOfWeek = getDayOfWeek(date);
        return dayOfWeek == 1 || dayOfWeek == 7;
    }

    /**
     * 判断闰年
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    // ==================== 快捷获取 ====================

    /**
     * 获取今天的日期（不含时间）
     */
    public static Date getToday() {
        return parseDate(formatDate(new Date()));
    }

    /**
     * 获取今天的开始时间（00:00:00）
     */
    public static Date getTodayStart() {
        return parseDateTime(formatDate(new Date()) + " 00:00:00");
    }

    /**
     * 获取今天的结束时间（23:59:59）
     */
    public static Date getTodayEnd() {
        return parseDateTime(formatDate(new Date()) + " 23:59:59");
    }

    /**
     * 获取月初第一天
     */
    public static Date getMonthFirst() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getTodayStart(calendar.getTime());
    }

    /**
     * 获取月末最后一天
     */
    public static Date getMonthLast() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getTodayEnd(calendar.getTime());
    }

    /**
     * 获取指定日期的开始时间
     */
    public static Date getTodayStart(Date date) {
        String dateStr = formatDate(date);
        return parseDateTime(dateStr + " 00:00:00");
    }

    /**
     * 获取指定日期的结束时间
     */
    public static Date getTodayEnd(Date date) {
        String dateStr = formatDate(date);
        return parseDateTime(dateStr + " 23:59:59");
    }

    /**
     * 获取月初第一天
     */
    public static Date getMonthFirst(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getTodayStart(calendar.getTime());
    }

    /**
     * 获取月末最后一天
     */
    public static Date getMonthLast(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getTodayEnd(calendar.getTime());
    }

    // ==================== 时间戳相关 ====================

    /**
     * 获取当前时间戳（秒）
     */
    public static long getTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    public static long getTimestampMs() {
        return System.currentTimeMillis();
    }

    /**
     * 时间戳转 Date
     */
    public static Date fromTimestamp(long timestamp) {
        return new Date(timestamp * 1000);
    }

    /**
     * 毫秒时间戳转 Date
     */
    public static Date fromTimestampMs(long timestampMs) {
        return new Date(timestampMs);
    }

    /**
     * Date 转时间戳（秒）
     */
    public static long toTimestamp(Date date) {
        return date == null ? 0 : date.getTime() / 1000;
    }

    /**
     * Date 转毫秒时间戳
     */
    public static long toTimestampMs(Date date) {
        return date == null ? 0 : date.getTime();
    }

    // ==================== 实用工具方法 ====================

    /**
     * 判断日期是否有效
     */
    public static boolean isValidDate(String str, String pattern) {
        try {
            parse(str, pattern);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取年龄（根据出生日期）
     */
    public static int getAge(Date birthDate) {
        if (birthDate == null) return 0;
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthDate);
        Calendar now = Calendar.getInstance();
        int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (now.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    /**
     * 计算两个日期之间的工作日天数（排除周末）
     */
    public static long diffWorkDays(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) return 0;
        long days = diffDays(startDate, endDate);
        long workDays = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        for (int i = 0; i <= days; i++) {
            if (!isWeekend(calendar.getTime())) {
                workDays++;
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return workDays;
    }

    /**
     * 转换为 ZoneDateTime（UTC）
     */
    public static ZonedDateTime toUtc(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneOffset.UTC);
    }

    /**
     * 格式化 UTC 时间
     */
    public static String formatUtc(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * 获取当前日期的 LocalDate
     */
    public static LocalDate getLocalDate() {
        return LocalDate.now();
    }

    /**
     * 获取当前日期的 LocalDateTime
     */
    public static LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前时间格式化字符串
     */
    public static String getCurrentDateTime() {
        return formatDateTime(new Date());
    }

    /**
     * 获取当前日期格式化字符串
     */
    public static String getCurrentDate() {
        return formatDate(new Date());
    }
}
