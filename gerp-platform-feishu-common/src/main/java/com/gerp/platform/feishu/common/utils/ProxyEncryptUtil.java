package com.gerp.platform.feishu.common.utils;

import cn.hutool.crypto.SecureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @Author: duanzengqiang
 * @Date: 2024/11/15 11:39
 */
@Component
public class ProxyEncryptUtil {

    @Value("${subaccount.proxy.encrypt.key:tdOYyyOnmEo=}")
    private String encryptKey;

    /**
     * 加密
     *
     * @param password    需要加密的密码
     * @return
     */
    public String encrypt( String password) {
        return SecureUtil.des(encryptKey.getBytes(StandardCharsets.UTF_8)).encryptHex(password);
    }


    /**
     * 解密
     *
     * @param password    需要解密的密码
     * @return
     */
    public String decrypt( String password) {
        return SecureUtil.des(encryptKey.getBytes(StandardCharsets.UTF_8)).decryptStr(password);
    }
}
