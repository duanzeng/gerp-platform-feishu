package com.gerp.platform.feishu.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/19 17:40
 */
@Data
public class OpenApiBase implements Serializable {
    private String baseUserId;
    private String baseUserName;
    private String appId;
    private String appKey;

    private String datasourceConfig;


}
