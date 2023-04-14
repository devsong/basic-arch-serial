package io.github.devsong.serial.inf.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhisong.guan
 * @date 2022/10/12 11:40
 */
@Data
public class FeatureToggleDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String OPEN = "open";
    private static final String CLOSE = "close";

    private String testToggle;

    public static boolean isOpen(String toggle) {
        return OPEN.equalsIgnoreCase(toggle);
    }

    public static boolean isClose(String toggle) {
        return CLOSE.equalsIgnoreCase(toggle);
    }

}
