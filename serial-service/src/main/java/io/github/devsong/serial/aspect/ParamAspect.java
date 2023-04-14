package io.github.devsong.serial.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import io.github.devsong.base.common.AppCoordinate;
import io.github.devsong.base.common.aspect.ParamBaseAspect;
import io.github.devsong.serial.config.properties.SerialProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 参数aop
 *
 * @author zhisong.guan
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ParamAspect extends ParamBaseAspect {

    private final SerialProperties serialProperties;

    /**
     * 定义切入点
     */
    @Pointcut(REST_POINT_CUT)
    public void log4Param() {

    }

    @Around("log4Param()")
    public Object log4ParamAround(ProceedingJoinPoint point) throws Throwable {
        return paramLog(point);
    }

    @Override
    public AppCoordinate appCoordinate() {
        return serialProperties;
    }
}
