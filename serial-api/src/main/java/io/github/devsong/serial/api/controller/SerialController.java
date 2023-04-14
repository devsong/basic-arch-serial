package io.github.devsong.serial.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import io.github.devsong.base.entity.BaseResponseDto;
import io.github.devsong.base.entity.PageResponseDto;
import io.github.devsong.serial.config.properties.FeatureToggleProperties;
import io.github.devsong.serial.entity.common.Result;
import io.github.devsong.serial.entity.common.Status;
import io.github.devsong.serial.entity.po.SerialAlloc;
import io.github.devsong.serial.inf.dto.SegmentSearchDto;
import io.github.devsong.serial.inf.dto.SerialAllocDto;
import io.github.devsong.serial.inf.dto.SerialSnowflakeInfo;
import io.github.devsong.serial.inf.dto.SerialStatus;
import io.github.devsong.serial.inf.dto.SerialStatusDto;
import io.github.devsong.serial.ms.FeatureToggleMS;
import io.github.devsong.serial.ms.SerialAllocMS;
import io.github.devsong.serial.service.segment.SerialAllocService;
import io.github.devsong.serial.service.segment.impl.SegmentIDGenImpl;
import io.github.devsong.serial.service.snowflake.SnowflakeIDGenImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhisong.guan
 * @date 2022/9/30 16:30
 */
@RestController
@RequestMapping("serial")
@RequiredArgsConstructor
@Slf4j
public class SerialController {

    private final SerialAllocService serialAllocService;

    private final SegmentIDGenImpl segmentService;

    private final SnowflakeIDGenImpl snowflakeIDGen;

    private final FeatureToggleProperties toggle;

    @PostMapping("add-serial-alloc")
    public BaseResponseDto<Boolean> addSegment(@RequestBody SerialAllocDto serialAllocDto) {
        final String key = serialAllocDto.getKey();
        SerialAlloc exist = serialAllocService.getBizKey(key);
        if (exist != null) {
            String msg = String.format("key[%s] already exists", key);
            return BaseResponseDto.bizError(msg);
        }

        boolean success = serialAllocService.add(serialAllocDto);
        return BaseResponseDto.success(success);
    }

    @PostMapping("update-serial-alloc")
    public BaseResponseDto<Boolean> updateSegment(@RequestBody SerialAllocDto serialAllocDto) {
        boolean success = serialAllocService.update(serialAllocDto);
        return BaseResponseDto.success(success);
    }

    @GetMapping("serial-alloc/{key}")
    public BaseResponseDto<SerialAllocDto> getSegmentByKey(@PathVariable("key") String key) {
        SerialAlloc serialAlloc = serialAllocService.getBizKey(key);
        if (serialAlloc == null) {
            String msg = String.format("key [%s] not found", key);
            return BaseResponseDto.bizError(msg);
        }
        SerialAllocDto serialAllocDto = SerialAllocMS.INST.toDto(serialAlloc);
        return BaseResponseDto.success(serialAllocDto);
    }

    @PostMapping("search-serial-alloc")
    public PageResponseDto<SerialAllocDto> searchSerial(@RequestBody SegmentSearchDto searchDto) {
        return serialAllocService.searchBizKeys(searchDto);
    }

    @GetMapping("/segment/{key}")
    public BaseResponseDto<Long> getSegmentId(@PathVariable("key") String key) {
        Result r = segmentService.get(key);
        if (r.getStatus() == Status.SUCCESS) {
            long id = r.getId();
            return BaseResponseDto.success(id);
        }
        return BaseResponseDto.bizError(r.getStatus().toString());
    }

    @GetMapping("/snowflake")
    public BaseResponseDto<Long> snowflake() {
        Result r = snowflakeIDGen.get(null);
        if (r.getStatus() == Status.SUCCESS) {
            long id = r.getId();
            return BaseResponseDto.success(id);
        }
        return BaseResponseDto.bizError(r.getStatus().toString());
    }

    @GetMapping("/snowflake-decode/{id}")
    public BaseResponseDto<SerialSnowflakeInfo> snowflakeDecode(@PathVariable("id") long id) {
        if (id <= 0) {
            return BaseResponseDto.bizError("id must gt 0");
        }
        SerialSnowflakeInfo serialSnowflakeInfo = snowflakeIDGen.decodeSnowflake(id);
        return BaseResponseDto.success(serialSnowflakeInfo);
    }

    @GetMapping("serial-status")
    public BaseResponseDto<List<SerialStatusDto>> serialStatus() {
        List<SerialStatusDto> result = Lists.newArrayList();
        for (SerialStatus s : SerialStatus.values()) {
            SerialStatusDto dto = new SerialStatusDto();
            dto.setCode(s.getCode());
            dto.setDesc(s.getDesc());

            result.add(dto);
        }
        return BaseResponseDto.success(result);
    }

    @GetMapping("feature-toggle")
    public BaseResponseDto<FeatureToggleProperties> featureToggle() {
        return BaseResponseDto.success(FeatureToggleMS.INSTANCE.fromProp(toggle));
    }
}
