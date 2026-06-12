package sc.laplace.test.tencent.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import sc.laplace.test.tencent.util.JsonUtil;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jxwu
 */
@Slf4j
@Service
public class QueryService {
    @Resource
    private TencentApiQuery tencentApiQuery;
    @Resource
    private TencentSdkQuery tencentSdkQuery;

    public void query(Path path) {
        log.info("query start...");
        String now = new SimpleDateFormat("yyyyMMddHHmmss").format(Date.from(Instant.now()));
        Path resultDirPath = Paths.get("result_" + now);

        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> {
                    try (Stream<String> lines = Files.lines(path)) {
                        FileUtils.writeLines(resultDirPath.resolve("api_query_result_" + now + ".txt").toFile(),
                                lines.parallel()
                                        .map(line -> tencentApiQuery.query(line, "domain"))
                                        .filter(Objects::nonNull)
                                        .map(JsonUtil::toJson)
                                        .collect(Collectors.toList()),
                                true);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }).exceptionally(t -> {
                    log.error(t.getMessage(), t);
                    return null;
                }),
                CompletableFuture.runAsync(() -> {
                    try (Stream<String> lines = Files.lines(path)) {
                        FileUtils.writeLines(resultDirPath.resolve("sdk_query_result_" + now + ".txt").toFile(),
                                lines.parallel()
                                        .map(line -> tencentSdkQuery.query(line, "domain"))
                                        .filter(Objects::nonNull)
                                        .map(JsonUtil::toJson)
                                        .collect(Collectors.toList()),
                                true);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }).exceptionally(t -> {
                    log.error(t.getMessage(), t);
                    return null;
                })
        ).join();

        log.info("query end...");
    }
}
