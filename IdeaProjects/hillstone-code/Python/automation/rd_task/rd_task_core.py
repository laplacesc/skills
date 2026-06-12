import datetime
from datetime import date
from typing import Dict, Any, Set, Optional

import pandas
import requests
from requests import HTTPError


# ===========================
# 1. 节假日计算模块
# ===========================

class WorkdayCalculator:
    """处理节假日获取与日期计算"""

    def __init__(self):
        self._cached_year: Optional[int] = None
        self._work_days: Set[date] = set()
        self._off_days: Set[date] = set()

    def _load_holidays(self, year: int, timeout=10):
        """懒加载：仅在年份变化时请求网络"""
        if self._cached_year == year:
            return

        # 使用 fastly.jsdelivr 加速
        url = f'https://fastly.jsdelivr.net/gh/NateScarlet/holiday-cn@master/{year}.json'
        try:
            response = requests.get(url, timeout=timeout)
            response.raise_for_status()
            data = response.json()
            df = pandas.DataFrame(data['days'])

            self._work_days.clear()
            self._off_days.clear()

            for _, row in df.iterrows():
                d = date.fromisoformat(row['date'])
                if row['isOffDay']:
                    self._off_days.add(d)
                else:
                    self._work_days.add(d)

            self._cached_year = year
        except HTTPError as e:
            raise RuntimeError(f"获取 {year} 年节假日数据失败，HTTP 错误: {e}")
        except Exception as e:
            raise RuntimeError(f"获取 {year} 年节假日数据失败，请检查网络: {e}")

    def is_workday(self, d: date) -> bool:
        """判断某天是否为工作日"""
        self._load_holidays(d.year)
        if d in self._work_days:
            return True
        if d in self._off_days:
            return False
        # 周一(0)到周五(4)为工作日
        return d.weekday() < 5

    def init_start_date(self, start_date: date) -> date:
        """如果起始日是非工作日，顺延至第一个工作日"""
        self._load_holidays(start_date.year)
        curr = start_date
        while not self.is_workday(curr):
            curr += datetime.timedelta(days=1)
            # 跨年处理：如果跨年了，需加载新一年的数据
            if curr.year != self._cached_year:
                self._load_holidays(curr.year)
        return curr

    def workdays_between(self, start_date: date, end_date: date) -> int:
        """计算两个日期之间（含首尾）的工作日天数"""
        if end_date < start_date:
            return 0
        self._load_holidays(start_date.year)
        count = 0
        curr = start_date
        while curr <= end_date:
            if curr.year != self._cached_year:
                self._load_holidays(curr.year)
            if self.is_workday(curr):
                count += 1
            curr += datetime.timedelta(days=1)
        return count

    def calculate_end_date(self, start_date: date, days: float) -> date:
        """根据工作日天数计算结束日期 (days 支持浮点数)"""
        if days <= 0:
            return start_date

        # 1. 确定实际的起始工作日
        curr = self.init_start_date(start_date)

        # 2. 计算需要消耗的整数工作日
        # 任何大于0的工作量，即使是 0.1 天，也至少会消耗 1 个工作日的时间跨度 (当天)
        # 如果 workdays = 2.5 天, 那么需要 3 个工作日的跨度 (周一、周二、周三)
        # 如果 workdays = 1.0 天, 那么需要 1 个工作日的跨度 (周一)

        # 消耗当天工作日
        remaining_days = days

        # 如果工作量是浮点数，比如 2.5 天，我们将其视为需要跨越 3 个工作日 (周一/周二/周三)
        # 如果工作量是整数，比如 2.0 天，我们将其视为需要跨越 2 个工作日 (周一/周二)
        # WorkdayCalculator.calculate_end_date 的核心逻辑是计算时间跨度
        # WorkdayCalculator 内部逻辑：days>0 且当天是工作日，消耗掉1天

        if self.is_workday(curr):
            remaining_days -= 1.0

        # 如果剩余工作量小于等于0，则当天就是结束日
        if remaining_days <= 0:
            return curr

        # 3. 跨日查找结束日期
        while remaining_days > 0:
            curr += datetime.timedelta(days=1)

            # 跨年检测
            if curr.year != self._cached_year:
                self._load_holidays(curr.year)

            if self.is_workday(curr):
                remaining_days -= 1.0

        return curr


# ===========================
# 2. RD 系统 API 客户端模块
# ===========================

