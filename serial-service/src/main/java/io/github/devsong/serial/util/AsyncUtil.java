package io.github.devsong.serial.util;

import java.util.concurrent.Future;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.github.devsong.base.common.util.SpringContextUtil;
import io.github.devsong.serial.config.ThreadPoolConfig;

/**
 * 异步任务提交的工具类
 *
 * @author guanzhisong
 * @date 2021-07-19
 */
public class AsyncUtil {

    /**
     *
     * @param r
     */
    public static Future<?> submit(Runnable r) {
        return submit(r, false);
    }

    /**
     *
     * @param r
     * @param isAsyncTaskPool 是否使用异步任务task线程池
     */
    public static Future<?> submit(Runnable r, boolean isAsyncTaskPool) {
        ThreadPoolTaskExecutor executor;
        if (isAsyncTaskPool) {
            executor = SpringContextUtil.getBean(ThreadPoolConfig.POOL_NAME_ASYNC);
        } else {
            executor = SpringContextUtil.getBean(ThreadPoolConfig.POOL_NAME);
        }
        return executor.submit(r);
    }
}
