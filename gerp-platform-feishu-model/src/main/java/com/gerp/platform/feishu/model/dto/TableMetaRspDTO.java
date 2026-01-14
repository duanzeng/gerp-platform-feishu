package com.gerp.platform.feishu.model.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/10 16:49
 */
@NoArgsConstructor
@Data
public class TableMetaRspDTO implements Serializable {

    private String tableName;
    private List<FieldsBean> fields;

    @NoArgsConstructor
    @Data
    public static class FieldsBean {
        private String fieldID;
        private String fieldName;
        private Integer fieldType;
        private Boolean isPrimary;
        private String description;
        private JSONObject property;     // 扩展属性
    }
}
