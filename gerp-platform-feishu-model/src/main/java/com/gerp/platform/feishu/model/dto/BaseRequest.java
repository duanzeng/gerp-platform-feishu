package com.gerp.platform.feishu.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/19 16:49
 */
@Data
public class BaseRequest implements Serializable {
    @NotBlank
    private String params;


    private String context;
}
