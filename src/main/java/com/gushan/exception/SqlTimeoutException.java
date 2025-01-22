package com.gushan.exception;

import lombok.Getter;

@Getter
public class SqlTimeoutException extends RuntimeException {
    private final String sql;
    private final long executionTime;
    private final long timeout;

    public SqlTimeoutException(String sql, long executionTime, long timeout) {
        super(String.format("SQL执行超时 - 执行时间: %dms, 超时阈值: %dms", executionTime, timeout));
        this.sql = sql;
        this.executionTime = executionTime;
        this.timeout = timeout;
    }
} 