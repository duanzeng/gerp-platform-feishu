package com.gerp.platform.feishu.common.consts;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/19 16:58
 */
public class OpenApiConst {

    public static final String OPEN_BASE_URL="https://open.gerpgo.com";

    public static final String OPEN_API_BASE_URL = OPEN_BASE_URL + "/api/open";


    public static final String OPEN_API_TOKEN_URL = OPEN_API_BASE_URL + "/api_token";


    //产品
    public static final String OPEN_API_PRODUCT_SWAGGER_URL=OPEN_BASE_URL+"/api/openAdmin/openDoc/platform/createOpenApi?id=53";
    public static final String OPEN_API_PRODUCT_URL=OPEN_API_BASE_URL+"/purchase/goods/product/page";


    //仓库
    public static final String OPEN_API_PRODUCT_WAREHOUSE_SWAGGER_URL=OPEN_BASE_URL+"/api/openAdmin/openDoc/platform/createOpenApi?id=15";
    public static final String OPEN_API_PRODUCT_WAREHOUSE_URL=OPEN_API_BASE_URL+"/purchase/store/inventory/page";

    //配货单
    public static final String OPEN_API_DISTRIBUTION_SWAGGER_URL=OPEN_BASE_URL+"/api/openAdmin/openDoc/platform/createOpenApi?id=1366";
    public static final String OPEN_API_DISTRIBUTION_URL=OPEN_API_BASE_URL+"/fulfillment/order/foOrder/page";


    //订单
    public static final String OPEN_API_ORDER_SWAGGER_URL=OPEN_BASE_URL+"/api/openAdmin/openDoc/platform/createOpenApi?id=70";
    public static final String OPEN_API_ORDER_URL=OPEN_API_BASE_URL+"/operation/sale/order/page";

    //采购订单
    public static final String OPEN_API_PURCHASE_ORDER_SWAGGER_URL=OPEN_BASE_URL+"/api/openAdmin/openDoc/platform/createOpenApi?id=86";
    public static final String OPEN_API_PURCHASE_ORDER_URL=OPEN_API_BASE_URL+"/purchase/srm/procure/page";

    //FBA货件
    public static final String OPEN_API_FBA_GOODS_SWAGGER_URL=OPEN_BASE_URL+"/api/openAdmin/openDoc/platform/createOpenApi?id=1033";
    public static final String OPEN_API_FBA_GOODS_URL=OPEN_API_BASE_URL+"/fulfillment/ship/shipment/page";
}
