# Hillstone Codes

按技术栈和用途整理后的个人代码仓库。

## 仓库结构

```text
.
├── Java
│   ├── apps/          # 可直接运行或业务型 Java 项目
│   ├── data/          # 数据处理、图数据库、HBase 相关项目
│   ├── integration/   # 中间件与外部平台集成项目
│   └── labs/          # 技术验证与实验性项目
├── Python
│   ├── automation/    # 自动化脚本与效率工具
│   ├── diagnostics/   # 排障、缺陷定位相关脚本
│   └── tools/         # 通用小工具
├── Script
│   ├── api/           # HTTP 请求与接口调试脚本
│   ├── db/            # ArangoDB、Nebula 相关脚本
│   ├── infra/         # Kubernetes 等基础设施配置
│   ├── ops/           # 运维和业务相关 shell 工具
│   ├── perf/          # k6 压测脚本
│   └── misc/          # 临时资料与杂项文件
└── Slidev             # Slidev 演示文稿项目
```

## 当前分类说明

- `Java/apps`: `Simulator`、`HillstoneTest`、`PingTest`
- `Java/data`: `Arango2Nebula`、`HbaseFilter`、`HbaseQuery`
- `Java/integration`: `RabbitMq`、`TencentQuery`
- `Java/labs`: `CompletableFutureTest`
- `Python/automation`: `apply_for_internet_access`、`device_push_rule`、`rd_task`
- `Python/diagnostics`: `bug`
- `Python/tools`: `ping.py`
- `Script/api`: 接口请求与 HTTP 调试脚本
- `Script/db`: ArangoDB、Nebula 相关脚本与部署文件
- `Script/infra`: Kubernetes 资源配置
- `Script/ops`: 运维工具与 DSGP 相关脚本
- `Script/perf`: k6 压测脚本
- `Script/misc`: 账号示例和临时测试文件

## 说明

- 顶层的 `Java` 和根目录 `pom.xml` 仍保持 Maven 聚合结构。
- `Script` 已进一步按用途拆分，但仍保留原有脚本内容与主题边界。
- 各一级目录下已补充 `README.md`，用于说明分类规则。
