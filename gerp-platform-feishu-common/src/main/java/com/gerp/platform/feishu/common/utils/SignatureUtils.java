package com.gerp.platform.feishu.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/19 16:04
 */
public class SignatureUtils {
    /**
     * 生成Post请求签名，逻辑与Go版本完全一致
     * @param nonce 随机字符串
     * @param timestamp 时间戳字符串
     * @param body 请求体字符串
     * @param secretKey 密钥字符串
     * @return 十六进制格式的SHA1签名
     */
    public static String genPostRequestSignature(String nonce, String timestamp, String body, String secretKey) {
        // 1. 拼接字符串（与Go版本的拼接顺序完全一致：timestamp + nonce + secretKey + body）
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp)
                .append(nonce)
                .append(secretKey)
                .append(body);

        try {
            // 2. 创建SHA1哈希实例
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            // 3. 将拼接后的字符串转换为UTF-8字节数组并计算哈希
            byte[] hashBytes = sha1.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            // 4. 将哈希结果转换为十六进制字符串（小写）
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            // SHA1是Java标准库内置算法，理论上不会抛出此异常，这里做兜底处理
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }

    /**
     * 辅助方法：将字节数组转换为小写十六进制字符串（对应Go的%x格式化）
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            // 转换为两位十六进制，不足补0
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
