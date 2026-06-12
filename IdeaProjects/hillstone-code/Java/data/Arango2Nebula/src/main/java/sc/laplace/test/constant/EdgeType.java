package sc.laplace.test.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sc.laplace.test.model.edge.IpOpenPort;
import sc.laplace.test.model.edge.Relation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jxwu
 */
@Getter
@RequiredArgsConstructor
public enum EdgeType {
    IP_OPEN_PORT("ip_open_port", IpOpenPort.class, ipOpenPortQuery()),
    IP_RESOLVE_DOMAIN("ip_resolve_domain", Relation.class, ipResolveDomainQuery()),
    IP_DOWNLOAD_FILE("ip_download_file", Relation.class, fromToFileQuery("ip_download_file", "ip_address")),
    IP_CONNECT_URL("ip_connect_url", Relation.class, simpleQuery("ip_related_url", "ip_address", "url")),
    IP_HAS_TAG("ip_has_tag", Relation.class, simpleQuerySwapFromTag("ip_related_tag", "ip_address")),
    DOMAIN_RESOLVE_IP("domain_resolve_ip", Relation.class, domainResolveIpQuery()),
    DOMAIN_INCLUDE_SUBDOMAIN("domain_include_subdomain", Relation.class, simpleQuery("sub_domain", "domain_name", "domain_name")),
    DOMAIN_DOWNLOAD_FILE("domain_download_file", Relation.class, fromToFileQuery("domain_download_file", "domain_name")),
    DOMAIN_CONNECT_URL("domain_connect_url", Relation.class, simpleQuery("domain_related_url", "domain_name", "url")),
    DOMAIN_HAS_TAG("domain_has_tag", Relation.class, simpleQuerySwapFromTag("domain_related_tag", "domain_name")),
    FILE_CONNECT_IP("file_connect_ip", Relation.class, fileToQuery("file_connect_ip", "ip_address")),
    FILE_REFER_IP("file_refer_ip", Relation.class, fileToQuery("file_referer_ip", "ip_address")),
    FILE_CONNECT_DOMAIN("file_connect_domain", Relation.class, fileToQuery("file_connect_domain", "domain_name")),
    FILE_REFER_DOMAIN("file_refer_domain", Relation.class, fileToQuery("file_referer_domain", "domain_name")),
    FILE_CONNECT_URL("file_connect_url", Relation.class, fileToQuery("file_related_url", "url")),
    FILE_HAS_TAG("file_has_tag", Relation.class, fromToFileQuerySwapFromTag("file_related_tag")),
    URL_HAS_TAG("url_has_tag", Relation.class, simpleQuerySwapFromTag("url_related_tag", "url"));

    private static final String HASH_FIELDS_TO_DOC =
            "[toDoc.hash_md5, toDoc.hash_sha1, toDoc.hash_sha256, toDoc.hash_sha512]";
    private static final String HASH_FIELDS_FROM_DOC =
            "[fromDoc.hash_md5, fromDoc.hash_sha1, fromDoc.hash_sha256, fromDoc.hash_sha512]";
    private static final int RDNS_CURSOR_INDEX_BASE = 1000000000;
    private static final int RDNS_CURSOR_SUFFIX_LENGTH = 11;
    private static final String CURSOR_FILTER = " FILTER doc._key > NOT_NULL(@cursor, \"\")";
    private static final Map<String, EdgeType> COLLECTION_INDEX = createCollectionIndex();

    private final String nebulaEdge;
    private final Class<?> clazz;
    private final String query;

    public static EdgeType getByCollection(String collection) {
        return collection == null ? null : COLLECTION_INDEX.get(collection.trim().toLowerCase());
    }

    private static Map<String, EdgeType> createCollectionIndex() {
        Map<String, EdgeType> index = new HashMap<String, EdgeType>(values().length);
        for (EdgeType value : values()) {
            index.put(value.nebulaEdge, value);
        }
        return Collections.unmodifiableMap(index);
    }

