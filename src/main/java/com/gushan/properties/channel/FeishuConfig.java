package com.gushan.properties.channel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FeishuConfig extends BaseChannelConfig {
    private String webhook;
    private String secret;

    @Override
    public FeishuConfig copy() {
        FeishuConfig copy = new FeishuConfig();
        copy.setEnabled(this.isEnabled());
        copy.setWebhook(this.webhook);
        copy.setSecret(this.secret);
        return copy;
    }
}
