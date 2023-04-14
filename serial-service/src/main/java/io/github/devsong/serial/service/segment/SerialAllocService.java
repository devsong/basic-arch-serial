package io.github.devsong.serial.service.segment;


import java.util.List;

import io.github.devsong.base.entity.PageResponseDto;
import io.github.devsong.serial.entity.po.SerialAlloc;
import io.github.devsong.serial.inf.dto.SegmentSearchDto;
import io.github.devsong.serial.inf.dto.SerialAllocDto;

/**
 * @author zhisong.guan
 */
public interface SerialAllocService {

    SerialAlloc updateMaxIdAndGetSerialAlloc(String key);

    SerialAlloc updateMaxIdByCustomStepAndGetSerialAlloc(SerialAlloc serialAlloc);

    PageResponseDto<SerialAllocDto> searchBizKeys(SegmentSearchDto segmentSearchDto);

    boolean add(SerialAllocDto serialAllocDto);

    SerialAlloc getBizKey(String bizKey);

    boolean update(SerialAllocDto serialAllocDto);

    List<String> getAllTags();

}
