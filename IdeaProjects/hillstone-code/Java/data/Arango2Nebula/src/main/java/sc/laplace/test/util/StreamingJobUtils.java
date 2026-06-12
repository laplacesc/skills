package sc.laplace.test.util;

import lombok.experimental.UtilityClass;

import java.util.*;

/**
 * Flink 作业用到的静态字段定义和小工具。
 *
 * <p>这里集中维护 Nebula tag 字段列表与对应 positions，
 * 保证 VertexMappings、VertexRowMapper 和 sink 配置使用同一份约定。
 */
@UtilityClass
public class StreamingJobUtils {
    public static final List<String> IOC_FIELDS = Collections.unmodifiableList(Arrays.asList(
            "create_time",
            "update_time",
            "type",
            "result",
            "credibility",
            "lifecycle_status",
            "flow_direction",
            "first_seen",
            "last_seen",
            "malicious_level"
    ));
    public static final List<String> IP_FIELDS = Collections.unmodifiableList(Arrays.asList(
            "ip_address",
            "certificate"
    ));
    public static final List<String> DOMAIN_FIELDS = Collections.unmodifiableList(Arrays.asList(
            "domain_name",
            "detail_type",
            "top_domain_name",
            "certificate"
    ));
    public static final List<String> FILE_FIELDS = Collections.unmodifiableList(Arrays.asList(
            "file_type",
            "file_size"
    ));
    public static final List<String> URL_FIELDS = Collections.singletonList(
            "url"
    );
    public static final List<String> HASH_FIELDS = Collections.unmodifiableList(Arrays.asList(
            "hash_md5",
            "hash_sha1",
            "hash_sha256",
            "hash_sha512"
    ));
    public static final List<String> TAG_FIELDS = Collections.unmodifiableList(Arrays.asList(
            "id", "type", "subtype", "name_cn", "name_en",
            "description_cn", "description_en", "visibility", "severity"
    ));
    public static final List<Integer> IOC_POSITIONS = positions(IOC_FIELDS);
    public static final List<Integer> IP_POSITIONS = positions(IP_FIELDS);
    public static final List<Integer> DOMAIN_POSITIONS = positions(DOMAIN_FIELDS);
    public static final List<Integer> FILE_POSITIONS = positions(FILE_FIELDS);
    public static final List<Integer> URL_POSITIONS = positions(URL_FIELDS);
    public static final List<Integer> TAG_POSITIONS = positions(TAG_FIELDS);
    public static final List<Integer> SINGLE_VALUE_POSITION = Collections.singletonList(1);

    /**
     * 解析逗号分隔配置，并保持原始顺序去重。
     */
    public static List<String> split(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String[] items = raw.split(",");
        LinkedHashSet<String> results = new LinkedHashSet<>(items.length);
        for (String item : items) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty()) {
                results.add(trimmed);
            }
        }
        return Collections.unmodifiableList(new ArrayList<>(results));
    }

    /**
     * Nebula connector 的属性位从 1 开始，因为 0 号位固定保留给 VID。
     */
    public static List<Integer> positions(List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> results = new ArrayList<>(fields.size());
        for (int i = 1; i <= fields.size(); i++) {
            results.add(i);
        }
        return Collections.unmodifiableList(results);
    }
}
