package com.gushan.exception;

import com.gushan.annotation.NotifyIgnore;

@NotifyIgnore
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
} 