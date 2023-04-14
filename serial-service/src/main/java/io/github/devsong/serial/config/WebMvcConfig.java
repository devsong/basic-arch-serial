package io.github.devsong.serial.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.devsong.base.log.LogInterceptor;
import lombok.RequiredArgsConstructor;

/**
 * @author zhisong.guan
 * @date 2022/10/15 12:30
 */
@Component
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final LogInterceptor logInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor).addPathPatterns("/**");
    }
}
