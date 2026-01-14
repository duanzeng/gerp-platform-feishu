package com.gerp.platform.feishu;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.platform.feishu.common.config.TargetField;
import com.gerp.platform.feishu.common.consts.OpenApiConst;
import com.gerp.platform.feishu.common.utils.ConnectorUtil;
import com.gerp.platform.feishu.model.dto.PageDataRspDTO;
import com.gerp.platform.feishu.model.dto.TableRecordRspDTO;
import com.gerp.platform.feishu.model.openapi.AccessTokenReqDTO;
import com.gerp.platform.feishu.model.openapi.AccessTokenRspDTO;
import com.gerp.platform.feishu.model.openapi.ProductReqDTO;
import com.gerpgo.xcloudframework.util.JsonUtil;
import com.kmniu.erpweb.v2.common.model.common.CustomResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/18 10:13
 */
public class OpenApiTest {
    private static final String OPEN_API_BASE_URL = "https://open.gerpgo.com/api/open";

    @Test
    public void getAccessToken(){
        AccessTokenReqDTO reqDTO = new AccessTokenReqDTO();
        reqDTO.setAppId("e6551e21ee4b0a4c2e521294e");
        reqDTO.setAppKey("k6617aa44e4b0201e98bb2b96");
        String result = HttpUtil.post(OpenApiConst.OPEN_API_TOKEN_URL, JsonUtil.toString(reqDTO));
        System.out.println( result);
        CustomResponse<AccessTokenRspDTO> cus = JsonUtil.toObject(result, new TypeReference<CustomResponse<AccessTokenRspDTO>>() {
        });

        Assertions.assertEquals(200,cus.getCode());
    }

    @Test
    public void getSwaggerApiTest(){
        String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjkzODMyOTM3NDIwMjgzNDk0NCwidXNlcm5hbWUiOiIiLCJvcGVuVXNlcklkIjo5MzgzMjkzNzQyMDI4MzQ5NDQsInRlbmFudENvZGUiOiJwcmVkZW1vIiwidHlwZSI6MSwiaWF0IjoxNzY2MDM5ODYzLCJleHAiOjE3NjYxMjYyNjN9.2ajHukIv0Sb9uFxqDtGalKalMXbzAJ5urp3PSoK9C2A";
        HttpRequest get = HttpUtil.createGet(OpenApiConst.OPEN_API_PRODUCT_SWAGGER_URL);
        get.header("accessToken",token);
        String execute = get.execute().body();
        System.out.println(execute);
        CustomResponse<String> cus = JsonUtil.toObject(execute, new TypeReference<CustomResponse<String>>() {
        });
        System.out.println(cus.getData());
        //{"traceId":"open_eeb9f3cb13254b07914ff2812cb2d5dc","code":40005,"messages":["token已失效，请重新授权"],"message":"token已失效，请重新授权"}
        List<TargetField> targetFields = ConnectorUtil.parseSwaggerJson(cus.getData());
        System.out.println(targetFields);
        Assertions.assertTrue(cus.isSuccess());
    }

    @Test
    public void getProductTest(){
        String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjkzODMyOTM3NDIwMjgzNDk0NCwidXNlcm5hbWUiOiIiLCJvcGVuVXNlcklkIjo5MzgzMjkzNzQyMDI4MzQ5NDQsInRlbmFudENvZGUiOiJwcmVkZW1vIiwidHlwZSI6MSwiaWF0IjoxNzY2MDM5ODYzLCJleHAiOjE3NjYxMjYyNjN9.2ajHukIv0Sb9uFxqDtGalKalMXbzAJ5urp3PSoK9C2A";
        HttpRequest post = HttpUtil.createPost(OpenApiConst.OPEN_API_PRODUCT_URL);
        post.header("accessToken",token);
        ProductReqDTO reqDTO = new ProductReqDTO();
        reqDTO.setPage(1);
        reqDTO.setPagesize(100);
        String body = JsonUtil.toString(reqDTO);
//        System.out.println( body);
        post.body(body);
        String execute = post.execute().body();
//        System.out.println(execute);
        CustomResponse<Object> cus = JsonUtil.toObject(execute, new TypeReference<CustomResponse<Object>>() {
        });
        System.out.println(cus.getData());
        Assertions.assertEquals(200,cus.getCode());
    }


    @SneakyThrows
    @Test
    public void parseDataJsonTest(){
        String data = FileUtil.readUtf8String("./productData.json");

        TableRecordRspDTO tableRecordRspDTO = new TableRecordRspDTO();
        PageDataRspDTO pageData = JsonUtil.toObject(data, PageDataRspDTO.class);
        tableRecordRspDTO.setHasMore( pageData.hasMore());
        tableRecordRspDTO.setNextPageToken(String.valueOf(pageData.getNextPageToken()));
        List<TableRecordRspDTO.RecordsBean> recordsBeans = new ArrayList<>();



        // 创建ObjectMapper实例
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 读取JSON文件并解析为JsonNode
            JsonNode rootNode = objectMapper.readTree(data);
            // 获取rows数组节点
            JsonNode rowsNode = rootNode.get("rows");
            // 将rows数组转换为List<Map<String, Object>>
            List<Map<String, Object>> rowsList = objectMapper.convertValue(
                    rowsNode,
                    new TypeReference<List<Map<String, Object>>>() {}
            );


            // 遍历结果
            for (Map<String, Object> row : rowsList) {
                TableRecordRspDTO.RecordsBean recordsBean = new TableRecordRspDTO.RecordsBean();
                recordsBean.setPrimaryID(String.valueOf(row.get("id")));
                recordsBean.setData(row);
                recordsBeans.add(recordsBean);
            }

            tableRecordRspDTO.setRecords(recordsBeans);
            System.out.println(JsonUtil.toString(tableRecordRspDTO));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
