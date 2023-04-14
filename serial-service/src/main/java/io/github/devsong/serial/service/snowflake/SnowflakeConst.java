package io.github.devsong.serial.service.snowflake;

/**
 * @author zhisong.guan
 */
public interface SnowflakeConst {
    /**
     * 时间点纪元2010-01-01 00:00:00
     */
    long TWEPOCH = 1262275200000L;
    /**
     * 数据中心bit位数,最大4个数据中心,可以根据需求自己定制
     */
    long DATA_CENTER_ID_BITS = 2;
    /**
     * 最大的数据中心ID数
     */
    long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    /**
     * 机器bit位数,一个数据中心最多64台机器
     */
    long WORKER_ID_BITS = 6L;
    /**
     * 最大机器数目
     */
    long MAX_WORKER_ID = ~(-1L << (DATA_CENTER_ID_BITS + WORKER_ID_BITS));
    /**
     * 自增序列号位数
     */
    long SEQUENCE_BITS = 12L;
    /**
     * 序列号掩码
     */
    long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    /**
     * 序号
     */
    long SEQUENCE_SHIFT = 0L;
    /**
     * 机器
     */
    long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /**
     * 数据中心
     */
    long DATA_CENTER_ID_SHIFT = WORKER_ID_SHIFT + WORKER_ID_BITS;
    /**
     * 时间戳
     */
    long TIMESTAMP_SHIFT = DATA_CENTER_ID_SHIFT + DATA_CENTER_ID_BITS;
}
