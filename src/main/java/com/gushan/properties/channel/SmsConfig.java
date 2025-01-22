package com.gushan.properties.channel;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;

@Data
@EqualsAndHashCode(callSuper = true)
public class SmsConfig extends BaseChannelConfig {
    private String accessKey;
    private String accessSecret;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String templateCode;
    private String[] phoneNumbers;

    public String[] getPhoneNumbers() {
        return phoneNumbers != null ? Arrays.copyOf(phoneNumbers, phoneNumbers.length) : new String[0];
    }

    public void setPhoneNumbers(String[] phoneNumbers) {
        this.phoneNumbers = phoneNumbers != null ? Arrays.copyOf(phoneNumbers, phoneNumbers.length) : new String[0];
    }

    @Override
    public SmsConfig copy() {
        SmsConfig copy = new SmsConfig();
        copy.setEnabled(this.isEnabled());
        copy.setAccessKey(this.accessKey);
        copy.setAccessSecret(this.accessSecret);
        copy.setAccessKeyId(this.accessKeyId);
        copy.setAccessKeySecret(this.accessKeySecret);
        copy.setSignName(this.signName);
        copy.setTemplateCode(this.templateCode);
        copy.setPhoneNumbers(this.phoneNumbers);
        return copy;
    }
}
