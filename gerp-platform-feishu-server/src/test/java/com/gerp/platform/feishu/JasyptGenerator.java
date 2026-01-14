package com.gerp.platform.feishu;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * 配置文件加密
 */
public class JasyptGenerator {

    public static void main(String[] args) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //加密所需的salt(盐)
        textEncryptor.setPassword("G0CvDz7oJn6");
        //redis密码加密
        String redisPassword = textEncryptor.encrypt("gerp@uac2022");
        System.out.println("redisPassword:"+redisPassword);
        //数据库密码加密
        String dbPassword = textEncryptor.encrypt("Erpgo2021");
        System.out.println("dbPassword:"+dbPassword);
    }
}
