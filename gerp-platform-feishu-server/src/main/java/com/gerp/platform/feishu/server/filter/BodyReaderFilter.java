package com.gerp.platform.feishu.server.filter;

import cn.hutool.core.util.StrUtil;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @Author: duanzengqiang
 * @Date: 2024/11/25 10:31
 */
@Component
public class BodyReaderFilter  implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(servletRequest instanceof HttpServletRequest) {
            initTraceId((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


     private void initTraceId(HttpServletRequest request, HttpServletResponse servletResponse) {
        //尝试获取http请求中的traceId
        String traceId = request.getHeader("traceId");

        //如果当前traceId为空或者为默认traceId， 则生成新的traceId
        if (StrUtil.isBlank(traceId) ) {
            traceId = UUID.randomUUID().toString();
            servletResponse.setHeader("traceId", traceId);
            MDC.put("traceId", traceId);
        }
    }


}
