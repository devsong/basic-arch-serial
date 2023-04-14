package io.github.devsong.serial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author zhisong.guan
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@EnableFeignClients
@EnableDiscoveryClient
public class SerialApiApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(SerialApiApp.class);
        ctx.registerShutdownHook();
    }

}
