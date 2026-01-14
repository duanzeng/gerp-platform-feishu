package com.gerp.platform.feishu.service;

import com.gerp.platform.feishu.model.dto.OpenApiBase;
import com.gerp.platform.feishu.model.dto.TableMetaReqDTO;
import com.gerp.platform.feishu.model.dto.TableRecordReqDTO;
import com.gerp.platform.feishu.model.dto.TableRecordRspDTO;
import com.gerp.platform.feishu.model.openapi.AccessTokenReqDTO;
import com.gerp.platform.feishu.model.openapi.AccessTokenRspDTO;
import com.kmniu.erpweb.v2.common.model.common.CustomResponse;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/19 17:25
 */
public interface OpenApiService {

    CustomResponse<AccessTokenRspDTO> getAccessToken(OpenApiBase reqDTO);

    String getSwagger(TableMetaReqDTO req);

    TableRecordRspDTO getData(TableRecordReqDTO reqDTO);


}
