package io.github.devsong.serial.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import io.github.devsong.serial.api.ErrorResponse;
import io.github.devsong.serial.api.SystemErrorEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhisong.guan
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Exception e) {
        log.error("system error", e);
        return new ErrorResponse(SystemErrorEnum.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleThrowable(IllegalArgumentException e) {
        log.error("illegal argument", e);
        return new ErrorResponse(SystemErrorEnum.ILLEGAL_ARGUMENT_ERROR.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleThrowable(MethodArgumentTypeMismatchException e) {
        log.error("illegal argument with request parameter", e);
        return new ErrorResponse(SystemErrorEnum.ILLEGAL_ARGUMENT_ERROR.getErrorCode(), e.getMessage());
    }
}
