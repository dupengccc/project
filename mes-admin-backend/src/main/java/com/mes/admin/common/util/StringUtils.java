package com.mes.admin.common.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 字符串工具类
 * <p>
 * 功能包括：
 * - 字符串判空、判空trim
 * - 字符串转换（下划线、驼峰、大小写等）
 * - 字符串截断、填充
 * - HTML/JSON转义
 * - 脱敏处理（手机号、身份证等）
 * - 常用正则校验
 * </p>
 *
 * @author MES Admin
 */
public class StringUtil {

    /** 空字符串 */
    public static final String EMPTY = "";

    /** 逗号 */
    public static final String COMMA = ",";

    /** 点号 */
    public static final String DOT = ".";

    /** 下划线 */
    public static final String UNDERSCORE = "_";

    /** 横线 */
    public static final String HYPHEN = "-";

    // ==================== 判空方法 ====================

    /**
     * 判断字符串是否为空
     * 包括 null 和空字符串
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空（包括空白字符）
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空（包括空白字符）
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 判断字符串是否为空，如果为空返回默认值
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    /**
     * 判断字符串是否为空（包括空白字符），如果为空返回默认值
     */
    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    /**
     * 如果字符串为空，返回空字符串
     */
    public static String nvl(String str) {
        return defaultIfEmpty(str, EMPTY);
    }

    /**
     * 如果字符串为空，返回空字符串
     */
    public static String nvl(String str, String defaultStr) {
        return defaultIfEmpty(str, defaultStr);
    }

    // ==================== Trim 方法 ====================

    /**
     * 去除字符串两端空白
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 去除字符串两端空白，如果为空返回 null
     */
    public static String trimToNull(String str) {
        String trimmed = trim(str);
        return isEmpty(trimmed) ? null : trimmed;
    }

    /**
     * 去除字符串两端空白，如果为空返回空字符串
     */
    public static String trimToEmpty(String str) {
        return str == null ? EMPTY : str.trim();
    }

    // ==================== 大小写转换 ====================

    /**
     * 转换为小写
     */
    public static String toLowerCase(String str) {
        return str == null ? null : str.toLowerCase();
    }

