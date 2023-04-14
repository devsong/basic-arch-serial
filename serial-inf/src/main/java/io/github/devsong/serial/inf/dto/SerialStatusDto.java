package io.github.devsong.serial.inf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 通用的异常返回Code
 *
 * @author guanzhisong
 * @date 2021-07-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerialStatusDto implements Serializable {
    /***/
    private static final long serialVersionUID = 1L;
    /** 状态编码 */
    private int code;
    /** 状态描述 */
    private String desc;
}
