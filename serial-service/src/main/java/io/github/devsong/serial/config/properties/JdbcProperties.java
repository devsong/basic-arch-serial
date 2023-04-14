package io.github.devsong.serial.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "jdbc")
public class JdbcProperties {
    private String url;
    private String driverClassName;
    private String dataSourceType;
    private String wAccount;
    private String wPassword;
    private String rAccount;
    private String rPassword;
    private String initialSize;
    private String minIdle;
    private String maxActive;
    private String maxWait;
    private String timeBetweenEvictionRunsMillis;
    private String minEvictableIdleTimeMillis;
    private String maxEvictableIdleTimeMillis;
    private String validationQuery;
    private String testWhileIdle;
    private String testOnBorrow;
    private String testOnReturn;
    private String logAbandoned;
    private String removeAbandoned;
    private String removeAbandonedTimeout;
}
