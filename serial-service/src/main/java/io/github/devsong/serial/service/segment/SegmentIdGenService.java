package io.github.devsong.serial.service.segment;


import io.github.devsong.serial.entity.common.Result;

/**
 * @author zhisong.guan
 */
public interface SegmentIdGenService {

    /**
     * 获取指定key值的序列号
     *
     * @param key speicified key
     * @return result
     */
    Result get(String key);

}
