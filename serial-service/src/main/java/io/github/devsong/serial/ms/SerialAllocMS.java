package io.github.devsong.serial.ms;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import io.github.devsong.serial.entity.po.SerialAlloc;
import io.github.devsong.serial.inf.dto.SerialAllocDto;

@Mapper
public interface SerialAllocMS {
    SerialAllocMS INST = Mappers.getMapper(SerialAllocMS.class);

    @Mapping(target = "bizTag", source = "key")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SerialAlloc fromDto(SerialAllocDto serialAllocDto);

    @Mapping(target = "key", source = "bizTag")
    SerialAllocDto toDto(SerialAlloc serialAlloc);
}
