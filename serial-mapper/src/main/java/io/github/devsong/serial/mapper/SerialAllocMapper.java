package io.github.devsong.serial.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.devsong.serial.entity.po.SerialAlloc;
import io.github.devsong.serial.inf.dto.SegmentSearchDto;
import org.apache.ibatis.annotations.Param;

public interface SerialAllocMapper extends BaseMapper<SerialAlloc> {

    void updateMaxId(@Param("bizTag") String bizTag);

    void updateMaxIdByCustomStep(SerialAlloc serialAlloc);

    IPage<SerialAlloc> search(IPage<SerialAlloc> page, @Param("search") SegmentSearchDto segmentSearchDto);
}
