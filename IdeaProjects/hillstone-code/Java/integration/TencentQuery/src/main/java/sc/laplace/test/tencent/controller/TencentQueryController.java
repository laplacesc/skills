package sc.laplace.test.tencent.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sc.laplace.test.tencent.service.QueryService;
import sc.laplace.test.tencent.service.TencentApiQuery;
import sc.laplace.test.tencent.service.TencentSdkQuery;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jxwu
 */
@RestController
public class TencentQueryController {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    @Resource
    private TencentApiQuery tencentApiQuery;
    @Resource
    private TencentSdkQuery tencentSdkQuery;
    @Resource
    private QueryService queryService;

    @GetMapping("/tx/api/query")
    public ResponseEntity<Object> apiQuery(@RequestParam String key, @RequestParam String type) {
        return ResponseEntity.ok().body(tencentApiQuery.query(key, type));
    }

    @GetMapping("/tx/sdk/query")
    public ResponseEntity<Object> sdkQuery(@RequestParam String key, @RequestParam String type) {
        return ResponseEntity.ok().body(tencentSdkQuery.query(key, type));
    }

    @GetMapping("/tx/sdk/upgrade")
    public ResponseEntity<Void> sdkUpgrade(@RequestParam String mod) {
        CompletableFuture.runAsync(() -> tencentSdkQuery.upGrade(mod), executor);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tx/file/query")
    public ResponseEntity<Void> query(@RequestParam String path) throws IOException {
        CompletableFuture.runAsync(() -> queryService.query(Paths.get(path)), executor);
        return ResponseEntity.ok().build();
    }
}
