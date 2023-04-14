package io.github.devsong.serial.service.segment.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;

import io.github.devsong.base.entity.PageResponseDto;
import io.github.devsong.base.entity.PageResponseDto.PageResponse;
import io.github.devsong.base.entity.enums.StatusEnums;
import io.github.devsong.serial.entity.po.SerialAlloc;
import io.github.devsong.serial.exception.SerialException;
import io.github.devsong.serial.inf.dto.SegmentSearchDto;
import io.github.devsong.serial.inf.dto.SerialAllocDto;
import io.github.devsong.serial.inf.dto.SerialStatus;
import io.github.devsong.serial.mapper.SerialAllocMapper;
import io.github.devsong.serial.ms.SerialAllocMS;
import io.github.devsong.serial.service.segment.SerialAllocService;
import lombok.RequiredArgsConstructor;

/**
 * @author zhisong.guan
 */
@Service
@RequiredArgsConstructor
public class SerialAllocServiceImpl implements SerialAllocService {

    private final SerialAllocMapper serialAllocMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SerialAlloc updateMaxIdAndGetSerialAlloc(String key) {
        serialAllocMapper.updateMaxId(key);
        return serialAllocMapper.selectById(key);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SerialAlloc updateMaxIdByCustomStepAndGetSerialAlloc(SerialAlloc serialAlloc) {
        serialAllocMapper.updateMaxIdByCustomStep(serialAlloc);
        return serialAllocMapper.selectById(serialAlloc.getBizTag());
    }

    @Override
    public PageResponseDto<SerialAllocDto> searchBizKeys(SegmentSearchDto segmentSearchDto) {
        Page<SerialAlloc> p = new Page<>(segmentSearchDto.getPageNum(), segmentSearchDto.getPageSize());

        IPage<SerialAlloc> pageInfo = serialAllocMapper.search(p, segmentSearchDto);
        if (pageInfo.getTotal() == 0) {
            return PageResponseDto.success(Lists.newArrayList(), PageResponse.getNullPage());
        }
        List<SerialAlloc> list = pageInfo.getRecords();
        List<SerialAllocDto> result = Lists.newArrayList();
        for (SerialAlloc serialAlloc : list) {
            SerialAllocDto dto = SerialAllocMS.INST.toDto(serialAlloc);

            result.add(dto);
        }
        PageResponse page = PageResponse.builder()
                .page(segmentSearchDto.getPageNum())
                .pageSize(segmentSearchDto.getPageSize())
                .total((int) pageInfo.getTotal())
                .build();

        return PageResponseDto.success(result, page);
    }

    @Override
    public boolean add(SerialAllocDto serialAllocDto) {
        SerialAlloc serialAlloc = SerialAllocMS.INST.fromDto(serialAllocDto);
        serialAlloc.setCreateTime(new Date());
        int row = serialAllocMapper.insert(serialAlloc);
        return row == 1;
    }

    @Override
    public SerialAlloc getBizKey(String bizKey) {
        return serialAllocMapper.selectById(bizKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SerialAllocDto serialAllocDto) {
        final String key = serialAllocDto.getKey();
        SerialAlloc exist = getBizKey(key);
        if (exist == null) {
            String msg = String.format("key[%s] not found", key);
            throw new SerialException(msg);
        }
        if (exist.getStatus() == SerialStatus.DELETE.getCode()) {
            String msg = String.format("key[%s] status is [DELETE]", key);
            throw new SerialException(msg);
        }
        SerialAlloc po = SerialAllocMS.INST.fromDto(serialAllocDto);
        po.setUpdateTime(new Date());
        int row = serialAllocMapper.updateById(po);
        return row == 1;
    }

    @Override
    public List<String> getAllTags() {
        Wrapper<SerialAlloc> wrapper = Wrappers.lambdaQuery(new SerialAlloc())
                .eq(SerialAlloc::getStatus, StatusEnums.ENABLE.getCode())
                .select(SerialAlloc::getBizTag);
        List<SerialAlloc> list = serialAllocMapper.selectList(wrapper);
        return list.stream().map(SerialAlloc::getBizTag).collect(Collectors.toList());
    }
}
