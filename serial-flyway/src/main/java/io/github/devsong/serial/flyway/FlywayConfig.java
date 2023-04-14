package io.github.devsong.serial.flyway;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(prefix = "flyway", name = "enabled", havingValue = "true")
@Data
@RequiredArgsConstructor
@Slf4j
public class FlywayConfig implements InitializingBean {
    private final FlywayCustomProperties flywayCustomProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void init() {
        for (FlywayProperties prop : flywayCustomProperties.getDs()) {
            Flyway flyway = Flyway.configure()
                    .dataSource(prop.getUrl(), prop.getUser(), prop.getPassword())
                    .cleanDisabled(prop.isCleanDisabled())
                    .locations(prop.getLocations().toArray(new String[0]))
                    .table(prop.getTable())
                    .baselineOnMigrate(prop.isBaselineOnMigrate())
                    .baselineVersion(prop.getBaselineVersion())
                    .validateOnMigrate(prop.isValidateOnMigrate())
                    .schemas(prop.getSchemas().toArray(new String[0]))
                    .defaultSchema(prop.getDefaultSchema())
                    .encoding(prop.getEncoding())
                    .outOfOrder(prop.isOutOfOrder())
                    .load();
            flyway.migrate();
            log.info("migrate ds {} success", prop.getUrl());
        }
    }


}
