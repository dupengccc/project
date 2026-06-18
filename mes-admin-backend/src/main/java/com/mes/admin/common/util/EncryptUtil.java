package com.mes.admin.common.util;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密解密工具类
 * <p>
 * 功能包括：
 * - MD5 加密
 * - SHA 家族（SHA1/SHA256/SHA512）
 * - AES 对称加密/解密
 * - Base64 编解码
 * - UUID 生成
 * </p>
 *
 * @author MES Admin
 */
public class EncryptUtil {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    // ==================== MD5 ====================

    /**
     * MD5 加密（32位小写）
     */
    public static String md5(String input) {
        return md5(input, false);
    }

    /**
     * MD5 加密
     *
     * @param input   输入字符串
     * @param uppercase 是否大写
     * @return MD5 哈希值
     */
    public static String md5(String input, boolean uppercase) {
        if (StringUtil.isEmpty(input)) return input;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            String hex = bytesToHex(bytes);
            return uppercase ? hex.toUpperCase() : hex;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 加密失败", e);
        }
    }

    /**
     * MD5 加密（16位小写）
     */
    public static String md5_16(String input) {
        if (StringUtil.isEmpty(input)) return input;
        return md5(input).substring(8, 24);
    }

    /**
     * MD5 盐值加密
     */
    public static String md5WithSalt(String input, String salt) {
        return md5(md5(input) + salt);
    }

    // ==================== SHA 系列 ====================

    /**
     * SHA-1 加密
     */
    public static String sha1(String input) {
        return sha(input, "SHA-1", false);
    }

    /**
     * SHA-256 加密
     */
    public static String sha256(String input) {
        return sha(input, "SHA-256", false);
    }

    /**
     * SHA-512 加密
     */
    public static String sha512(String input) {
        return sha(input, "SHA-512", false);
    }

    /**
     * SHA 加密
     */
    public static String sha(String input, String algorithm) {
        return sha(input, algorithm, false);
    }

    public static String sha(String input, String algorithm, boolean uppercase) {
        if (StringUtil.isEmpty(input)) return input;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            String hex = bytesToHex(bytes);
            return uppercase ? hex.toUpperCase() : hex;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA 加密失败: " + algorithm, e);
        }
    }

    // ==================== AES 加密/解密 ====================

    /**
     * AES 加密（使用默认密钥）
     */
    public static String aesEncrypt(String input, String key) {
        try {
            byte[] keyBytes = getAesKey(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES_ALGORITHM);

            // 生成随机 IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

            // 拼接 IV + 加密数据
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("AES 加密失败", e);
        }
    }

    /**
     * AES 解密
     */
    public static String aesDecrypt(String encrypted, String key) {
        try {
            byte[] combined = Base64.getDecoder().decode(encrypted);
            byte[] keyBytes = getAesKey(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, AES_ALGORITHM);

            // 分离 IV 和加密数据
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] cipherText = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decrypted = cipher.doFinal(cipherText);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES 解密失败", e);
        }
    }

    /**
     * 生成 AES 密钥（128位）
     */
    public static String generateAesKey() {
        return generateAesKey(128);
    }

    /**
     * 生成 AES 密钥
     *
     * @param bits 密钥位数（128/192/256）
     */
    public static String generateAesKey(int bits) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(bits);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES 密钥生成失败", e);
        }
    }

    /**
     * 获取 AES 密钥字节数组
     */
    private static byte[] getAesKey(String key) {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        // 确保密钥长度为 16/24/32 字节
        if (keyBytes.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
            return paddedKey;
        }
        return keyBytes;
    }

    // ==================== Base64 ====================

    /**
     * Base64 编码
     */
    public static String base64Encode(String input) {
        if (StringUtil.isEmpty(input)) return input;
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64 解码
     */
    public static String base64Decode(String encoded) {
        if (StringUtil.isEmpty(encoded)) return encoded;
        return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    }

    /**
     * Base64 编码（URL 安全）
     */
    public static String base64UrlEncode(String input) {
        if (StringUtil.isEmpty(input)) return input;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64 解码（URL 安全）
     */
    public static String base64UrlDecode(String encoded) {
        if (StringUtil.isEmpty(encoded)) return encoded;
        return new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
    }

    // ==================== 十六进制 ====================

    /**
     * 字节数组转十六进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串转字节数组
     */
    public static byte[] hexToBytes(String hex) {
        if (StringUtil.isEmpty(hex)) return null;
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    // ==================== UUID ====================

    /**
     * 生成 UUID（无中划线）
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成带中划线的 UUID
     */
    public static String uuidWithDash() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成指定数量的 UUID
     */
    public static String[] uuid(int count) {
        String[] uuids = new String[count];
        for (int i = 0; i < count; i++) {
            uuids[i] = uuid();
        }
        return uuids;
    }

    // ==================== 密码验证 ====================

    /**
     * 验证密码是否匹配（BCrypt 风格）
     * 实际项目中建议使用 Spring Security 的 BCryptPasswordEncoder
     */
    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        if (StringUtil.isEmpty(rawPassword) || StringUtil.isEmpty(encodedPassword)) {
            return false;
        }
        // 简单验证：MD5 + 盐值
        // 实际项目中应使用 BCrypt 等专业密码哈希
        return md5(rawPassword).equals(encodedPassword) ||
               md5(rawPassword, true).equals(encodedPassword);
    }

    /**
     * 密码加密（建议使用 BCrypt）
     */
    public static String encryptPassword(String password) {
        return md5(password);
    }

    /**
     * 密码加密（带盐值）
     */
    public static String encryptPassword(String password, String salt) {
        return md5WithSalt(password, salt);
    }

    // ==================== Token 生成 ====================

    /**
     * 生成随机令牌
     */
    public static String generateToken() {
        return uuid();
    }

    /**
     * 生成带前缀的令牌
     */
    public static String generateToken(String prefix) {
        return prefix + "_" + uuid();
    }

    /**
     * 生成数字验证码
     */
    public static String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
