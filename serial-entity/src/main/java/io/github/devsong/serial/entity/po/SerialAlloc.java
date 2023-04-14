package io.github.devsong.serial.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author guanzhisong
 * @date 2021-07-14 17:00:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerialAlloc implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 业务Key
     */
    @TableId(type = IdType.INPUT)
    private String bizTag;

    /**
     * 最大已使用ID
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
     * 状态(0 正常 1 禁用 2 删除)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
