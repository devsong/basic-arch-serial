package io.github.devsong.serial.flyway;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "flyway")
@ConditionalOnProperty(prefix = "flyway",name = "enabled",havingValue = "true")
@Data
public class FlywayCustomProperties {
    private Boolean enabled;
    private List<FlywayProperties> ds;
}


