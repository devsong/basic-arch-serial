package io.github.devsong.serial.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.github.devsong.base.common.AppCoordinate;
import lombok.Data;

/**
 * @author zhisong.guan
 * @date 2021/7/14
 */
@Configuration
@ConfigurationProperties(prefix = "serial.app")
@Data
public class SerialProperties extends AppCoordinate {
    /**
     * 项目名称
     */
    private String name;

    /**
     * 应用标识
     */
    private String appId;

    /**
     * 数据中心ID
     */
    private Integer dataCenterId;

    /**
     * 服务端口
     */
    private Integer serverPort;

    /**
     * zk地址
     */
    private String zk;
}
