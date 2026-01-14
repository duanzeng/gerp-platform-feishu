package com.gerp.platform.feishu.server.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @Author: duanzengqiang
 * @Date: 2025/12/19 17:35
 */
@Aspect
@Component
@Slf4j
public class ServiceLogAspect {

    @Around("execution(* com.gerp.platform.feishu.service.impl.OpenApiServiceImpl.*(..))")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        String fullMethodName = signature.getDeclaringTypeName() + "." + methodName;

        long startTime = System.currentTimeMillis();

        // 记录请求参数
        Object[] args = joinPoint.getArgs();
        log.info("[{}] 方法调用开始 - 参数: {}", fullMethodName, args);

        try {
            Object result = joinPoint.proceed();

            // 记录响应结果
            long endTime = System.currentTimeMillis();
            log.info("[{}] 方法调用结束 - 耗时: {}ms, 返回值: {}",
                    fullMethodName, (endTime - startTime), result);

            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("[{}] 方法调用异常 - 耗时: {}ms, 错误: {}",
                    fullMethodName, (endTime - startTime), e.getMessage(), e);
            throw e;
        }
    }
}