class RDClient:
    """封装与 RD 系统的交互"""
    BASE_URL = "https://rd.hillstonenet.com"

    def __init__(self):
        self.session = requests.Session()
        self.session.trust_env = False
        self.token: Optional[str] = None
        self.uid: Optional[int] = None
        self.username: Optional[str] = None

    def login(self, username: str, password: str) -> str:
        """登录并返回 Token"""
        url = f"{self.BASE_URL}/api/login/account"
        payload = {"userName": username, "password": password}
        try:
            resp = self.session.post(url, json=payload, verify=True, timeout=10)
            resp.raise_for_status()
            data = resp.json()
            token = data.get("specialData", {}).get("data", {}).get("access_token")
            if not token:
                # 尝试解析更详细的错误信息
                msg = data.get("message", "Token获取失败，可能是账号或密码错误。")
                raise ValueError(msg)

            self.token = token
            self.session.headers.update({"x-auth-token": self.token})
            self.username = username
            return self.token
        except HTTPError as e:
            raise RuntimeError(f"登录失败，HTTP 错误: {e}")
        except Exception as e:
            raise RuntimeError(f"登录失败: {e}")

    def fetch_user_uid(self) -> int:
        """获取当前登录用户的 UID"""
        url = f"{self.BASE_URL}/api/user/user_data"
        if not self.username:
            raise RuntimeError("请先登录 (username 为空)。")

        try:
            resp = self.session.post(url, verify=True, timeout=10)
            resp.raise_for_status()
            user_list = resp.json().get("specialData", [])

            # 查找匹配的 UID
            found = next((item["uid"] for item in user_list if item.get("uname") == self.username), None)
            if not found:
                raise ValueError(f"在用户列表中未找到 {self.username}，请检查用户名是否为邮箱前缀。")

            self.uid = found
            return self.uid
        except HTTPError as e:
            raise RuntimeError(f"获取用户信息失败，HTTP 错误: {e}")
        except Exception as e:
            raise RuntimeError(f"获取用户信息失败: {e}")

    def get_my_tasks(self, fr_id: int) -> Dict[str, Dict[str, Any]]:
        """获取指定 FR 下属于当前用户的任务"""
        if not self.uid:
            self.fetch_user_uid()

        # 强制转换 self.uid 为 str，以便与 API 返回的 director 字段比较
        uid_str = str(self.uid)

        url = f"{self.BASE_URL}/api/fr_info/get_task_list?fr_id={fr_id}"
        try:
            resp = self.session.get(url, verify=True, timeout=15)
            resp.raise_for_status()
            raw_data = resp.json().get("specialData", {})
            return self._extract_tasks(raw_data, uid_str)
        except HTTPError as e:
            raise RuntimeError(f"获取任务列表失败，HTTP 错误: {e}")
        except Exception as e:
            raise RuntimeError(f"获取任务列表失败: {e}")

    def _extract_tasks(self, task_data: Dict[str, Any], target_uid_str: str) -> Dict[str, Dict[str, Any]]:
        """递归提取属于当前用户的【叶子任务】（无子任务的任务）"""
        my_tasks = {}

        def traverse(nodes):
            if not nodes:
                return
            for node in nodes:
                children = node.get('children')
                node_id = node.get('id')
                director = str(node.get('director', ''))

                has_children = children and isinstance(children, list) and len(children) > 0

                # 先递归处理子节点
                if has_children:
                    traverse(children)

                # 只有是叶子节点 + 负责人匹配，才加入
                if not has_children and director == target_uid_str:
                    t_id = str(node_id) if node_id is not None else None
                    if not t_id:
                        continue

                    workdays = node.get('estimated_workload', 0)
                    try:
                        workdays = float(workdays)
                    except (TypeError, ValueError):
                        workdays = 0.0

                    my_tasks[t_id] = {
                        "id": t_id,
                        "title": node.get('title', '未知任务'),
                        "workdays": workdays,
                        "start_time": node.get('start_time'),
                        "end_time": node.get('end_time')
                    }

        traverse(task_data.get('data', []))
        return my_tasks

    def update_task_time(self, task_id: str, start_str: str, end_str: str):
        """更新单个任务时间"""
        url = f"{self.BASE_URL}/api/fr_info/edit_task"
        payload = {
            "task_id": task_id,
            "data": {
                "start_time": start_str,
                "end_time": end_str
            }
        }
        try:
            resp = self.session.post(url, json=payload, verify=True, timeout=10)
            resp.raise_for_status()
        except HTTPError as e:
            raise RuntimeError(f"更新任务 {task_id} 时间失败，HTTP 错误: {e}")
        except Exception as e:
            raise RuntimeError(f"更新任务 {task_id} 时间失败: {e}")

    def update_task_worktime(self, task_id: str, workdays: float):
        """更新单个任务工时"""
        url = f"{self.BASE_URL}/api/fr_info/edit_task"
        payload = {
            "task_id": task_id,
            "data": {
                "estimated_workload": workdays
            }
        }
        try:
            resp = self.session.post(url, json=payload, verify=True, timeout=10)
            resp.raise_for_status()
        except HTTPError as e:
            raise RuntimeError(f"更新任务 {task_id} 工时失败，HTTP 错误: {e}")
        except Exception as e:
            raise RuntimeError(f"更新任务 {task_id} 工时失败: {e}")
