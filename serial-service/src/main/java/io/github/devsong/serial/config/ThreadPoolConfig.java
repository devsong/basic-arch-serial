package io.github.devsong.serial.config;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.alibaba.ttl.threadpool.TtlExecutors;

import io.github.devsong.base.common.thread.TtlThreadToolTaskExecutor;
import io.github.devsong.base.common.util.Threads;
import io.github.devsong.serial.config.properties.ThreadPoolProperties;
import lombok.RequiredArgsConstructor;

/**
 * 线程池配置
 *
 * @author guanzhisong
 **/
@Configuration
@ConditionalOnProperty(prefix = "threadpool.config", name = "enable", havingValue = "true")
@RequiredArgsConstructor
public class ThreadPoolConfig {
    public static final String POOL_NAME = "threadPoolTaskExecutor";
    public static final String POOL_NAME_ASYNC = "asyncThreadPoolTaskExecutor";
    public static final String POOL_NAME_SCHEDULE = "scheduledExecutorService";
    public static final String PREFIX_COMMON = "serial-common-pool-%d";
    public static final String PREFIX_ASYNC = "serial-async-pool-%d";
    public static final String PREFIX_SCHEDULE = "serial-schedule-pool-%d";
    private final ThreadPoolProperties threadPoolProperties;

    @Bean(name = POOL_NAME)
    @Primary
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = getThreadPoolTaskExecutor();
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern(PREFIX_COMMON).daemon(true).build();
        executor.setThreadFactory(threadFactory);
        return executor;
    }

    @Bean(name = POOL_NAME_ASYNC)
    public ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = getThreadPoolTaskExecutor();
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern(PREFIX_ASYNC).daemon(true).build();
        executor.setThreadFactory(threadFactory);
        return executor;
    }

    /**
     * 执行周期性或定时任务
     */
    @Bean(name = POOL_NAME_SCHEDULE)
    public ScheduledExecutorService scheduledExecutorService() {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern(PREFIX_SCHEDULE).daemon(true).build();

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(threadPoolProperties.getCorePoolSize(), threadFactory) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                Threads.printException(r, t);
            }
        };
        return TtlExecutors.getTtlScheduledExecutorService(scheduledExecutorService);
    }

    @NotNull
    private ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new TtlThreadToolTaskExecutor();
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        // 线程池对拒绝任务(无线程可用)的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
