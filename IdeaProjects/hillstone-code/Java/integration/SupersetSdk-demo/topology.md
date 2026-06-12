# Superset SDK Demo 部署拓扑

## 架构总览

```
┌────────────────────────────────────────────────────────────────────────────┐
│                          你的开发机器                                       │
│                                                                            │
│  ┌─────────────────────────────────────────┐                               │
│  │  DemoApplication (Spring Boot)           │                               │
│  │  port: 0 (随机)                          │                               │
│  │                                          │                               │
│  │  ┌─────────────────────────────────────┐ │                               │
│  │  │  6 个 Service + SupersetClient      │ │                               │
│  │  │  DashboardService                   │ │                               │
│  │  │  ChartService                       │ │                               │
│  │  │  DatabaseService                    │ │  注入                         │
│  │  │  DatasetService          ──────────── SupersetClient                 │
│  │  │  SqlLabService                      │ │                               │
│  │  │  SavedQueryService                  │ │                               │
│  │  └─────────────────────────────────────┘ │                               │
│  └──────────────────┬──────────────────────┘                               │
│                     │                                                       │
│                     │  HTTP REST 请求                                       │
│                     │  Authorization: Bearer <JWT>                         │
│                     │                                                       │
│                     │  POST /api/v1/security/login   (认证)                │
│                     │  GET  /api/v1/database/         (列出数据库)         │
│                     │  POST /api/v1/sqllab/execute/   (执行 SQL)           │
│                     │  GET  /api/v1/sqllab/results/   (获取结果)           │
│                     │  GET  /api/v1/dashboard/        (仪表盘)             │
│                     │  GET  /api/v1/chart/            (图表)               │
│                     │  GET  /api/v1/dataset/          (数据集)             │
│                     │  ...                                                 │
└─────────────────────┼──────────────────────────────────────────────────────┘
                      │
                      │  http://<superset-host>:8088
                      ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                       Apache Superset (Docker)                             │
│                       port: 8088                                           │
│                                                                            │
│  ┌───────────────────────────────────────────────────────┐                 │
│  │  Gunicorn / Flask (Web Server)                         │                 │
│  │                                                         │                 │
│  │  ┌──────────────────────────────────────────────────┐  │                 │
│  │  │  REST API /api/v1/                               │  │                 │
│  │  │                                                   │  │                 │
│  │  │  ┌── 认证 ────────────────────────────────────┐  │  │                 │
│  │  │  │  POST /security/login                       │  │  │                 │
│  │  │  │  POST /security/csrf_token                  │  │  │                 │
│  │  │  └────────────────────────────────────────────┘  │  │                 │
│  │  │                                                   │  │                 │
│  │  │  ┌── 数据库连接 ─────────────────────────────┐  │  │                 │
│  │  │  │  GET  /database/                           │  │  │                 │
│  │  │  │  GET  /database/{id}/schemas               │  │  │                 │
│  │  │  │  GET  /database/{id}/tables/{schema}/      │  │  │                 │
│  │  │  └────────────────────────────────────────────┘  │  │                 │
│  │  │                                                   │  │                 │
│  │  │  ┌── SQL Lab ────────────────────────────────┐  │  │                 │
│  │  │  │  POST /sqllab/execute/                    │  │  │                 │
│  │  │  │  GET  /sqllab/results/{query_id}          │  │  │                 │
│  │  │  │  POST /sqllab/stop/{query_id}             │  │  │                 │
│  │  │  └────────────────────────────────────────────┘  │  │                 │
│  │  │                                                   │  │                 │
│  │  │  ┌── 资源管理 ───────────────────────────────┐  │  │                 │
│  │  │  │  GET/POST /dashboard/                     │  │  │                 │
│  │  │  │  GET/POST /chart/                         │  │  │                 │
│  │  │  │  GET/POST /dataset/                       │  │  │                 │
│  │  │  │  GET/POST /saved_query/                   │  │  │                 │
│  │  │  └────────────────────────────────────────────┘  │  │                 │
│  │  └──────────────────────────────────────────────────┘  │                 │
│  │                          │                               │                 │
│  │  ┌───────────────────────▼────────────────────────────┐ │                 │
│  │  │  SQLAlchemy (数据库抽象层)                           │ │                 │
│  │  │                                                     │ │                 │
│  │  │  • 97-mysql:     mysql+pymysql://root@10.182.139.130:3306/tip_db     │ │
│  │  │  • 97-clickhouse: clickhouse+native://hillstone@10.182.139.129:9000/tip_db │ │
│  │  └───────────────────┬────────────────────┬──────────────┘ │                 │
│  └──────────────────────┼────────────────────┼────────────────┘                 │
└─────────────────────────┼────────────────────┼──────────────────────────────────┘
                          │                    │
              ┌───────────▼──────────┐  ┌──────▼──────────────┐
              │     MySQL 97         │  │   ClickHouse 97     │
              │  10.182.139.130      │  │  10.182.139.129     │
              │  port: 3306          │  │  port: 9000 (native)│
              │  user: root          │  │  port: 8123 (HTTP)  │
              │                      │  │  user: hillstone    │
              │  ┌──────────────┐    │  │  ┌──────────────┐   │
              │  │   tip_db     │    │  │  │   tip_db     │   │
              │  │  ├─ tbl_a    │    │  │  │  ├─ tbl_x    │   │
              │  │  ├─ tbl_b    │    │  │  │  ├─ tbl_y    │   │
              │  │  └ ...       │    │  │  │  └ ...       │   │
              │  └──────────────┘    │  │  └──────────────┘   │
              │  行存 OLTP          │  │  列存 OLAP          │
              └─────────────────────┘  └──────────────────────┘
```

