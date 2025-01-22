package com.gushan.integration;

import com.gushan.config.NotifyAutoConfiguration;
import com.gushan.handler.GlobalExceptionHandler;
import com.gushan.properties.NotifyProperties;
import com.gushan.sender.NotifySender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * 通知功能集成测试类
 * <p>
 * 测试通知功能的完整集成流程，包括配置加载、异常处理、通知发送等
 */
@SpringBootTest(classes = NotifyAutoConfiguration.class)
@TestPropertySource(properties = {
    "notify.scan-packages=com.gushan",
    "notify.threshold=1",
    "notify.frequency=0",
    "notify.email.enabled=true",
    "notify.email.host=smtp.example.com",
    "notify.email.port=465",
    "notify.email.username=test@example.com",
    "notify.email.password=password"
})
class NotifyIntegrationTest {

    @Autowired
    private GlobalExceptionHandler exceptionHandler;

    @Autowired
    private NotifyProperties properties;

    @MockBean
    private NotifySender notifySender;

    /**
     * 测试当异常发生时应该发送通知
     */
    @Test
    void shouldSendNotificationWhenExceptionOccurs() {
        RuntimeException exception = new RuntimeException("Test Exception");
        exceptionHandler.handleException(exception);

        verify(notifySender, times(1)).send(any(Exception.class));
    }

    /**
     * 测试应该遵守阈值设置
     */
    @Test
    void shouldRespectThresholdSettings() {
        properties.setThreshold(Integer.valueOf(2));
        RuntimeException exception = new RuntimeException("Test Exception");

        exceptionHandler.handleException(exception);
        verify(notifySender, times(0)).send(any(Exception.class));

        exceptionHandler.handleException(exception);
        verify(notifySender, times(1)).send(any(Exception.class));
    }

    /**
     * 测试应该遵守频率设置
     */
    @Test
    void shouldRespectFrequencySettings() {
        properties.setFrequency(1L);
        RuntimeException exception = new RuntimeException("Test Exception");

        exceptionHandler.handleException(exception);
        exceptionHandler.handleException(exception);

        verify(notifySender, times(1)).send(any(Exception.class));
    }
}
