package com.gushan.properties.channel;

import lombok.Data;

@Data
public abstract class BaseChannelConfig {
    private boolean enabled;

    public abstract BaseChannelConfig copy();
}
