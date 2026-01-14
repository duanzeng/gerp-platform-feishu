package com.gerp.platform.feishu.web.controller.connector.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 黑名单配置类
 * 用于管理各模块的字段黑名单配置
 * 配置格式：
 * module.blacklist:
 *   fields:
 *     MODULE_CODE: [field1, field2, field3]
 */
@Data
@Component
@ConfigurationProperties(prefix = "module.blacklist")
@RefreshScope
public class BlacklistConfig {
    
    /**
     * 模块字段黑名单配置
     * key: moduleCode
     * value: 黑名单字段列表
     */
    private Map<String, List<String>> fields = new HashMap<>();
    
    /**
     * 获取指定模块的黑名单字段列表
     * @param moduleCode 模块编码
     * @return 黑名单字段列表，若不存在则返回空列表
     */
    public List<String> getBlacklistFields(String moduleCode) {
        return fields.getOrDefault(moduleCode, Collections.emptyList());
    }
}