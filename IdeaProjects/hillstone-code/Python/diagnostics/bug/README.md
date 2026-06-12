# Bugzilla Client

Bugzilla 缺陷管理客户端，基于 Flet 框架，支持 Web 和桌面两种模式。

## 功能

- 登录 Bugzilla（`https://bug.hillstonenet.com`）
- **My Bugs**：查看当前用户的 Bug 列表，点击行跳转到详情
- **Fetch & Update Bug**：按 ID 获取 Bug 详情并编辑更新
- **Create Bug**：填写表单创建新 Bug
- Dev Owner 选中后自动同步 Assigned To 和 QA Contact
- Product 切换后自动加载对应 Component 列表

## 文件结构

```
bug/
├── bug_core.py      # BugClient 核心逻辑（无 UI 依赖）
├── bug_app.py       # Flet Web 版
├── requirements.txt
└── Dockerfile
```

## 运行

### Flet 版

```bash
pip install -r requirements.txt

# 桌面模式
python bug_app.py

# Web 模式（浏览器访问 http://localhost:8551）
python bug_app.py --web
```

### Docker

```bash
docker build -t bugzilla-client .
docker run -p 8551:8551 bugzilla-client
# 访问 http://localhost:8551
```
