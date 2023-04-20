package io.github.devsong.serial.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthContributorAutoConfiguration;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;

/**
 * @author zhisong.guan
 * @date 2022/9/16 16:33
 */
// @Configuration
public class DataSourceHealthCheckConfig extends DataSourceHealthContributorAutoConfiguration {
    public DataSourceHealthCheckConfig( ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders) {
        super( metadataProviders);
    }

    @Value("${jdbc.validation-query:select 1}")
    private String defaultQuery;

//    @Override
//    protected AbstractHealthIndicator createIndicator(DataSource source) {
//        DataSourceHealthIndicator indicator = (DataSourceHealthIndicator) super.createIndicator(source);
//        indicator.setQuery(defaultQuery);
//        return indicator;
//    }
}
