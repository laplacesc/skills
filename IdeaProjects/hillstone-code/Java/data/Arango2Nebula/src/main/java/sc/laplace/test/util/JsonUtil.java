package sc.laplace.test.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * @author jxwu
 */
@Slf4j
@UtilityClass
public class JsonUtil {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    public static <T> T toObject(Object content, Class<T> clazz) {
        try {
            if (content == null) {
                return null;
            }
            if (content instanceof String) {
                return JSON_MAPPER.readerFor(clazz).readValue((String) content);
            } else if (content instanceof InputStream) {
                return JSON_MAPPER.readerFor(clazz).readValue((InputStream) content);
            } else if (content instanceof byte[]) {
                return JSON_MAPPER.readerFor(clazz).readValue((byte[]) content);
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static Map<String, Object> toMap(Object value) {
        try {
            if (value == null) {
                return Collections.emptyMap();
            }
            if (value instanceof String) {
                return JSON_MAPPER.readValue((String) value, new TypeReference<Map<String, Object>>() {
                });
            }
            if (value instanceof InputStream) {
                return JSON_MAPPER.readValue((InputStream) value, new TypeReference<Map<String, Object>>() {
                });
            }
            if (value instanceof byte[]) {
                return JSON_MAPPER.readValue((byte[]) value, new TypeReference<Map<String, Object>>() {
                });
            }
            return JSON_MAPPER.convertValue(value, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyMap();
        }
    }
}
