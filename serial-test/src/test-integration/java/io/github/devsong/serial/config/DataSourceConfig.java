package io.github.devsong.serial.config;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import ch.vorburger.mariadb4j.springframework.MariaDB4jSpringService;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariDataSource;
import io.github.devsong.base.common.OSInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.apache.shardingsphere.core.yaml.swapper.ShardingRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.spring.boot.common.SpringBootPropertiesConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.encrypt.SpringBootEncryptRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.masterslave.SpringBootMasterSlaveRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.shadow.SpringBootShadowRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.sharding.SpringBootShardingRuleConfigurationProperties;
import org.apache.shardingsphere.underlying.common.config.inline.InlineExpressionParser;
import org.flywaydb.core.Flyway;
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
import java.util.*;

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
    public static final String MYSQL_DRIVER_CLASS_NAME = "org.mariadb.jdbc.Driver";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "123456";
    public static final int START_PORT = 50000;
    public static final int RANDOM_PORT_RANGE = 1000;

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
                dataSource = dataSource(mariaDB4jSpringService(), SCHEMA);
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

    private MariaDB4jSpringService mariaDB4jSpringService() {
        MariaDB4jSpringService mariaDB4jSpringService = new MariaDB4jSpringService();
        int port = new Random().nextInt(RANDOM_PORT_RANGE) + START_PORT;
        mariaDB4jSpringService.setDefaultPort(port);
        DBConfigurationBuilder config = mariaDB4jSpringService.getConfiguration();
        config.addArg("--character-set-server=utf8mb4");
        config.addArg("--lower_case_table_names=1");
        config.addArg("--collation-server=utf8mb4_general_ci");
        config.addArg("--user=root");
        config.addArg("--max-connections=512");
        config.setBaseDir(SystemUtils.JAVA_IO_TMPDIR + "/MariaDB4j/base");
        config.setDataDir(SystemUtils.JAVA_IO_TMPDIR + "/MariaDB4j/data");
        config.setDeletingTemporaryBaseAndDataDirsOnShutdown(true);

        if (OSInfo.isMacOSX() || OSInfo.isMacOS()) {
            // MacOS/MacOSX m1芯片可以选择使用本机的mariadb启动
            config.setUnpackingFromClasspath(false);
            config.setBaseDir("/opt/homebrew");
        }
        config.setLibDir(System.getProperty("java.io.tmpdir") + "/MariaDB4j/no-libs");

        log.info("mariadb4j port {}", port);
        mariaDB4jSpringService.start();
        return mariaDB4jSpringService;
    }

    private DataSource dataSource(MariaDB4jSpringService mariaDB4jSpringService, String schema) throws ManagedProcessException {
        mariaDB4jSpringService.getDB().createDB(schema);
        DBConfigurationBuilder config = mariaDB4jSpringService.getConfiguration();
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(MYSQL_DRIVER_CLASS_NAME);
        hikariDataSource.setJdbcUrl(config.getURL(schema));
        hikariDataSource.setUsername(USERNAME);
        hikariDataSource.setPassword(PASSWORD);

        Flyway flyway = Flyway.configure()
                .dataSource(hikariDataSource)
                .cleanDisabled(true)
                .locations("classpath:db/migration")
                .table("flyway_schema_history")
                .baselineOnMigrate(true)
                .baselineVersion("1")
                .validateOnMigrate(true)
                .schemas(schema)
                .defaultSchema(schema)
                .encoding("UTF-8")
                .outOfOrder(true)
                .load();
        flyway.migrate();

        return hikariDataSource;
    }
}
