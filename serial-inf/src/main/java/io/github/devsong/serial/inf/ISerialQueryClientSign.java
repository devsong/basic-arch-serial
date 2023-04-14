package io.github.devsong.serial.inf;

import io.github.devsong.base.entity.BaseResponseDto;
import io.github.devsong.base.entity.PageResponseDto;
import io.github.devsong.serial.inf.dto.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 封装spring cloud 方法签名,方便客户端开发
 *
 * @author zhisong.guan
 * @date 2022/10/12 11:29
 */
@SuppressWarnings("ALL")
@RequestMapping("serial")
public interface ISerialQueryClientSign {
    @GetMapping("/serial-alloc/{key}")
    BaseResponseDto<SerialAllocDto> getSerialInfoByKey(@PathVariable("bizKey") String bizKey);

    @PostMapping("search-serial-alloc")
    PageResponseDto<SerialAllocDto> searchList(@RequestBody SegmentSearchDto searchDto);

    @GetMapping("/segment/{bizKey}")
    BaseResponseDto<Long> getSegmentId(@PathVariable("bizKey") String bizKey);

    @GetMapping("/snowflake")
    BaseResponseDto<Long> getSnowflakeId();

    @GetMapping("/snowflake-decode/{id}")
    BaseResponseDto<SerialSnowflakeInfo> decodeSnowflake(@PathVariable("id") long id);

    @GetMapping("serial-status")
    BaseResponseDto<List<SerialStatusDto>> getSerialStatus();

    @GetMapping("feature-toggle")
    BaseResponseDto<FeatureToggleDto> getNacosConfig();
}
