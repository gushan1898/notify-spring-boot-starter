package com.gushan.handler;

import com.gushan.properties.NotifyProperties;
import com.gushan.sender.NotifySender;
import com.gushan.util.NotifyUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * <p>
 * 负责捕获并处理应用程序中的异常，根据配置决定是否发送通知
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    private final List<NotifySender> notifySenders;
    private final NotifyProperties properties;
    private final RateLimitHandler rateLimitHandler;
    
    /**
     * 异常处理方法
     * <p>
     * 处理流程：
     * 1. 检查异常是否在忽略列表中
     * 2. 检查是否达到通知阈值
     * 3. 尝试通过配置的通知渠道发送异常通知
     * 
     * @param e 捕获的异常对象
     */
    @ExceptionHandler(Exception.class)
    public void handleException(Exception e) {
        if (NotifyUtils.shouldIgnore(e, properties)) {
            log.debug("异常被忽略: {}", e.getMessage());
            return;
        }
        
        // 先检查是否应该发送通知
        if (!rateLimitHandler.shouldNotify(e)) {
            log.debug("异常未达到通知阈值: {}", e.getMessage());
            return;
        }

        // 确保在允许发送通知的情况下才执行发送逻辑
        boolean sent = false;
        for (NotifySender sender : notifySenders) {
            try {
                if (rateLimitHandler.shouldNotify(e)) {
                    sender.send(e);
                    sent = true;
                    break;
                }
            } catch (Exception ex) {
                log.error("通知发送失败: {}", sender.getClass().getSimpleName(), ex);
            }
        }
        
        if (!sent) {
            log.debug("未发送任何通知");
        }
    }
}
