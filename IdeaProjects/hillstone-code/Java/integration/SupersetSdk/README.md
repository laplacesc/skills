# Apache Superset Java SDK

`SupersetSdk` 是 [Apache Superset](https://superset.apache.org/) REST API 的 Java SDK，基于 Spring Boot 2.7.6 构建，提供类型安全的 API 调用封装。

## 快速开始

### 1. 添加依赖

先在本模块根目录执行 `mvn install` 安装 SDK 到本地仓库：

```bash
cd Java/integration/SupersetSdk
mvn clean install
```

然后在项目中添加 Maven 依赖：

```xml
<dependency>
    <groupId>sc.laplace.test</groupId>
    <artifactId>SupersetSdk</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

如果项目使用了 Spring Boot BOM，直接引入即可；SDK 的 bean 会通过 `@ComponentScan` 自动注册。

### 2. 配置

在 `application.yml` 中配置 Superset 连接信息：

```yaml
superset:
  base-url: http://localhost:8088    # Superset 实例地址
  username: admin                     # 用户名（密码模式）
  password: admin                     # 密码
  # access-token: eyJ...             # 或直接提供 JWT Token
  connect-timeout: 10000              # 连接超时（毫秒，可选）
  read-timeout: 30000                 # 读取超时（毫秒，可选）
```

也可以通过环境变量覆盖配置（`AI_BASE_URL` / `AI_API_KEY` 风格，前缀 `SUPERSET_`，全大写 + 下划线）。

### 3. 注入并使用

```java
@SpringBootApplication
@ComponentScan(basePackages = "sc.laplace.test.superset")
public class MyApp implements CommandLineRunner {

    @Autowired
    private DashboardService dashboardService;

    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }

    @Override
    public void run(String... args) {
        PaginatedResponse<DashboardInfo> dashboards = dashboardService.list();
        System.out.println("Dashboard count: " + dashboards.getCount());
        dashboards.getResult().forEach(d ->
            System.out.println(d.getId() + ": " + d.getDashboardTitle()));
    }
}
```

## 配置参考

| 属性 | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `superset.base-url` | String | `http://localhost:8088` | Superset 实例基础 URL |
| `superset.username` | String | — | 用户名（密码认证模式） |
| `superset.password` | String | — | 密码 |
| `superset.access-token` | String | — | JWT Token（替代 username/password） |
| `superset.connect-timeout` | int | `10000` | 连接超时（毫秒） |
| `superset.read-timeout` | int | `30000` | 读取超时（毫秒） |

## API 参考

所有 Service 均通过 SDK 的 Spring 自动配置自动注册为 Bean，直接注入即可使用。

### DashboardService

操作 Superset 仪表盘。

| 方法 | 说明 |
|---|---|
| `list()` | 列出所有仪表盘 |
| `list(queryParams)` | 带查询参数（分页/过滤）列出 |
| `get(id)` | 获取仪表盘详情 |
| `create(request)` | 创建仪表盘 |
| `update(id, request)` | 更新仪表盘 |
| `delete(id)` | 删除仪表盘 |
| `getCharts(dashboardId)` | 获取仪表盘内的图表列表 |
| `export(id)` | 导出仪表盘（二进制） |
| `getData(id)` | 获取原始 JSON 数据 |

```java
@Autowired
private DashboardService dashboardService;

// 列出
PaginatedResponse<DashboardInfo> list = dashboardService.list();

// 带分页参数：?q=(page:0,page_size:20,filters:!())
PaginatedResponse<DashboardInfo> filtered = dashboardService.list("?q=(page:0,page_size:10)");

// 获取详情
DashboardDetail detail = dashboardService.get(1);

// 创建
DashboardCreateRequest req = new DashboardCreateRequest("My Dashboard");
req.setDescription("Created via SDK");
DashboardDetail created = dashboardService.create(req);

// 更新
DashboardCreateRequest update = new DashboardCreateRequest("Renamed");
update.setDescription("Updated description");
DashboardDetail updated = dashboardService.update(1, update);

// 删除
dashboardService.delete(1);

// 获取仪表盘中的图表
List<ChartInfo> charts = dashboardService.getCharts(1);
```

### ChartService

操作 Superset 图表（Charts / Slices）。

| 方法 | 说明 |
|---|---|
| `list()` | 列出所有图表 |
| `list(queryParams)` | 带查询参数列出 |
| `get(id)` | 获取图表详情 |
| `create(request)` | 创建图表 |
| `update(id, request)` | 更新图表 |
| `delete(id)` | 删除图表 |
| `getData(chartId)` | 获取图表查询结果数据 |
| `createPermalink(chartId, request)` | 创建探索页面的永久链接 |

