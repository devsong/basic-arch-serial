package io.github.devsong.serial.inf.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用的异常返回Code
 *
 * @author guanzhisong
 * @date 2021-07-15
 */
@Getter
public enum SerialStatus {

    ENABLE(0, "启用"),

    DISABLE(1, "禁用"),

    DELETE(2, "删除"),;

    private static final Map<Integer, SerialStatus> HOLDER = new HashMap<Integer, SerialStatus>();

    static {
        for (SerialStatus e : values()) {
            HOLDER.put(e.code, e);
        }
    }

    int code;

    String desc;

    SerialStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SerialStatus get(int code) {
        return HOLDER.get(code);
    }

    public static String getDesc(int code) {
        SerialStatus e = HOLDER.get(code);
        return e == null ? "" : e.desc;
    }
}
