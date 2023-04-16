package io.github.devsong.serial.api.controller;

import com.google.common.collect.Lists;
import io.github.devsong.base.common.util.JsonUtil;
import io.github.devsong.base.entity.PageResponseDto;
import io.github.devsong.base.entity.ResponseCode;
import io.github.devsong.base.test.ControllerBaseTest;
import io.github.devsong.base.test.NotMatcher;
import io.github.devsong.serial.config.properties.FeatureToggleProperties;
import io.github.devsong.serial.entity.common.Result;
import io.github.devsong.serial.entity.common.Status;
import io.github.devsong.serial.entity.po.SerialAlloc;
import io.github.devsong.serial.inf.dto.SegmentSearchDto;
import io.github.devsong.serial.inf.dto.SerialAllocDto;
import io.github.devsong.serial.inf.dto.SerialSnowflakeInfo;
import io.github.devsong.serial.inf.dto.SerialStatus;
import io.github.devsong.serial.ms.SerialAllocMS;
import io.github.devsong.serial.service.segment.SerialAllocService;
import io.github.devsong.serial.service.segment.impl.SegmentIDGenImpl;
import io.github.devsong.serial.service.snowflake.SnowflakeIDGenImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author zhisong.guan
 * @date 2022/11/2 17:48
 */
@Slf4j
class SerialControllerUnitTest extends ControllerBaseTest {
    long snowflakeId = 424849292519567361L;
    @Mock
    SerialAllocService serialAllocService;

    @Mock
    SegmentIDGenImpl segmentService;

    @Mock
    SnowflakeIDGenImpl snowflakeIDGen;

    @Mock
    FeatureToggleProperties toggle;

    @InjectMocks
    SerialController serialController;

    @BeforeEach
    void setUp() {
        try (AutoCloseable ignored = MockitoAnnotations.openMocks(this)) {
            mockMvc = MockMvcBuilders
                    .standaloneSetup(serialController)
                    .setControllerAdvice(new GlobalExceptionHandler())
                    .build();
            Mockito.reset(serialAllocService, segmentService, snowflakeIDGen, toggle);
        } catch (Exception e) {
            log.error("unit test [{}] set error", getClass().getName(), e);
        }
    }

