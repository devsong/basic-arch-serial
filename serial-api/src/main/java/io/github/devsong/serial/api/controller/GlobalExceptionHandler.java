package io.github.devsong.serial.api.controller;

import io.github.devsong.base.entity.BaseResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

import static io.github.devsong.base.entity.BaseResponseDto.error;
import static io.github.devsong.serial.api.SystemErrorEnum.ILLEGAL_ARGUMENT_ERROR;
import static io.github.devsong.serial.api.SystemErrorEnum.INTERNAL_SERVER_ERROR;

/**
 * @author zhisong.guan
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponseDto<?> handleThrowable(Exception e) {
        log.error("system error", e);
        return error(INTERNAL_SERVER_ERROR.getErrorCode(), INTERNAL_SERVER_ERROR.getMsg());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponseDto<?> handleThrowable(IllegalArgumentException e) {
        log.error("illegal argument", e);
        return error(ILLEGAL_ARGUMENT_ERROR.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponseDto<?> handleThrowable(MethodArgumentTypeMismatchException e) {
        log.error("illegal argument with request parameter", e);
        return error(ILLEGAL_ARGUMENT_ERROR.getErrorCode(), e.getMessage());
    }
}
