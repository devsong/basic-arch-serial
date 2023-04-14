package io.github.devsong.serial.flyway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * @author zhisong.guan
 */
@Slf4j
@SpringBootApplication
public class RunFlywayApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(RunFlywayApp.class)
                .web(WebApplicationType.NONE)
                .run(args);
        ctx.registerShutdownHook();
        log.info("start flyway migration");
    }
}
