package sc.laplace.test.superset.demo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sc.laplace.test.superset.exception.SupersetApiException;
import sc.laplace.test.superset.model.*;
import sc.laplace.test.superset.service.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST API 控制器 —— 将 Superset SDK 的能力暴露给前端。
 * <p>
 * 所有端点以 {@code /api/} 为前缀，返回 JSON 数据。
 */
@RestController
@RequestMapping("/api")
public class SupersetController {

    private static final Logger log = LoggerFactory.getLogger(SupersetController.class);

    private static final String SCHEMA = "tip_db";

    private final DashboardService dashboardService;
    private final ChartService chartService;
    private final DatasetService datasetService;
    private final DatabaseService databaseService;
    private final SqlLabService sqlLabService;
    private final SavedQueryService savedQueryService;
    private final GuestTokenService guestTokenService;

    @Value("${superset.base-url:http://localhost:8088}")
    private String supersetBaseUrl;

    public SupersetController(DashboardService dashboardService,
                               ChartService chartService,
                               DatasetService datasetService,
                               DatabaseService databaseService,
                               SqlLabService sqlLabService,
                               SavedQueryService savedQueryService,
                               GuestTokenService guestTokenService) {
        this.dashboardService = dashboardService;
        this.chartService = chartService;
        this.datasetService = datasetService;
        this.databaseService = databaseService;
        this.sqlLabService = sqlLabService;
        this.savedQueryService = savedQueryService;
        this.guestTokenService = guestTokenService;
    }

    // ============================================================
    // Database（数据库连接）
    // ============================================================