## 数据流时序

```
DemoApplication                          Superset API                     MySQL / ClickHouse
      │                                       │                                │
      │  (1) 认证                               │                                │
      │  POST /api/v1/security/login            │                                │
      │  {username, password, provider:"db"}    │                                │
      │──────────────────────────────────────▶  │                                │
      │  {access_token: "eyJhbGci..."}          │                                │
      │◀──────────────────────────────────────│  │                                │
      │                                       │                                │
      │  (2) 列出数据库                          │                                │
      │  GET /api/v1/database/                  │                                │
      │  Authorization: Bearer eyJ...          │                                │
      │──────────────────────────────────────▶  │                                │
      │  {count: 2, result: [                   │                                │
      │    {id:1, database_name:"97-mysql",     │                                │
      │     backend:"MySQL"},                   │                                │
      │    {id:2, database_name:"97-clickhouse",│                                │
      │     backend:"ClickHouse"}               │                                │
      │  ]}                                     │                                │
      │◀──────────────────────────────────────│  │                                │
      │                                       │                                │
      │  (3) 获取 Schema/表                     │                                │
      │  GET /api/v1/database/1/schemas        │                                │
      │──────────────────────────────────────▶  │                                │
      │  {result: ["tip_db", ...]}              │                                │
      │◀──────────────────────────────────────│  │                                │
      │                                       │                                │
      │  GET /api/v1/database/1/tables/tip_db/ │                                │
      │──────────────────────────────────────▶  │                                │
      │  {result: [{label:"tbl_a"},...]}        │                                │
      │◀──────────────────────────────────────│  │                                │
      │                                       │                                │
      │  (4) SQL Lab 查询                      │                                │
      │  POST /api/v1/sqllab/execute/          │                                │
      │  {database_id: 1,                      │                                │
      │   sql: "SHOW TABLES FROM tip_db"}      │                                │
      │──────────────────────────────────────▶  │                                │
      │                                       │  (翻译为方言 SQL)               │
      │                                       │────────────────────────────────▶│
      │  {query_id: "q-abc123", status:"pending"}│                             │
      │◀──────────────────────────────────────│  ◀──────────────────────────────│
      │                                       │                                │
      │  GET /api/v1/sqllab/results/q-abc123  │                                │
      │──────────────────────────────────────▶  │                                │
      │  {status:"success", columns:[...],     │                                │
      │   data: [{...}]}                       │                                │
      │◀──────────────────────────────────────│                                │
      │                                       │                                │
      │  (5) 跨引擎对比                         │                                │
      │  MySQL: SELECT COUNT(*) FROM tbl_a    │                                │
      │  ClickHouse: SELECT COUNT(*) FROM tbl_a  │                                │
      │  → 表格输出两侧行数差异                    │                                │
```

