package com.gerp.platform.feishu.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.platform.feishu.common.consts.OpenApiConst;
import com.gerp.platform.feishu.common.enums.DataModuleEnum;
import com.gerp.platform.feishu.model.dto.*;
import com.gerp.platform.feishu.model.entity.group.FeishuBind;
import com.gerp.platform.feishu.model.openapi.AccessTokenRspDTO;
import com.gerp.platform.feishu.model.openapi.ProductReqDTO;
import com.gerp.platform.feishu.service.OpenApiService;
import com.gerp.platform.feishu.service.group.FeishuBindService;
import com.gerp.uac.common.exception.BusinessException;
import com.gerpgo.xcloudframework.util.JsonUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.kmniu.erpweb.v2.common.model.common.CustomResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/19 17:25
 */
@Service
@Slf4j
public class OpenApiServiceImpl implements OpenApiService {
    @Autowired
    private Cache<String, String> tokenCache;

    @Autowired
    FeishuBindService feishuBindService;

    @Override
    public synchronized CustomResponse<AccessTokenRspDTO> getAccessToken(OpenApiBase reqDTO) {
        FeishuBind feishuBind = feishuBindService.get(reqDTO.getBaseUserId());
        if(feishuBind==null){
            throw new BusinessException("飞书绑定信息不存在");
        }
        reqDTO.setAppId(feishuBind.getAppId());
        reqDTO.setAppKey(feishuBind.getAppKey());
        String cacheKey=reqDTO.getAppId()+"_"+reqDTO.getAppKey();
        String cacheValue = tokenCache.getIfPresent(cacheKey);
        if(cacheValue!=null){
            return CustomResponse.success(JsonUtil.toObject(cacheValue, AccessTokenRspDTO.class));
        }
        String result = HttpUtil.post(OpenApiConst.OPEN_API_TOKEN_URL, JsonUtil.toString(reqDTO));
        CustomResponse<AccessTokenRspDTO> cus = JsonUtil.toObject(result, new TypeReference<CustomResponse<AccessTokenRspDTO>>() {
        });
        if(cus.getCode()== HttpStatus.HTTP_OK){
            log.info("从接口请求");
            tokenCache.put(cacheKey, JsonUtil.toStringPretty(cus.getData()));
            return CustomResponse.success(cus.getData());
        }
        return cus;
    }

    @Override
    public String getSwagger(TableMetaReqDTO req) {

        CustomResponse<AccessTokenRspDTO> accessTokenCus = getAccessToken(req);
        if(!accessTokenCus.isSuccess()){
            throw new BusinessException("获取access_token失败");
        }
        String token = accessTokenCus.getData().getAccessToken();

        DataModuleEnum moduleEnum = DataModuleEnum.getByCode(req.getModuleCode());
        HttpRequest get = HttpUtil.createGet(moduleEnum.getSwaggerUrl());
        get.header("accessToken",token);
        String execute = get.execute().body();
        CustomResponse<String> cus = JsonUtil.toObject(execute, new TypeReference<CustomResponse<String>>() {
        });
        if(!cus.isSuccess()){
            throw new BusinessException("获取swagger失败");
        }
        return cus.getData();
    }

    @SneakyThrows
    @Override
    public TableRecordRspDTO getData(TableRecordReqDTO reqDTO) {
        CustomResponse<AccessTokenRspDTO> accessTokenCus = getAccessToken(reqDTO);
        if(!accessTokenCus.isSuccess()){
            throw new BusinessException("获取access_token失败");
        }
        String token = accessTokenCus.getData().getAccessToken();

        DataModuleEnum moduleEnum = DataModuleEnum.getByCode(reqDTO.getModuleCode());
        HttpRequest post = HttpUtil.createPost(moduleEnum.getDataUrl());
        post.header("accessToken",token);

        Map<String, Object> apiReqMap = new ConcurrentHashMap<>();
        apiReqMap.put("order", "descend");
        apiReqMap.put("sort", "id");
        apiReqMap.put("page", 1);

        if(StrUtil.isNotBlank(reqDTO.getPageToken()) && NumberUtil.isInteger(reqDTO.getPageToken())){
            apiReqMap.put("page", Integer.valueOf(reqDTO.getPageToken()));
        }
        apiReqMap.put("pagesize", reqDTO.getMaxPageSize());

        if(StrUtil.isNotBlank(reqDTO.getStartDate()) && StrUtil.isNotBlank(moduleEnum.getStartDate())){
            apiReqMap.put(moduleEnum.getStartDate(), reqDTO.getStartDate());
        }

        if(StrUtil.isNotBlank(reqDTO.getEndDate()) && StrUtil.isNotBlank(moduleEnum.getEndDate())){
            apiReqMap.put(moduleEnum.getEndDate(), reqDTO.getEndDate());
        }

        String body = JsonUtil.toString(apiReqMap);
        log.info("请求参数:{}",body);
        post.body(body);
        String execute = post.execute().body();
        CustomResponse<PageDataRspDTO> cus = JsonUtil.toObject(execute, new TypeReference<CustomResponse<PageDataRspDTO>>() {
        });
        if(cus.getCode()!= HttpStatus.HTTP_OK){
            log.warn("获取数据失败:{}",execute);
            throw new BusinessException("获取数据失败");
        }
        if(cus.getData().getPage()==null){
            cus.getData().setPage(Integer.valueOf(reqDTO.getPageToken()));
        }
        if(cus.getData().getPagesize()==null){
            cus.getData().setPagesize(reqDTO.getMaxPageSize());
        }

        TableRecordRspDTO tableRecordRspDTO =  new TableRecordRspDTO();
        PageDataRspDTO pageData = cus.getData();
        tableRecordRspDTO.setHasMore(pageData.hasMore());
        tableRecordRspDTO.setNextPageToken(String.valueOf(pageData.getNextPageToken()));
        List<TableRecordRspDTO.RecordsBean> recordsBeans = new ArrayList<>();

        // 创建ObjectMapper实例
        ObjectMapper objectMapper = new ObjectMapper();

        // 读取JSON文件并解析为JsonNode
        JsonNode rootNode = objectMapper.readTree(execute);
        // 获取rows数组节点
        JsonNode rowsNode = rootNode.get("data").get("rows");
        if(rowsNode!=null) {
            // 将rows数组转换为List<Map<String, Object>>
            List<Map<String, Object>> rowsList = objectMapper.convertValue(
                    rowsNode,
                    new TypeReference<List<Map<String, Object>>>() {
                    }
            );




            // 遍历结果
            for (Map<String, Object> row : rowsList) {
                Map<String, Object> rowData = row;
                if (CollUtil.isNotEmpty(reqDTO.getFields())) {
                    //rowsList对应的列需要在fields里面
                    Map<String, Object> filteredRow = new LinkedHashMap<>();
                    for (Map.Entry<String, Object> entry : row.entrySet()) {
                        if (entry.getKey() != null && reqDTO.getFields().contains(entry.getKey())) {
                            filteredRow.put(entry.getKey(), entry.getValue());
                        }
                    }
                    rowData = filteredRow;
                }
                TableRecordRspDTO.RecordsBean recordsBean = new TableRecordRspDTO.RecordsBean();
                recordsBean.setPrimaryID(String.valueOf(row.get("id")));
                recordsBean.setData(rowData);
                recordsBeans.add(recordsBean);
            }
        }

        tableRecordRspDTO.setRecords(recordsBeans);

        return tableRecordRspDTO;
    }
}
