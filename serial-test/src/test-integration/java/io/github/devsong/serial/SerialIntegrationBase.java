package io.github.devsong.serial;


import io.github.devsong.base.entity.GlobalConstant;
import io.github.devsong.base.test.IntegrationBaseTest;
import io.github.devsong.base.test.TestConstants;
import io.github.devsong.serial.mapper.SerialAllocMapper;
import io.github.devsong.serial.service.segment.SerialAllocService;
import io.github.devsong.serial.service.snowflake.SnowflakeIDGenImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import redis.embedded.RedisServer;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SerialApiApp.class, webEnvironment = RANDOM_PORT)
@ComponentScan(basePackageClasses = {SerialIntegrationBase.class, SerialApiApp.class})
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        SpringBootConfiguration.class,
        FlywayAutoConfiguration.class
})
@ActiveProfiles(GlobalConstant.PROFILE_TEST_ENV)
public class SerialIntegrationBase extends IntegrationBaseTest {
    private static RedisServer REDIS_SERVER = new RedisServer(TestConstants.REDIS_EMBEDDED_PORT);

    static {
        REDIS_SERVER.start();
    }

    ///////////////////////////////////////
    // service
    ///////////////////////////////////////
    @Autowired
    protected SerialAllocService serialAllocService;

    @Autowired
    protected SnowflakeIDGenImpl snowflakeIDGen;

    ///////////////////////////////////////
    // mapper
    ///////////////////////////////////////
    @Autowired
    protected SerialAllocMapper serialAllocMapper;

    @Autowired
    protected ApplicationContext context;

    @Override
    public ApplicationContext getContext() {
        return context;
    }

    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

}
