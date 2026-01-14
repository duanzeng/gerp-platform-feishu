package com.gerp.platform.feishu.web.controller.connector;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gerp.platform.feishu.common.config.TargetField;
import com.gerp.platform.feishu.common.enums.DataModuleEnum;
import com.gerp.platform.feishu.common.utils.ConnectorUtil;
import com.gerp.platform.feishu.common.utils.SignatureUtils;
import com.gerp.platform.feishu.model.base.BaseResponse;
import com.gerp.platform.feishu.model.dto.*;
import com.gerp.platform.feishu.service.OpenApiService;
import com.gerp.platform.feishu.service.group.FeishuBindService;
import com.gerpgo.xcloudframework.util.JsonUtil;
import com.kmniu.erpweb.v2.common.model.common.exception.CustomBadRequestException;
import com.kmniu.rediscache.controller.util.RedisHelper;
import com.kmniu.rediscache.enumerate.CacheRegionEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import com.gerp.platform.feishu.web.controller.connector.config.BlacklistConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/10 16:28
 */
@RestController
@RequestMapping("/connector")
@Api(value = "ConnectorController", tags = "连接器")
@RefreshScope
@Slf4j
public class ConnectorController {

    @Autowired
    OpenApiService openApiService;

    @Autowired
    FeishuBindService feishuBindService;

    @Value("${feishu.secret.key:}")
    String feishuSecretKey;

    @Value("${feishu.open.sign:true}")
    boolean openSign;

    @Autowired
    RedisHelper redisHelper;
    
    @Autowired
    BlacklistConfig blacklistConfig;

    @ApiOperation(value = "获取数据模块", notes = "获取数据模块")
    @GetMapping("/getDataModule")
    public BaseResponse<List<DataModuleRspDTO>> getDataModule(@RequestHeader(value = "X-Base-Request-Timestamp", required = false) String timestamp,
                                                              @RequestHeader(value = "X-Base-Request-Nonce", required = false) String nonce){

        //将枚举DataModuleEnum转化为List<DataModuleRspDTO>
        List<DataModuleRspDTO> dataModuleRspDTOS = new ArrayList<>();
        for (DataModuleEnum value : DataModuleEnum.values()) {
            DataModuleRspDTO dataModuleRspDTO = new DataModuleRspDTO();
            dataModuleRspDTO.setCode(value.getCode());
            dataModuleRspDTO.setName(value.getName());
            dataModuleRspDTO.setDescription(value.getDescription());
            dataModuleRspDTOS.add(dataModuleRspDTO);
        }

        return BaseResponse.success(dataModuleRspDTOS);
    }


    @ApiOperation(value = "绑定飞书", notes = "绑定飞书")
    @PostMapping("/bindFeishu")
    public BaseResponse bindFeishu(@RequestBody OpenApiBase reqDTO){

        if(StrUtil.isBlank(reqDTO.getAppId()) || StrUtil.isBlank(reqDTO.getAppKey()) || StrUtil.isBlank(reqDTO.getBaseUserId()) || StrUtil.isBlank(reqDTO.getBaseUserName())){
            return BaseResponse.error(new BaseResponse.MsgBean(){
                {
                    setZh("参数不能为空");
                    setEn("Parameters cannot be empty");
                }
            });
        }
        feishuBindService.bindFeishu(reqDTO);
        return BaseResponse.success("");
    }

    @ApiOperation(value = "变更昵称", notes = "变更昵称")
    @PostMapping("/changeUserName")
    public BaseResponse changeUserName(@RequestBody OpenApiBase reqDTO){

        if(StrUtil.isBlank(reqDTO.getBaseUserId()) || StrUtil.isBlank(reqDTO.getBaseUserName())){
            return BaseResponse.error(new BaseResponse.MsgBean(){
                {
                    setZh("参数不能为空");
                    setEn("Parameters cannot be empty");
                }
            });
        }
        feishuBindService.changeUserName(reqDTO);
        return BaseResponse.success("");
    }


