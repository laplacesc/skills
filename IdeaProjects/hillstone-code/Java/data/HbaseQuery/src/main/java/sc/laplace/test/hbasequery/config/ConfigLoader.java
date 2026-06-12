package sc.laplace.test.hbasequery.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Map;

/**
 * 加载配置文件
 *
 * @author jxwu
 **/
@Slf4j
@Getter
public class ConfigLoader {
    private static ConfigLoader instance;
    private Map<String, String> configMap;

    private ConfigLoader() {
        JsonMapper jsonMapper = new JsonMapper();
        try (InputStream in = ConfigLoader.class.getResourceAsStream("/config.json")) {
            configMap = jsonMapper.readValue(in, new TypeReference<Map<String, String>>() {
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static synchronized ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }
}
