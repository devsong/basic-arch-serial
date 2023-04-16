package io.github.devsong.serial.config;

import io.github.devsong.base.test.truncate.TruncateDatabaseService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * date:  2023/4/16
 * author:guanzhisong
 */
@Configuration
public class TestBeanConfig {
    @Bean
    public TruncateDatabaseService truncateDatabaseService() {
        return new TruncateDatabaseService();
    }
}
