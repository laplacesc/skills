import json
import re
from typing import Any, Optional

import requests
from bs4 import BeautifulSoup


class BugClient:
    """Bugzilla HTTP client — handles auth, CRUD, and option fetching."""

    BASE_URL = "https://bug.hillstonenet.com"

    def __init__(self):
        self.session = requests.Session()
        self.session.trust_env = False
        self.token: Optional[str] = None
        self.current_user: Optional[str] = None
        self._option_cache: dict[str, Any] = {}

    def login(self, username: str, password: str):
        """登录并自动获取页面 token"""
        url = f"{self.BASE_URL}/index.cgi"
        payload = {
            "Bugzilla_login": username,
            "Bugzilla_password": password,
            "Bugzilla_remember": "on",
            "GoAheadAndLogIn": "登录"
        }
        try:
            resp = self.session.post(url, data=payload, verify=True, timeout=10)
            resp.raise_for_status()
            self.token = self.session.cookies.get_dict().get("Bugzilla_logincookie")
            if not self.token:
                raise RuntimeError("登录失败：用户名或密码错误。")
            self.current_user = username
        except Exception as e:
            raise RuntimeError(f"登录失败: {e}")

    def get_bug_detail(self, bug_id: int) -> dict:
        """根据 BUGID 获取详细信息"""
        url = f"{self.BASE_URL}/jquery.cgi"
        params = {"id": bug_id}
        try:
            resp = self.session.get(url, params=params, verify=True, timeout=10)
            resp.raise_for_status()

            soup = BeautifulSoup(resp.text, 'html.parser')
            detail = {}
            bug_id_suffix = f"_{bug_id}"

            for inp in soup.find_all('input'):
                name = inp.get('name')
                if name:
                    detail[name] = inp.get('value', '')

            for sel in soup.find_all('select'):
                name = sel.get('name')
                if name:
                    selected_opt = sel.find('option', selected=True)
                    if not selected_opt:
                        selected_opt = sel.find('option')
                    detail[name] = selected_opt.get('value', '') if selected_opt else ''

            for txt in soup.find_all('textarea'):
                name = txt.get('name')
                if name:
                    detail[name] = txt.get_text() if txt.get_text() else ''

            normalized = {}
            for key, value in detail.items():
                if key.endswith(bug_id_suffix):
                    base_key = key[: -len(bug_id_suffix)]
                    if base_key and (base_key not in detail or detail.get(base_key) in (None, "")):
                        normalized[base_key] = value
            if normalized:
                detail.update(normalized)

            return detail
        except Exception as e:
            raise RuntimeError(f"获取 Bug {bug_id} 详情失败: {e}")

    def _get_create_bug_token(self, product_name: str) -> str:
        token_url = f"{self.BASE_URL}/create_bug_scope.cgi"
        params = {'product': product_name}
        try:
            resp = self.session.get(token_url, params=params, verify=True, timeout=10)
            resp.raise_for_status()
            soup = BeautifulSoup(resp.text, 'html.parser')
            token_element = soup.find('input', {'name': 'token'})
            if not token_element:
                raise RuntimeError("在页面上找不到 'token' 字段。")
            return token_element['value']
        except Exception as e:
            raise RuntimeError(f"获取创建 Bug 的 Token 失败: {e}")

    def create_bug(self, **kwargs):
        """创建一个新的 Bug"""
        create_url = f"{self.BASE_URL}/post_bug_scope.cgi"
        product = kwargs.get("product")
        if not product:
            raise ValueError("创建 Bug 需要 'product' 字段。")

        token = self._get_create_bug_token(product)

        payload = {
            "token": token,
            "click_maketemplate": "0",
            "cf_class": "Bug Fix",
            "bug_status": "项目测试负责人审批",
            "cf_know_version": "---",
            "bug_severity": "enhancement",
            "rep_platform": "All",
            "cf_regression": "非衰退",
            "cf_teststage": "Mainline GoldenWeek",
            "cf_replication_rate": "---",
            "cf_fr_id": "",
            "cf_ce_owner": "---",
            "keywords": "",
            "version": "TIP 1.9.12",
            "cf_dailybuild_version": "---",
            "cf_planned_version": "TIP 1.9.12",
            "default_qacontact": "xldong@Hillstonenet.com",
            "set_qacontact": "reporter",
            "cc": "",
        }
        payload.update(kwargs)

        if 'comment' in payload:
            payload['comment'] = f"<p>{payload['comment']}</p>"

        try:
            resp = self.session.post(create_url, data=payload, verify=True, timeout=15)
            resp.raise_for_status()

            soup = BeautifulSoup(resp.text, 'html.parser')
            title_tag = soup.find('title')
            if title_tag and title_tag.string:
                match = re.search(r'Bug\s+(\d+)', title_tag.string)
                if match:
                    return match.group(1)

            raise RuntimeError(f"创建 Bug 失败，未知的响应: {resp.status_code}")
        except Exception as e:
            raise RuntimeError(f"创建 Bug 时发生网络或解析错误: {e}")

    def update_bug(self, bug_id: int, status: str = None, resolution: str = None, short_desc: str = None,
                   assigned_to: str = None, qa_contact: str = None, cf_dev_owner: str = None,
                   cf_fix_version: str = None, product: str = None, component: str = None, **kwargs):
        """更新 Bug 信息 (JSON 格式)"""
        url = f"{self.BASE_URL}/process_scope.cgi"

        current_detail = kwargs if kwargs else self.get_bug_detail(bug_id)

        bugs_payload = [{
            "bug_id": int(bug_id),
            "bug_status_reason": current_detail.get("bug_status_reason", "ANALYZE"),
            "bug_status": status if status is not None else current_detail.get("bug_status"),
            "resolution": resolution if resolution is not None else current_detail.get("resolution"),
            "assigned_to": assigned_to if assigned_to is not None else current_detail.get("assigned_to"),
            "qa_contact": qa_contact if qa_contact is not None else current_detail.get("qa_contact"),
            "cf_dev_owner": cf_dev_owner if cf_dev_owner is not None else current_detail.get("cf_dev_owner"),
            "cf_fix_version": cf_fix_version if cf_fix_version is not None else current_detail.get("cf_fix_version"),
            "cf_introducing_version": current_detail.get("cf_introducing_version"),
            "laterversion": current_detail.get("laterversion", "")
        }]

        summary_payload = [{
            "short_desc": short_desc if short_desc is not None else current_detail.get("short_desc", ""),
            "product": product if product is not None else current_detail.get("product", ""),
            "keywords": current_detail.get("keywords", []),
            "component": component if component is not None else current_detail.get("component", ""),
            "cf_cvbc_breaking_change": current_detail.get("cf_cvbc_breaking_change", "N"),
            "cf_cvbc_classification": current_detail.get("cf_cvbc_classification", "命令行变更"),
            "cf_customer_visible_change": current_detail.get("cf_customer_visible_change", "N"),
            "cf_dailybuild_version": current_detail.get("cf_dailybuild_version", "---"),
            "cf_project_requirement": current_detail.get("cf_project_requirement", "---"),
            "bug_id": str(bug_id)
        }]

        payload = {
            "type": "commit",
            "bug_id": int(bug_id),
            "cc": current_detail.get("cc", ""),
            "bugs": json.dumps(bugs_payload),
            "bug_summary_detail": json.dumps(summary_payload),
            "cvbc_data": "[]"
        }

        try:
            resp = self.session.post(url, json=payload, verify=True, timeout=10)
            resp.raise_for_status()
        except Exception as e:
            raise RuntimeError(f"更新 Bug 失败: {e}")

    def get_cf_dev_owner_values(self) -> list[str]:
        """获取 cf_dev_owner 下拉候选项列表"""
        cached = self._option_cache.get("cf_dev_owner")
        if isinstance(cached, list):
            return cached
        url = f"{self.BASE_URL}/process_scope.cgi"
        params = {"field": "cf_dev_owner"}
        try:
            resp = self.session.get(url, params=params, verify=True, timeout=10)
            resp.raise_for_status()
            data = resp.json()
            values = data.get("legal_values", [])
            if not isinstance(values, list):
                raise RuntimeError("返回的 legal_values 不是列表。")
            self._option_cache["cf_dev_owner"] = values
            return values
        except Exception as e:
            raise RuntimeError(f"获取 cf_dev_owner 候选项失败: {e}")

    def get_product_values(self, sample_bug_id: int = 580130) -> list[str]:
        """从 Bug 详情页面解析 product 下拉候选项列表"""
        cached = self._option_cache.get("product_values")
        if isinstance(cached, list):
            return cached
        url = f"{self.BASE_URL}/jquery.cgi"
        params = {"id": sample_bug_id}
        try:
            resp = self.session.get(url, params=params, verify=True, timeout=10)
            resp.raise_for_status()
            soup = BeautifulSoup(resp.text, 'html.parser')
            product_select = soup.find('select', {'name': 'product'}) or soup.find('select', {'id': 'product'})
            if not product_select:
                raise RuntimeError("在页面上找不到 product 下拉框。")
            values: list[str] = []
            for opt in product_select.find_all('option'):
                value = opt.get('value') or opt.get_text(strip=True)
                if value and value not in values:
                    values.append(value)
            if not values:
                raise RuntimeError("product 下拉框没有有效选项。")
            self._option_cache["product_values"] = values
            return values
        except Exception as e:
            raise RuntimeError(f"获取 product 候选项失败: {e}")

    def get_component_values(self, product_name: str) -> list[str]:
        """根据 product 获取 component 下拉候选项列表"""
        if not product_name:
            raise ValueError("获取 component 候选项需要 product。")
        cache_key = f"component_values:{product_name}"
        cached = self._option_cache.get(cache_key)
        if isinstance(cached, list):
            return cached
        url = f"{self.BASE_URL}/process.cgi"
        data = {
            "product": product_name,
            "type": "get_product_component"
        }
        try:
            resp = self.session.post(url, data=data, verify=True, timeout=10)
            resp.raise_for_status()
            payload = resp.json()
            rows = payload.get("rows", [])
            if not isinstance(rows, list):
                raise RuntimeError("返回的 rows 不是列表。")
            values: list[str] = []
            for row in rows:
                name = row.get("name")
                if name and name not in values:
                    values.append(name)
            if not values:
                raise RuntimeError("component 列表为空。")
            self._option_cache[cache_key] = values
            return values
        except Exception as e:
            raise RuntimeError(f"获取 component 候选项失败: {e}")

    def get_my_bugs(self) -> list[dict]:
        """获取 'My Bugs' 列表"""
        url = f"{self.BASE_URL}/process.cgi"
        try:
            resp = self.session.post(url, data={"type": "my_bugs", "value": ""}, verify=True, timeout=10)
            resp.raise_for_status()
            response_json = resp.json()
            html_content = response_json.get("content", "")
            return self._parse_my_bugs_html(html_content) if html_content else []
        except Exception as e:
            raise RuntimeError(f"获取Bugs列表失败: {e}")

    def _parse_my_bugs_html(self, html: str) -> list[dict]:
        """解析 Bug 列表表格"""
        bugs = []
        soup = BeautifulSoup(html, 'html.parser')
        table = soup.find('table', id='right_div_table')
        if not table or not table.find('tbody'):
            return []

        rows = table.find('tbody').find_all('tr')
        for row in rows:
            cells = row.find_all('td')
            if len(cells) < 7:
                continue
            link = cells[0].find('a')
            if not link:
                continue

            bug_id_match = re.search(r'id=(\d+)', link.get('href', ''))
            bug_id = bug_id_match.group(1) if bug_id_match else link.get_text(strip=True)

            bugs.append({
                'id': bug_id,
                'title': cells[0].get_text(strip=True).replace(bug_id, "", 1).lstrip('-').strip(),
                'product': cells[1].get_text(strip=True),
                'status': cells[4].get_text(strip=True),
            })
        return bugs


if __name__ == '__main__':
    print("This module provides BugClient. Run bug_app.py (Flet) or bug_ui.py (PyQt6) to start the app.")
