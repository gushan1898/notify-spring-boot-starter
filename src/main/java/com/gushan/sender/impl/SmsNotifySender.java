package com.gushan.sender.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.gushan.properties.NotifyProperties;
import com.gushan.sender.NotifySender;
import com.gushan.template.MessageTemplateResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "notify", name = "sms.enabled", havingValue = "true")
public class SmsNotifySender implements NotifySender {
    
    private final NotifyProperties properties;
    private final MessageTemplateResolver templateResolver;
    private IAcsClient acsClient;
    
    private IAcsClient getAcsClient() {
        if (acsClient == null) {
            DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou",
                properties.getSms().getAccessKey(),
                properties.getSms().getAccessSecret()
            );
            acsClient = new DefaultAcsClient(profile);
        }
        return acsClient;
    }
    
    @Override
    public void send(Exception exception) {
        try {
            String message = templateResolver.resolve(exception);
            
            SendSmsRequest request = new SendSmsRequest();
            request.setSignName(properties.getSms().getSignName());
            request.setTemplateCode(properties.getSms().getTemplateCode());
            request.setPhoneNumbers(String.join(",", properties.getSms().getPhoneNumbers()));
            request.setTemplateParam("{\"message\":\"" + message + "\"}");
            
            log.info("开始发送短信通知: {}", message);
            getAcsClient().getAcsResponse(request);
            log.info("短信通知发送成功");
        } catch (Exception e) {
            log.error("发送短信失败", e);
            throw new RuntimeException("发送短信失败", e);
        }
    }
} 