    private static String fromToFileQuery(String collection, String fromField) {
        return "FOR doc IN " + collection
                + CURSOR_FILTER
                + " AND doc._from != null AND doc._to != null"
                + " SORT doc._key"
                + " LET fromDoc = DOCUMENT(doc._from)"
                + " LET toDoc = DOCUMENT(doc._to)"
                + " FILTER fromDoc." + fromField + " != null"
                + " AND (toDoc.hash_md5 != null OR toDoc.hash_sha1 != null OR toDoc.hash_sha256 != null OR toDoc.hash_sha512 != null)"
                + " LIMIT @limit"
                + " RETURN { __cursor: doc._key, from: " + prefixedField("fromDoc", fromField)
                + ", to: " + firstNonNullHash(HASH_FIELDS_TO_DOC)
                + ", rank: doc.timestamp }";
    }

    private static String fileToQuery(String collection, String toField) {
        return "FOR doc IN " + collection
                + CURSOR_FILTER
                + " AND doc._from != null AND doc._to != null"
                + " SORT doc._key"
                + " LET fromDoc = DOCUMENT(doc._from)"
                + " LET toDoc = DOCUMENT(doc._to)"
                + " FILTER toDoc." + toField + " != null"
                + " AND (fromDoc.hash_md5 != null OR fromDoc.hash_sha1 != null OR fromDoc.hash_sha256 != null OR fromDoc.hash_sha512 != null)"
                + " LIMIT @limit"
                + " RETURN { __cursor: doc._key, from: " + firstNonNullHash(HASH_FIELDS_FROM_DOC)
                + ", to: " + prefixedField("toDoc", toField)
                + ", rank: doc.timestamp }";
    }

    private static String simpleQuery(String collection, String fromField, String toField) {
        return simpleQuery(collection, fromField, toField, null);
    }

    private static String simpleQuery(String collection, String fromField, String toField, String docFilter) {
        return "FOR doc IN " + collection
                + CURSOR_FILTER
                + (docFilter == null || docFilter.isEmpty() ? "" : " AND " + docFilter)
                + " AND doc._from != null AND doc._to != null"
                + " SORT doc._key"
                + " LET fromDoc = DOCUMENT(doc._from)"
                + " LET toDoc = DOCUMENT(doc._to)"
                + " FILTER fromDoc." + fromField + " != null AND toDoc." + toField + " != null"
                + " LIMIT @limit"
                + " RETURN { __cursor: doc._key, from: " + prefixedField("fromDoc", fromField)
                + ", to: " + prefixedField("toDoc", toField)
                + ", rank: doc.timestamp }";
    }

    private static String simpleQuerySwapFromTag(String collection, String toField) {
        return "FOR doc IN " + collection
                + CURSOR_FILTER
                + " AND doc._from != null AND doc._to != null"
                + " SORT doc._key"
                + " LET toDoc = DOCUMENT(doc._to)"
                + " LET fromId = PARSE_IDENTIFIER(doc._from)"
                + " LET tagVid = fromId != null AND fromId.key != null ? CONCAT(\"tag:\", TO_STRING(fromId.key)) : null"
                + " FILTER tagVid != null AND toDoc." + toField + " != null"
                + " LIMIT @limit"
                + " RETURN { __cursor: doc._key, from: " + prefixedField("toDoc", toField) + ", to: tagVid, rank: doc.timestamp }";
    }

    private static String fromToFileQuerySwapFromTag(String collection) {
        return "FOR doc IN " + collection
                + CURSOR_FILTER
                + " AND doc._from != null AND doc._to != null"
                + " SORT doc._key"
                + " LET toDoc = DOCUMENT(doc._to)"
                + " LET fromId = PARSE_IDENTIFIER(doc._from)"
                + " LET tagVid = fromId != null AND fromId.key != null ? CONCAT(\"tag:\", TO_STRING(fromId.key)) : null"
                + " FILTER tagVid != null"
                + " AND (toDoc.hash_md5 != null OR toDoc.hash_sha1 != null OR toDoc.hash_sha256 != null OR toDoc.hash_sha512 != null)"
                + " LIMIT @limit"
                + " RETURN { __cursor: doc._key, from: " + firstNonNullHash(HASH_FIELDS_TO_DOC)
                + ", to: tagVid"
                + ", rank: doc.timestamp }";
    }

