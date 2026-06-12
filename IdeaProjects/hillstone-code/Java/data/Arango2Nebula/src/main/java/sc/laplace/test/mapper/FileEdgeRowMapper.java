package sc.laplace.test.mapper;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import sc.laplace.test.config.AppConfig;
import sc.laplace.test.model.Record;
import sc.laplace.test.model.edge.Relation;
import sc.laplace.test.util.DigestHelper;
import sc.laplace.test.util.VidHelper;

import java.util.Collections;
import java.util.List;

/**
 * file 相关边的特殊 mapper。
 *
 * <p>源边里通常保存的是文件 hash，而不是最终 file 顶点 VID。
 * 这里直接按 file:<hash> 规则构造 file 顶点 VID，再转换成 Nebula 边。
 */
public class FileEdgeRowMapper extends RichFlatMapFunction<Record, Row> {
    private final FileEdgeMode mode;

    public FileEdgeRowMapper(AppConfig config, FileEdgeMode mode) {
        this.mode = mode;
    }

    @Override
    public void flatMap(Record value, Collector<Row> out) {
        if (!(value.getEdge() instanceof Relation)) {
            return;
        }
        Relation relation = (Relation) value.getEdge();
        String hash = normalize(mode.extractHash(relation));
        String otherSide = normalize(mode.extractOtherSide(relation));
        if (hash == null || otherSide == null) {
            return;
        }

        List<String> fileVids = queryFileVidsByHash(hash);
        if (fileVids.isEmpty()) {
            return;
        }

        Long rank = relation.getRank() == null ? 0L : relation.getRank();
        String otherSideVid = DigestHelper.sha256Hex(otherSide);
        for (String fileVid : fileVids) {
            Row row = new Row(3);
            row.setField(0, mode.isFileSource() ? fileVid : otherSideVid);
            row.setField(1, mode.isFileSource() ? otherSideVid : fileVid);
            row.setField(2, rank);
            out.collect(row);
        }
    }

    private List<String> queryFileVidsByHash(String hash) {
        String normalized = normalize(hash);
        if (normalized == null) {
            return Collections.emptyList();
        }
        // 直接按 file:<hash> 规则构造 file 顶点 VID（VidHelper 内部会做前缀标准化并 sha256）。
        return Collections.singletonList(VidHelper.vidFromFileHash(normalized));
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    /**
     * 描述 file 在边方向中的位置。
     * 不同 edge type 对“from/to”字段的语义不同，这里集中封装，避免在 flatMap 里散落条件分支。
     */
    public enum FileEdgeMode {
        FILE_TO_ENTITY(true) {
            @Override
            String extractHash(Relation relation) {
                return relation.getFrom();
            }

            @Override
            String extractOtherSide(Relation relation) {
                return relation.getTo();
            }
        },
        ENTITY_TO_FILE(false) {
            @Override
            String extractHash(Relation relation) {
                return relation.getTo();
            }

            @Override
            String extractOtherSide(Relation relation) {
                return relation.getFrom();
            }
        };

        private final boolean fileSource;

        FileEdgeMode(boolean fileSource) {
            this.fileSource = fileSource;
        }

        boolean isFileSource() {
            return fileSource;
        }

        abstract String extractHash(Relation relation);

        abstract String extractOtherSide(Relation relation);
    }
}
