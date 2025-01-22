package com.gushan.properties.channel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SqlMonitorConfig extends BaseChannelConfig {

    @Override
    public SqlMonitorConfig copy() {
        SqlMonitorConfig config = new SqlMonitorConfig();
        config.setEnabled(this.isEnabled());
        config.setTimeout(this.getTimeout());
        config.setIncludeFullSql(this.getIncludeFullSql());
        config.setIncludeParams(this.getIncludeParams());
        return config;
    }
    /**
     * SQL执行超时阈值（毫秒）
     */
    private Long timeout = 3000L;

    /**
     * 是否记录完整SQL语句
     */
    private Boolean includeFullSql = true;

    /**
     * 是否记录SQL参数
     */
    private Boolean includeParams = true;
}