```java
@Autowired
private ChartService chartService;

// 列出
PaginatedResponse<ChartInfo> charts = chartService.list();

// 获取详情
ChartDetail detail = chartService.get(1);

// 创建图表
Map<String, Object> formData = new HashMap<>();
formData.put("metrics", Arrays.asList("count"));
formData.put("groupby", Arrays.asList("id"));

ChartCreateRequest req = new ChartCreateRequest(
    "My Chart",          // slice_name
    "dist_bar",          // viz_type
    "1",                 // datasource_id
    "table"              // datasource_type
);
req.setFormData(formData);
ChartDetail created = chartService.create(req);

// 获取图表数据
JsonNode data = chartService.getData(1);
```

### DatabaseService

管理 Superset 数据库连接。

| 方法 | 说明 |
|---|---|
| `list()` | 列出所有数据库连接 |
| `list(queryParams)` | 带查询参数列出 |
| `get(id)` | 获取数据库连接详情 |
| `create(request)` | 新建数据库连接 |
| `update(id, request)` | 更新数据库连接 |
| `delete(id)` | 删除数据库连接 |
| `testConnection(request)` | 测试数据库连接是否可用 |
| `getSchemas(databaseId)` | 获取数据库的 Schema 列表 |
| `getTables(databaseId, schema)` | 获取指定 Schema 的表列表 |
| `validateSql(databaseId, sql)` | 验证 SQL 语句 |

```java
@Autowired
private DatabaseService databaseService;

// 列出
PaginatedResponse<DatabaseInfo> databases = databaseService.list();

// 获取 Schema
JsonNode schemas = databaseService.getSchemas(1);
// 获取某个 Schema 的表
JsonNode tables = databaseService.getTables(1, "public");

// 测试连接
DatabaseCreateRequest testReq = new DatabaseCreateRequest(
    "Test DB", "postgresql://user:pass@host:5432/db");
JsonNode result = databaseService.testConnection(testReq);

// 新建数据库连接
DatabaseCreateRequest newDb = new DatabaseCreateRequest(
    "My Database", "postgresql://user:pass@host:5432/mydb");
DatabaseInfo created = databaseService.create(newDb);
```

### DatasetService

管理数据集。

| 方法 | 说明 |
|---|---|
| `list()` | 列出所有数据集 |
| `list(queryParams)` | 带查询参数列出 |
| `get(id)` | 获取数据集详情（含列、指标定义） |
| `create(request)` | 创建数据集 |
| `update(id, request)` | 更新数据集 |
| `delete(id)` | 删除数据集 |
| `refreshSchema(datasetId)` | 刷新数据集 Schema（同步列变更） |

```java
@Autowired
private DatasetService datasetService;

// 列出
PaginatedResponse<DatasetInfo> datasets = datasetService.list();

// 获取详情（包含 columns, metrics 定义）
DatasetDetail detail = datasetService.get(1);
detail.getColumns();  // 列定义列表

// 创建数据集
DatasetCreateRequest req = new DatasetCreateRequest("my_table", 1);
req.setSchema("public");
req.setDescription("Created via SDK");
DatasetDetail created = datasetService.create(req);

// 刷新 schema
datasetService.refreshSchema(1);
```

### SqlLabService

在 SQL Lab 中执行查询。

| 方法 | 说明 |
|---|---|
| `execute(request)` | 异步执行 SQL 查询 |
| `executeSync(databaseId, sql, maxWaitMs)` | 同步执行（内部轮询直至完成） |
| `getResult(queryId)` | 获取查询结果 |
| `stop(queryId)` | 停止正在运行的查询 |
| `estimate(request)` | 估算查询返回的行数 |

```java
@Autowired
private SqlLabService sqlLabService;

// 异步执行
SqlExecuteRequest req = new SqlExecuteRequest("1", "SELECT * FROM my_table LIMIT 10");
SqlExecuteResponse resp = sqlLabService.execute(req);
String queryId = resp.getQueryId();

// 轮询结果
QueryResult result = sqlLabService.getResult(queryId);
result.getColumns();  // 列名列表
result.getData();     // 数据行列表（Map<String, Object>）
result.getRowCount(); // 行数

// 或使用同步包装方法（自动轮询直到完成或超时）
QueryResult syncResult = sqlLabService.executeSync("1", "SELECT count(*) FROM my_table", 30000);
```

### SavedQueryService

管理已保存的 SQL 查询。

