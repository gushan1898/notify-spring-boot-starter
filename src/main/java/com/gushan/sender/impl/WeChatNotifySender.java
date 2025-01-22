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

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "notify", name = "wechat.enabled", havingValue = "true")
public class WeChatNotifySender implements NotifySender {
    
    private final NotifyProperties properties;
    private final MessageTemplateResolver templateResolver;
    private final RestTemplate restTemplate;
    
    @Override
    public void send(Exception exception) {
        String message = templateResolver.resolve(exception);
        String accessToken = getAccessToken();
        
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + accessToken;
        
        Map<String, Object> content = new HashMap<>();
        content.put("touser", properties.getWeChat().getToUser());
        content.put("msgtype", "text");
        content.put("agentid", properties.getWeChat().getAgentId());
        content.put("text", new HashMap<String, String>() {{
            put("content", message);
        }});
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(content, headers);
        restTemplate.postForEntity(url, request, String.class);
    }
    
    private String getAccessToken() {
        String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s",
                properties.getWeChat().getCorpId(),
                properties.getWeChat().getCorpSecret());
        
        Map<String, String> response = restTemplate.getForObject(url, Map.class);
        return response != null ? response.get("access_token") : null;
    }
} 