package com.gerp.platform.feishu.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/22 11:43
 */
@Data
public class TableRecordReqDTO extends TableMetaReqDTO{
    private String pageToken;
    private Integer maxPageSize;

    private String transactionID;

    private List<String> fields;

    @ApiModelProperty(value = "开始时间,格式：yyyy-MM-dd")
    private String startDate;

    @ApiModelProperty(value = "结束时间,格式：yyyy-MM-dd")
    private String endDate;

}