    @Test
    void should_create_segment_success_when_segment_key_not_exist() throws Exception {
        SerialAllocDto serialAllocDto = buildSerialAllocDto();
        ArgumentCaptor<SerialAllocDto> argumentCaptor = ArgumentCaptor.forClass(SerialAllocDto.class);
        boolean result = true;
        when(serialAllocService.getBizKey(anyString())).thenReturn(null);
        when(serialAllocService.add(argumentCaptor.capture())).thenReturn(result);
        mockMvc.perform(post("/serial/add-serial-alloc")
                        .headers(jsonHeaders)
                        .content(Objects.requireNonNull(JsonUtil.toJSONString(serialAllocDto)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(result))
        ;
    }

    @Test
    void should_create_segment_fail_when_segment_key_exist() throws Exception {
        SerialAllocDto serialAllocDto = buildSerialAllocDto();
        SerialAlloc serialAlloc = SerialAllocMS.INST.fromDto(serialAllocDto);
        when(serialAllocService.getBizKey(serialAlloc.getBizTag())).thenReturn(serialAlloc);
        mockMvc.perform(post("/serial/add-serial-alloc")
                        .headers(jsonHeaders)
                        .content(Objects.requireNonNull(JsonUtil.toJSONString(serialAllocDto)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.BIZ_ERROR.getCode()))
        ;
    }

    @Test
    void should_update_segment_success_when_segment_key_not_exist() throws Exception {
        SerialAllocDto serialAllocDto = buildSerialAllocDto();
        boolean result = true;
        ArgumentCaptor<SerialAllocDto> captor = ArgumentCaptor.forClass(SerialAllocDto.class);
        when(serialAllocService.update(captor.capture())).thenReturn(result);
        mockMvc.perform(post("/serial/update-serial-alloc")
                        .headers(jsonHeaders)
                        .content(Objects.requireNonNull(JsonUtil.toJSONString(serialAllocDto)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(result))
        ;
    }

    @Test
    void should_get_serial_info_by_specific_key() throws Exception {
        SerialAllocDto serialAllocDto = buildSerialAllocDto();
        SerialAlloc serialAlloc = SerialAllocMS.INST.fromDto(serialAllocDto);
        String bizKey = serialAllocDto.getKey();
        when(serialAllocService.getBizKey(bizKey)).thenReturn(serialAlloc);
        mockMvc.perform(get("/serial/serial-alloc/" + bizKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.key").value(serialAllocDto.getKey()))
                .andExpect(jsonPath("$.data.step").value(serialAllocDto.getStep()))
                .andExpect(jsonPath("$.data.status").value(serialAllocDto.getStatus()))
                .andExpect(jsonPath("$.data.description").value(serialAllocDto.getDescription()))
                .andExpect(jsonPath("$.data.maxId").value(serialAllocDto.getMaxId()))
                .andExpect(jsonPath("$.data.randomLen").value(serialAllocDto.getRandomLen()))
        ;
    }

    @Test
    void should_get_serial_fail_when_key_not_exist() throws Exception {
        String bizKey = "notExistsKey";
        when(serialAllocService.getBizKey(bizKey)).thenReturn(null);
        mockMvc.perform(get("/serial/serial-alloc/" + bizKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.BIZ_ERROR.getCode()))
        ;
    }

    @Test
    void should_search_serial_alloc_list_success() throws Exception {
        SerialAllocDto serialAllocDto = buildSerialAllocDto();
        List<SerialAllocDto> list = Lists.newArrayList(serialAllocDto);
        SegmentSearchDto searchDto = new SegmentSearchDto();
        searchDto.setPageNum(1);
        searchDto.setPageSize(10);
        searchDto.setStatus(0);
        when(serialAllocService.searchBizKeys(any(SegmentSearchDto.class))).thenReturn(PageResponseDto.success(list, 1, 10));
        mockMvc.perform(post("/serial/search-serial-alloc")
                        .headers(jsonHeaders)
                        .content(Objects.requireNonNull(JsonUtil.toJSONString(searchDto)))
                )
                .andExpect(status().isOk())
                .andDo(r -> System.out.println(r.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.data[0].key").value(serialAllocDto.getKey()))
                .andExpect(jsonPath("$.data[0].step").value(serialAllocDto.getStep()))
                .andExpect(jsonPath("$.data[0].status").value(serialAllocDto.getStatus()))
                .andExpect(jsonPath("$.data[0].description").value(serialAllocDto.getDescription()))
                .andExpect(jsonPath("$.data[0].maxId").value(serialAllocDto.getMaxId()))
                .andExpect(jsonPath("$.data[0].randomLen").value(serialAllocDto.getRandomLen()))
        ;
    }

    @Test
    void should_get_segment_key_success() throws Exception {
        String bizKey = "testKey";
        Result result = new Result();
        result.setId(1L);
        result.setStatus(Status.SUCCESS);
        when(segmentService.get(bizKey)).thenReturn(result);
        mockMvc.perform(get("/serial/segment/" + bizKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(result.getId()))
        ;
    }

    @Test
    void should_get_segment_key_fail_when_result_status_is_exception() throws Exception {
        String bizKey = "testKey";
        Result result = new Result();
        result.setStatus(Status.EXCEPTION);
        when(segmentService.get(bizKey)).thenReturn(result);
        mockMvc.perform(get("/serial/segment/" + bizKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.BIZ_ERROR.getCode()))
        ;
    }

    private static SerialAllocDto buildSerialAllocDto() {
        return SerialAllocDto.builder()
                .key("testKey")
                .step(1000)
                .createTime(LocalDateTime.now())
                .maxId(1000000L)
                .randomLen(0)
                .description("des")
                .status(0)
                .build();
    }

    @Test
    void should_get_snowflake_success() throws Exception {
        Result result = new Result(snowflakeId, Status.SUCCESS);
        when(snowflakeIDGen.get(null)).thenReturn(result);
        mockMvc.perform(get("/serial/snowflake"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(snowflakeId));
    }

    @Test
    void should_get_snowflake_failed_when_result_status_is_exception() throws Exception {
        Result result = new Result(snowflakeId, Status.EXCEPTION);
        when(snowflakeIDGen.get(null)).thenReturn(result);
        mockMvc.perform(get("/serial/snowflake"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.BIZ_ERROR.getCode()));
    }

    @Test
    void should_decode_snowflake_success() throws Exception {
        SerialSnowflakeInfo info = new SerialSnowflakeInfo();
        info.setTimestamp("1667443058619");
        info.setTime("2022-11-03 10:37:38.619");
        info.setSeq(1);
        info.setDataCenterId(1);
        info.setWorkerId(7);
        when(snowflakeIDGen.decodeSnowflake(snowflakeId)).thenReturn(info);
        mockMvc.perform(get("/serial/snowflake-decode/" + snowflakeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.timestamp").value(info.getTimestamp()))
                .andExpect(jsonPath("$.data.time").value(info.getTime()))
                .andExpect(jsonPath("$.data.seq").value(info.getSeq()))
                .andExpect(jsonPath("$.data.dataCenterId").value(info.getDataCenterId()))
                .andExpect(jsonPath("$.data.workerId").value(info.getWorkerId()))
        ;
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void should_decode_snowflake_fail_when_input_is_illegal(int id) throws Exception {
        mockMvc.perform(get("/serial/snowflake-decode/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.BIZ_ERROR.getCode()))
        ;
    }

    @Test
    void should_decode_snowflake_failed_when_snowflake_contains_alphabet() throws Exception {
        String alphabetSnowflakeId = "12dfsdfsd";
        mockMvc.perform(get("/serial/snowflake-decode/" + alphabetSnowflakeId))
                .andExpect(status().is4xxClientError())
        ;
    }

    @Test
    void should_get_serial_status_success() throws Exception {
        int len = SerialStatus.values().length;
        mockMvc.perform(get("/serial/serial-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(len)))
        ;
    }

    @ParameterizedTest
    @ValueSource(strings = {"open", "close"})
    void should_get_feature_toggle_success(String targetToggleStatus) throws Exception {
        when(toggle.getTestToggle()).thenReturn(targetToggleStatus);
        mockMvc.perform(get("/serial/feature-toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.testToggle").value(targetToggleStatus))
        ;
    }

    @ParameterizedTest
    @ValueSource(classes = {RuntimeException.class, IllegalArgumentException.class})
    void should_throw_exception_and_handled_by_exception_handler(Class<RuntimeException> cls) throws Exception {
        SerialAllocDto serialAllocDto = buildSerialAllocDto();
        RuntimeException e = cls.getDeclaredConstructor(String.class).newInstance("runtime exception");
        when(serialAllocService.getBizKey(anyString())).thenThrow(e);
        mockMvc.perform(post("/serial/add-serial-alloc")
                        .headers(jsonHeaders)
                        .content(Objects.requireNonNull(JsonUtil.toJSONString(serialAllocDto)))
                ).andExpect(status().is(new NotMatcher<Integer>(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.code").value(new NotMatcher<Integer>(ResponseCode.SUCCESS.getCode())))
        ;
    }
}
