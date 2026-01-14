package com.gerp.platform.feishu.common.config;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/11 14:38
 * @Description: 目标字段  https://feishu.feishu.cn/docx/GwBXdq7cmoZahfx8faQchcS2nXf
 */
@Data
public class TargetField implements Serializable {
    private String fieldID;          // 字段ID（嵌套用_分隔）
    private String fieldName;        // 字段名称（从Swagger自动解析）
    private Integer fieldType;       // 字段类型（1-多行文本，2-数字，3-单选等）
    private Boolean isPrimary;       // 是否索引列
    private String description;      // 字段描述（Swagger的description）
    private JSONObject property;     // 扩展属性
}
