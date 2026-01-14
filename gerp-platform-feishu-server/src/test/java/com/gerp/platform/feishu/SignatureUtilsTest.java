package com.gerp.platform.feishu;

import com.gerp.platform.feishu.common.utils.SignatureUtils;
import org.junit.jupiter.api.Test;

/**
 * @Author: duanzengqiang
 * @Date: 2026/1/8 14:20
 */
public class SignatureUtilsTest {

    @Test
    public void signTest(){
        String sign = SignatureUtils.genPostRequestSignature("f21e925a-16cf-4b8b-9549-a651f6b8e546", "Thursday, 08-Jan-26 14:11:08 CST", "{\n" +
                "\t\t\"params\": \"{\\\"datasourceConfig\\\":\\\"{\\\\\\\"baseUserId\\\\\\\":\\\\\\\"bou_6f911ff80155e82bd2684d276c2f42ae\\\\\\\",\\\\\\\"baseUserName\\\\\\\":\\\\\\\"我的“积加 ERP”连接器\\\\\\\",\\\\\\\"moduleCode\\\\\\\":\\\\\\\"PRODUCT\\\\\\\",\\\\\\\"maxPageSize\\\\\\\":100}\\\"}\",\n" +
                "\t\t\"context\": \"{\\\"type\\\":\\\"script\\\",\\\"bitable\\\":{\\\"token\\\":\\\"\\\",\\\"logID\\\":\\\"02176785266803600000000000000000000ffffc4d04a6b71f6a8\\\",\\\"trigger\\\":null},\\\"authorization\\\":null,\\\"packID\\\":\\\"debug_31a08cfe8aedef22\\\",\\\"faasType\\\":\\\"\\\",\\\"extensionID\\\":\\\"\\\",\\\"route\\\":null,\\\"tenantAccessToken\\\":\\\"\\\",\\\"tenantKey\\\":\\\"13827be5c2ce175e\\\",\\\"userTenantKey\\\":\\\"13827be5c2ce175e\\\",\\\"bizInstanceID\\\":\\\"0\\\",\\\"scriptArgs\\\":{\\\"projectURL\\\":\\\"https://gatewayuat.apist.gerpgo.com/connector\\\",\\\"baseOpenID\\\":\\\"bou_6f911ff80155e82bd2684d276c2f42ae\\\"}}\"\n" +
                "\t}", "e65OV515e21ee4bD0a4c2e5fa1294e");
        System.out.println(sign);
    }
}
