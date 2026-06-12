package sc.laplace.test.job;

import sc.laplace.test.util.StreamingJobUtils;

import java.util.*;

public final class VertexMappings {
    // 一个 collection 可以映射成多个 tag，这里集中维护“源 collection -> Nebula tag 写入定义”。
    private static final Map<String, List<VertexSinkDefinition>> VERTEX_SINK_DEFINITIONS = createVertexSinkDefinitions();

    private VertexMappings() {
    }

    public static List<VertexSinkDefinition> getDefinitions(String collection) {
        if (collection == null) {
            return null;
        }
        return VERTEX_SINK_DEFINITIONS.get(collection.trim().toLowerCase());
    }

    private static Map<String, List<VertexSinkDefinition>> createVertexSinkDefinitions() {
        Map<String, List<VertexSinkDefinition>> definitions = new LinkedHashMap<>();
        definitions.put("ipv4", Arrays.asList(
                new VertexSinkDefinition("ioc", StreamingJobUtils.IOC_FIELDS, StreamingJobUtils.IOC_POSITIONS),
                new VertexSinkDefinition("ipv4", StreamingJobUtils.IP_FIELDS, StreamingJobUtils.IP_POSITIONS)
        ));
        definitions.put("domain", Arrays.asList(
                new VertexSinkDefinition("ioc", StreamingJobUtils.IOC_FIELDS, StreamingJobUtils.IOC_POSITIONS),
                new VertexSinkDefinition("domain", StreamingJobUtils.DOMAIN_FIELDS, StreamingJobUtils.DOMAIN_POSITIONS)
        ));
        definitions.put("url", Arrays.asList(
                new VertexSinkDefinition("ioc", StreamingJobUtils.IOC_FIELDS, StreamingJobUtils.IOC_POSITIONS),
                new VertexSinkDefinition("url", StreamingJobUtils.URL_FIELDS, StreamingJobUtils.URL_POSITIONS)
        ));
        definitions.put("file", Arrays.asList(
                new VertexSinkDefinition("ioc", StreamingJobUtils.IOC_FIELDS, StreamingJobUtils.IOC_POSITIONS),
                new VertexSinkDefinition("file", StreamingJobUtils.FILE_FIELDS, StreamingJobUtils.FILE_POSITIONS)
        ));
        definitions.put("tag", Collections.singletonList(
                new VertexSinkDefinition("tag", StreamingJobUtils.TAG_FIELDS, StreamingJobUtils.TAG_POSITIONS)
        ));
        return Collections.unmodifiableMap(definitions);
    }

    /**
     * 单个 Nebula 顶点 sink 的静态定义。
     * fields/positions 必须与 VertexRowMapper 输出的 Row 结构严格对应。
     */
    public static final class VertexSinkDefinition {
        private final String tag;
        private final List<String> fields;
        private final List<Integer> positions;

        public VertexSinkDefinition(String tag, List<String> fields, List<Integer> positions) {
            this.tag = tag;
            this.fields = fields;
            this.positions = positions;
        }

        public String getTag() {
            return tag;
        }

        public List<String> getFields() {
            return fields;
        }

        public List<Integer> getPositions() {
            return positions;
        }
    }
}
