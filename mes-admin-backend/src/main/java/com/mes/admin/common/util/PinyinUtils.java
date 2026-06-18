package com.mes.admin.common.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.regex.Pattern;

/**
 * 汉字转拼音工具类
 * <p>
 * 功能包括：
 * - 中文转全拼
 * - 中文转首字母
 * - 中文转拼音缩写
 * - 多音字处理
 * </p>
 *
 * @author MES Admin
 */
public class PinyinUtils {

    // ==================== 配置 ====================

    private static final HanyuPinyinOutputFormat FORMAT = new HanyuPinyinOutputFormat();

    static {
        // 设置小写
        FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 设置无声调
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        // 设置 ü 的表示方式为 "ü"
        FORMAT.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
    }

    // 中文字符正则
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+");

    // ==================== 获取全拼 ====================

    /**
     * 获取汉字的全拼
     *
     * @param chinese 汉字字符串
     * @return 拼音字符串（非汉字字符保持原样）
     */
    public static String getPinyin(String chinese) {
        if (StringUtil.isEmpty(chinese)) {
            return chinese;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < chinese.length(); i++) {
            char c = chinese.charAt(i);
            if (isChineseChar(c)) {
                try {
                    String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
                    if (pinyins != null && pinyins.length > 0) {
                        // 取第一个拼音（默认多音字第一个读音）
                        result.append(pinyins[0]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 获取汉字的全拼（带分隔符）
     *
     * @param chinese   汉字字符串
     * @param separator 分隔符
     * @return 带分隔符的拼音字符串
     */
    public static String getPinyin(String chinese, String separator) {
        if (StringUtil.isEmpty(chinese)) {
            return chinese;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < chinese.length(); i++) {
            char c = chinese.charAt(i);
            if (isChineseChar(c)) {
                try {
                    String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
                    if (pinyins != null && pinyins.length > 0) {
                        if (result.length() > 0 && !isChineseChar(chinese.charAt(i - 1))) {
                            result.append(separator);
                        } else if (result.length() > 0) {
                            result.append(separator);
                        }
                        result.append(pinyins[0]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    result.append(c);
                }
            } else {
                if (result.length() > 0 && !Character.isWhitespace(result.charAt(result.length() - 1))) {
                    result.append(separator);
                }
                result.append(c);
            }
        }
        return result.toString();
    }

    // ==================== 获取首字母 ====================

    /**
     * 获取汉字的首字母
     *
     * @param chinese 汉字字符串
     * @return 首字母字符串（如 "中华人民共和国" -> "zhrgehg"）
     */
    public static String getPinyinInitials(String chinese) {
        if (StringUtil.isEmpty(chinese)) {
            return chinese;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < chinese.length(); i++) {
            char c = chinese.charAt(i);
            if (isChineseChar(c)) {
                try {
                    String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
                    if (pinyins != null && pinyins.length > 0) {
                        result.append(pinyins[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    // 忽略
                }
            } else if (!Character.isWhitespace(c)) {
                // 非汉字非空白字符直接添加
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 获取汉字的首字母（大写）
     */
    public static String getPinyinInitialsUpper(String chinese) {
        return getPinyinInitials(chinese).toUpperCase();
    }

    /**
     * 获取汉字的首字母（按空格分组）
     *
     * @param chinese 汉字字符串
     * @param separator 分隔符
     * @return 分组的首字母字符串
     */
    public static String getPinyinInitials(String chinese, String separator) {
        if (StringUtil.isEmpty(chinese)) {
            return chinese;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < chinese.length(); i++) {
            char c = chinese.charAt(i);
            if (isChineseChar(c)) {
                try {
                    String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
                    if (pinyins != null && pinyins.length > 0) {
                        result.append(pinyins[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    // 忽略
                }
            } else if (Character.isWhitespace(c)) {
                // 遇到空格或分隔符，重置
                if (result.length() > 0) {
                    result.append(separator);
                }
            }
        }
        return result.toString();
    }

    // ==================== 获取简拼（拼音缩写） ====================

    /**
     * 获取汉字的简拼（每个字拼音的首字母）
     *
     * @param chinese 汉字字符串
     * @return 简拼字符串（如 "中华人民共和国" -> "ZHRGHG"）
     */
    public static String getPinyinAbbreviation(String chinese) {
        return getPinyinInitialsUpper(chinese);
    }

    // ==================== 提取中文 ====================

    /**
     * 从字符串中提取所有中文
     */
    public static String extractChinese(String str) {
        if (StringUtil.isEmpty(str)) {
            return str;
        }
        Matcher matcher = CHINESE_PATTERN.matcher(str);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            result.append(matcher.group());
        }
        return result.toString();
    }

    // ==================== 判断方法 ====================

    /**
     * 判断字符是否为汉字
     */
    public static boolean isChineseChar(char c) {
        return c >= 0x4e00 && c <= 0x9fa5;
    }

    /**
     * 判断字符串是否包含汉字
     */
    public static boolean containsChinese(String str) {
        if (StringUtil.isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (isChineseChar(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否全为汉字
     */
    public static boolean isAllChinese(String str) {
        if (StringUtil.isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!isChineseChar(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // ==================== 多音字处理 ====================

    /**
     * 获取多音字的所有读音
     */
    public static String[] getMultiplePinyins(char chinese) {
        if (!isChineseChar(chinese)) {
            return new String[]{String.valueOf(chinese)};
        }
        try {
            return PinyinHelper.toHanyuPinyinStringArray(chinese, FORMAT);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            return new String[]{String.valueOf(chinese)};
        }
    }

    /**
     * 获取多音字的所有读音（带声调）
     */
    public static String[] getMultiplePinyinsWithTone(char chinese) {
        if (!isChineseChar(chinese)) {
            return new String[]{String.valueOf(chinese)};
        }
        HanyuPinyinOutputFormat formatWithTone = new HanyuPinyinOutputFormat();
        formatWithTone.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        formatWithTone.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
        formatWithTone.setVCharType(HanyuPinyinVCharType.WITH_V);
        try {
            return PinyinHelper.toHanyuPinyinStringArray(chinese, formatWithTone);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            return new String[]{String.valueOf(chinese)};
        }
    }

    // ==================== 实用方法 ====================

    /**
     * 生成拼音索引
     * 用于按拼音首字母排序或搜索
     */
    public static String getPinyinIndex(String chinese) {
        if (StringUtil.isEmpty(chinese)) {
            return chinese;
        }
        return getPinyinInitials(chinese).toUpperCase().charAt(0) + "";
    }

    /**
     * 转换为拼音友好的字符串
     * 将中文转换为拼音，非中文保持原样
     */
    public static String toPinyinFriendly(String str) {
        if (StringUtil.isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        String pinyin = getPinyin(str, " ");
        for (String part : pinyin.split("\\s+")) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(part);
        }
        return result.toString();
    }

    /**
     * 中文名字转拼音
     * 支持复姓处理
     */
    public static String getNamePinyin(String name) {
        if (StringUtil.isEmpty(name)) {
            return name;
        }
        String[] parts = name.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            result.append(capitalize(getPinyin(parts[i])));
        }
        return result.toString();
    }

    /**
     * 中文名字转拼音首字母
     */
    public static String getNamePinyinInitials(String name) {
        if (StringUtil.isEmpty(name)) {
            return name;
        }
        String[] parts = name.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            result.append(getPinyinInitials(part).toUpperCase());
        }
        return result.toString();
    }

    /**
     * 首字母大写
     */
    private static String capitalize(String str) {
        if (StringUtil.isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
