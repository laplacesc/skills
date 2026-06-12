package com.hillstone.simulator.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * @author bohuachen
 * @date 2025/3/18 20:49
 * @description
 */
public class YamlConfig {


    public static final String REGISTER_TYPE = "process.register.type";

    private static final Map<String, Object> props = new HashMap<>(8);

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try (InputStream input = YamlConfig.class.getResourceAsStream("/application-common.yml")) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(input);
            parseConfig(config, ""); // 递归解析配置
        } catch (Exception e) {
            throw new RuntimeException("加载配置失败", e);
        }
    }

    private static void parseConfig(Map<String, Object> node, String parentKey) {
        for (Map.Entry<String, Object> entry : node.entrySet()) {
            String key = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                parseConfig((Map<String, Object>) entry.getValue(), key);
            } else {
                props.put(key, entry.getValue());
            }
        }
    }


    public static Object getProp(String prop){
        return props.get(prop);
    }
}
