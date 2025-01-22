package com.gushan.sender.impl;

import com.gushan.properties.NotifyProperties;
import com.gushan.properties.channel.WeChatConfig;
import com.gushan.template.MessageTemplateResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeChatNotifySenderTest {
    
    @Mock
    private NotifyProperties properties;
    
    @Mock
    private MessageTemplateResolver templateResolver;
    
    @Mock
    private RestTemplate restTemplate;
    
    private WeChatNotifySender sender;
    
    @BeforeEach
    void setUp() {
        WeChatConfig config = new WeChatConfig();
        config.setCorpId("test-corp-id");
        config.setCorpSecret("test-secret");
        config.setAgentId("1000001");
        config.setToUser("user1|user2");
        
        when(properties.getWeChat()).thenReturn(config);
        when(templateResolver.resolve(any())).thenReturn("Test message");
        
        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("access_token", "test-token");
        when(restTemplate.getForObject(any(String.class), eq(Map.class)))
                .thenReturn(tokenResponse);
        
        sender = new WeChatNotifySender(properties, templateResolver, restTemplate);
    }
    
    @Test
    void shouldSendMessage() {
        Exception testException = new RuntimeException("Test exception");
        sender.send(testException);
        
        verify(restTemplate).postForEntity(any(String.class), any(), eq(String.class));
    }
} 