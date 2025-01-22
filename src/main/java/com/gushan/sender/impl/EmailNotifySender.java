package com.gushan.sender.impl;

import com.gushan.properties.NotifyProperties;
import com.gushan.sender.NotifySender;
import com.gushan.template.MessageTemplateResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "notify.email", name = "enabled", havingValue = "true")
@Order(2)
public class EmailNotifySender implements NotifySender {
    
    private final NotifyProperties properties;
    private final MessageTemplateResolver templateResolver;
    private final JavaMailSender mailSender;
    
    @Override
    public void send(Exception exception) {
        String message = templateResolver.resolve(exception);
        
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(properties.getEmail().getFrom());
        mailMessage.setTo(properties.getEmail().getTo());
        mailMessage.setSubject("异常通知");
        mailMessage.setText(message);
        
        mailSender.send(mailMessage);
    }
} 