package sc.laplace.test.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sc.laplace.test.model.vertex.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jxwu
 */
@Getter
@RequiredArgsConstructor
public enum VertexType {
    IP("ipv4", Ipv4.class, "FOR doc IN ip FILTER doc._key > NOT_NULL(@cursor, \"\") SORT doc._key LIMIT @limit RETURN { __cursor: doc._key, create_time: doc.create_time, update_time: doc.update_time, type: doc.type != null ? doc.type : \"ip\", result: doc.result, credibility: doc.credibility, lifecycle_status: doc.lifecycle.status, flow_direction: doc.flow_direction, first_seen: doc.first_seen, last_seen: doc.last_seen, malicious_level: doc.malicious_level, ip_address: doc.ip_address, certificate: doc.certificate }"),
    DOMAIN("domain", Domain.class, "FOR doc IN domain FILTER doc._key > NOT_NULL(@cursor, \"\") SORT doc._key LIMIT @limit RETURN { __cursor: doc._key, create_time: doc.create_time, update_time: doc.update_time, type: doc.type != null ? doc.type : \"domain\", result: doc.result, credibility: doc.credibility, lifecycle_status: doc.lifecycle.status, flow_direction: doc.flow_direction, first_seen: doc.first_seen, last_seen: doc.last_seen, malicious_level: doc.malicious_level, domain_name: doc.domain_name, detail_type: doc.detail_type != null ? doc.detail_type : \"FQDN\", top_domain_name: doc.top_domain_name, certificate: doc.last_https_certificate }"),
    FILE("file", File.class, "FOR doc IN file FILTER doc._key > NOT_NULL(@cursor, \"\") SORT doc._key LIMIT @limit RETURN { __cursor: doc._key, create_time: doc.create_time, update_time: doc.update_time, type: doc.type != null ? doc.type : \"file\", result: doc.result, credibility: doc.credibility, lifecycle_status: doc.lifecycle.status, flow_direction: doc.flow_direction, first_seen: doc.first_seen, last_seen: doc.last_seen, malicious_level: doc.malicious_level, hash_md5: doc.hash_md5, hash_sha1: doc.hash_sha1, hash_sha256: doc.hash_sha256, hash_sha512: doc.hash_sha512, file_type: doc.file_type, file_size: doc.file_size }"),
    URL("url", Url.class, "FOR doc IN url FILTER doc._key > NOT_NULL(@cursor, \"\") SORT doc._key LIMIT @limit RETURN { __cursor: doc._key, create_time: doc.create_time, update_time: doc.update_time, type: doc.type != null ? doc.type : \"url\", result: doc.result, credibility: doc.credibility, lifecycle_status: doc.lifecycle.status, flow_direction: doc.flow_direction, first_seen: doc.first_seen, last_seen: doc.last_seen, malicious_level: doc.malicious_level, url: doc.url }"),
    TAG("tag", Tag.class, "FOR doc IN tag FILTER doc._key > NOT_NULL(@cursor, \"\") AND doc.name_en != null AND TO_NUMBER(doc._key) != null SORT doc._key LIMIT @limit RETURN { __cursor: doc._key, id: TO_NUMBER(doc._key), type: doc.type, subtype: doc.subtype, name_cn: doc.name_cn, name_en: doc.name_en, description_cn: doc.description_cn, description_en: doc.description_en, visibility: doc.visibility, severity: doc.severity }");

    private static final Map<String, VertexType> COLLECTION_INDEX = createCollectionIndex();

    private final String nebulaTag;
    private final Class<?> clazz;
    private final String query;

    public static VertexType getByCollection(String collection) {
        return collection == null ? null : COLLECTION_INDEX.get(collection.trim().toLowerCase());
    }

    private static Map<String, VertexType> createCollectionIndex() {
        Map<String, VertexType> index = new HashMap<String, VertexType>(values().length);
        for (VertexType value : values()) {
            index.put(value.nebulaTag, value);
        }
        return Collections.unmodifiableMap(index);
    }
}
