package sc.laplace.test.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sc.laplace.test.StreamingJob;
import sc.laplace.test.util.StreamingJobUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * 迁移任务运行配置。
 *
 * <p>所有参数都从 classpath 下的 application.properties 读取，
 * 并在这里统一做默认值和基础合法性处理，避免业务链路里散落解析逻辑。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig implements Serializable {
    private List<ArangoEndpoint> arangoHosts;
    private String arangoDatabase;
    private String arangoUser;
    private String arangoPassword;
    private int arangoTimeoutMs;
    private int arangoCursorBatchSize;
    private String arangoOffsetStateFile;
    private String nebulaHosts;
    private String nebulaMetaHosts;
    private String nebulaUser;
    private String nebulaPassword;
    private String nebulaSpace;
    private int nebulaBatchSize;
    private int nebulaBatchIntervalMs;
    private int nebulaPoolSize;
    private List<String> nebulaTags;
    private List<String> nebulaEdges;
    private int parallelism;
    private int flinkCheckpointIntervalMs;

    private AppConfig(Properties props) {
        this.arangoHosts = parseArangoHosts(props);
        this.arangoDatabase = props.getProperty("arango.database", "_system").trim();
        this.arangoUser = props.getProperty("arango.user", "root").trim();
        this.arangoPassword = props.getProperty("arango.password", "");
        this.arangoTimeoutMs = parsePositiveInt(props, "arango.timeout.ms", 120000);
        this.arangoCursorBatchSize = parsePositiveInt(props, "arango.cursor.batch.size", 1000);
        this.arangoOffsetStateFile = props.getProperty("arango.offset.state.file", "arango-offset-state.properties").trim();
        this.nebulaHosts = props.getProperty("nebula.hosts", "127.0.0.1:9669").trim();
        this.nebulaMetaHosts = props.getProperty("nebula.meta.hosts", "127.0.0.1:9559").trim();
        this.nebulaUser = props.getProperty("nebula.user", "root").trim();
        this.nebulaPassword = props.getProperty("nebula.password", "nebula");
        this.nebulaSpace = props.getProperty("nebula.space", "default_space").trim();
        this.nebulaBatchSize = parsePositiveInt(props, "nebula.batch.size", 200);
        this.nebulaBatchIntervalMs = parseNonNegativeInt(props, "nebula.batch.interval.ms", 0);
        this.nebulaPoolSize = parsePositiveInt(props, "nebula.pool.size", 10);
        this.nebulaTags = StreamingJobUtils.split(props.getProperty("nebula.tags", ""));
        this.nebulaEdges = StreamingJobUtils.split(props.getProperty("nebula.edges", ""));
        this.parallelism = parsePositiveInt(props, "flink.parallelism", 1);
        this.flinkCheckpointIntervalMs = parseNonNegativeInt(props, "flink.checkpoint.interval.ms", 0);
    }

    /**
     * 从资源文件加载配置。
     */
    public static AppConfig load() throws IOException {
        Properties props = new Properties();
        try (InputStream in = StreamingJob.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        }
        return new AppConfig(props);
    }

    private static List<ArangoEndpoint> parseArangoHosts(Properties props) {
        String rawHosts = props.getProperty("arango.hosts", "127.0.0.1:8529");

        List<ArangoEndpoint> endpoints = new ArrayList<>();
        for (String hostToken : rawHosts.split(",")) {
            ArangoEndpoint endpoint = parseArangoEndpoint(hostToken, 8529);
            if (endpoint != null) {
                endpoints.add(endpoint);
            }
        }

        return endpoints.isEmpty() ? Collections.singletonList(new ArangoEndpoint("127.0.0.1", 8529)) : endpoints;
    }

    private static ArangoEndpoint parseArangoEndpoint(String rawHost, int defaultPort) {
        if (rawHost == null) {
            return null;
        }

        String candidate = rawHost.trim();
        if (candidate.isEmpty()) {
            return null;
        }

        int portSeparatorIndex = candidate.lastIndexOf(':');
        if (portSeparatorIndex < 0) {
            return new ArangoEndpoint(candidate, defaultPort);
        }

        String host = candidate.substring(0, portSeparatorIndex).trim();
        String rawPort = candidate.substring(portSeparatorIndex + 1).trim();
        if (host.isEmpty()) {
            return null;
        }
        return new ArangoEndpoint(host, parsePositiveInt(rawPort, defaultPort));
    }

    private static int parsePositiveInt(Properties props, String key, int defaultValue) {
        String raw = props.getProperty(key);
        return parsePositiveInt(raw, defaultValue);
    }

    private static int parsePositiveInt(String raw, int defaultValue) {
        if (raw == null || raw.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(raw.trim());
            return value > 0 ? value : defaultValue;
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private static int parseNonNegativeInt(Properties props, String key, int defaultValue) {
        String raw = props.getProperty(key);
        if (raw == null || raw.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(raw.trim());
            return value >= 0 ? value : defaultValue;
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArangoEndpoint implements Serializable {
        private String host;
        private int port;
    }
}
