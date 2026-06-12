package sc.laplace.test.mapper;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.types.Row;
import sc.laplace.test.model.Record;
import sc.laplace.test.model.vertex.*;
import sc.laplace.test.util.JsonUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class VertexRowMapper implements MapFunction<Record, Row> {
    private final FieldExtractor[] extractors;
    private final int rowArity;

    public VertexRowMapper(List<String> fields) {
        this.extractors = createExtractors(fields);
        this.rowArity = 1 + this.extractors.length;
    }

    /**
     * 把统一 Record 映射成 Nebula vertex sink 所需的 Row。
     * Row[0] 是 VID，后续列顺序由 fields 决定。
     */
    @Override
    public Row map(Record value) {
        Vertex vertex = value.getVertex();
        // Row[0] 固定是 VID，后续字段顺序必须和 VertexExecutionOptions.positions 对齐。
        Row row = new Row(rowArity);
        row.setField(0, vertex.vid());
        for (int i = 0; i < extractors.length; i++) {
            row.setField(i + 1, extractors[i].extract(vertex));
        }
        return row;
    }

    static FieldExtractor createFieldExtractor(String field) {
        // 先走显式 extractor，避免每条记录都做 JSON fallback 带来的反射/序列化开销。
        if (field == null) {
            return NullFieldExtractor.INSTANCE;
        }
        if ("id".equals(field)) {
            return TagIdFieldExtractor.INSTANCE;
        }
        if ("type".equals(field)) {
            return TypeFieldExtractor.INSTANCE;
        }
        if ("subtype".equals(field)) {
            return TagSubtypeFieldExtractor.INSTANCE;
        }
        if ("name_cn".equals(field)) {
            return TagNameCnFieldExtractor.INSTANCE;
        }
        if ("name_en".equals(field)) {
            return TagNameEnFieldExtractor.INSTANCE;
        }
        if ("description_cn".equals(field)) {
            return TagDescriptionCnFieldExtractor.INSTANCE;
        }
        if ("description_en".equals(field)) {
            return TagDescriptionEnFieldExtractor.INSTANCE;
        }
        if ("visibility".equals(field)) {
            return TagVisibilityFieldExtractor.INSTANCE;
        }
        if ("severity".equals(field)) {
            return TagSeverityFieldExtractor.INSTANCE;
        }
        if ("create_time".equals(field)) {
            return IocCreateTimeFieldExtractor.INSTANCE;
        }
        if ("update_time".equals(field)) {
            return IocUpdateTimeFieldExtractor.INSTANCE;
        }
        if ("result".equals(field)) {
            return IocResultFieldExtractor.INSTANCE;
        }
        if ("credibility".equals(field)) {
            return IocCredibilityFieldExtractor.INSTANCE;
        }
        if ("lifecycle_status".equals(field)) {
            return IocLifecycleStatusFieldExtractor.INSTANCE;
        }
        if ("flow_direction".equals(field)) {
            return IocFlowDirectionFieldExtractor.INSTANCE;
        }
        if ("first_seen".equals(field)) {
            return IocFirstSeenFieldExtractor.INSTANCE;
        }
        if ("last_seen".equals(field)) {
            return IocLastSeenFieldExtractor.INSTANCE;
        }
        if ("malicious_level".equals(field)) {
            return IocMaliciousLevelFieldExtractor.INSTANCE;
        }
        if ("ip_address".equals(field)) {
            return Ipv4AddressFieldExtractor.INSTANCE;
        }
        if ("certificate".equals(field)) {
            return CertificateFieldExtractor.INSTANCE;
        }
        if ("domain_name".equals(field)) {
            return DomainNameFieldExtractor.INSTANCE;
        }
        if ("detail_type".equals(field)) {
            return DomainDetailTypeFieldExtractor.INSTANCE;
        }
        if ("top_domain_name".equals(field)) {
            return DomainTopDomainNameFieldExtractor.INSTANCE;
        }
        if ("hash_md5".equals(field)) {
            return FileHashMd5FieldExtractor.INSTANCE;
        }
        if ("hash_sha1".equals(field)) {
            return FileHashSha1FieldExtractor.INSTANCE;
        }
        if ("hash_sha256".equals(field)) {
            return FileHashSha256FieldExtractor.INSTANCE;
        }
        if ("hash_sha512".equals(field)) {
            return FileHashSha512FieldExtractor.INSTANCE;
        }
        if ("file_type".equals(field)) {
            return FileTypeFieldExtractor.INSTANCE;
        }
        if ("file_size".equals(field)) {
            return FileSizeFieldExtractor.INSTANCE;
        }
        if ("url".equals(field)) {
            return UrlFieldExtractor.INSTANCE;
        }
        return new FallbackFieldExtractor(field);
    }

    static Object resolveField(Vertex vertex, String field) {
        return createFieldExtractor(field).extract(vertex);
    }

    private static FieldExtractor[] createExtractors(List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return new FieldExtractor[0];
        }
        FieldExtractor[] fieldExtractors = new FieldExtractor[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            fieldExtractors[i] = createFieldExtractor(fields.get(i));
        }
        return fieldExtractors;
    }

    private static Object fallback(Vertex vertex, String field) {
        if (vertex == null || field == null) {
            return null;
        }
        // 未显式支持的字段最后退回到 Map 访问，兼容模型新增属性时不必先改 mapper。
        return JsonFallbackHolder.asMap(vertex).get(field);
    }

    private static final class JsonFallbackHolder {
        private JsonFallbackHolder() {
        }

        private static Map<String, Object> asMap(Object value) {
            return JsonUtil.toMap(value);
        }
    }

    /**
     * 轻量字段提取器。
     * 这里用预构建 extractor 数组替代运行时反射，减少大批量迁移时的序列化和分支开销。
     */
    interface FieldExtractor extends Serializable {
        Object extract(Vertex vertex);
    }

    private static final class NullFieldExtractor implements FieldExtractor {
        private static final NullFieldExtractor INSTANCE = new NullFieldExtractor();

        @Override
        public Object extract(Vertex vertex) {
            return null;
        }
    }

    private abstract static class IocFieldExtractor implements FieldExtractor {
        @Override
        public final Object extract(Vertex vertex) {
            return vertex instanceof Ioc ? doExtract((Ioc) vertex) : null;
        }

        abstract Object doExtract(Ioc ioc);
    }

    private abstract static class TagFieldExtractor implements FieldExtractor {
        @Override
        public final Object extract(Vertex vertex) {
            return vertex instanceof Tag ? doExtract((Tag) vertex) : null;
        }

        abstract Object doExtract(Tag tag);
    }

    private abstract static class Ipv4FieldExtractor implements FieldExtractor {
        @Override
        public final Object extract(Vertex vertex) {
            return vertex instanceof Ipv4 ? doExtract((Ipv4) vertex) : null;
        }

        abstract Object doExtract(Ipv4 ipv4);
    }

    private abstract static class DomainFieldExtractor implements FieldExtractor {
        @Override
        public final Object extract(Vertex vertex) {
            return vertex instanceof Domain ? doExtract((Domain) vertex) : null;
        }

        abstract Object doExtract(Domain domain);
    }

    private abstract static class FileFieldExtractor implements FieldExtractor {
        @Override
        public final Object extract(Vertex vertex) {
            return vertex instanceof File ? doExtract((File) vertex) : null;
        }

        abstract Object doExtract(File file);
    }

    private abstract static class UrlVertexFieldExtractor implements FieldExtractor {
        @Override
        public final Object extract(Vertex vertex) {
            return vertex instanceof Url ? doExtract((Url) vertex) : null;
        }

        abstract Object doExtract(Url url);
    }

    private static final class TagIdFieldExtractor extends TagFieldExtractor {
        private static final TagIdFieldExtractor INSTANCE = new TagIdFieldExtractor();

        @Override
        Object doExtract(Tag tag) {
            return tag.getId();
        }
    }

    private static final class TypeFieldExtractor implements FieldExtractor {
        private static final TypeFieldExtractor INSTANCE = new TypeFieldExtractor();

        @Override
        public Object extract(Vertex vertex) {
            if (vertex instanceof Tag) {
                return ((Tag) vertex).getType();
            }
            if (vertex instanceof Ioc) {
                return ((Ioc) vertex).getType();
            }
            return null;
        }
    }

    private static final class TagSubtypeFieldExtractor extends TagFieldExtractor {
        private static final TagSubtypeFieldExtractor INSTANCE = new TagSubtypeFieldExtractor();

        @Override
        Object doExtract(Tag tag) {
            return tag.getSubtype();
        }
    }

    private static final class TagNameCnFieldExtractor extends TagFieldExtractor {
        private static final TagNameCnFieldExtractor INSTANCE = new TagNameCnFieldExtractor();

        @Override
        Object doExtract(Tag tag) {
            return tag.getNameCn();
        }
    }

    private static final class TagNameEnFieldExtractor extends TagFieldExtractor {
        private static final TagNameEnFieldExtractor INSTANCE = new TagNameEnFieldExtractor();

        @Override
        Object doExtract(Tag tag) {
            return tag.getNameEn();
        }
    }

    private static final class TagDescriptionCnFieldExtractor extends TagFieldExtractor {
        private static final TagDescriptionCnFieldExtractor INSTANCE = new TagDescriptionCnFieldExtractor();

        @Override
        Object doExtract(Tag tag) {
            return tag.getDescriptionCn();
        }
    }

    private static final class TagDescriptionEnFieldExtractor extends TagFieldExtractor {
        private static final TagDescriptionEnFieldExtractor INSTANCE = new TagDescriptionEnFieldExtractor();

        @Override
        Object doExtract(Tag tag) {
            return tag.getDescriptionEn();
        }
    }

    private static final class TagVisibilityFieldExtractor extends TagFieldExtractor {
        private static final TagVisibilityFieldExtractor INSTANCE = new TagVisibilityFieldExtractor();

        @Override
        Object doExtract(Tag tag) {
            return tag.getVisibility();
        }
    }

    private static final class TagSeverityFieldExtractor extends TagFieldExtractor {
        private static final TagSeverityFieldExtractor INSTANCE = new TagSeverityFieldExtractor();

        @Override
        Object doExtract(Tag tag) {
            return tag.getSeverity();
        }
    }

    private static final class IocCreateTimeFieldExtractor extends IocFieldExtractor {
        private static final IocCreateTimeFieldExtractor INSTANCE = new IocCreateTimeFieldExtractor();

        @Override
        Object doExtract(Ioc ioc) {
            return ioc.getCreateTime();
        }
    }

    private static final class IocUpdateTimeFieldExtractor extends IocFieldExtractor {
        private static final IocUpdateTimeFieldExtractor INSTANCE = new IocUpdateTimeFieldExtractor();

        @Override
        Object doExtract(Ioc ioc) {
            return ioc.getUpdateTime();
        }
    }

    private static final class IocResultFieldExtractor extends IocFieldExtractor {
        private static final IocResultFieldExtractor INSTANCE = new IocResultFieldExtractor();

        @Override
        Object doExtract(Ioc ioc) {
            return ioc.getResult();
        }
    }

    private static final class IocCredibilityFieldExtractor extends IocFieldExtractor {
        private static final IocCredibilityFieldExtractor INSTANCE = new IocCredibilityFieldExtractor();

        @Override
        Object doExtract(Ioc ioc) {
            return ioc.getCredibility();
        }
    }

    private static final class IocLifecycleStatusFieldExtractor extends IocFieldExtractor {
        private static final IocLifecycleStatusFieldExtractor INSTANCE = new IocLifecycleStatusFieldExtractor();

        @Override
        Object doExtract(Ioc ioc) {
            return ioc.getLifecycleStatus();
        }
    }

    private static final class IocFlowDirectionFieldExtractor extends IocFieldExtractor {
        private static final IocFlowDirectionFieldExtractor INSTANCE = new IocFlowDirectionFieldExtractor();

        @Override
        Object doExtract(Ioc ioc) {
            return ioc.getFlowDirection();
        }
    }

    private static final class IocFirstSeenFieldExtractor extends IocFieldExtractor {
        private static final IocFirstSeenFieldExtractor INSTANCE = new IocFirstSeenFieldExtractor();

        @Override
        Object doExtract(Ioc ioc) {
            return ioc.getFirstSeen();
        }
    }

    private static final class IocLastSeenFieldExtractor extends IocFieldExtractor {
        private static final IocLastSeenFieldExtractor INSTANCE = new IocLastSeenFieldExtractor();

        @Override
        Object doExtract(Ioc ioc) {
            return ioc.getLastSeen();
        }
    }

    private static final class IocMaliciousLevelFieldExtractor extends IocFieldExtractor {
        private static final IocMaliciousLevelFieldExtractor INSTANCE = new IocMaliciousLevelFieldExtractor();

        @Override
        Object doExtract(Ioc ioc) {
            return ioc.getMaliciousLevel();
        }
    }

    private static final class Ipv4AddressFieldExtractor extends Ipv4FieldExtractor {
        private static final Ipv4AddressFieldExtractor INSTANCE = new Ipv4AddressFieldExtractor();

        @Override
        Object doExtract(Ipv4 ipv4) {
            return ipv4.getIpAddress();
        }
    }

    private static final class CertificateFieldExtractor implements FieldExtractor {
        private static final CertificateFieldExtractor INSTANCE = new CertificateFieldExtractor();

        @Override
        public Object extract(Vertex vertex) {
            if (vertex instanceof Domain) {
                return ((Domain) vertex).getCertificate();
            }
            if (vertex instanceof Ipv4) {
                return ((Ipv4) vertex).getCertificate();
            }
            return null;
        }
    }

    private static final class DomainNameFieldExtractor extends DomainFieldExtractor {
        private static final DomainNameFieldExtractor INSTANCE = new DomainNameFieldExtractor();

        @Override
        Object doExtract(Domain domain) {
            return domain.getDomainName();
        }
    }

    private static final class DomainDetailTypeFieldExtractor extends DomainFieldExtractor {
        private static final DomainDetailTypeFieldExtractor INSTANCE = new DomainDetailTypeFieldExtractor();

        @Override
        Object doExtract(Domain domain) {
            return domain.getDetailType();
        }
    }

    private static final class DomainTopDomainNameFieldExtractor extends DomainFieldExtractor {
        private static final DomainTopDomainNameFieldExtractor INSTANCE = new DomainTopDomainNameFieldExtractor();

        @Override
        Object doExtract(Domain domain) {
            return domain.getTopDomainName();
        }
    }

    private static final class FileHashMd5FieldExtractor extends FileFieldExtractor {
        private static final FileHashMd5FieldExtractor INSTANCE = new FileHashMd5FieldExtractor();

        @Override
        Object doExtract(File file) {
            return file.getHashMd5();
        }
    }

    private static final class FileHashSha1FieldExtractor extends FileFieldExtractor {
        private static final FileHashSha1FieldExtractor INSTANCE = new FileHashSha1FieldExtractor();

        @Override
        Object doExtract(File file) {
            return file.getHashSha1();
        }
    }

    private static final class FileHashSha256FieldExtractor extends FileFieldExtractor {
        private static final FileHashSha256FieldExtractor INSTANCE = new FileHashSha256FieldExtractor();

        @Override
        Object doExtract(File file) {
            return file.getHashSha256();
        }
    }

    private static final class FileHashSha512FieldExtractor extends FileFieldExtractor {
        private static final FileHashSha512FieldExtractor INSTANCE = new FileHashSha512FieldExtractor();

        @Override
        Object doExtract(File file) {
            return file.getHashSha512();
        }
    }

    private static final class FileTypeFieldExtractor extends FileFieldExtractor {
        private static final FileTypeFieldExtractor INSTANCE = new FileTypeFieldExtractor();

        @Override
        Object doExtract(File file) {
            return file.getFileType();
        }
    }

    private static final class FileSizeFieldExtractor extends FileFieldExtractor {
        private static final FileSizeFieldExtractor INSTANCE = new FileSizeFieldExtractor();

        @Override
        Object doExtract(File file) {
            return file.getFileSize();
        }
    }

    private static final class UrlFieldExtractor extends UrlVertexFieldExtractor {
        private static final UrlFieldExtractor INSTANCE = new UrlFieldExtractor();

        @Override
        Object doExtract(Url url) {
            return url.getUrl();
        }
    }

    private static final class FallbackFieldExtractor implements FieldExtractor {
        private final String field;

        private FallbackFieldExtractor(String field) {
            this.field = field;
        }

        @Override
        public Object extract(Vertex vertex) {
            return fallback(vertex, field);
        }
    }
}
