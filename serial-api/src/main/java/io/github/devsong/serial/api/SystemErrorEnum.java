package io.github.devsong.serial.api;

import static io.github.devsong.serial.SerialConstants.SysMajorCode.MAJOR_4XX;
import static io.github.devsong.serial.SerialConstants.SysMajorCode.MAJOR_5XX;
import static io.github.devsong.serial.SerialConstants.SysMinorCode.ILLEGAL_ARGUMENT;
import static io.github.devsong.serial.SerialConstants.SysMinorCode.START_UP_CODE;
import static io.github.devsong.serial.SerialConstants.SysMinorCode.USER_NOT_FOUND;

import lombok.Getter;


/**
 * @author zhisong.guan
 */
@Getter
public enum SystemErrorEnum {

    INTERNAL_SERVER_ERROR(MAJOR_5XX, START_UP_CODE, "system error"),

    ILLEGAL_ARGUMENT_ERROR(MAJOR_4XX, ILLEGAL_ARGUMENT, "illegal argument"),

    USER_NOT_FOUND_ERROR(MAJOR_4XX, USER_NOT_FOUND, "user not found");

    private int major;
    private int minor;
    private String msg;

    SystemErrorEnum(int major, int minor, String msg) {
        this.major = major;
        this.minor = minor;
        this.msg = msg;
    }

    public int getErrorCode() {
        return this.major + minor;
    }
}