    private static String ipOpenPortQuery() {
        return "FOR doc IN ip_related_threat_port"
                + CURSOR_FILTER
                + " AND doc._from != null AND doc._to != null"
                + " SORT doc._key"
                + " LET fromDoc = DOCUMENT(doc._from)"
                + " LET toDoc = DOCUMENT(doc._to)"
                + " FILTER fromDoc.ip_address != null AND toDoc.port != null"
                + " LIMIT @limit"
                + " RETURN { __cursor: doc._key, ip_address: fromDoc.ip_address, rank: toDoc.port }";
    }

    private static String ipResolveDomainQuery() {
        return "LET rawCursor = NOT_NULL(@cursor, \"\")"
                + " LET hasCursor = LENGTH(rawCursor) > " + RDNS_CURSOR_SUFFIX_LENGTH
                + " LET cursorDocKey = hasCursor ? SUBSTRING(rawCursor, 0, LENGTH(rawCursor) - " + RDNS_CURSOR_SUFFIX_LENGTH + ") : \"\""
                + " LET cursorIdx = hasCursor ? TO_NUMBER(SUBSTRING(rawCursor, LENGTH(rawCursor) - 10, 10)) - " + RDNS_CURSOR_INDEX_BASE + " : -1"
                + " FOR doc IN ip"
                + " FILTER doc._key >= cursorDocKey"
                + " AND doc.ip_address != null"
                + " AND doc.rdns_records != null"
                + " AND LENGTH(doc.rdns_records) > 0"
                + " SORT doc._key"
                + " FOR idx IN 0..LENGTH(doc.rdns_records)-1"
                + " FILTER doc._key > cursorDocKey OR idx > cursorIdx"
                + " LET rdns = doc.rdns_records[idx]"
                + " LIMIT @limit"
                + " RETURN { __cursor: CONCAT(doc._key, \":\", TO_STRING(idx + " + RDNS_CURSOR_INDEX_BASE + ")), from: CONCAT(\"ipv4:\", doc.ip_address), to: CONCAT(\"domain:\", rdns.domain_name), rank: rdns.lookup_time }";
    }

    private static String domainResolveIpQuery() {
        return "FOR doc IN domain_ip_relation"
                + CURSOR_FILTER
                + " AND doc.is_current == true"
                + " AND doc._from != null AND doc._to != null"
                + " SORT doc._key"
                + " LET fromDoc = DOCUMENT(doc._from)"
                + " LET toDoc = DOCUMENT(doc._to)"
                + " FILTER fromDoc != null AND toDoc != null"
                + " AND fromDoc.domain_name != null AND toDoc.ip_address != null"
                + " LIMIT @limit"
                + " RETURN { __cursor: doc._key, from: CONCAT(\"domain:\", fromDoc.domain_name), to: CONCAT(\"ipv4:\", toDoc.ip_address), rank: doc.timestamp }";
    }

    private static String prefixedField(String alias, String field) {
        if ("ip_address".equals(field)) {
            return "CONCAT(\"ipv4:\", " + alias + "." + field + ")";
        }
        if ("domain_name".equals(field)) {
            return "CONCAT(\"domain:\", " + alias + "." + field + ")";
        }
        if ("url".equals(field)) {
            return "CONCAT(\"url:\", " + alias + "." + field + ")";
        }
        return alias + "." + field;
    }

    private static String firstNonNullHash(String hashFields) {
        return "FIRST(FOR h IN " + hashFields + " FILTER h != null RETURN h)";
    }
}
