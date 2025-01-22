package com.gushan.handler;

import com.gushan.properties.NotifyProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RateLimitHandler 测试类
 * <p>
 * 测试 RateLimitHandler 的频率限制功能
 */

class RateLimitHandlerTest {
    
    private RateLimitHandler rateLimitHandler;
    private NotifyProperties properties;
    
    /**
     * 初始化测试环境
     */
    @BeforeEach
    void setUp() {
        properties = new NotifyProperties();
        properties.setFrequency(60L);
        properties.setThreshold(3);
        rateLimitHandler = new RateLimitHandler(properties);
    }
    
    /**
     * 测试首次通知应该被允许
     */
    @Test
    void shouldAllowFirstNotification() {
        Exception exception = new RuntimeException("test");
        assertTrue(rateLimitHandler.shouldNotify(exception));
    }
    
    /**
     * 测试不应超过频率限制
     */
    @Test
    void shouldNotExceedFrequencyLimit() {
        Exception exception = new RuntimeException("test");
        assertTrue(rateLimitHandler.shouldNotify(exception));
        assertFalse(rateLimitHandler.shouldNotify(exception)); // 第二次应该被限制
    }
    
    /**
     * 测试不同的异常类型应该被允许
     */
    @Test
    void shouldAllowDifferentExceptions() {
        Exception exception1 = new RuntimeException("test1");
        Exception exception2 = new RuntimeException("test2");
        assertTrue(rateLimitHandler.shouldNotify(exception1));
        assertTrue(rateLimitHandler.shouldNotify(exception2));
    }
    
    /**
     * 测试在频率窗口结束后应该重置计数
     * @throws InterruptedException 如果线程被中断
     */
    @Test
    void shouldResetAfterFrequencyWindow() throws InterruptedException {
        properties.setFrequency(1L); // 设置1秒的频率窗口
        Exception exception = new RuntimeException("test");
        assertTrue(rateLimitHandler.shouldNotify(exception));
        assertFalse(rateLimitHandler.shouldNotify(exception));
        Thread.sleep(1100); // 等待超过频率窗口
        assertTrue(rateLimitHandler.shouldNotify(exception));
    }
}
