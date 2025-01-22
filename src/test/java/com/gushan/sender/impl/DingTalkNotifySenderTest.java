package com.gushan.sender.impl;

import com.gushan.properties.NotifyProperties;
import com.gushan.properties.channel.DingTalkConfig;
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
class DingTalkNotifySenderTest {
    
    @Mock
    private NotifyProperties properties;
    
    @Mock
    private MessageTemplateResolver templateResolver;
    
    @Mock
    private RestTemplate restTemplate;
    
    private DingTalkNotifySender sender;
    
    @BeforeEach
    void setUp() {
        DingTalkConfig config = new DingTalkConfig();
        config.setWebhook("https://oapi.dingtalk.com/robot/send");
        config.setSecret("test-secret");
        
        when(properties.getDingTalk()).thenReturn(config);
        when(templateResolver.resolve(any())).thenReturn("Test message");
        
        sender = new DingTalkNotifySender(properties, templateResolver, restTemplate);
    }
    
    @Test
    void shouldSendMessage() {
        Exception testException = new RuntimeException("Test exception");
        sender.send(testException);
        
        verify(restTemplate).postForEntity(any(String.class), any(), eq(String.class));
    }
} 