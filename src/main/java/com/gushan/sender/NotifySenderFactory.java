package com.gushan.sender;

import com.gushan.properties.NotifyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知发送器工厂类
 * <p>
 * 负责管理所有通知发送器实例，并提供统一的发送接口
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotifySenderFactory {
    private final ObjectProvider<NotifySender> senderProvider;
    private final NotifyProperties properties;
    
    private List<NotifySender> senders;
    
    /**
     * 获取所有通知发送器实例
     * <p>
     * 该方法会返回所有已启用的通知发送器，并按优先级排序
     * 
     * @return 已排序的通知发送器列表
     * @throws IllegalStateException 如果没有启用任何通知渠道
     */
    public List<NotifySender> getNotifySenders() {
        if (senders == null) {
            // 获取所有通知发送器并按优先级排序
            senders = new ArrayList<>();
            senderProvider.forEach(senders::add);
            senders.sort(Comparator.comparing(sender -> 
                sender.getClass().getAnnotation(org.springframework.core.annotation.Order.class)
                    .value(), Comparator.naturalOrder()));
            
            if (senders.isEmpty()) {
                throw new IllegalStateException("No notify sender is enabled. Please enable at least one notify channel.");
            }
            
            log.info("已启用的通知渠道: {}", 
                senders.stream()
                    .map(sender -> sender.getClass().getSimpleName())
                    .collect(Collectors.toList()));
        }
        return senders;
    }
    
    /**
     * 发送异常通知
     * <p>
     * 该方法会遍历所有通知发送器，直到成功发送通知为止
     * 
     * @param exception 需要通知的异常对象
     * @throws RuntimeException 如果所有通知渠道都发送失败
     */
    public void send(Exception exception) {
        List<Exception> errors = new ArrayList<>();
        
        // 遍历所有通知渠道发送通知
        for (NotifySender sender : getNotifySenders()) {
            try {
                sender.send(exception);
                // 发送成功后直接返回
                return;
            } catch (Exception e) {
                log.error("通知发送失败: {}", sender.getClass().getSimpleName(), e);
                errors.add(e);
            }
        }
        
        // 如果所有通知渠道都失败了，抛出异常
        if (!errors.isEmpty()) {
            throw new RuntimeException("所有通知渠道都发送失败", errors.get(0));
        }
    }
}
