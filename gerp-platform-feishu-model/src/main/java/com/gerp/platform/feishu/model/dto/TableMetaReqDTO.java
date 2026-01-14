package com.gerp.platform.feishu.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/10 16:50
 */
@Data
public class TableMetaReqDTO extends OpenApiBase implements Serializable {

    private String moduleCode;
}