    /**
     * 转换为大写
     */
    public static String toUpperCase(String str) {
        return str == null ? null : str.toUpperCase();
    }

    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 首字母小写
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) return str;
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 驼峰转下划线（userName -> user_name）
     */
    public static String camelToUnderscore(String str) {
        if (isEmpty(str)) return str;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) sb.append('_');
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰（user_name -> userName）
     */
    public static String underscoreToCamel(String str) {
        if (isEmpty(str)) return str;
        StringBuilder sb = new StringBuilder();
        boolean upperNext = false;
        for (char c : str.toCharArray()) {
            if (c == '_') {
                upperNext = true;
            } else {
                if (upperNext) {
                    sb.append(Character.toUpperCase(c));
                    upperNext = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰（支持全大写转小写驼峰）
     */
    public static String underscoreToCamel2(String str) {
        if (isEmpty(str)) return str;
        str = str.toLowerCase();
        StringBuilder sb = new StringBuilder();
        boolean upperNext = false;
        for (char c : str.toCharArray()) {
            if (c == '_') {
                upperNext = true;
            } else {
                if (upperNext) {
                    sb.append(Character.toUpperCase(c));
                    upperNext = false;
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    // ==================== 字符串截断与填充 ====================

    /**
     * 左侧填充
     */
    public static String padLeft(String str, int length, char padChar) {
        if (str == null) return null;
        if (str.length() >= length) return str;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length - str.length(); i++) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }

    /**
     * 右侧填充
     */
    public static String padRight(String str, int length, char padChar) {
        if (str == null) return null;
        if (str.length() >= length) return str;
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < length - str.length(); i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    /**
     * 左侧填充空格
     */
    public static String padLeft(String str, int length) {
        return padLeft(str, length, ' ');
    }

    /**
     * 右侧填充空格
     */
    public static String padRight(String str, int length) {
        return padRight(str, length, ' ');
    }

    /**
     * 字符串截断（超出部分省略号）
     */
    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) return str;
        return str.substring(0, maxLength) + "...";
    }

    /**
     * 字符串截断（不添加省略号）
     */
    public static String truncateString(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) return str;
        return str.substring(0, maxLength);
    }

    // ==================== 字符串包含与匹配 ====================

    /**
     * 判断字符串是否包含子串（忽略大小写）
     */
    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) return false;
        return str.toLowerCase().contains(searchStr.toLowerCase());
    }

    /**
     * 判断字符串是否以指定前缀开头（忽略大小写）
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) return false;
        return str.toLowerCase().startsWith(prefix.toLowerCase());
    }

    /**
     * 判断字符串是否以指定后缀结尾（忽略大小写）
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) return false;
        return str.toLowerCase().endsWith(suffix.toLowerCase());
    }

    /**
     * 统计子串出现次数
     */
    public static int countMatches(String str, String sub) {
        if (isEmpty(str) || isEmpty(sub)) return 0;
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(sub, index)) != -1) {
            count++;
            index += sub.length();
        }
        return count;
    }

    // ==================== 字符串替换 ====================

    /**
     * 替换所有匹配的字符串
     */
    public static String replaceAll(String str, String target, String replacement) {
        if (isEmpty(str) || isEmpty(target)) return str;
        return str.replace(target, replacement);
    }

    /**
     * 替换第一个匹配的字符串
     */
    public static String replaceFirst(String str, String regex, String replacement) {
        if (isEmpty(str) || isEmpty(regex)) return str;
        return str.replaceFirst(regex, replacement);
    }

    /**
     * 删除所有空白字符
     */
    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) return str;
        return str.replaceAll("\\s+", "");
    }

    /**
     * 删除字符串两端空白
     */
    public static String strip(String str) {
        return trim(str);
    }

    // ==================== 字符串分割与连接 ====================

    /**
     * 分割字符串为列表
     */
    public static List<String> splitToList(String str, String separator) {
        if (isEmpty(str)) return new ArrayList<>();
        String[] array = str.split(separator);
        return Arrays.stream(array)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 分割字符串为 Set
     */
    public static Set<String> splitToSet(String str, String separator) {
        if (isEmpty(str)) return new HashSet<>();
        return Arrays.stream(str.split(separator))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * 字符串数组转逗号分隔字符串
     */
    public static String join(String[] array, String separator) {
        if (array == null || array.length == 0) return EMPTY;
        return String.join(separator, array);
    }

    /**
     * List 转逗号分隔字符串
     */
    public static String join(Collection<?> collection, String separator) {
        if (collection == null || collection.isEmpty()) return EMPTY;
        return String.join(separator, collection.stream()
                .map(Object::toString)
                .toArray(String[]::new));
    }

    /**
     * List 转逗号分隔字符串
     */
    public static String join(Collection<?> collection) {
        return join(collection, COMMA);
    }

    /**
     * Long 集合转逗号分隔字符串
     */
    public static String joinLong(Collection<Long> collection) {
        if (collection == null || collection.isEmpty()) return EMPTY;
        return collection.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(COMMA));
    }

    // ==================== 字符串反转 ====================

    /**
     * 反转字符串
     */
    public static String reverse(String str) {
        if (isEmpty(str)) return str;
        return new StringBuilder(str).reverse().toString();
    }

    // ==================== 重复字符串 ====================

    /**
     * 重复字符串
     */
    public static String repeat(String str, int count) {
        if (str == null || count <= 0) return EMPTY;
        return str.repeat(count);
    }

    /**
     * 重复字符串并加分隔符
     */
    public static String repeat(String str, String separator, int count) {
        if (str == null || count <= 0) return EMPTY;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(separator);
            sb.append(str);
        }
        return sb.toString();
    }

    // ==================== 随机字符串 ====================

    private static final String RANDOM_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String RANDOM_NUM = "0123456789";

    /**
     * 生成随机字符串
     */
    public static String randomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM_STR.charAt(random.nextInt(RANDOM_STR.length())));
        }
        return sb.toString();
    }

    /**
     * 生成随机数字字符串
     */
    public static String randomNumeric(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM_NUM.charAt(random.nextInt(RANDOM_NUM.length())));
        }
        return sb.toString();
    }

    /**
     * 生成随机 UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成带前缀的随机字符串
     */
    public static String randomStringWithPrefix(String prefix, int length) {
        return prefix + randomString(length);
    }

    // ==================== 数字格式化 ====================

    /**
     * 格式化为带千分位的数字
     */
    public static String formatNumber(Object number) {
        if (number == null) return EMPTY;
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);
    }

    /**
     * 格式化为带小数的数字
     */
    public static String formatDecimal(Object number, int decimals) {
        if (number == null) return EMPTY;
        String pattern = "#,##0." + repeat("0", decimals);
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(number);
    }

    /**
     * 左补零
     */
    public static String leftPad(String str, int length) {
        return padLeft(str, length, '0');
    }

    /**
     * 右补零
     */
    public static String rightPad(String str, int length) {
        return padRight(str, length, '0');
    }

    // ==================== HTML/JSON 转义 ====================

    /**
     * HTML 特殊字符转义
     */
    public static String escapeHtml(String str) {
        if (isEmpty(str)) return str;
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * HTML 特殊字符反转义
     */
    public static String unescapeHtml(String str) {
        if (isEmpty(str)) return str;
        return str.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'");
    }

    /**
     * JavaScript 特殊字符转义
     */
    public static String escapeJavaScript(String str) {
        if (isEmpty(str)) return str;
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    /**
     * SQL 特殊字符转义（防止 SQL 注入）
     */
    public static String escapeSql(String str) {
        if (isEmpty(str)) return str;
        return str.replace("'", "''");
    }

    // ==================== 脱敏处理 ====================

    /**
     * 手机号脱敏（138****5678）
     */
    public static String maskMobile(String mobile) {
        if (isEmpty(mobile) || mobile.length() < 11) return mobile;
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }

    /**
     * 邮箱脱敏（t***@example.com）
     */
    public static String maskEmail(String email) {
        if (isEmpty(email) || !email.contains("@")) return email;
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) return email;
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    /**
     * 身份证号脱敏（430***199001011234）
     */
    public static String maskIdCard(String idCard) {
        if (isEmpty(idCard) || idCard.length() < 8) return idCard;
        return idCard.substring(0, 3) + "***" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 姓名脱敏（张*丰）
     */
    public static String maskName(String name) {
        if (isEmpty(name) || name.length() < 2) return name;
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + repeat("*", name.length() - 1);
    }

    /**
     * 银行卡号脱敏（6222********1234）
     */
    public static String maskBankCard(String cardNo) {
        if (isEmpty(cardNo) || cardNo.length() < 8) return cardNo;
        return cardNo.substring(0, 4) + "****" + cardNo.substring(cardNo.length() - 4);
    }

    /**
     * 地址脱敏（保留省市区）
     */
    public static String maskAddress(String address) {
        if (isEmpty(address) || address.length() <= 6) return address;
        return address.substring(0, 6) + "***";
    }

    // ==================== 正则校验 ====================

    /**
     * 校验手机号
     */
    public static boolean isMobile(String mobile) {
        if (isEmpty(mobile)) return false;
        return Pattern.matches("^1[3-9]\\d{9}$", mobile);
    }

    /**
     * 校验邮箱
     */
    public static boolean isEmail(String email) {
        if (isEmpty(email)) return false;
        return Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", email);
    }

    /**
     * 校验身份证号
     */
    public static boolean isIdCard(String idCard) {
        if (isEmpty(idCard)) return false;
        // 15位或18位
        return Pattern.matches("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$", idCard)
                || Pattern.matches("^[1-9]\\d{5}\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}$", idCard);
    }

    /**
     * 校验 URL
     */
    public static boolean isUrl(String url) {
        if (isEmpty(url)) return false;
        return Pattern.matches("^https?://[\\w.-]+(:\\d+)?(/.*)?$", url);
    }

    /**
     * 校验 IP 地址
     */
    public static boolean isIp(String ip) {
        if (isEmpty(ip)) return false;
        return Pattern.matches("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$", ip);
    }

    /**
     * 校验纯数字
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) return false;
        return Pattern.matches("^\\d+$", str);
    }

    /**
     * 校验纯字母
     */
    public static boolean isAlpha(String str) {
        if (isEmpty(str)) return false;
        return Pattern.matches("^[a-zA-Z]+$", str);
    }

    /**
     * 校验字母和数字
     */
    public static boolean isAlphanumeric(String str) {
        if (isEmpty(str)) return false;
        return Pattern.matches("^[a-zA-Z0-9]+$", str);
    }

    /**
     * 校验中文
     */
    public static boolean isChinese(String str) {
        if (isEmpty(str)) return false;
        return Pattern.matches("^[\\u4e00-\\u9fa5]+$", str);
    }

    /**
     * 校验邮编
     */
    public static boolean isZipCode(String zipCode) {
        if (isEmpty(zipCode)) return false;
        return Pattern.matches("^\\d{6}$", zipCode);
    }

    // ==================== 编码转换 ====================

    /**
     * 字符串转字节数组
     */
    public static byte[] toBytes(String str, String charset) {
        if (isEmpty(str)) return null;
        return str.getBytes(Charset.forName(charset));
    }

    /**
     * 字节数组转字符串
     */
    public static String toString(byte[] bytes, String charset) {
        if (bytes == null || bytes.length == 0) return EMPTY;
        return new String(bytes, Charset.forName(charset));
    }

    /**
     * UTF-8 转字节数组
     */
    public static byte[] toUtf8Bytes(String str) {
        return toBytes(str, "UTF-8");
    }

    /**
     * 字节数组转 UTF-8 字符串
     */
    public static String utf8ToString(byte[] bytes) {
        return toString(bytes, "UTF-8");
    }

    /**
     * 16进制字符串转字节数组
     */
    public static byte[] hexToBytes(String hex) {
        if (isEmpty(hex)) return null;
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    /**
     * 字节数组转16进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return EMPTY;
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    // ==================== Base64 编解码 ====================

    /**
     * Base64 编码
     */
    public static String base64Encode(String str) {
        if (isEmpty(str)) return str;
        return Base64.getEncoder().encodeToString(toUtf8Bytes(str));
    }

    /**
     * Base64 解码
     */
    public static String base64Decode(String str) {
        if (isEmpty(str)) return str;
        return utf8ToString(Base64.getDecoder().decode(str));
    }

    /**
     * URL 安全的 Base64 编码
     */
    public static String base64UrlEncode(String str) {
        if (isEmpty(str)) return str;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(toUtf8Bytes(str));
    }

    /**
     * URL 安全的 Base64 解码
     */
    public static String base64UrlDecode(String str) {
        if (isEmpty(str)) return str;
        return utf8ToString(Base64.getUrlDecoder().decode(str));
    }

    // ==================== 拼音相关 ====================

    /**
     * 获取字符串拼音首字母
     */
    public static String getPinyinInitial(String str) {
        if (isEmpty(str)) return str;
        return PinyinUtils.getPinyinInitials(str);
    }

    /**
     * 获取字符串全拼
     */
    public static String getPinyin(String str) {
        if (isEmpty(str)) return str;
        return PinyinUtils.getPinyin(str);
    }

    // ==================== 其他实用方法 ====================

    /**
     * 判断字符串是否全为空白字符
     */
    public static boolean isWhitespace(String str) {
        if (str == null) return true;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将字符串中的占位符 {0}, {1} 等替换为对应参数
     */
    public static String format(String template, Object... args) {
        if (isEmpty(template) || args == null || args.length == 0) return template;
        for (int i = 0; i < args.length; i++) {
            template = template.replace("{" + i + "}", args[i] == null ? "" : args[i].toString());
        }
        return template;
    }

    /**
     * 字符串相似度（Levenshtein 距离）
     */
    public static int levenshteinDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return 0;
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[s1.length()][s2.length()];
    }

    /**
     * 相似度百分比
     */
    public static double similarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0;
        if (s1.equals(s2)) return 1.0;
        int distance = levenshteinDistance(s1, s2);
        return 1.0 - (double) distance / Math.max(s1.length(), s2.length());
    }

    /**
     * 生成文件名的唯一后缀
     */
    public static String generateUniqueSuffix(String originalName) {
        if (isEmpty(originalName)) return randomString(8);
        int dotIndex = originalName.lastIndexOf(".");
        String suffix = "";
        String prefix = originalName;
        if (dotIndex > 0) {
            suffix = originalName.substring(dotIndex);
            prefix = originalName.substring(0, dotIndex);
        }
        return prefix + "_" + System.currentTimeMillis() + suffix;
    }

    /**
     * 获取文件扩展名
     */
    public static String getExtension(String filename) {
        if (isEmpty(filename)) return EMPTY;
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return EMPTY;
    }

    /**
     * 获取不带扩展名的文件名
     */
    public static String getNameWithoutExtension(String filename) {
        if (isEmpty(filename)) return filename;
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0) {
            return filename.substring(0, dotIndex);
        }
        return filename;
    }
}