    @GetMapping("/databases")
    public ResponseEntity<?> listDatabases() {
        try {
            PaginatedResponse<DatabaseInfo> resp = databaseService.list();
            return ResponseEntity.ok(resp);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("Superset API 调用失败: " + e.getMessage()));
        }
    }

    @GetMapping("/databases/{id}/schemas")
    public ResponseEntity<?> getDatabaseSchemas(@PathVariable Integer id) {
        try {
            JsonNode schemas = databaseService.getSchemas(id);
            return ResponseEntity.ok(schemas);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取 schema 失败: " + e.getMessage()));
        }
    }

    @GetMapping("/databases/{id}/tables/{schema}")
    public ResponseEntity<?> getDatabaseTables(@PathVariable Integer id,
                                                @PathVariable String schema) {
        try {
            JsonNode tables = databaseService.getTables(id, schema);
            return ResponseEntity.ok(tables);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取表列表失败: " + e.getMessage()));
        }
    }

    @GetMapping("/databases/{id}")
    public ResponseEntity<?> getDatabase(@PathVariable Integer id) {
        try {
            DatabaseInfo db = databaseService.get(id);
            return ResponseEntity.ok(db);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取数据库详情失败: " + e.getMessage()));
        }
    }

    // ============================================================
    // SQL Lab（SQL 查询）
    // ============================================================

    @PostMapping("/sqllab/execute")
    public ResponseEntity<?> executeSql(@RequestBody Map<String, Object> request) {
        try {
            Integer dbId = null;
            Object dbIdRaw = request.get("databaseId");
            if (dbIdRaw instanceof Number) {
                dbId = ((Number) dbIdRaw).intValue();
            } else if (dbIdRaw instanceof String) {
                try {
                    dbId = Integer.parseInt((String) dbIdRaw);
                } catch (NumberFormatException ignored) {}
            }
            String sql = Objects.toString(request.get("sql"), null);
            if (dbId == null || sql == null) {
                return ResponseEntity.badRequest().body(errorBody("缺少必要参数: databaseId, sql"));
            }
            SqlExecuteRequest execReq = new SqlExecuteRequest(dbId, sql);
            SqlExecuteResponse execResp = sqlLabService.execute(execReq);
            return ResponseEntity.ok(execResp);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("SQL 执行失败: " + e.getMessage()));
        }
    }

    @GetMapping("/sqllab/results/{queryId}")
    public ResponseEntity<?> getQueryResult(@PathVariable String queryId) {
        return ResponseEntity.status(501).body(errorBody(
            "此 Superset 版本为同步模式，结果已内嵌在 /sqllab/execute 响应中，不支持独立查询结果端点"));
    }

    @PostMapping("/sqllab/stop/{queryId}")
    public ResponseEntity<?> stopQuery(@PathVariable String queryId) {
        return ResponseEntity.status(501).body(errorBody(
            "此 Superset 版本为同步模式，不支持停止查询"));
    }

    // ============================================================
    // Dashboard（仪表盘）
    // ============================================================

    @GetMapping("/dashboards")
    public ResponseEntity<?> listDashboards() {
        try {
            PaginatedResponse<DashboardInfo> resp = dashboardService.list();
            return ResponseEntity.ok(resp);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取仪表盘列表失败: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboards/{id}")
    public ResponseEntity<?> getDashboard(@PathVariable Integer id) {
        try {
            DashboardDetail detail = dashboardService.get(id);
            return ResponseEntity.ok(detail);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取仪表盘详情失败: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboards/{id}/charts")
    public ResponseEntity<?> getDashboardCharts(@PathVariable Integer id) {
        try {
            List<ChartInfo> charts = dashboardService.getCharts(id);
            return ResponseEntity.ok(charts);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取仪表盘图表失败: " + e.getMessage()));
        }
    }

    // ============================================================
    // Chart（图表）
    // ============================================================

    @GetMapping("/charts")
    public ResponseEntity<?> listCharts() {
        try {
            PaginatedResponse<ChartInfo> resp = chartService.list();
            return ResponseEntity.ok(resp);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取图表列表失败: " + e.getMessage()));
        }
    }

    @GetMapping("/charts/{id}")
    public ResponseEntity<?> getChart(@PathVariable Integer id) {
        try {
            ChartDetail detail = chartService.get(id);
            return ResponseEntity.ok(detail);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取图表详情失败: " + e.getMessage()));
        }
    }

    @GetMapping("/charts/{id}/data")
    public ResponseEntity<?> getChartData(@PathVariable Integer id) {
        try {
            JsonNode data = chartService.getData(id);
            return ResponseEntity.ok(data);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取图表数据失败: " + e.getMessage()));
        }
    }

    // ============================================================
    // Dataset（数据集）
    // ============================================================

    @GetMapping("/datasets")
    public ResponseEntity<?> listDatasets() {
        try {
            PaginatedResponse<DatasetInfo> resp = datasetService.list();
            return ResponseEntity.ok(resp);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取数据集列表失败: " + e.getMessage()));
        }
    }

    @GetMapping("/datasets/{id}")
    public ResponseEntity<?> getDataset(@PathVariable Integer id) {
        try {
            DatasetDetail detail = datasetService.get(id);
            return ResponseEntity.ok(detail);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取数据集详情失败: " + e.getMessage()));
        }
    }

    // ============================================================
    // Saved Query（已保存查询）
    // ============================================================

    @GetMapping("/saved-queries")
    public ResponseEntity<?> listSavedQueries() {
        try {
            PaginatedResponse<SavedQueryInfo> resp = savedQueryService.list();
            return ResponseEntity.ok(resp);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取已保存查询列表失败: " + e.getMessage()));
        }
    }

    @GetMapping("/saved-queries/{id}")
    public ResponseEntity<?> getSavedQuery(@PathVariable Integer id) {
        try {
            SavedQueryInfo info = savedQueryService.get(id);
            return ResponseEntity.ok(info);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取已保存查询详情失败: " + e.getMessage()));
        }
    }

    // ============================================================
    // 跨引擎对比（为前端封装的便捷端点）
    // ============================================================

    /**
     * 获取所有数据库连接，并按引擎分组返回。
     */
    @GetMapping("/engines")
    public ResponseEntity<?> getEngines() {
        try {
            PaginatedResponse<DatabaseInfo> databases = databaseService.list();
            Map<String, List<Map<String, Object>>> engines = new LinkedHashMap<>();
            for (DatabaseInfo db : databases.getResult()) {
                String backend = db.getBackend() != null ? db.getBackend() : "unknown";
                engines.computeIfAbsent(backend, k -> new ArrayList<>());
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("id", db.getId());
                info.put("name", db.getDatabaseName());
                info.put("backend", db.getBackend());
                engines.get(backend).add(info);
            }
            return ResponseEntity.ok(engines);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取引擎列表失败: " + e.getMessage()));
        }
    }

    /**
     * 对两数据库执行同一条 SQL，返回对比结果（用于跨引擎对比）。
     */
    @PostMapping("/cross-db/compare")
    public ResponseEntity<?> crossDbCompare(@RequestBody Map<String, Object> request) {
        Integer dbIdA = request.get("dbIdA") instanceof Number ? ((Number) request.get("dbIdA")).intValue() : null;
        Integer dbIdB = request.get("dbIdB") instanceof Number ? ((Number) request.get("dbIdB")).intValue() : null;
        String sql = Objects.toString(request.get("sql"), null);

        if (dbIdA == null || dbIdB == null || sql == null) {
            return ResponseEntity.badRequest().body(errorBody("缺少必要参数: dbIdA, dbIdB, sql"));
        }

        try {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("sql", sql);
            result.put("dbA", executeAndCollect(dbIdA, sql));
            result.put("dbB", executeAndCollect(dbIdB, sql));
            return ResponseEntity.ok(result);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("跨引擎对比失败: " + e.getMessage()));
        }
    }

    /**
     * 获取 overview 概览数据（首页用）。
     */
    @GetMapping("/overview")
    public ResponseEntity<?> overview() {
        try {
            Map<String, Object> overview = new LinkedHashMap<>();
            overview.put("databases", databaseService.list().getCount());
            overview.put("dashboards", dashboardService.list().getCount());
            overview.put("charts", chartService.list().getCount());
            overview.put("datasets", datasetService.list().getCount());
            overview.put("savedQueries", savedQueryService.list().getCount());
            return ResponseEntity.ok(overview);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取概览失败: " + e.getMessage()));
        }
    }

    // ---------------------------------------------------------------
    // 内部辅助方法
    // ---------------------------------------------------------------

    private Map<String, Object> executeAndCollect(Integer dbId, String sql) {
        try {
            SqlExecuteRequest req = new SqlExecuteRequest(dbId, sql);
            SqlExecuteResponse execResp = sqlLabService.execute(req);

            Map<String, Object> info = new LinkedHashMap<>();
            info.put("queryId", execResp.getQueryId());
            info.put("status", execResp.getStatus());
            // columns 是列元数据对象列表，提取 name 字段作为列名列表
            List<String> columnNames = execResp.getColumns() != null
                ? execResp.getColumns().stream()
                    .map(col -> Objects.toString(col.get("name"), ""))
                    .collect(Collectors.toList())
                : null;
            info.put("columns", columnNames);
            info.put("rowCount", execResp.getRowCount());
            info.put("data", execResp.getData());
            info.put("errorMessage", execResp.getErrorMessage());
            return info;
        } catch (Exception e) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    // ============================================================
    // Embedding（仪表盘嵌入）
    // ============================================================

    /**
     * Generate a guest token for embedding a Superset dashboard.
     * <p>
     * The frontend uses this token with {@code @superset-ui/embedded-sdk}
     * to render the dashboard in an iframe.
     */
    @PostMapping("/embed/guest-token")
    public ResponseEntity<?> createGuestToken(@RequestBody Map<String, Object> request) {
        try {
            Object dashboardIdRaw = request.get("dashboardId");
            if (dashboardIdRaw == null) {
                return ResponseEntity.badRequest().body(errorBody("缺少必要参数: dashboardId"));
            }
            Integer dashboardId = dashboardIdRaw instanceof Number
                    ? ((Number) dashboardIdRaw).intValue()
                    : Integer.parseInt(dashboardIdRaw.toString());

            GuestTokenResponse token = guestTokenService.createDashboardToken(dashboardId);
            return ResponseEntity.ok(token);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("生成 Guest Token 失败: " + e.getMessage()));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(errorBody("dashboardId 格式无效"));
        }
    }

    /**
     * Get dashboard embedding metadata — returns the dashboard detail
     * along with a pre-generated guest token for iframe embedding.
     */
    @GetMapping("/embed/dashboard/{id}")
    public ResponseEntity<?> getEmbedDashboard(@PathVariable Integer id) {
        try {
            DashboardDetail detail = dashboardService.get(id);
            GuestTokenResponse token = guestTokenService.createDashboardToken(id);

            Map<String, Object> embedInfo = new LinkedHashMap<>();
            embedInfo.put("dashboard", detail);
            embedInfo.put("guestToken", token.getToken());
            embedInfo.put("supersetDomain", getSupersetBaseUrl());
            embedInfo.put("dashboardId", id);
            return ResponseEntity.ok(embedInfo);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取仪表盘嵌入信息失败: " + e.getMessage()));
        }
    }

    /**
     * Get embedding metadata for a chart.
     */
    @GetMapping("/embed/chart/{id}")
    public ResponseEntity<?> getEmbedChart(@PathVariable Integer id) {
        try {
            ChartDetail detail = chartService.get(id);
            // For chart embedding, we use chart type resource
            GuestTokenCreateRequest.GuestResource resource =
                    new GuestTokenCreateRequest.GuestResource("chart", String.valueOf(id));
            GuestTokenResponse token = guestTokenService.createToken(
                    java.util.Collections.singletonList(resource));

            Map<String, Object> embedInfo = new LinkedHashMap<>();
            embedInfo.put("chart", detail);
            embedInfo.put("guestToken", token.getToken());
            embedInfo.put("supersetDomain", getSupersetBaseUrl());
            embedInfo.put("chartId", id);
            return ResponseEntity.ok(embedInfo);
        } catch (SupersetApiException e) {
            return ResponseEntity.status(502).body(errorBody("获取图表嵌入信息失败: " + e.getMessage()));
        }
    }

    // ---------------------------------------------------------------
    // 内部辅助方法
    // ---------------------------------------------------------------

    /**
     * 获取已配置的 Superset 基础 URL。
     */
    private String getSupersetBaseUrl() {
        return supersetBaseUrl;
    }

    private static Map<String, Object> errorBody(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", message);
        return body;
    }
}
