package io.github.devsong.serial.inf;

import io.github.devsong.base.entity.BaseResponseDto;
import io.github.devsong.serial.inf.dto.SerialAllocDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 封装spring cloud 方法签名,方便客户端开发
 *
 * @author zhisong.guan
 * @date 2022/10/12 11:29
 */
@SuppressWarnings("ALL")
@RequestMapping("serial")
public interface ISerialClientSign {
    @PostMapping("/add-serial-alloc")
    BaseResponseDto<Boolean> addSegment(@RequestBody SerialAllocDto serialAllocDto);

    @PostMapping("/update-serial-alloc")
    BaseResponseDto<Boolean> updateSegment(@RequestBody SerialAllocDto serialAllocDto);
}
