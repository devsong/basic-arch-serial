package io.github.devsong.serial.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SerialException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SerialException(String msg){
        super(msg);
    }
}
