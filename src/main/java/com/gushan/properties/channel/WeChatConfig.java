package com.gushan.properties.channel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WeChatConfig extends BaseChannelConfig {
    private String toUser;

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    private String corpId;
    private String corpSecret;
    private String agentId;

    @Override
    public WeChatConfig copy() {
        WeChatConfig copy = new WeChatConfig();
        copy.setEnabled(this.isEnabled());
        copy.setToUser(this.toUser);
        copy.setCorpId(this.corpId);
        copy.setCorpSecret(this.corpSecret);
        copy.setAgentId(this.agentId);
        return copy;
    }
}
