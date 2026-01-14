package com.gerp.platform.feishu.model.openapi;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/18 10:24
 */
@NoArgsConstructor
@Data
public class AccessTokenRspDTO implements Serializable {

    private String accessToken;
    private Integer expiresIn;
    private Integer expiresOut;
}
