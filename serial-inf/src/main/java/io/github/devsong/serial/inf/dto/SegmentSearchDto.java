package io.github.devsong.serial.inf.dto;

import io.github.devsong.base.entity.PageRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class SegmentSearchDto extends PageRequestDto {
    private static final long serialVersionUID = 1L;
    /** 名称 */
    private String key;
    /** 状态 */
    private int status;
    /** 创建时间起始 */
    private Date beginTime;
    /** 创建时间结束 */
    private Date endTime;
}
