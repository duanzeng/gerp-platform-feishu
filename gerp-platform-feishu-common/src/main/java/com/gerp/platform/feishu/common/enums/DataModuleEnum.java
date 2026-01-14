package com.gerp.platform.feishu.common.enums;

import com.gerp.platform.feishu.common.consts.OpenApiConst;
import lombok.Getter;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/19 16:56
 */
@Getter
public enum DataModuleEnum {


    PRODUCT("PRODUCT","产品","查询产品列表", OpenApiConst.OPEN_API_PRODUCT_SWAGGER_URL, OpenApiConst.OPEN_API_PRODUCT_URL,"startDate","endDate"),
    PRODUCT_WAREHOUSE("PRODUCT_WAREHOUSE","产品库存","查询产品库存列表", OpenApiConst.OPEN_API_PRODUCT_WAREHOUSE_SWAGGER_URL, OpenApiConst.OPEN_API_PRODUCT_WAREHOUSE_URL,"",""),
    DISTRIBUTION("DISTRIBUTION","配货单","查询配货单列表", OpenApiConst.OPEN_API_DISTRIBUTION_SWAGGER_URL, OpenApiConst.OPEN_API_DISTRIBUTION_URL,"createdTimeAfter","createdTimeBefore"),

    ORDER("ORDER","订单","查询订单列表", OpenApiConst.OPEN_API_ORDER_SWAGGER_URL, OpenApiConst.OPEN_API_ORDER_URL,"startDate","endDate"),
    PURCHASE_ORDER("PURCHASE_ORDER","采购订单","查询采购订单列表", OpenApiConst.OPEN_API_PURCHASE_ORDER_SWAGGER_URL, OpenApiConst.OPEN_API_PURCHASE_ORDER_URL,"createStartDate","createEndDate"),
    FBA_GOODS("FBA_GOODS","FBA货件","查询FBA货件列表", OpenApiConst.OPEN_API_FBA_GOODS_SWAGGER_URL, OpenApiConst.OPEN_API_FBA_GOODS_URL,"recordStartDate","recordEndDate");


    private String code;
    private String name;
    private String description;
    private String swaggerUrl;
    private String dataUrl;
    private String startDate;
    private String endDate;

    DataModuleEnum(String code, String name, String description, String swaggerUrl, String dataUrl, String startDate, String endDate) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.swaggerUrl = swaggerUrl;
        this.dataUrl = dataUrl;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static DataModuleEnum getByCode(String code){
        for (DataModuleEnum value : values()) {
            if (value.code.equals(code)){
                return value;
            }
        }
        return null;
    }
}
