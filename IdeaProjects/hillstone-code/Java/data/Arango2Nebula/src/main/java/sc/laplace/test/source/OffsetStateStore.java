package sc.laplace.test.source;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;

@Slf4j
/**
 * 本地 offset 状态存储。
 *
 * <p>状态按 phase + collection 维度保存处理条数和最后游标，
 * 用于一次性迁移中断后的断点恢复。
 */
public class OffsetStateStore {
    private final String pathValue;
    private final long flushIntervalMs;
    private final Map<String, Integer> processedCountState = new HashMap<String, Integer>();
    private final Map<String, String> cursorState = new HashMap<String, String>();
    private final Object lock = new Object();
    private volatile boolean dirty;
    private volatile long lastFlushAt;

    public OffsetStateStore(String pathValue, long flushIntervalMs) {
        this.pathValue = pathValue;
        this.flushIntervalMs = flushIntervalMs;
    }

    /**
     * 从 properties 文件恢复内存态进度。
     */
    public void load() {
        if (pathValue == null || pathValue.trim().isEmpty()) {
            return;
        }
        Path path = Paths.get(pathValue);
        if (!Files.exists(path)) {
            return;
        }
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(path)) {
            props.load(in);
        } catch (IOException e) {
            log.warn("Failed to load offset state file: {}", path.toAbsolutePath(), e);
            return;
        }
        synchronized (lock) {
            processedCountState.clear();
            cursorState.clear();
            for (Entry<Object, Object> entry : props.entrySet()) {
                String key = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());
                // 兼容旧格式 offset 和新格式 count/cursor，避免状态文件升级时丢失断点。
                if (key.endsWith(".cursor")) {
                    String cursor = value.trim();
                    if (!cursor.isEmpty()) {
                        cursorState.put(key.substring(0, key.length() - ".cursor".length()), cursor);
                    }
                    continue;
                }
                if (key.endsWith(".count")) {
                    putProcessedCount(key.substring(0, key.length() - ".count".length()), value, "count");
                    continue;
                }
                putProcessedCount(key, value, "offset");
            }
        }
        dirty = false;
        log.info("Loaded offset state from {}", path.toAbsolutePath());
    }

    public int getProcessedCount(String stateKey) {
        synchronized (lock) {
            Integer value = processedCountState.get(stateKey);
            return value == null ? 0 : value.intValue();
        }
    }

    public String getCursor(String stateKey) {
        synchronized (lock) {
            return cursorState.get(stateKey);
        }
    }

    /**
     * 更新单个 stateKey 的处理进度。
     */
    public void updateProgress(String stateKey, int processedCount, String cursor) {
        int nextCount = Math.max(0, processedCount);
        String normalizedCursor = normalizeCursor(cursor);
        synchronized (lock) {
            Integer previousCount = processedCountState.put(stateKey, nextCount);
            String previousCursor = cursorState.get(stateKey);
            if (normalizedCursor == null) {
                cursorState.remove(stateKey);
            } else {
                cursorState.put(stateKey, normalizedCursor);
            }
            boolean cursorChanged = previousCursor == null ? normalizedCursor != null : !previousCursor.equals(normalizedCursor);
            if (previousCount == null || previousCount.intValue() != nextCount || cursorChanged) {
                dirty = true;
            }
        }
    }

    /**
     * 把内存态刷到磁盘。
     * force=false 时会做节流，避免高频分页导致频繁文件写入。
     */
    public void save(boolean force) {
        synchronized (lock) {
            if (!dirty || pathValue == null || pathValue.trim().isEmpty()) {
                return;
            }
            long now = System.currentTimeMillis();
            // 非强制刷盘走节流，避免高频批次把本地状态文件写爆。
            if (!force && now - lastFlushAt < flushIntervalMs) {
                return;
            }

            Properties props = buildSortedProperties();

            try {
                writeStateFile(props, now);
            } catch (IOException e) {
                log.warn("Failed to save offset state file: {}", Paths.get(pathValue).toAbsolutePath(), e);
            }
        }
    }

    private void writeStateFile(Properties props, long now) throws IOException {
        Path path = Paths.get(pathValue);
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path tempPath = parent == null
                ? Files.createTempFile(path.getFileName().toString(), ".tmp")
                : Files.createTempFile(parent, path.getFileName().toString(), ".tmp");
        try {
            try (OutputStream out = Files.newOutputStream(tempPath,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                props.store(out, "ArangoSource offset state");
            }
            try {
                // 先写临时文件再原子替换，避免进程中断时把状态文件写坏。
                Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicMoveError) {
                Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
            }
            dirty = false;
            lastFlushAt = now;
        } finally {
            Files.deleteIfExists(tempPath);
        }
    }

    private Properties buildSortedProperties() {
        // 排序输出让状态文件更稳定，便于排查恢复点，也减少版本管理里的无序 diff。
        List<String> keys = new ArrayList<String>(processedCountState.size() + cursorState.size());
        for (String key : processedCountState.keySet()) {
            keys.add(key + ".count");
        }
        for (String key : cursorState.keySet()) {
            keys.add(key + ".cursor");
        }
        Collections.sort(keys);

        Properties props = new SortedProperties();
        for (String key : keys) {
            if (key.endsWith(".count")) {
                String stateKey = key.substring(0, key.length() - ".count".length());
                props.setProperty(key, String.valueOf(processedCountState.get(stateKey)));
                continue;
            }
            String stateKey = key.substring(0, key.length() - ".cursor".length());
            props.setProperty(key, cursorState.get(stateKey));
        }
        return props;
    }

    private void putProcessedCount(String key, String value, String valueType) {
        try {
            processedCountState.put(key, Math.max(0, Integer.parseInt(value)));
        } catch (NumberFormatException e) {
            log.warn("Invalid {} value for key {}: {}", valueType, key, value);
        }
    }

    private String normalizeCursor(String cursor) {
        if (cursor == null) {
            return null;
        }
        String normalized = cursor.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private static final class SortedProperties extends Properties {
        private SortedProperties() {
            super();
        }

        @Override
        public synchronized java.util.Enumeration<Object> keys() {
            // Properties 默认迭代顺序不稳定，这里显式排序保证 store() 输出顺序固定。
            List<Object> keys = new ArrayList<Object>(super.keySet());
            Collections.sort((List) keys);
            return Collections.enumeration(keys);
        }
    }
}
