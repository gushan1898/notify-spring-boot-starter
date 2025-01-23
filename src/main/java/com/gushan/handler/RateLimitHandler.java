package com.gushan.handler;

import com.gushan.properties.NotifyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知频率限制处理器
 * <p>
 * 用于控制异常通知的频率，防止短时间内发送过多通知
 */
@Component
@RequiredArgsConstructor
public class RateLimitHandler {
    private final NotifyProperties properties;
    private final Map<String, Integer> countMap = new ConcurrentHashMap<>();
    private final Map<String, Long> timeMap = new ConcurrentHashMap<>();
    
    /**
     * 判断是否应该发送通知
     * <p>
     * 根据异常类型和配置的频率阈值进行判断：
     * 1. 如果距离上次通知时间超过配置频率，则允许通知
     * 2. 如果未超过频率，则检查是否达到通知阈值
     * 
     * @param exception 发生的异常
     * @return true表示允许发送通知，false表示需要限制
     */
    public boolean shouldNotify(Exception exception) {
        // 如果频率限制为0，则不进行频率限制
        if (properties.getFrequency() == 0) {
            return true;
        }
        
        String key = exception.getClass().getName() + ":" + exception.getMessage();
        long now = System.currentTimeMillis();
        
        synchronized (this) {
            Long lastTime = timeMap.get(key);
            if (lastTime == null || now - lastTime > properties.getFrequency() * 1000) {
                // 超过频率限制时间，重置计数
                countMap.put(key, 0);
                timeMap.put(key, now);
            }
            
            int count = countMap.getOrDefault(key, 0);
            if (count < properties.getThreshold()) {
                // 未达到阈值，允许发送通知并增加计数
                countMap.put(key, count + 1);
                return true;
            }
            
            // 达到阈值，限制发送
            return false;
        }
    }
}
