package io.github.devsong.serial;


import io.github.devsong.base.test.TestBase;
import io.github.devsong.base.test.TestConstants;
import io.github.devsong.serial.mapper.SerialAllocMapper;
import io.github.devsong.serial.service.segment.SerialAllocService;
import io.github.devsong.serial.service.snowflake.SnowflakeIDGenImpl;
import io.github.devsong.serial.truncate.TruncateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import redis.embedded.RedisServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Disabled
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SerialApiApp.class, webEnvironment = RANDOM_PORT)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, SpringBootConfiguration.class, FlywayAutoConfiguration.class})
public class IntegrationTestBase extends TestBase {
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


    ///////////////////////////////////////
    // common bean
    ///////////////////////////////////////
    @Autowired
    protected ApplicationContext context;

    protected List<Object> mockbeans = new ArrayList<>();

    @BeforeEach
    protected void setUp() {
        truncate();
        resetMocks();
    }

    private void truncate() {
        Map<String, TruncateService> truncateServiceMap = context.getBeansOfType(TruncateService.class);
        for (Map.Entry<String, TruncateService> entry : truncateServiceMap.entrySet()) {
            String key = entry.getKey();
            TruncateService service = entry.getValue();
            try {
                service.truncate();
            } catch (Exception e) {
                log.error("truncate key {} do truncate resources error", key, e);
            }
        }
    }

    private void resetMocks() {
        if (mockbeans.size() == 0) {
            for (String name : context.getBeanDefinitionNames()) {
                try {
                    Object bean = context.getBean(name);
                    if (MockUtil.isMock(bean)) {
                        mockbeans.add(bean);
                    }
                } catch (Exception e) {
                    log.error("reset mock bean {} error", name, e);
                }
            }
        }
        for (Object bean : mockbeans) {
            Mockito.reset(bean);
        }
    }
}
