package sc.laplace.test.tencent.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * @author jxwu
 */
@Slf4j
@UtilityClass
public class JsonUtil {

    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    public static String toJson(Object value) {
        try {
            return JSON_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T toObject(Object content, TypeReference<T> valueTypeRef) {
        try {
            if (content == null) {
                return null;
            }
            if (content instanceof String) {
                return JSON_MAPPER.readValue((String) content, valueTypeRef);
            } else if (content instanceof InputStream) {
                return JSON_MAPPER.readValue((InputStream) content, valueTypeRef);
            } else if (content instanceof byte[]) {
                return JSON_MAPPER.readValue((byte[]) content, valueTypeRef);
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
