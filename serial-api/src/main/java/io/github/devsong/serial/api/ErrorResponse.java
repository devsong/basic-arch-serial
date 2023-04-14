package io.github.devsong.serial.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zhisong.guan
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("code")
    private int code;

    private String message;

    private Object data;

    public ErrorResponse(int code) {
        this.code = code;
    }

    public ErrorResponse(SystemErrorEnum systemErrorEnum) {
        this(systemErrorEnum.getErrorCode(), systemErrorEnum.getMsg());
    }

    public ErrorResponse(int code, String message) {
        this(code, message, null);
    }

    public ErrorResponse(int code, Object data) {
        this(code, null, data);
    }
}
