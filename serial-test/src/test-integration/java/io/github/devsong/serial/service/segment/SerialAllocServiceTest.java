package io.github.devsong.serial.service.segment;

import io.github.devsong.base.entity.PageResponseDto;
import io.github.devsong.serial.SerialIntegrationBase;
import io.github.devsong.serial.entity.po.SerialAlloc;
import io.github.devsong.serial.inf.dto.SegmentSearchDto;
import io.github.devsong.serial.inf.dto.SerialAllocDto;
import io.github.devsong.base.test.ResourceParseUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SerialAllocServiceTest extends SerialIntegrationBase {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void should_return_first_page_success(boolean prepare) throws IOException {
        if (prepare) {
            prepareData();
        }
        SegmentSearchDto searchDto = new SegmentSearchDto();
        searchDto.setPageNum(1);
        searchDto.setPageSize(10);
        PageResponseDto<SerialAllocDto> pageResponseDto = serialAllocService.searchBizKeys(searchDto);
        if (prepare) {
            assertThat(pageResponseDto).isNotNull();
            assertThat(pageResponseDto.getData().size()).isGreaterThan(0);
        } else {
            assertThat(pageResponseDto).isNotNull();
            assertThat(pageResponseDto.getData().size()).isEqualTo(0);
        }
    }

    @Test
    void should_get_all_tags() throws IOException {
        prepareData();
        List<String> allTags = serialAllocService.getAllTags();
        assertThat(allTags).isNotNull();
    }

    @Test
    void should_update_max_id_success() throws Exception {
        SerialAlloc serialAlloc = prepareData();
        SerialAlloc newSerialAlloc = serialAllocService.updateMaxIdAndGetSerialAlloc(serialAlloc.getBizTag());
        assertThat(newSerialAlloc.getMaxId()).isEqualTo(serialAlloc.getMaxId() + serialAlloc.getStep());
    }

    @Test
    void should_update_max_id_by_custom_step_success() throws Exception {
        SerialAlloc serialAlloc = prepareData();
        serialAlloc.setStep(1000);
        SerialAlloc newSerialAlloc = serialAllocService.updateMaxIdByCustomStepAndGetSerialAlloc(serialAlloc);
        assertThat(newSerialAlloc.getMaxId()).isEqualTo(serialAlloc.getMaxId() + serialAlloc.getStep());
    }

    SerialAlloc prepareData() throws IOException {
        SerialAlloc serialAlloc = ResourceParseUtil.parseObject(ResourceParseUtil.BASE_JSON_PATH + "serial_alloc.json", SerialAlloc.class);
        serialAllocMapper.insert(serialAlloc);
        return serialAllocMapper.selectById(serialAlloc.getBizTag());
    }
}
