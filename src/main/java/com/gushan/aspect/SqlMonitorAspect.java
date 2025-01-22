package com.gushan.aspect;

import com.gushan.exception.SqlTimeoutException;
import com.gushan.properties.NotifyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SqlMonitorAspect {
    
    private final NotifyProperties properties;

    @Around("execution(* org.springframework.jdbc.core.JdbcTemplate.*(String, ..))")
    public Object monitorSqlExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String sql = joinPoint.getArgs()[0].toString();
        
        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            long timeout = properties.getSqlMonitor().getTimeout();
            
            if (executionTime > timeout) {
                throw new SqlTimeoutException(sql, executionTime, timeout);
            }
            
            if (log.isDebugEnabled()) {
                log.debug("SQL执行时间: {}ms, SQL: {}", executionTime, sql);
            }
        }
    }
} 