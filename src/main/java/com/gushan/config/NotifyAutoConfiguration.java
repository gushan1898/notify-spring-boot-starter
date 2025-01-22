package com.gushan.config;

import com.gushan.handler.GlobalExceptionHandler;
import com.gushan.handler.RateLimitHandler;
import com.gushan.properties.NotifyProperties;
import com.gushan.sender.NotifySender;
import com.gushan.sender.impl.*;
import com.gushan.template.MessageTemplateResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * 通知自动配置类
 * <p>
 * 负责初始化通知相关的Bean，包括异常处理器、限流处理器、消息模板解析器等
 */
@Configuration
@EnableConfigurationProperties(NotifyProperties.class)
public class NotifyAutoConfiguration {
    
    /**
     * 全局异常处理器Bean
     * @param notifySenders 通知发送器列表
     * @param properties 通知配置属性
     * @param rateLimitHandler 限流处理器
     * @return 全局异常处理器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler(List<NotifySender> notifySenders,
                                                       NotifyProperties properties,
                                                       RateLimitHandler rateLimitHandler) {
        return new GlobalExceptionHandler(notifySenders, properties, rateLimitHandler);
    }
    
    /**
     * 限流处理器Bean
     * @param properties 通知配置属性
     * @return 限流处理器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public RateLimitHandler rateLimitHandler(NotifyProperties properties) {
        return new RateLimitHandler(properties);
    }
    
    /**
     * 消息模板解析器Bean
     * @param properties 通知配置属性
     * @return 消息模板解析器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageTemplateResolver messageTemplateResolver(NotifyProperties properties) {
        MessageTemplateResolver resolver = new MessageTemplateResolver(properties);
        // 配置模板缓存
        if (properties.getTemplateCache().getEnabled()) {
            resolver.enableCache(
                properties.getTemplateCache().getMaxSize(),
                properties.getTemplateCache().getExpireAfterWrite()
            );
            // 添加缓存统计日志
            resolver.setCacheStatsEnabled(true);
        }
        return resolver;
    }
    
    /**
     * RestTemplate Bean
     * @return RestTemplate实例
     */
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    /**
     * 邮件通知配置类
     */
    @Configuration
    @ConditionalOnProperty(prefix = "notify.email", name = "enabled", havingValue = "true")
    static class EmailConfiguration {
        /**
         * JavaMailSender Bean
         * @param properties 通知配置属性
         * @return JavaMailSender实例
         */
        @Bean
        @ConditionalOnMissingBean
        public JavaMailSender javaMailSender(NotifyProperties properties) {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(properties.getEmail().getHost());
            mailSender.setPort(properties.getEmail().getPort());
            mailSender.setUsername(properties.getEmail().getUsername());
            mailSender.setPassword(properties.getEmail().getPassword());
            return mailSender;
        }
    }
}
