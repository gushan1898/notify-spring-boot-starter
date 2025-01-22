package com.gushan.sender.impl;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.gushan.properties.NotifyProperties;
import com.gushan.properties.channel.SmsConfig;
import com.gushan.template.MessageTemplateResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmsNotifySenderTest {
    
    @Mock
    private NotifyProperties properties;
    
    @Mock
    private MessageTemplateResolver templateResolver;
    
    @Mock
    private IAcsClient acsClient;
    
    private SmsNotifySender sender;
    
    @BeforeEach
    void setUp() throws Exception {
        SmsConfig config = new SmsConfig();
        config.setAccessKey("test-key");
        config.setAccessSecret("test-secret");
        config.setSignName("测试签名");
        config.setTemplateCode("SMS_123456");
        config.setPhoneNumbers(new String[]{"13800138000"});
        
        when(properties.getSms()).thenReturn(config);
        when(templateResolver.resolve(any())).thenReturn("Test message");
        
        SendSmsResponse response = new SendSmsResponse();
        response.setCode("OK");
        response.setMessage("success");
        when(acsClient.getAcsResponse(any(SendSmsRequest.class))).thenReturn(response);
        
        sender = new SmsNotifySender(properties, templateResolver);
        // 使用反射注入 mock 的 acsClient
        ReflectionTestUtils.setField(sender, "acsClient", acsClient);
    }
    
    @Test
    void shouldSendMessage() throws Exception {
        Exception testException = new RuntimeException("Test exception");
        sender.send(testException);
        
        verify(acsClient).getAcsResponse(any(SendSmsRequest.class));
    }
} 