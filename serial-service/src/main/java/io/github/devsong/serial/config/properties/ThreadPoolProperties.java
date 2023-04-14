package io.github.devsong.serial.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @author zhisong.guan
 * @date 2022/10/18 16:49
 */
@Configuration
@ConfigurationProperties(prefix = "threadpool.config")
@Data
public class ThreadPoolProperties {
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;
    private Integer keepAliveSeconds;
}
