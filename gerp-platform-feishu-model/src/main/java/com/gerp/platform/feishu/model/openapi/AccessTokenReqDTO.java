package com.gerp.platform.feishu.model.openapi;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/18 10:20
 */
@NoArgsConstructor
@Data
public class AccessTokenReqDTO {

    private String appId;
    private String appKey;
}
