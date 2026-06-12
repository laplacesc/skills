package sc.laplace.test.hbasequery.process;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import sc.laplace.test.hbasequery.config.HbaseConnection;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author jxwu
 */
@Slf4j
public class HbaseQueryProcess extends ProcessAllWindowFunction<String, List<String>, TimeWindow> {
    private static final JsonMapper JSON_MAPPER = new JsonMapper();
    private static final Map<String, String> TI_SOURCE_INTELLIGENCE_MAP = ImmutableMap.of(
            "ip", "ti_source_intelligence_ip",
            "domain", "ti_source_intelligence_domain",
            "file", "ti_source_intelligence_file",
            "url", "ti_source_intelligence_url"
    );
    private static final Map<String, String> TI_MAP = ImmutableMap.of(
            "ip", "ti_ip",
            "domain", "ti_domain",
            "file", "ti_file",
            "url", "ti_url"
    );
    private static final Map<String, String> COMMERCIAL_SOURCE_MAP = ImmutableMap.of(
            "tencent", "ti-tencent-data-table",
            "qihoo", "ti-qihoo-data-table",
            "virustotal", "thirdparty-data-table",
            "virustotal-url", "ti-virustotal-url-data-table"
    );
    private Path outputPath;
    private ParameterTool parameterTool;
    private HbaseConnection hbaseConnection;

    public static Object tryParseJson(String jsonString) {
        try {
            // 尝试解析为Map
            return JSON_MAPPER.readValue(jsonString, Map.class);
        } catch (Exception e1) {
            try {
                // 尝试解析为List
                return JSON_MAPPER.readValue(jsonString, List.class);
            } catch (Exception e2) {
                // 都不是有效的JSON格式
                return jsonString;
            }
        }
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        hbaseConnection = HbaseConnection.getInstance(parameterTool.get("hbaseUrl"));
        outputPath = Paths.get(parameterTool.get("output"));
    }

    @Override
    public void process(ProcessAllWindowFunction<String, List<String>, TimeWindow>.Context context, Iterable<String> elements, Collector<List<String>> out) throws Exception {
        List<String> list = StreamSupport.stream(elements.spliterator(), false).collect(Collectors.toList());
        CompletableFuture.allOf(
                // CompletableFuture.runAsync(() -> getTiSourceIntelligenceData(list)),
                CompletableFuture.runAsync(() -> getTiData(list))
                // CompletableFuture.runAsync(() -> getCommercialSourceData(list))
        ).join();
    }

