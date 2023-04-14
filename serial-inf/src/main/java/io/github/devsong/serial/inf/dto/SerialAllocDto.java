package io.github.devsong.serial.inf.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import static io.github.devsong.base.entity.GlobalConstant.DATE_TIME_FORMAT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SerialAllocDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 业务键值
     */
    private String key;
    /**
     * 以使用的最大ID
     */
    private Long maxId;
    /**
     * 步长
     */
    private Integer step;
    /**
     * 尾部随机数位数
     */
    private Integer randomLen;
    /**
     * 描述
     */
    private String description;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime createTime;
}
