package com.gushan.sender.impl;

import com.gushan.properties.NotifyProperties;
import com.gushan.properties.channel.FeishuConfig;
import com.gushan.template.MessageTemplateResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeishuNotifySenderTest {
    
    @Mock
    private NotifyProperties properties;
    
    @Mock
    private MessageTemplateResolver templateResolver;
    
    @Mock
    private RestTemplate restTemplate;
    
    private FeishuNotifySender sender;
    
    @BeforeEach
    void setUp() {
        FeishuConfig config = new FeishuConfig();
        config.setWebhook("https://open.feishu.cn/open-apis/bot/v2/hook/xxx");
        config.setSecret("test-secret");
        
        when(properties.getFeishu()).thenReturn(config);
        when(templateResolver.resolve(any())).thenReturn("Test message");
        
        sender = new FeishuNotifySender(properties, templateResolver, restTemplate);
    }
    
    @Test
    void shouldSendMessage() {
        Exception testException = new RuntimeException("Test exception");
        sender.send(testException);
        
        verify(restTemplate).postForEntity(any(String.class), any(), eq(String.class));
    }
} 