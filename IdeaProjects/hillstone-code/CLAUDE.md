# CLAUDE.md

本文件为 Claude Code（claude.ai/code）在此仓库中工作时提供指导。

## 构建与运行

```bash
# 从根目录构建所有 Java 模块
mvn clean compile -pl Java

# 构建指定模块（Java/ 下的任意子模块）
mvn clean package -pl Java/apps/AiIocAnalyzer

# 运行 Spring Boot 应用
mvn spring-boot:run -pl Java/apps/AiIocAnalyzer

# 运行指定模块的测试
mvn test -pl Java/apps/HillstoneTest

# 运行单个测试类
mvn test -pl Java/apps/HillstoneTest -Dtest=SpELTest

# Python：安装依赖
pip install -r Python/requirements.txt

# Bug 诊断客户端（独立依赖，位于 Python/diagnostics/bug/）
cd Python/diagnostics/bug && pip install -r requirements.txt
python bug_app.py          # 桌面模式
python bug_app.py --web    # Web 模式（http://localhost:8551）
docker build -t bugzilla-client . && docker run -p 8551:8551 bugzilla-client

# Slidev 项目
cd Slidev/<project> && npm install && npm run dev
```

- 所有模块使用 Java 8。各模块的 Spring Boot 版本不同（2.7.6 或 2.7.9），各自管理自己的 `spring-boot-dependencies` BOM。
- IDE：IntelliJ IDEA（`.idea/` 已在 gitignore 中）。
- Python 虚拟环境位于 `.venv/`。
- Shell 脚本（`.sh`）通过 `.gitattributes` 强制使用 LF 换行符。

## 项目架构

```
根 pom.xml（SomeCode，聚合父工程）
├── Java/（聚合 pom，包含 10 个模块）
│   ├── apps/
│   │   ├── AiIocAnalyzer    — Spring Boot REST API，AI IOC 分析，两阶段 SSE 流式输出
│   │   ├── Simulator         — Spring Boot 设备模拟器，支持 WebSocket、Avro 数据模拟、多设备并发
│   │   ├── HillstoneTest     — Spring Boot WebFlux + AOP + Redis + Quartz + JDBC/MySQL
│   │   └── PingTest          — Spring Cloud Gateway + jnetpcap
│   ├── data/
│   │   ├── Arango2Nebula     — ArangoDB 到 Nebula Graph 的数据迁移
│   │   ├── HbaseFilter       — HBase 过滤工具
│   │   └── HbaseQuery        — HBase 查询任务，支持基于配置的触发器
│   ├── integration/
│   │   ├── RabbitMq          — RabbitMQ 集成
│   │   └── TencentQuery      — 腾讯平台集成
│   └── labs/
│       └── CompletableFutureTest — Java 并发编程实验
├── Python/
│   ├── automation/           — RD 任务调度、设备推送规则、外网申请自动化
│   ├── diagnostics/          — 缺陷诊断，带 PyQt6 图形界面
│   └── tools/                — ping.py 等小工具
├── Script/
│   ├── api/                  — HTTP 请求文件，用于接口调试
│   ├── db/                   — ArangoDB/Nebula 部署与导入脚本
│   ├── infra/k8s/            — Kubernetes 资源配置
│   ├── ops/                  — DSGP 测试、Arthas、Shell 工具
│   ├── perf/k6/              — k6 压测脚本（ArangoDB 和 Nebula）
│   └── misc/                 — 临时资料和样例
└── Slidev/                   — 独立的 Slidev 演示项目（npm 管理）
```

### AiIocAnalyzer（核心项目）

IOC 分析的两阶段 AI 流水线：
1. **报告生成**（`IocAnalyzeService.generateReport`）—— 将 system/user prompt 发送给 AI API，获取结构化分析报告
2. **流式清洗**（`IocAnalyzeService.streamClean`）—— 将原始报告通过第二次 AI 调用以 SSE 流式输出，同时应用格式清洗规则

Prompt 模板存放在 `src/main/resources/prompts/`。配置通过 `AiProperties`（前缀 `ai`）管理，支持环境变量覆盖（`AI_BASE_URL`、`AI_API_KEY`、`AI_MODEL`）。默认端口 8089。

快速配置示例：
```bash
export AI_BASE_URL=https://api.example.com
export AI_API_KEY=your-key
export AI_MODEL=gpt-4o
```
内置静态前端页面：`src/main/resources/static/index.html`，随应用启动自动提供服务。

### Simulator（设备模拟器）

用于山石安全平台测试的设备模拟器。支持多版本设备注册、心跳模拟、Avro 数据上送、文件数据上送、多设备并发。使用 `IAvroMocker`/`IFileMocker` 接口，实现类通过类路径扫描自动发现。服务模式通过配置中的 `device.connect.info.process_model` 选择。

### 测试模式

