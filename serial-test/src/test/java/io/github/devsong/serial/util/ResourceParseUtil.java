package io.github.devsong.serial.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceParseUtil {
    public static final String BASE_JSON_PATH = "data/json/";
    public static final String BASE_JSON_UNIT_PATH = "data/json/unit/";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    static {
        JavaTimeModule TIME_MODULE = new JavaTimeModule();
        TIME_MODULE.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)));
        TIME_MODULE.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN)));
        TIME_MODULE.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN)));
        TIME_MODULE.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN)));
        OBJECT_MAPPER.registerModule(TIME_MODULE);
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_PATTERN));
    }

    public static <T> T parseObject(String relativePath, Class<T> cls) throws IOException {
        try (InputStream in = getResourcesInputStream(relativePath)) {
            return OBJECT_MAPPER.readValue(in, cls);
        }
    }

    public static <T> T parseCollection(String relativePath, TypeReference<T> ref) throws IOException {
        try (InputStream in = getResourcesInputStream(relativePath)) {
            return OBJECT_MAPPER.readValue(in, ref);
        }
    }

    public static InputStream getResourcesInputStream(String path) {
        ClassLoader clsLoader = Thread.currentThread().getContextClassLoader();
        InputStream in = clsLoader.getResourceAsStream(path);
        if (in == null) {
            log.error("error load resources from path {},loader {}", path, clsLoader.getName());
            throw new IllegalArgumentException(String.format("can not found resource path %s", path));
        }
        return in;
    }
}
