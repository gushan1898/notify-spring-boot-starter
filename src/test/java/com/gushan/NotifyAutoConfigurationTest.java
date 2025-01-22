package com.gushan;

import com.gushan.config.NotifyAutoConfiguration;
import com.gushan.handler.GlobalExceptionHandler;
import com.gushan.handler.RateLimitHandler;
import com.gushan.properties.NotifyProperties;
import com.gushan.sender.NotifySenderFactory;
import com.gushan.template.MessageTemplateResolver;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;

class NotifyAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(NotifyAutoConfiguration.class));

    @Test
    void shouldLoadDefaultConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(NotifyProperties.class);
            assertThat(context).hasSingleBean(GlobalExceptionHandler.class);
            assertThat(context).hasSingleBean(RateLimitHandler.class);
            assertThat(context).hasSingleBean(MessageTemplateResolver.class);
            assertThat(context).hasSingleBean(NotifySenderFactory.class);
        });
    }

    @Test
    void shouldLoadEmailConfiguration() {
        contextRunner
                .withPropertyValues(
                        "notify.email.enabled=true",
                        "notify.email.host=smtp.example.com",
                        "notify.email.port=465",
                        "notify.email.username=test@example.com",
                        "notify.email.password=password"
                )
                .run(context -> {
                    assertThat(context).hasBean("javaMailSender");
                });
    }

    @Test
    void shouldNotLoadEmailConfigurationWhenDisabled() {
        contextRunner
                .withPropertyValues("notify.email.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean("javaMailSender");
                });
    }
} 