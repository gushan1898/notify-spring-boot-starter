package com.gushan.template;

import com.gushan.properties.NotifyProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MessageTemplateResolver {
    private final NotifyProperties properties;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Cache<String, String> templateCache;
    private boolean cacheStatsEnabled = false;

    public void enableCache(int maxSize, long expireAfterWrite) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(expireAfterWrite, TimeUnit.SECONDS);
            
        if (cacheStatsEnabled) {
            builder.recordStats();
        }
        
        this.templateCache = builder.build();
    }

    public void setCacheStatsEnabled(boolean enabled) {
        this.cacheStatsEnabled = enabled;
    }

    public String resolve(Exception exception) {
        if (templateCache != null) {
            String cacheKey = exception.getClass().getName() + ":" + exception.getMessage();
            return templateCache.get(cacheKey, key -> {
                String template = properties.getTemplate();
                Map<String, String> params = new HashMap<>();
                
                params.put("timestamp", LocalDateTime.now().format(FORMATTER));
                params.put("exception.message", exception.getMessage());
                params.put("exception.type", exception.getClass().getName());
                params.put("exception.stackTrace", getStackTrace(exception));
                
                return replaceParams(template, params);
            });
        }
        
        String template = properties.getTemplate();
        Map<String, String> params = new HashMap<>();
        
        params.put("timestamp", LocalDateTime.now().format(FORMATTER));
        params.put("exception.message", exception.getMessage());
        params.put("exception.type", exception.getClass().getName());
        params.put("exception.stackTrace", getStackTrace(exception));
        
        return replaceParams(template, params);
    }
    
    private String getStackTrace(Exception exception) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
    
    private String replaceParams(String template, Map<String, String> params) {
        String result = template;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result = result.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
