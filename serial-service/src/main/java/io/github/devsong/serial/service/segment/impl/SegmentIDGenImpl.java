package io.github.devsong.serial.service.segment.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import io.github.devsong.serial.entity.Segment;
import io.github.devsong.serial.entity.SegmentBuffer;
import io.github.devsong.serial.entity.common.Result;
import io.github.devsong.serial.entity.common.Status;
import io.github.devsong.serial.entity.po.SerialAlloc;
import io.github.devsong.serial.service.IDGen;
import io.github.devsong.serial.service.segment.SegmentIdGenService;
import io.github.devsong.serial.service.segment.SerialAllocService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhisong.guan
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SegmentIDGenImpl implements IDGen, SegmentIdGenService {
    /**
     * 左移最长位数
     */
    private static final int MAX_SHIFT = 4;
    /**
     * IDCache未初始化成功时的异常码
     */
    private static final long EXCEPTION_ID_IDCACHE_INIT_FALSE = -1;
    /**
     * key不存在时的异常码
     */
    private static final long EXCEPTION_ID_KEY_NOT_EXISTS = -2;
    /**
     * SegmentBuffer中的两个Segment均未从DB中装载时的异常码
     */
    private static final long EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL = -3;
    /**
     * 最大步长不超过100,0000
     */
    private static final int MAX_STEP = 1000000;
    /**
     * 一个Segment维持时间为15分钟
     */
    private static final long SEGMENT_DURATION = 15 * 60 * 1000L;

    private final ScheduledExecutorService idCacheCheckPool = new ScheduledThreadPoolExecutor(1, r -> {
        Thread t = new Thread(r);
        t.setName("check-idcache-thread");
        t.setDaemon(true);
        return t;
    });

    volatile boolean initOK = false;

    final Map<String, SegmentBuffer> cache = new ConcurrentHashMap<>();

    private final SerialAllocService serialAllocService;

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    @PostConstruct
    public boolean init() {
        log.info("Init ...");
        // 确保加载到kv后才初始化成功
        updateCacheFromDb();
        initOK = true;
        updateCacheFromDbAtEveryMinute();
        return initOK;
    }

    private void updateCacheFromDbAtEveryMinute() {
        idCacheCheckPool.scheduleWithFixedDelay(this::updateCacheFromDb, 60, 60, TimeUnit.SECONDS);
    }

    private void updateCacheFromDb() {
        log.info("update cache from db");
        try {
            List<String> dbTags = serialAllocService.getAllTags();
            if (dbTags == null || dbTags.isEmpty()) {
                return;
            }
            List<String> cacheTags = new ArrayList<>(cache.keySet());
            Set<String> insertTagsSet = new HashSet<>(dbTags);
            Set<String> removeTagsSet = new HashSet<>(cacheTags);
            // db中新加的tags灌进cache
            for (String tmp : cacheTags) {
                insertTagsSet.remove(tmp);
            }
            for (String tag : insertTagsSet) {
                SegmentBuffer buffer = new SegmentBuffer();
                buffer.setKey(tag);
                Segment segment = buffer.getCurrent();
                segment.setValue(new AtomicLong(0));
                segment.setMax(0);
                segment.setStep(0);
                cache.put(tag, buffer);
                log.info("Add tag {} from db to IdCache, SegmentBuffer {}", tag, buffer);
            }
            // cache中已失效的tags从cache删除
            for (String tmp : dbTags) {
                removeTagsSet.remove(tmp);
            }
            for (String tag : removeTagsSet) {
                cache.remove(tag);
                log.info("Remove tag {} from IdCache", tag);
            }
        } catch (Exception e) {
            log.warn("update cache from db exception", e);
        }
    }

    @Override
    public Result get(final String key) {
        if (!initOK) {
            return new Result(EXCEPTION_ID_IDCACHE_INIT_FALSE, Status.EXCEPTION);
        }
        if (!cache.containsKey(key)) {
            return new Result(EXCEPTION_ID_KEY_NOT_EXISTS, Status.EXCEPTION);
        }
        SegmentBuffer buffer = cache.get(key);
        if (!buffer.isInitOk()) {
            synchronized (buffer) {
                if (!buffer.isInitOk()) {
                    try {
                        updateSegmentFromDb(key, buffer.getCurrent());
                        log.info("Init buffer. Update leafkey {} {} from db", key, buffer.getCurrent());
                        buffer.setInitOk(true);
                    } catch (Exception e) {
                        log.warn("Init buffer {} exception", buffer.getCurrent(), e);
                    }
                }
            }
        }
        return getIdFromSegmentBuffer(cache.get(key));
    }

    public void updateSegmentFromDb(String key, Segment segment) {
        SegmentBuffer buffer = segment.getBuffer();
        SerialAlloc serialAlloc;
        if (!buffer.isInitOk()) {
            serialAlloc = serialAllocService.updateMaxIdAndGetSerialAlloc(key);
            buffer.setStep(serialAlloc.getStep());
            buffer.setRandomLen(serialAlloc.getRandomLen());
            buffer.setMinStep(serialAlloc.getStep());
        } else if (buffer.getUpdateTimestamp() == 0) {
            serialAlloc = serialAllocService.updateMaxIdAndGetSerialAlloc(key);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(serialAlloc.getStep());
            buffer.setRandomLen(serialAlloc.getRandomLen());
            buffer.setMinStep(serialAlloc.getStep());
        } else {
            long duration = System.currentTimeMillis() - buffer.getUpdateTimestamp();
            int nextStep = buffer.getStep();
            if (duration < SEGMENT_DURATION) {
                if (nextStep * 2 > MAX_STEP) {
                    // do nothing
                } else {
                    nextStep = nextStep * 2;
                }
            } else if (duration < SEGMENT_DURATION * 2) {
                // do nothing with nextStep
            } else {
                nextStep = nextStep / 2 >= buffer.getMinStep() ? nextStep / 2 : nextStep;
            }
            log.info("leafKey[{}], step[{}], duration[{}mins], nextStep[{}]", key, buffer.getStep(),
                    String.format("%.2f", ((double) duration / (1000 * 60))), nextStep);
            SerialAlloc temp = new SerialAlloc();
            temp.setBizTag(key);
            temp.setStep(nextStep);
            serialAlloc = serialAllocService.updateMaxIdByCustomStepAndGetSerialAlloc(temp);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(nextStep);
            buffer.setRandomLen(serialAlloc.getRandomLen());
            // leafAlloc的step为DB中的step
            buffer.setMinStep(serialAlloc.getStep());
        }
        // must set value before set max
        long value = serialAlloc.getMaxId() - buffer.getStep();
        segment.getValue().set(value);
        segment.setMax(serialAlloc.getMaxId());
        segment.setStep(buffer.getStep());
        segment.setRandomLen(serialAlloc.getRandomLen());
    }

    public Result getIdFromSegmentBuffer(final SegmentBuffer buffer) {
        while (true) {
            buffer.rLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                if (!buffer.isNextReady() && (segment.getIdle() < 0.9 * segment.getStep())
                        && buffer.getThreadRunning().compareAndSet(false, true)) {
                    threadPoolTaskExecutor.execute(() -> {
                        Segment next = buffer.getSegments()[buffer.nextPos()];
                        boolean updateOk = false;
                        try {
                            updateSegmentFromDb(buffer.getKey(), next);
                            updateOk = true;
                            log.info("update segment {} from db {}", buffer.getKey(), next);
                        } catch (Exception e) {
                            log.warn(buffer.getKey() + " updateSegmentFromDb exception", e);
                        } finally {
                            if (updateOk) {
                                buffer.wLock().lock();
                                buffer.setNextReady(true);
                                buffer.getThreadRunning().set(false);
                                buffer.wLock().unlock();
                            } else {
                                buffer.getThreadRunning().set(false);
                            }
                        }
                    });
                }
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    int shift = Math.min(segment.getRandomLen(), MAX_SHIFT);
                    if (shift <= 0) {
                        return new Result(value, Status.SUCCESS);
                    }
                    // 混淆
                    int low = (int) Math.pow(10, shift - 1);
                    int high = (int) Math.pow(10, shift) - 1;
                    int random = ThreadLocalRandom.current().nextInt(low, high);
                    // 十进制运算，得出的数字与maxid相比更为直观
                    long newVal = value * (int) Math.pow(10, shift) + random;
                    return new Result(newVal, Status.SUCCESS);
                }
            } finally {
                buffer.rLock().unlock();
            }
            waitAndSleep(buffer);
            buffer.wLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    int shift = Math.min(segment.getRandomLen(), MAX_SHIFT);
                    if (shift <= 0) {
                        return new Result(value, Status.SUCCESS);
                    }
                    // 混淆
                    int low = (int) Math.pow(10, shift - 1);
                    int high = (int) Math.pow(10, shift) - 1;
                    int random = ThreadLocalRandom.current().nextInt(low, high);
                    // 十进制运算，得出的数字与maxid相比更为直观
                    long newVal = value * (int) Math.pow(10, shift) + random;
                    return new Result(newVal, Status.SUCCESS);
                }
                if (buffer.isNextReady()) {
                    buffer.switchPos();
                    buffer.setNextReady(false);
                } else {
                    log.error("Both two segments in {} are not ready!", buffer);
                    return new Result(EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL, Status.EXCEPTION);
                }
            } finally {
                buffer.wLock().unlock();
            }
        }
    }

    private void waitAndSleep(SegmentBuffer buffer) {
        int roll = 0;
        while (buffer.getThreadRunning().get()) {
            roll += 1;
            if (roll <= 10000) {
                continue;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(10);
                break;
            } catch (InterruptedException e) {
                log.warn("Thread {} Interrupted", Thread.currentThread().getName());
                break;
            }
        }
    }

}
