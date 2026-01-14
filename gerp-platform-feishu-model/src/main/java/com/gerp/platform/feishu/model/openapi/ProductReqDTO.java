package com.gerp.platform.feishu.model.openapi;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/18 14:50
 */
@Data
public class ProductReqDTO implements Serializable {
    private Integer page;

    private Integer pagesize;

    private String order="descend";

    private String sort="id";
}
