package com.gushan.sender.impl;

import com.gushan.properties.NotifyProperties;
import com.gushan.sender.NotifySender;
import com.gushan.template.MessageTemplateResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "notify", name = "feishu.enabled", havingValue = "true")
public class FeishuNotifySender implements NotifySender {
    
    private final NotifyProperties properties;
    private final MessageTemplateResolver templateResolver;
    private final RestTemplate restTemplate;
    
    @Override
    public void send(Exception exception) {
        String message = templateResolver.resolve(exception);
        String webhook = properties.getFeishu().getWebhook();
        String secret = properties.getFeishu().getSecret();
        
        long timestamp = System.currentTimeMillis() / 1000;
        String sign = generateSign(timestamp, secret);
        
        Map<String, Object> content = new HashMap<>();
        content.put("timestamp", timestamp);
        content.put("sign", sign);
        content.put("msg_type", "text");
        content.put("content", new HashMap<String, String>() {{
            put("text", message);
        }});
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(content, headers);
        restTemplate.postForEntity(webhook, request, String.class);
    }
    
    private String generateSign(long timestamp, String secret) {
        String stringToSign = timestamp + "\n" + secret;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signData);
        } catch (Exception e) {
            throw new RuntimeException("生成飞书签名失败", e);
        }
    }
} 