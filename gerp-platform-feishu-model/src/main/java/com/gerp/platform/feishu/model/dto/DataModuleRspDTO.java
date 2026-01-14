package com.gerp.platform.feishu.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/10 16:31
 */
@Data
public class DataModuleRspDTO implements Serializable {
    @ApiModelProperty("数据模块编码")
    private String code;

    @ApiModelProperty("数据模块名称")
    private String name;

    @ApiModelProperty("数据模块描述")
    private String description;
}