- JUnit 5 (Jupiter) + Spring Boot Test
- 主要测试目录：`Java/apps/HillstoneTest/src/test/`、`Java/apps/AiIocAnalyzer/src/test/`

## 新增 Java 模块

1. 在 `Java/` 对应的子目录下创建 Maven 项目
2. 在 `Java/pom.xml` 中添加 `<module>` 条目
3. 遵循现有模式：使用 `spring-boot-starter-parent` 或通过 `dependencyManagement` 导入 BOM

<!-- superpowers-zh:begin (do not edit between these markers) -->
# Superpowers-ZH 中文增强版

本项目已安装 superpowers-zh 技能框架（20 个 skills）。

## 核心规则

1. **收到任务时，先检查是否有匹配的 skill** — 哪怕只有 1% 的可能性也要检查
2. **设计先于编码** — 收到功能需求时，先用 brainstorming skill 做需求分析
3. **测试先于实现** — 写代码前先写测试（TDD）
4. **验证先于完成** — 声称完成前必须运行验证命令

## 可用 Skills

Skills 位于 `.claude/skills/` 目录，每个 skill 有独立的 `SKILL.md` 文件。

- **brainstorming**: 在任何创造性工作之前必须使用此技能——创建功能、构建组件、添加功能或修改行为。在实现之前先探索用户意图、需求和设计。
- **chinese-code-review**: 中文 review 沟通参考——话术模板、分级标注（必须修复/建议修改/仅供参考）、国内团队常见反模式应对。仅在用户显式 /chinese-code-review 时调用，不要根据上下文自动触发。
- **chinese-commit-conventions**: 中文 commit 与 changelog 配置参考——Conventional Commits 中文适配、commitlint/husky/commitizen 中文模板、conventional-changelog 中文配置。仅在用户显式 /chinese-commit-conventions 时调用，不要根据上下文自动触发。
- **chinese-documentation**: 中文文档排版参考——中英文空格、全半角标点、术语保留、链接格式、中文文案排版指北约定。仅在用户显式 /chinese-documentation 时调用，不要根据上下文自动触发。
- **chinese-git-workflow**: 国内 Git 平台配置参考——Gitee、Coding.net、极狐 GitLab、CNB 的 SSH/HTTPS/凭据/CI 接入差异与镜像同步配置。仅在用户显式 /chinese-git-workflow 时调用，不要根据上下文自动触发。
- **dispatching-parallel-agents**: 当面对 2 个以上可以独立进行、无共享状态或顺序依赖的任务时使用
- **executing-plans**: 当你有一份书面实现计划需要在单独的会话中执行，并设有审查检查点时使用
- **finishing-a-development-branch**: 当实现完成、所有测试通过、需要决定如何集成工作时使用——通过提供合并、PR 或清理等结构化选项来引导开发工作的收尾
- **mcp-builder**: MCP 服务器构建方法论 — 系统化构建生产级 MCP 工具，让 AI 助手连接外部能力
- **receiving-code-review**: 收到代码审查反馈后、实施建议之前使用，尤其当反馈不明确或技术上有疑问时——需要技术严谨性和验证，而非敷衍附和或盲目执行
- **requesting-code-review**: 完成任务、实现重要功能或合并前使用，用于验证工作成果是否符合要求
- **subagent-driven-development**: 当在当前会话中执行包含独立任务的实现计划时使用
- **systematic-debugging**: 遇到任何 bug、测试失败或异常行为时使用，在提出修复方案之前执行
- **test-driven-development**: 在实现任何功能或修复 bug 时使用，在编写实现代码之前
- **using-git-worktrees**: 当需要开始与当前工作区隔离的功能开发，或在执行实现计划之前使用——通过原生工具或 git worktree 回退机制确保隔离工作区存在
- **using-superpowers**: 在开始任何对话时使用——确立如何查找和使用技能，要求在任何响应（包括澄清性问题）之前调用 Skill 工具
- **verification-before-completion**: 在宣称工作完成、已修复或测试通过之前使用，在提交或创建 PR 之前——必须运行验证命令并确认输出后才能声称成功；始终用证据支撑断言
- **workflow-runner**: 在 Claude Code / OpenClaw / Cursor 中直接运行 agency-orchestrator YAML 工作流——无需 API key，使用当前会话的 LLM 作为执行引擎。当用户提供 .yaml 工作流文件或要求多角色协作完成任务时触发。
- **writing-plans**: 当你有规格说明或需求用于多步骤任务时使用，在动手写代码之前
- **writing-skills**: 当创建新技能、编辑现有技能或在部署前验证技能是否有效时使用

## 如何使用

当任务匹配某个 skill 时，使用 `Skill` 工具加载对应 skill 并严格遵循其流程。绝不要用 Read 工具读取 SKILL.md 文件。

如果你认为哪怕只有 1% 的可能性某个 skill 适用于你正在做的事情，你必须调用该 skill 检查。
<!-- superpowers-zh:end -->
