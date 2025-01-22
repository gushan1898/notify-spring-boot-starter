package com.gushan.properties.channel;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;

@Data
@EqualsAndHashCode(callSuper = true)
public class DingTalkConfig extends BaseChannelConfig {
    private String webhook;
    private String secret;
    private String[] atMobiles;
    private Boolean atAll = false;

    public String[] getAtMobiles() {
        return atMobiles != null ? Arrays.copyOf(atMobiles, atMobiles.length) : new String[0];
    }

    public void setAtMobiles(String[] atMobiles) {
        this.atMobiles = atMobiles != null ? Arrays.copyOf(atMobiles, atMobiles.length) : new String[0];
    }

    @Override
    public DingTalkConfig copy() {
        DingTalkConfig copy = new DingTalkConfig();
        copy.setEnabled(this.isEnabled());
        copy.setWebhook(this.webhook);
        copy.setSecret(this.secret);
        copy.setAtMobiles(this.atMobiles);
        copy.setAtAll(this.atAll);
        return copy;
    }
}