## 部署 Superset

### 方式一：Docker（推荐）

在**能访问 `10.182.139.129` 和 `10.182.139.130`** 的机器上执行：

```bash
# 拉取镜像并启动
docker run -d --name superset \
  -p 8088:8088 \
  -e SUPERSET_SECRET_KEY='your-random-secret-here' \
  apache/superset:latest

# 创建管理员账号
docker exec -it superset superset fab create-admin \
  --username admin \
  --password admin \
  --firstname Admin \
  --lastname User \
  --email admin@local

# 安装数据库驱动
docker exec -it superset pip install pymysql clickhouse-sqlalchemy

# 初始化元数据库
docker exec -it superset superset db upgrade
docker exec -it superset superset init

# 重启
docker restart superset
```

### 方式二：Docker Compose

<details>
<summary>点击展开 docker-compose.yml</summary>

```yaml
version: '3.8'
services:
  superset:
    image: apache/superset:latest
    container_name: superset
    ports:
      - "8088:8088"
    environment:
      - SUPERSET_SECRET_KEY=your-random-secret-here
    volumes:
      - superset_data:/app/superset_home
    restart: unless-stopped

volumes:
  superset_data:
```
</details>

## Superset Web UI 配置

### 1. 登录

打开 `http://<部署机器IP>:8088`，用 `admin / admin` 登录。

### 2. 添加数据库连接

右上角 **Settings** → **Database Connections** → 点击右上角 **+ Database**：

#### MySQL 97

| 字段 | 值 |
|---|---|
| **Database Name** | `97-mysql` |
| **SQLAlchemy URI** | `mysql+pymysql://root:密码@10.182.139.130:3306/tip_db` |

#### ClickHouse 97

| 字段 | 值 |
|---|---|
| **Database Name** | `97-clickhouse` |
| **SQLAlchemy URI** | `clickhouse+native://hillstone:密码@10.182.139.129:9000/tip_db` |

> 添加后点击 **Test Connection** 按钮验证连通性。

## 运行 Demo

### 1. 修改配置

编辑 `Java/integration/SupersetSdk-demo/src/main/resources/application.yml`：

```yaml
superset:
  base-url: http://<部署机器IP>:8088    # ← 替换为实际 Superset 地址
  username: admin
  password: admin
```

### 2. 先安装 SDK 到本地仓库

```bash
cd Java/integration/SupersetSdk
mvn clean install -DskipTests
```

> `mvn install` 需要能写入 `~/.m2/repository`。如果遇到 `Operation not permitted`，可以改用：
> ```bash
> mvn clean package -DskipTests
> # 然后手动将 target/SupersetSdk-0.0.1-SNAPSHOT.jar 复制到
> # ~/.m2/repository/sc/laplace/test/SupersetSdk/0.0.1-SNAPSHOT/
> # 同时复制 pom.xml 为 SupersetSdk-0.0.1-SNAPSHOT.pom
> ```

### 3. 运行

```bash
cd Java/integration/SupersetSdk-demo
mvn spring-boot:run
```

### 4. 预期输出

Demo 运行后依次执行 8 个步骤：

| 步骤 | 内容 | 预期输出 |
|---|---|---|
| 1 | 列出数据库，定位 MySQL 97 / ClickHouse 97 | 显示两台数据库的 ID、schema、表清单 |
| 2 | SQL Lab 查询 | `SHOW TABLES` 结果 + `COUNT(*)` 行数 |
| 3 | 跨引擎对比 | 表格形式对比每张表的 MySQL/ClickHouse 行数 |
| 4-7 | 仪表盘/图表/数据集/已保存查询 | 列出已有资源 |
| 8 | SupersetClient 底层调用 | 直接调用 REST API 返回原始 JSON |

## 文件索引

```
Java/integration/SupersetSdk/
├── README.md                     ← SDK 使用文档
├── pom.xml
└── src/

Java/integration/SupersetSdk-demo/
├── pom.xml
├── topology.puml                 ← PlantUML 拓扑图（可在线渲染）
├── topology.md                   ← 本文件（含 ASCII 图 + 部署指南）
└── src/main/
    ├── java/sc/laplace/test/superset/demo/DemoApplication.java
    └── resources/application.yml
```
