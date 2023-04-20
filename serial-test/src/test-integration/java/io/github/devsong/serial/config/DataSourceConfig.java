package io.github.devsong.serial.config;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService;
import com.google.common.collect.Lists;
import io.github.devsong.base.test.Mariadb4jUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.core.yaml.swapper.ShardingRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.spring.boot.common.SpringBootPropertiesConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.encrypt.SpringBootEncryptRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.masterslave.SpringBootMasterSlaveRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.shadow.SpringBootShadowRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.sharding.SpringBootShardingRuleConfigurationProperties;
import org.apache.shardingsphere.underlying.common.config.inline.InlineExpressionParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhisong.guan
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({
        SpringBootShardingRuleConfigurationProperties.class,
        SpringBootMasterSlaveRuleConfigurationProperties.class, SpringBootEncryptRuleConfigurationProperties.class,
        SpringBootPropertiesConfigurationProperties.class, SpringBootShadowRuleConfigurationProperties.class})
@RequiredArgsConstructor
public class DataSourceConfig implements EnvironmentAware {
    public static final String SCHEMA = "arch_common";

    public static final String MIGRATION_SCRIPTS = "classpath:db/migration";

    private final SpringBootShardingRuleConfigurationProperties shardingRule;

    private final SpringBootPropertiesConfigurationProperties props;

    private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

    private final List<JdbcTemplate> jdbcTemplates = Lists.newArrayList();

    @Bean
    public DataSource shardingDataSource() throws SQLException {
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, new ShardingRuleConfigurationYamlSwapper().swap(shardingRule), props.getProps());
    }

    @Bean
    @Qualifier("jdbcTemplates")
    public List<JdbcTemplate> jdbcTemplates() {
        return jdbcTemplates;
    }

    @Override
    public void setEnvironment(final Environment environment) {
        String prefix = "spring.shardingsphere.datasource.";
        String slaveSuffix = "slave";
        List<String> dataSourceNames = getDataSourceNames(environment, prefix);
        Collections.sort(dataSourceNames);
        for (String each : dataSourceNames) {
            if (each.endsWith(slaveSuffix)) {
                continue;
            }
            DataSource dataSource = null;
            try {
                MariaDB4jSpringService mariaDB4jSpringService = Mariadb4jUtil.mariaDB4jSpringService();
                dataSource = Mariadb4jUtil.buildDataSource(mariaDB4jSpringService, SCHEMA, MIGRATION_SCRIPTS);
            } catch (ManagedProcessException e) {
                e.printStackTrace();
            }
            dataSourceMap.put(each, dataSource);
            dataSourceMap.put(each + slaveSuffix, dataSource);
            jdbcTemplates.add(new JdbcTemplate(dataSource));
        }
    }

    private List<String> getDataSourceNames(final Environment environment, final String prefix) {
        StandardEnvironment standardEnv = (StandardEnvironment) environment;
        standardEnv.setIgnoreUnresolvableNestedPlaceholders(true);
        return null == standardEnv.getProperty(prefix + "name")
                ? new InlineExpressionParser(standardEnv.getProperty(prefix + "names")).splitAndEvaluate() : Collections.singletonList(standardEnv.getProperty(prefix + "name"));
    }


}
