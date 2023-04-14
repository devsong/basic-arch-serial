package io.github.devsong.serial.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import io.github.devsong.base.common.AppCoordinate;
import io.github.devsong.base.common.aspect.PerfBaseAspect;
import io.github.devsong.serial.config.properties.SerialProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 记录接口耗时日志,用于elk的日志分析
 *
 * @author guanzhisong
 */
@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class PerfAspect extends PerfBaseAspect {

    private final SerialProperties serialProperties;

    @Pointcut(REST_POINT_CUT + "||" + ANNOTATION_PERFLOG)
    public void log4Perf() {
    }

    @Around("log4Perf()")
    public Object log4PerfAround(ProceedingJoinPoint point) throws Throwable {
        return perfLog(point);
    }

    @Override
    public AppCoordinate appCoordinate() {
        return serialProperties;
    }
}
