package com.gushan.properties.channel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmailConfig extends BaseChannelConfig {

    @Override
    public EmailConfig copy() {
        EmailConfig config = new EmailConfig();
        config.setEnabled(this.isEnabled());
        config.setHost(this.getHost());
        config.setPort(this.getPort());
        config.setUsername(this.getUsername());
        config.setPassword(this.getPassword());
        config.setFrom(this.getFrom());
        config.setTo(this.getTo() != null ? this.getTo().clone() : null);
        return config;
    }
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String from;
    private String[] to;
}
