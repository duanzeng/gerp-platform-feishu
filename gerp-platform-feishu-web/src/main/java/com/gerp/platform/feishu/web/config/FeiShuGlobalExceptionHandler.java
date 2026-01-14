package com.gerp.platform.feishu.web.config;

import cn.hutool.core.util.StrUtil;
import com.gerp.platform.feishu.model.base.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

/**
 * @Author: duanzengqiang
 * @Date: 2022/9/5 16:39
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FeiShuGlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HandlerMethod m) {
        log.error("请求出现异常，异常信息为:{}", e.getMessage(), e);
        String strReturn = StrUtil.format("请求出现异常，异常信息为:{}", e.getMessage());
        BaseResponse.MsgBean msgBean = new BaseResponse.MsgBean();
        msgBean.setZh(strReturn);
        String enStrReturn = StrUtil.format("An error occurred with your request. The error details are:{}", e.getMessage());
        msgBean.setEn(enStrReturn);
        return BaseResponse.error(msgBean);
    }
}
