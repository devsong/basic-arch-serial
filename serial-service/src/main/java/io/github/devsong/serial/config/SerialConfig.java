package io.github.devsong.serial.config;

import static io.github.devsong.base.entity.GlobalConstant.DATE_FORMAT;
import static io.github.devsong.base.entity.GlobalConstant.DATE_TIME_FORMAT;

import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.github.devsong.base.common.util.SpringContextUtil;
import io.github.devsong.base.log.LogInterceptor;

@Configuration
public class SerialConfig {

    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }


    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.simpleDateFormat(DATE_TIME_FORMAT);
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        };
    }

    @Bean
    public LogInterceptor logInterceptor() {
        return new LogInterceptor();
    }
}
