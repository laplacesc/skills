package sc.laplace.test.superset.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Custom Jackson deserializer for {@code Map<String, Object>} fields that may
 * arrive either as a JSON object or as a JSON-encoded string.
 * <p>
 * Superset occasionally returns nested JSON objects as escaped strings
 * (e.g. {@code "{\"allows_virtual_table_explore\":true}"}). This deserializer
 * transparently handles both forms: string values are parsed into a Map
 * before binding, while native JSON objects use the default behavior.
 */
public class StringToMapDeserializer extends JsonDeserializer<Map<String, Object>> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() == JsonToken.VALUE_STRING) {
            String text = p.getText();
            if (text == null || text.trim().isEmpty()) {
                return Collections.emptyMap();
            }
            try {
                //noinspection unchecked
                return mapper.readValue(text, Map.class);
            } catch (IOException e) {
                // If it's not valid JSON, return the raw string wrapped in a map
                // to preserve the value without breaking the caller.
                return Collections.singletonMap("value", text);
            }
        }
        // Native JSON object — use default tree-to-map conversion
        //noinspection unchecked
        return mapper.readValue(p, Map.class);
    }
}
