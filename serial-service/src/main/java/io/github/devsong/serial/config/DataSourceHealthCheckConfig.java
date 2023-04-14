package io.github.devsong.serial.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthContributorAutoConfiguration;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhisong.guan
 * @date 2022/9/16 16:33
 */
@Configuration
public class DataSourceHealthCheckConfig extends DataSourceHealthContributorAutoConfiguration {
    public DataSourceHealthCheckConfig(Map<String, DataSource> dataSources, ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders) {
        super(dataSources, metadataProviders);
    }

    @Value("${jdbc.validation-query:select 1}")
    private String defaultQuery;

    @Override
    protected AbstractHealthIndicator createIndicator(DataSource source) {
        DataSourceHealthIndicator indicator = (DataSourceHealthIndicator) super.createIndicator(source);
        indicator.setQuery(defaultQuery);
        return indicator;
    }
}