    /**
     * 获取数据模块表结构
     * @param body
     * @param timestamp
     * @param nonce
     * @param feishuSign
     * @return
     * 示例数据
     * {
     * 			"params": "{\"datasourceConfig\":\"{\\\"baseUserId\\\":\\\"bou_91cc24d2c4a1881bd0eaeaea466a7118\\\",\\\"baseUserName\\\":\\\"我的“积加 ERP”连接器\\\",\\\"moduleCode\\\":\\\"PRODUCT_WAREHOUSE\\\",\\\"maxPageSize\\\":100}\"}",
     * 			"context": "{\"type\":\"script\",\"bitable\":{\"token\":\"\",\"logID\":\"02176750730322200000000000000000000ffffa2d1a58bde16d6\",\"trigger\":null},\"authorization\":null,\"packID\":\"debug_31a5c1ffb06c6f2f\",\"faasType\":\"\",\"extensionID\":\"\",\"route\":null,\"tenantAccessToken\":\"\",\"tenantKey\":\"13827be5c2ce175e\",\"userTenantKey\":\"13827be5c2ce175e\",\"bizInstanceID\":\"0\",\"scriptArgs\":{\"projectURL\":\"https://gatewayuat.apist.gerpgo.com/connector\",\"baseOpenID\":\"bou_91cc24d2c4a1881bd0eaeaea466a7118\"}}"
     *                }
     */
    @SneakyThrows
    @ApiOperation(value = "获取数据模块表结构", notes = "获取数据模块表结构")
    @PostMapping("/getTableMeta")
    public BaseResponse<TableMetaRspDTO> getTableMeta(
            @RequestBody String body,
//            @RequestBody BaseRequest reqDTO,
            @RequestHeader(value = "X-Base-Request-Timestamp", required = false) String timestamp,
            @RequestHeader(value = "X-Base-Request-Nonce", required = false) String nonce,
            @RequestHeader(value = "x-base-signature", required = false) String feishuSign){

        checkSign(body, timestamp, nonce, feishuSign);

        BaseRequest reqDTO = JsonUtil.toObject(body, BaseRequest.class);
        //获取baseUserId
        //获取datasourceConfig
        DatasourceConfig<String> tableMetaReqDTODatasourceConfig = JsonUtil.toObject(reqDTO.getParams(), new TypeReference<DatasourceConfig<String>>() {
        });
        TableMetaReqDTO req = JsonUtil.toObject(tableMetaReqDTODatasourceConfig.getDatasourceConfig(),TableMetaReqDTO.class);
        if( StrUtil.isBlank(req.getModuleCode())){
            return BaseResponse.error(new BaseResponse.MsgBean(){
                {
                    setZh("参数不能为空");
                    setEn("Parameters cannot be empty");
                }
            });
        }



        String swagger = openApiService.getSwagger(req);

        TableMetaRspDTO tableMetaRspDTO = new TableMetaRspDTO();
        List<TargetField> targetFields = ConnectorUtil.parseSwaggerJson(swagger);
        List<TableMetaRspDTO.FieldsBean> tf=new ArrayList<>();
        
        // 获取黑名单字段配置
        String moduleCode = req.getModuleCode();
        List<String> blacklistFields = blacklistConfig.getBlacklistFields(moduleCode);

        for(TargetField c:targetFields) {
            if (c.getFieldID().startsWith("data_rows_")) {
                c.setFieldID(c.getFieldID().replace("data_rows_", ""));
                if (StrUtil.equals("id", c.getFieldID())) {
                    c.setIsPrimary(true);
                }

                //不支持多层结构
                if (c.getFieldID().contains("_")) {
                    continue;
                }
                
                // 黑名单过滤：如果字段ID在黑名单中，则跳过
                if (blacklistFields.contains(c.getFieldID())) {
                    log.info("被列入黑名单，跳过: 字段 {} ", c.getFieldID());
                    continue;
                }

                TableMetaRspDTO.FieldsBean fieldsBean = new TableMetaRspDTO.FieldsBean();
                fieldsBean.setFieldID(c.getFieldID());
                fieldsBean.setFieldName(c.getFieldName());
                fieldsBean.setFieldType(c.getFieldType());
                fieldsBean.setIsPrimary(c.getIsPrimary());
                fieldsBean.setDescription(c.getDescription());
                fieldsBean.setProperty(c.getProperty());
                tf.add(fieldsBean);
            }
        }
        tableMetaRspDTO.setFields(tf);
        tableMetaRspDTO.setTableName(req.getModuleCode());
        if(CollUtil.isNotEmpty( tf)) {
            List<String> fields = tf.stream().map(c -> c.getFieldID()).collect(Collectors.toList());
            redisHelper.set(CacheRegionEnum.MBD, req.getModuleCode(), fields);
        }
        return BaseResponse.success(tableMetaRspDTO);
    }

    private void checkSign(String body, String timestamp, String nonce, String feishuSign) {
        String sign = SignatureUtils.genPostRequestSignature(nonce, timestamp, body, feishuSecretKey);
        if(openSign &&  !StrUtil.equalsIgnoreCase(sign, feishuSign)){
            log.warn("签名失败：sign:{}  feishuSign:{}  body：{}", sign, feishuSign, body);
            throw new CustomBadRequestException("签名验证失败");
        }
    }

    @SneakyThrows
    @ApiOperation(value = "获取数据", notes = "获取数据")
    @PostMapping("/records")
    public BaseResponse<TableRecordRspDTO> records(@RequestBody String body,
//                                                    @RequestBody BaseRequest reqDTO,
                                                   @RequestHeader(value = "X-Base-Request-Timestamp", required = false) String timestamp,
                                                   @RequestHeader(value = "X-Base-Request-Nonce", required = false) String nonce,
                                                   @RequestHeader(value = "x-base-signature", required = false) String feishuSign) {
        checkSign(body, timestamp, nonce, feishuSign);

        BaseRequest reqDTO = JsonUtil.toObject(body, BaseRequest.class);

        DatasourceConfig<String> tableMetaReqDTODatasourceConfig = JsonUtil.toObject(reqDTO.getParams(), new TypeReference<DatasourceConfig<String>>() {
        });

        //获取分页信息
        TableRecordReqDTO tableRecordReqDTO = JsonUtil.toObject(reqDTO.getParams(), TableRecordReqDTO.class);

        //获取前端传递信息
        TableRecordReqDTO req = JsonUtil.toObject(tableMetaReqDTODatasourceConfig.getDatasourceConfig(),TableRecordReqDTO.class);
        req.setPageToken(tableRecordReqDTO.getPageToken());
        req.setMaxPageSize(100);
        if(StrUtil.isBlank(req.getBaseUserId())  || StrUtil.isBlank(req.getModuleCode())){
            return BaseResponse.error(new BaseResponse.MsgBean(){
                {
                    setZh("参数不能为空");
                    setEn("Parameters cannot be empty");
                }
            });
        }
        if(CollUtil.isEmpty(req.getFields())){
            req.setFields((List<String>) redisHelper.get(CacheRegionEnum.MBD, req.getModuleCode()));
        }

        TableRecordRspDTO tableRecordRspDTO =  openApiService.getData(req);
        ThreadUtil.safeSleep(1000);
        return BaseResponse.success(tableRecordRspDTO);
    }
}
