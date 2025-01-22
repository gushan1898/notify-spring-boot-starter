package com.gushan.extension;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "notify.custom", name = "enabled", havingValue = "true")
public class CustomNotifyAutoConfiguration {

    @Bean
    public CustomNotifySender customNotifySender() {
        return new CustomNotifySender();
    }
}