    private void getTiSourceIntelligenceData(List<String> elements) {
        try {
            List<String> list = new ArrayList<>();
            Table table = hbaseConnection.getConnection().getTable(TableName.valueOf(TI_SOURCE_INTELLIGENCE_MAP.get(parameterTool.get("type"))));
            Result[] results = table.get(
                    elements.stream()
                            .map(element -> "url".equals(parameterTool.get("type")) ? DigestUtils.sha256Hex(element.split(" ")[2]) : element.split(" ")[2])
                            .map(ioc -> new Get(ioc.getBytes()))
                            .collect(Collectors.toList())
            );
            for (Result result : results) {
                NavigableMap<byte[], byte[]> familyMap = result.getFamilyMap("cf".getBytes());
                if (result.getRow() == null || familyMap == null) {
                    continue;
                }
                Map<String, Object> m = new HashMap<>();
                familyMap.forEach((qualifier, value) -> {
                    String c = Bytes.toString(qualifier);
                    Object v;
                    if (Arrays.asList("result", "status").contains(c) || c.contains("_result") || c.contains("_status")) {
                        v = Bytes.toInt(value);
                    } else if (c.contains("timestamp")) {
                        v = Bytes.toLong(value);
                    } else {
                        v = tryParseJson(Bytes.toString(value));
                    }
                    m.put(c, v);
                });
                list.add(JSON_MAPPER.writeValueAsString(m));
            }
            FileUtils.writeLines(outputPath.resolve(parameterTool.get("type") + ".tis").toFile(), list, IOUtils.LINE_SEPARATOR_UNIX, true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void getTiData(List<String> elements) {
        try {
            List<String> list = new ArrayList<>();
            Table table = hbaseConnection.getConnection().getTable(TableName.valueOf(TI_MAP.get(parameterTool.get("type"))));
            Result[] results = table.get(
                    elements.stream()
                            .map(element -> "url".equals(parameterTool.get("type")) ? DigestUtils.sha256Hex(element.split(" ")[2]) : element.split(" ")[2])
                            .map(ioc -> new Get(ioc.getBytes()))
                            .collect(Collectors.toList())
            );
            for (Result result : results) {
                NavigableMap<byte[], byte[]> familyMap = result.getFamilyMap("cf".getBytes());
                if (result.getRow() == null || familyMap == null) {
                    continue;
                }
                Map<String, Object> m = new HashMap<>();
                familyMap.forEach((qualifier, value) -> {
                    String q = Bytes.toString(qualifier);
                    Object v;
                    try {
                        if (Arrays.asList("result", "status", "credibility").contains(q)) {
                            v = Bytes.toInt(value);
                        } else if (Arrays.asList("create_time", "update_time", "expire_time", "first_seen", "last_seen", "scan_date").contains(q)) {
                            v = Bytes.toLong(value);
                        } else if (Arrays.asList("longitude", "latitude").contains(q)) {
                            v = Bytes.toDouble(value);
                        } else {
                            v = tryParseJson(Bytes.toString(value));
                        }
                        m.put(q, v);
                    } catch (Exception e) {
                        log.error("qualifier: {}, message: {}", q, e.getMessage(), e);
                    }
                });
                list.add(JSON_MAPPER.writeValueAsString(m));
            }
            FileUtils.writeLines(outputPath.resolve(parameterTool.get("type") + ".ti").toFile(), list, IOUtils.LINE_SEPARATOR_UNIX, true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void getCommercialSourceData(List<String> elements) {
        try {
            List<String> list = new ArrayList<>();
            for (String element : elements) {
                Map<String, Object> map = new HashMap<>();
                for (Map.Entry<String, String> cs : COMMERCIAL_SOURCE_MAP.entrySet()) {
                    if ("virustotal-url".equals(cs.getKey()) && !"url".equals(parameterTool.get("type"))) {
                        continue;
                    }
                    Table table = hbaseConnection.getConnection().getTable(TableName.valueOf(cs.getValue()));
                    Scan scan = new Scan();
                    String rowKey = "url".equals(parameterTool.get("type")) ? DigestUtils.sha256Hex(element.split(" ")[2]) : element.split(" ")[2];
                    scan.setRowPrefixFilter(Bytes.toBytes(rowKey + "-"));
                    ResultScanner scanner = table.getScanner(scan);
                    for (Result result : scanner) {
                        NavigableMap<byte[], byte[]> familyMap = result.getFamilyMap("cf".getBytes());
                        if (result.getRow() == null || familyMap == null) {
                            continue;
                        }
                        Map<String, Object> m = new HashMap<>();
                        familyMap.forEach((qualifier, value) -> {
                            String c = Bytes.toString(qualifier);
                            Object v;
                            if (Arrays.asList("virustotal", "virustotal-url").contains(cs.getKey()) && "dataType".equals(c)) {
                                v = Bytes.toInt(value);
                            } else {
                                v = tryParseJson(Bytes.toString(value));
                            }
                            m.put(c, v);
                        });
                        map.put(cs.getKey(), m);
                        break;
                    }
                }
                if (!map.isEmpty()) {
                    map.put("element_value", element.split(" ")[2]);
                    list.add(JSON_MAPPER.writeValueAsString(map));
                }
            }
            FileUtils.writeLines(outputPath.resolve(parameterTool.get("type") + ".tics").toFile(), list, IOUtils.LINE_SEPARATOR_UNIX, true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
