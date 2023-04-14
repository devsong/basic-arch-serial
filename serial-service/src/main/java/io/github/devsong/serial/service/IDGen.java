package io.github.devsong.serial.service;


import io.github.devsong.serial.entity.common.Result;

public interface IDGen {
    Result get(String key);

    boolean init();
}