| 方法 | 说明 |
|---|---|
| `list()` | 列出所有已保存查询 |
| `list(queryParams)` | 带查询参数列出 |
| `get(id)` | 获取已保存查询详情 |
| `create(request)` | 保存新查询 |
| `update(id, request)` | 更新已保存查询 |
| `delete(id)` | 删除已保存查询 |

```java
@Autowired
private SavedQueryService savedQueryService;

// 列出已保存查询
PaginatedResponse<SavedQueryInfo> queries = savedQueryService.list();

// 创建
SavedQueryCreateRequest req = new SavedQueryCreateRequest("1", "Daily Stats", "SELECT count(*) FROM events");
SavedQueryInfo created = savedQueryService.create(req);
```

## 认证说明

SDK 支持两种认证模式，通过 `SupersetAuthInterceptor` 自动处理：

### 密码模式（默认）

配置 `username` + `password`，SDK 在首次请求时自动调用 `POST /api/v1/security/login` 获取 access_token，并在后续请求中自动附加 `Authorization: Bearer <token>` header。

### Token 模式

直接配置 `access-token`，SDK 跳过登录步骤，直接使用提供的 Token。

### 自动续期

使用密码模式时，如果 Token 过期（收到 401），`SupersetAuthInterceptor` 会自动重新登录并重试请求。此过程对调用方透明。

## 错误处理

所有 API 失败均抛出 `SupersetApiException`（继承 `RuntimeException`），包含以下信息：

```java
try {
    DashboardDetail detail = dashboardService.get(999);
} catch (SupersetApiException e) {
    e.getStatusCode();  // HTTP 状态码（如 404, 500）, 无法解析时返回 0
    e.getApiPath();     // 请求的 API 路径
    e.getMessage();     // 格式化消息: "Superset API error [404] api/v1/dashboard/999: Not Found"
}
```

常见 HTTP 状态码：

| 状态码 | 含义 | 排查方向 |
|---|---|---|
| 401 | 未认证 | 检查 username/password 或 access-token 配置 |
| 403 | 无权限 | 检查 Superset 用户角色权限 |
| 404 | 资源不存在 | 确认资源 ID 是否正确 |
| 422 | 请求参数校验失败 | 检查请求体字段是否完整 |
| 500 | 服务端错误 | 查看 Superset 日志 |

## 底层客户端

`SupersetClient` 是核心 HTTP 客户端，所有 Service 都基于它。如果需要调用 SDK 尚未封装的 Superset API 端点，可以直接注入 `SupersetClient` 使用其通用方法：

```java
@Autowired
private SupersetClient client;

// 调用自定义 API
JsonNode result = client.get("api/v1/your_custom_endpoint/", JsonNode.class);
List<MyDto> list = client.getList("api/v1/custom/", MyDto.class);
MyDto created = client.post("api/v1/custom/", request, MyDto.class);
client.delete("api/v1/custom/1");

// 二进制下载
byte[] fileBytes = client.download("api/v1/dashboard/export/1");
```

`SupersetClient` 支持的方法：

| 方法 | 说明 |
|---|---|
| `get(path, responseType)` | GET 请求 |
| `getList(path, elementType)` | GET 请求，返回列表（自动解包 `result` 字段） |
| `getPaginated(path, elementType)` | GET 请求，返回 `PaginatedResponse<T>` |
| `post(path, body, responseType)` | POST 请求 |
| `postForJson(path, body)` | POST 请求，返回 `JsonNode` |
| `put(path, body, responseType)` | PUT 请求 |
| `delete(path)` | DELETE 请求 |
| `download(path)` | 二进制下载（返回 `byte[]`） |
| `getAsJsonNode(path)` | GET 请求，返回 `JsonNode` |

## Demo 项目

`Java/integration/SupersetSdk-demo/` 是一个完整的可运行示例，演示了所有 Service 的核心用法。

### 启动方式

```bash
# 1. 先安装 SDK 到本地仓库
mvn clean install -pl Java/integration/SupersetSdk

# 2. 修改配置
#    编辑 Java/integration/SupersetSdk-demo/src/main/resources/application.yml
#    填写正确的 superset 地址和凭证

# 3. 运行 Demo
mvn spring-boot:run -pl Java/integration/SupersetSdk-demo
```

Demo 程序通过 `CommandLineRunner` 启动后依次：
1. 列出所有仪表盘，获取第一个详情
2. 列出所有图表，获取第一个详情和数据
3. 列出所有数据库连接，获取 Schema 和表
4. 列出所有数据集，获取第一个详情
5. 在 SQL Lab 中执行异步查询并获取结果
6. 列出已保存的查询

> **注意**：Demo 中的创建/更新/删除操作默认被注释掉，防止误操作生产环境。如有需要可取消注释后运行。
