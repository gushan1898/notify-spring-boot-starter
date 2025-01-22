package com.gushan.sender.impl;

import com.gushan.properties.NotifyProperties;
import com.gushan.sender.NotifySender;
import com.gushan.template.MessageTemplateResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "notify.dingtalk", name = "enabled", havingValue = "true")
public class DingTalkNotifySender implements NotifySender {
    
    private final NotifyProperties properties;
    private final MessageTemplateResolver templateResolver;
    private final RestTemplate restTemplate;

    @Override
    public void send(Exception exception) {
        log.info("开始发送钉钉通知: {}", exception.getMessage());
        String message = templateResolver.resolve(exception);
        String webhook = properties.getDingTalk().getWebhook();
        String secret = properties.getDingTalk().getSecret();
        
        long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        String sign = generateSign(stringToSign, secret);
        
        String url = webhook + "&timestamp=" + timestamp + "&sign=" + sign;
        
        Map<String, Object> content = new HashMap<>();
        content.put("msgtype", "text");
        content.put("text", new HashMap<String, String>() {{
            put("content", message);
        }});
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(content, headers);
        restTemplate.postForEntity(url, request, String.class);
        log.info("钉钉通知发送成功");
    }
    
    private String generateSign(String stringToSign, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signData);
        } catch (Exception e) {
            log.error("钉钉通知发送失败", e);
            throw new RuntimeException("生成钉钉签名失败", e);
        }
    }
} 