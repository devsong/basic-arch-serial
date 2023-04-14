package io.github.devsong.serial.service.snowflake;


import io.github.devsong.base.common.util.JsonUtil;
import io.github.devsong.serial.IntegrationTestBase;
import io.github.devsong.serial.entity.common.Result;
import io.github.devsong.serial.inf.dto.SerialSnowflakeInfo;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author zhisong.guan
 * @date 2022/11/3 10:34
 */
@Disabled
@Slf4j
class SnowflakeIDGemImplTest extends IntegrationTestBase {

    @Test
    void should_get_snowflake_id_success() {
        Result result = snowflakeIDGen.get(null);
        log.info("snowflake id {}", result.getId());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isGreaterThan(0);
    }

    @Test
    void should_decode_snowflake_id_success() {
        SerialSnowflakeInfo serialSnowflakeInfo = snowflakeIDGen.decodeSnowflake(424849292519567361L);
        log.info("snowflake info {}", JsonUtil.toJSONString(serialSnowflakeInfo));
    }
}
