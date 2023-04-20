package io.github.devsong.serial.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhisong.guan
 * @date 2022/10/2 07:16
 */
@Configuration
@ConfigurationProperties(prefix = "feature.toggle")
@Data
public class FeatureToggleProperties {
    private static final String OPEN = "open";
    private static final String CLOSE = "close";

    private String testToggle;

    public static boolean isOpen(String toggle) {
        return OPEN.equalsIgnoreCase(toggle);
    }

    public static boolean isClose(String toggle) {
        return CLOSE.equalsIgnoreCase(toggle);
    }
}
