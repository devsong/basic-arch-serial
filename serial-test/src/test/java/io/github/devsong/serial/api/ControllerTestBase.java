package io.github.devsong.serial.api;

import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.google.common.collect.Lists;

/**
 * @author zhisong.guan
 * @date 2022/11/2 17:51
 */
public class ControllerTestBase {
    protected static MultiValueMap<String, String> JSON_HEADER_MAP = new LinkedMultiValueMap<>();

    static {
        JSON_HEADER_MAP.put(HttpHeaders.CONTENT_TYPE, Lists.newArrayList("application/json;charset=utf-8"));
    }

    protected MockMvc mockMvc;

    protected HttpHeaders jsonHeaders = HttpHeaders.readOnlyHttpHeaders(JSON_HEADER_MAP);
}
