package io.github.devsong.serial.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.github.devsong.base.test.ResourceParseUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.github.devsong.serial.entity.po.SerialAlloc;
import io.github.devsong.serial.exception.SerialException;
import io.github.devsong.serial.inf.dto.SerialAllocDto;
import io.github.devsong.serial.inf.dto.SerialStatus;
import io.github.devsong.serial.mapper.SerialAllocMapper;
import io.github.devsong.serial.service.segment.impl.SerialAllocServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class SerialAllocServiceImplUnitTest {
    @Mock
    private SerialAllocMapper serialAllocMapper;

    @InjectMocks
    private SerialAllocServiceImpl serialAllocService;

    @BeforeEach
    void setUp() {
        try (AutoCloseable ignore = MockitoAnnotations.openMocks(this)) {
            Mockito.reset(serialAllocMapper);
        } catch (Exception e) {
            log.error("unit test [{}] set error", getClass().getName(), e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"existKey", "notExistKey"})
    void should_serial_add_success_when_key_exists_or_not_exist(String bizKey) {
        SerialAllocDto serialAllocDto = SerialAllocDto.builder().key(bizKey).build();
        ArgumentCaptor<SerialAlloc> captor = ArgumentCaptor.forClass(SerialAlloc.class);
        if (bizKey.startsWith("exist")) {
            when(serialAllocMapper.insert(captor.capture())).thenReturn(0);
            boolean resp = serialAllocService.add(serialAllocDto);
            assertThat(resp).isFalse();
        } else if (bizKey.startsWith("notExist")) {
            when(serialAllocMapper.insert(captor.capture())).thenReturn(1);
            boolean resp = serialAllocService.add(serialAllocDto);
            assertThat(resp).isTrue();
        }
    }

    @Test
    void should_update_success_when_key_exists() throws Exception {
        String existKey = "order";
        SerialAllocDto serialAllocDto = SerialAllocDto.builder()
                .key(existKey)
                .build();
        SerialAlloc serialAlloc = ResourceParseUtil.parseObject(ResourceParseUtil.BASE_JSON_UNIT_PATH + "serial_alloc.json", SerialAlloc.class);
        ArgumentCaptor<SerialAlloc> argumentCaptor = ArgumentCaptor.forClass(SerialAlloc.class);
        when(serialAllocMapper.selectById(existKey)).thenReturn(serialAlloc);
        when(serialAllocMapper.updateById(argumentCaptor.capture())).thenReturn(1);
        boolean resp = serialAllocService.update(serialAllocDto);
        assertThat(resp).isTrue();
    }

    @Test
    void should_update_throw_serial_exception_when_key_not_exist() {
        String notExistKey = "notExistKey";
        SerialAllocDto serialAllocDto = SerialAllocDto.builder()
                .key(notExistKey)
                .build();
        when(serialAllocMapper.selectById(notExistKey)).thenReturn(null);
        Assertions.assertThatExceptionOfType(SerialException.class).isThrownBy(() -> serialAllocService.update(serialAllocDto));
    }

    @Test
    void should_update_throw_serial_exception_when_key_is_deleted() throws Exception {
        String existKey = "order";
        SerialAllocDto serialAllocDto = SerialAllocDto.builder()
                .key(existKey)
                .build();
        SerialAlloc serialAlloc = ResourceParseUtil.parseObject(ResourceParseUtil.BASE_JSON_UNIT_PATH + "serial_alloc.json", SerialAlloc.class);
        serialAlloc.setStatus(SerialStatus.DELETE.getCode());
        when(serialAllocMapper.selectById(existKey)).thenReturn(serialAlloc);

        Assertions.assertThatExceptionOfType(SerialException.class).isThrownBy(() -> serialAllocService.update(serialAllocDto));
    }
}
