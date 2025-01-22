package com.gushan.properties;

import com.gushan.properties.channel.*;
import java.util.Map;
import java.util.HashMap;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 通知配置属性类
 * <p>
 * 包含所有通知相关的配置项，支持通过application.yml进行配置
 */
@Data
@ConfigurationProperties(prefix = "notify")
public class NotifyProperties {
    /**
     * 需要扫描的包路径列表
     */
    private List<String> scanPackages;

    /**
     * 需要排除的异常类名列表
     */
    private List<String> excludeExceptions;

    /**
     * 通知阈值，达到该次数后才会发送通知
     * 默认值：3
     */
    private Integer threshold = 3;

    /**
     * 通知频率限制，单位：秒
     * 默认值：60
     */
    private Long frequency = 60L;

    /**
     * 通知模板配置
     */
    private Map<String, String> templates = new HashMap<>();

    /**
     * 是否包含堆栈信息
     * 默认值：true
     */
    private Boolean includeStackTrace = true;

    /**
     * 模板缓存配置
     */
    private TemplateCacheConfig templateCache = new TemplateCacheConfig();

    @Data
    public static class TemplateCacheConfig {
        /**
         * 是否启用模板缓存
         * 默认值：true
         */
        private Boolean enabled = true;

        /**
         * 缓存最大数量
         * 默认值：1000
         */
        private Integer maxSize = 1000;

        /**
         * 缓存过期时间（秒）
         * 默认值：3600
         */
        private Long expireAfterWrite = 3600L;
    }

    /**
     * 邮件通知配置
     */
    private EmailConfig email = new EmailConfig();

    /**
     * 钉钉通知配置
     */
    private DingTalkConfig dingTalk = new DingTalkConfig();

    /**
     * 微信通知配置
     */
    private WeChatConfig weChat = new WeChatConfig();

    /**
     * 飞书通知配置
     */
    private FeishuConfig feishu = new FeishuConfig();

    /**
     * 短信通知配置
     */
    private SmsConfig sms = new SmsConfig();

    /**
     * SQL监控通知配置
     */
    private SqlMonitorConfig sqlMonitor = new SqlMonitorConfig();

    /**
     * 获取默认通知模板
     * @return 通知模板内容
     */
    public String getTemplate() {
        return templates.getOrDefault("default", 
            "异常类型: ${exception.type}\n" +
            "异常信息: ${exception.message}\n" +
            "发生时间: ${timestamp}\n" +
            "堆栈信息: ${exception.stackTrace}");
    }
}
