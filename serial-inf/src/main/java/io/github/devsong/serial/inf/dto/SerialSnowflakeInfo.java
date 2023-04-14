package io.github.devsong.serial.inf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerialSnowflakeInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 时间戳 */
    private String timestamp;
    /** 时间字符串格式 */
    private String time;
    /** 数据中心ID */
    private int dataCenterId;
    /** workerID */
    private int workerId;
    /** 序列号 */
    private int seq;
}
