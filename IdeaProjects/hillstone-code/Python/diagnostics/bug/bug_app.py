#!/usr/bin/env python3
"""Bugzilla Client — Flet Web App (v2.0) — Modern Material 3 Design"""

import datetime
import re
import threading
import warnings
from typing import List, Optional

import flet as ft

warnings.filterwarnings("ignore", category=DeprecationWarning, module="flet")

import bug_core

# ─── Design Tokens ───────────────────────────────────────────────────────────
PRIMARY = "#2563EB"
PRIMARY_LIGHT = "#3B82F6"
PRIMARY_SURFACE = "#EFF6FF"
BG_PAGE = "#F8FAFC"
BG_CARD = "#FFFFFF"
BG_SIDEBAR = "#F1F5F9"
BORDER = "#E2E8F0"
BORDER_FOCUS = "#93C5FD"
TEXT_1 = "#0F172A"
TEXT_2 = "#475569"
TEXT_3 = "#94A3B8"
GREEN = "#059669"
GREEN_SURFACE = "#ECFDF5"
AMBER = "#D97706"
RED = "#DC2626"
RED_SURFACE = "#FEF2F2"
RADIUS = 12
RADIUS_LG = 16

STATUS_RESOLUTION_MAP = {
    "ASSIGNED": ["Analyze", "WORKSFORME"],
    "RESOLVED": ["FIXED", "INVALID", "WONTFIX", "LATER", "DUPLICATE", "MOVED"],
    "VERIFIED": ["FIXED", "INVALID", "WONTFIX", "LATER", "DUPLICATE", "MOVED"],
}


# ─── Helpers ─────────────────────────────────────────────────────────────────
def _theme() -> ft.Theme:
    return ft.Theme(
        color_scheme_seed=PRIMARY,
        use_material3=True,
        font_family="Inter, -apple-system, sans-serif",
        text_theme=ft.TextTheme(
            title_medium=ft.TextStyle(size=15, weight=ft.FontWeight.W_600),
            body_medium=ft.TextStyle(size=14, weight=ft.FontWeight.W_400),
            body_small=ft.TextStyle(size=13, weight=ft.FontWeight.W_400),
            label_large=ft.TextStyle(size=14, weight=ft.FontWeight.W_500),
        ),
    )


def _field(label: str, expand=False, width=None, password=False,
           multiline=False, min_lines=1, max_lines=1,
           number_only=False, autofill=None, value="") -> ft.TextField:
    return ft.TextField(
        label=label, dense=True, expand=expand, width=width,
        password=password, can_reveal_password=password,
        multiline=multiline, min_lines=min_lines, max_lines=max_lines,
        border=ft.InputBorder.UNDERLINE,
        content_padding=ft.Padding(0, 8, 0, 8),
        text_style=ft.TextStyle(size=14), label_style=ft.TextStyle(size=12),
        input_filter=ft.NumbersOnlyInputFilter() if number_only else None,
        autofill_hints=autofill, value=value,
    )


def _dropdown(label: str, options: list = None, expand=False, width=None,
              editable=False, enable_filter=False, value=None) -> ft.Dropdown:
    dd = ft.Dropdown(
        label=label, dense=True, expand=expand, width=width,
        border=ft.InputBorder.UNDERLINE,
        content_padding=ft.Padding(0, 8, 0, 8),
        text_style=ft.TextStyle(size=14), label_style=ft.TextStyle(size=12),
        editable=editable, enable_filter=enable_filter,
        options=[ft.DropdownOption(key=o, text=o) for o in (options or [])],
    )
    if value:
        dd.value = value
    return dd


def _section_title(text: str) -> ft.Text:
    return ft.Text(text.upper(), size=11, weight=ft.FontWeight.W_600,
                   color=TEXT_3,
                   style=ft.TextStyle(letter_spacing=0.8))


def _card(content: ft.Control, padding=16) -> ft.Container:
    return ft.Container(
        content=content,
        bgcolor=BG_CARD,
        border_radius=RADIUS,
        padding=padding,
        border=ft.Border(
            top=ft.BorderSide(1, BORDER), right=ft.BorderSide(1, BORDER),
            bottom=ft.BorderSide(1, BORDER), left=ft.BorderSide(1, BORDER),
        ),
        shadow=ft.BoxShadow(
            spread_radius=0, blur_radius=12,
            color=ft.Colors.with_opacity(0.04, "#000000"),
            offset=ft.Offset(0, 2),
        ),
    )


def _extract_email(value: str) -> str:
    if not value:
        return ""
    m = re.search(r"<([^>]+)>", value)
    return m.group(1).strip() if m else value.strip()




class BugApp:
    def __init__(self, page: ft.Page):
        self.page = page
        self.client = bug_core.BugClient()
        self._logged_in = False
        self._dev_owners: List[str] = []
        self._products: List[str] = []
        self._detail: dict = {}
        self._init_controls()

    # ─── Control Creation ────────────────────────────────────────────────────
    def _init_controls(self):
        # Login
        self.tf_user = _field("用户名", expand=True, autofill=ft.AutofillHint.USERNAME)
        self.tf_pass = _field("密码", expand=True, password=True, autofill=ft.AutofillHint.PASSWORD)
        self.btn_login = ft.FilledButton(
            "登录", icon=ft.Icons.LOGIN_ROUNDED, on_click=self._on_login,
            style=ft.ButtonStyle(shape=ft.RoundedRectangleBorder(radius=8),
                                 padding=ft.Padding(16, 8, 16, 8)),
        )
        self.login_badge = ft.Text("未登录", size=12, color=TEXT_3)

        # Tab 0: My Bugs
        self.bugs_table = ft.DataTable(
            columns=[
                ft.DataColumn(ft.Text("ID", size=12, color=TEXT_3, weight=ft.FontWeight.W_500)),
                ft.DataColumn(ft.Text("标题", size=12, color=TEXT_3, weight=ft.FontWeight.W_500)),
                ft.DataColumn(ft.Text("产品", size=12, color=TEXT_3, weight=ft.FontWeight.W_500)),
                ft.DataColumn(ft.Text("状态", size=12, color=TEXT_3, weight=ft.FontWeight.W_500)),
            ],
            heading_row_height=34, data_row_min_height=36, data_row_max_height=36,
            heading_row_color=ft.Colors.with_opacity(0.03, "#0F172A"),
            border_radius=10, horizontal_margin=12, column_spacing=16,
            divider_thickness=0, expand=True,
        )
        self.bugs_empty = ft.Container(
            content=ft.Column([
                ft.Icon(ft.Icons.BUG_REPORT_OUTLINED, size=36, color=BORDER),
                ft.Text("暂无 Bug 数据", size=13, color=TEXT_3),
                ft.Text("登录后点击刷新", size=12, color=TEXT_3),
            ], horizontal_alignment=ft.CrossAxisAlignment.CENTER,
               alignment=ft.MainAxisAlignment.CENTER, spacing=4),
            alignment=ft.Alignment(0, 0),
            expand=True,
        )
        self.btn_refresh = ft.OutlinedButton(
            "刷新", icon=ft.Icons.REFRESH_ROUNDED, on_click=self._on_refresh,
            style=ft.ButtonStyle(shape=ft.RoundedRectangleBorder(radius=8),
                                 padding=ft.Padding(12, 6, 12, 6)),
            disabled=True,
        )

        # Tab 1: Update Bug
        self.tf_bug_id = _field("Bug ID", width=130, number_only=True)
        self.btn_fetch = ft.OutlinedButton(
            "获取", icon=ft.Icons.SEARCH_ROUNDED, on_click=self._on_fetch,
            style=ft.ButtonStyle(shape=ft.RoundedRectangleBorder(radius=8),
                                 padding=ft.Padding(12, 6, 12, 6)),
            disabled=True,
        )
        self.upd_short_desc = _field("Short Description", expand=True)
        self.upd_status = _dropdown("Status", ["ASSIGNED", "RESOLVED", "VERIFIED"], width=170)
        self.upd_status.on_select = self._on_status_changed
        self.upd_resolution = _dropdown("Resolution", STATUS_RESOLUTION_MAP["ASSIGNED"], width=170)
        self.upd_assigned_to = _field("Assigned To", expand=True)
        self.upd_qa_contact = _field("QA Contact", expand=True)
        self.upd_dev_owner = _dropdown("Dev Owner", expand=True, editable=True, enable_filter=True)
        self.upd_dev_owner.on_select = self._on_upd_dev_owner
        self.upd_fix_version = _field("Fix Version", width=150)
        self.upd_product = _dropdown("Product", expand=True)
        self.upd_product.on_select = self._on_upd_product
        self.upd_component = _dropdown("Component", expand=True)
        self.btn_update = ft.FilledButton(
            "提交更新", icon=ft.Icons.SAVE_ROUNDED, on_click=self._on_update,
            style=ft.ButtonStyle(shape=ft.RoundedRectangleBorder(radius=8),
                                 padding=ft.Padding(16, 8, 16, 8)),
            disabled=True,
        )

        # Tab 2: Create Bug
        self.crt_product = _dropdown("Product", expand=True)
        self.crt_product.on_select = self._on_crt_product
        self.crt_component = _dropdown("Component", expand=True)
        self.crt_short_desc = _field("Short Description", expand=True)
        self.crt_cf_class = _dropdown("Class", ["Bug Fix", "Code Sync"], width=150, value="Bug Fix")
        self.crt_priority = _dropdown("Priority", ["P1", "P2", "P3", "P4", "P5"], width=110, value="P5")
        self.crt_branch = _field("Branch", width=150, value="CT_REL")
        self.crt_dev_owner = _dropdown("Dev Owner", expand=True, editable=True, enable_filter=True)
        self.crt_dev_owner.on_select = self._on_crt_dev_owner
        self.crt_assigned_to = _field("Assigned To", expand=True)
        self.crt_qa_contact = _field("QA Contact", expand=True)
        self.crt_comment = _field("Comment", expand=True, multiline=True, min_lines=3, max_lines=6)
        self.btn_create = ft.FilledButton(
            "创建 Bug", icon=ft.Icons.ADD_CIRCLE_ROUNDED, on_click=self._on_create,
            style=ft.ButtonStyle(shape=ft.RoundedRectangleBorder(radius=8),
                                 padding=ft.Padding(16, 8, 16, 8)),
            disabled=True,
        )

        # Log panel
        self.log_view = ft.ListView(spacing=1, expand=True, auto_scroll=True,
                                    padding=ft.Padding(10, 6, 10, 6))
        self.confirm_dlg = ft.AlertDialog(
            modal=True, title=ft.Text("确认更新"),
            content=ft.Text("修改将写入系统，此操作不可撤销。"),
            actions=[
                ft.TextButton("取消", on_click=lambda _: self.page.pop_dialog()),
                ft.FilledButton("确认", on_click=lambda _: self._do_update()),
            ],
            actions_alignment=ft.MainAxisAlignment.END,
        )

    # ─── Layout Build ────────────────────────────────────────────────────────
    def build(self) -> ft.Column:
        toolbar = ft.Container(
            content=ft.Row([
                ft.Icon(ft.Icons.BUG_REPORT_ROUNDED, size=20, color=PRIMARY),
                ft.Text("Bugzilla", size=16, weight=ft.FontWeight.W_700, color=TEXT_1),
                ft.Container(width=16),
                self.tf_user, self.tf_pass, self.btn_login,
                ft.Container(width=8),
                self.login_badge,
            ], spacing=10, vertical_alignment=ft.CrossAxisAlignment.CENTER),
            padding=ft.Padding(20, 10, 20, 10),
            bgcolor=BG_CARD,
            border=ft.Border(bottom=ft.BorderSide(1, BORDER)),
            shadow=ft.BoxShadow(spread_radius=0, blur_radius=4,
                color=ft.Colors.with_opacity(0.04, "#000000"), offset=ft.Offset(0, 1)),
        )

        tab_bar = ft.TabBar(
            tabs=[
                ft.Tab(label="我的 Bug", icon=ft.Icons.LIST_ALT_ROUNDED),
                ft.Tab(label="查询 & 更新", icon=ft.Icons.EDIT_NOTE_ROUNDED),
                ft.Tab(label="新建 Bug", icon=ft.Icons.ADD_CIRCLE_OUTLINE_ROUNDED),
            ],
            scrollable=False,
        )
        tab_views = ft.TabBarView(
            controls=[self._build_tab_bugs(), self._build_tab_update(), self._build_tab_create()],
            expand=True,
        )
        tabs = ft.Tabs(
            content=ft.Column([tab_bar, tab_views], expand=True, spacing=0),
            length=3, selected_index=0, expand=True,
            on_change=self._on_tab_change,
        )
        self._tabs = tabs

        main_panel = ft.Container(content=tabs, expand=3, bgcolor=BG_PAGE)
        log_panel = self._build_log_panel()
        body = ft.Row([main_panel, log_panel], spacing=0, expand=True)
        return ft.Column([toolbar, body], expand=True, spacing=0)

    def _build_tab_bugs(self) -> ft.Container:
        bugs_scroll = ft.Column(
            [self.bugs_table], scroll=ft.ScrollMode.AUTO, expand=True,
            horizontal_alignment=ft.CrossAxisAlignment.STRETCH,
        )
        self._bugs_scroll = bugs_scroll
        list_area = ft.Container(
            content=ft.Stack([self.bugs_empty, bugs_scroll], expand=True),
            expand=True,
            bgcolor=BG_CARD,
            border_radius=RADIUS,
            border=ft.Border(
                top=ft.BorderSide(1, BORDER), right=ft.BorderSide(1, BORDER),
                bottom=ft.BorderSide(1, BORDER), left=ft.BorderSide(1, BORDER),
            ),
            shadow=ft.BoxShadow(
                spread_radius=0, blur_radius=8,
                color=ft.Colors.with_opacity(0.03, "#000000"),
                offset=ft.Offset(0, 1),
            ),
        )
        return ft.Container(
            content=ft.Column([
                ft.Row([
                    _section_title("我的 Bug"),
                    ft.Container(expand=True),
                    self.btn_refresh,
                ], vertical_alignment=ft.CrossAxisAlignment.CENTER),
                list_area,
            ], spacing=12, expand=True),
            padding=ft.Padding(20, 16, 20, 16), expand=True,
        )

    def _build_tab_update(self) -> ft.Container:
        return ft.Container(
            content=ft.Column([
                _section_title("查询 & 更新 Bug"),
                _card(ft.Column([
                    ft.Row([self.tf_bug_id, self.btn_fetch], spacing=12,
                           vertical_alignment=ft.CrossAxisAlignment.END),
                ], spacing=8)),
                _card(ft.Column([
                    ft.Text("基本信息", size=13, weight=ft.FontWeight.W_500, color=TEXT_2),
                    self.upd_short_desc,
                    ft.Row([self.upd_status, self.upd_resolution, self.upd_fix_version], spacing=12),
                ], spacing=10)),
                _card(ft.Column([
                    ft.Text("人员分配", size=13, weight=ft.FontWeight.W_500, color=TEXT_2),
                    ft.Row([self.upd_dev_owner], spacing=12),
                    ft.Row([self.upd_assigned_to, self.upd_qa_contact], spacing=12),
                ], spacing=10)),
                _card(ft.Column([
                    ft.Text("产品归属", size=13, weight=ft.FontWeight.W_500, color=TEXT_2),
                    ft.Row([self.upd_product, self.upd_component], spacing=12),
                ], spacing=10)),
                ft.Row([ft.Container(expand=True), self.btn_update]),
            ], spacing=12, scroll=ft.ScrollMode.AUTO, expand=True),
            padding=ft.Padding(20, 16, 20, 16), expand=True,
        )

    def _build_tab_create(self) -> ft.Container:
        return ft.Container(
            content=ft.Column([
                _section_title("新建 Bug"),
                _card(ft.Column([
                    ft.Text("产品 & 描述", size=13, weight=ft.FontWeight.W_500, color=TEXT_2),
                    ft.Row([self.crt_product, self.crt_component], spacing=12),
                    self.crt_short_desc,
                ], spacing=10)),
                _card(ft.Column([
                    ft.Text("分类 & 优先级", size=13, weight=ft.FontWeight.W_500, color=TEXT_2),
                    ft.Row([self.crt_cf_class, self.crt_priority, self.crt_branch], spacing=12),
                ], spacing=10)),
                _card(ft.Column([
                    ft.Text("人员分配", size=13, weight=ft.FontWeight.W_500, color=TEXT_2),
                    ft.Row([self.crt_dev_owner], spacing=12),
                    ft.Row([self.crt_assigned_to, self.crt_qa_contact], spacing=12),
                ], spacing=10)),
                _card(ft.Column([
                    ft.Text("备注", size=13, weight=ft.FontWeight.W_500, color=TEXT_2),
                    self.crt_comment,
                ], spacing=10)),
                ft.Row([ft.Container(expand=True), self.btn_create]),
            ], spacing=12, scroll=ft.ScrollMode.AUTO, expand=True),
            padding=ft.Padding(20, 16, 20, 16), expand=True,
        )

    def _build_log_panel(self) -> ft.Container:
        return ft.Container(
            content=ft.Column([
                ft.Row([
                    ft.Icon(ft.Icons.TERMINAL_ROUNDED, size=14, color=TEXT_3),
                    ft.Text("日志", size=12, weight=ft.FontWeight.W_500, color=TEXT_2),
                    ft.Container(expand=True),
                    ft.TextButton(
                        "清空", on_click=lambda _: self._clear_log(),
                        style=ft.ButtonStyle(color=TEXT_3, padding=ft.Padding(8, 4, 8, 4)),
                    ),
                ], spacing=6, vertical_alignment=ft.CrossAxisAlignment.CENTER),
                ft.Container(
                    content=self.log_view, expand=True, border_radius=8,
                    bgcolor="#F8F9FA",
                    border=ft.Border(
                        top=ft.BorderSide(1, BORDER), right=ft.BorderSide(1, BORDER),
                        bottom=ft.BorderSide(1, BORDER), left=ft.BorderSide(1, BORDER),
                    ),
                ),
            ], spacing=6, expand=True),
            expand=1,
            padding=ft.Padding(12, 14, 16, 14),
            bgcolor=BG_PAGE,
            border=ft.Border(left=ft.BorderSide(1, BORDER)),
        )

    # ─── Tab Guard ───────────────────────────────────────────────────────────
    def _on_tab_change(self, e):
        if not self._logged_in and self._tabs.selected_index != 0:
            self._tabs.selected_index = 0
            self.page.update()
            self._snack("请先登录")

    # ─── Login ───────────────────────────────────────────────────────────────
    def _on_login(self, e):
        u = (self.tf_user.value or "").strip()
        p = (self.tf_pass.value or "").strip()
        if not u or not p:
            self._snack("请填写用户名和密码")
            return
        self.btn_login.disabled = True
        self.btn_login.text = "登录中..."
        self.page.update()
        self._log(f"登录 ({u})...")
        threading.Thread(target=self._do_login, args=(u, p), daemon=True).start()

    def _do_login(self, user, pwd):
        try:
            self.client.login(user, pwd)
            self._logged_in = True
            self.page.run_task(self._after_login, user)
        except Exception as ex:
            self._log(f"登录失败: {ex}")
            self.page.run_task(self._reset_login_btn)

    async def _after_login(self, user):
        self.login_badge.value = f"{user}"
        self.login_badge.color = GREEN
        self.btn_login.disabled = False
        self.btn_login.text = "重新登录"
        self.btn_refresh.disabled = False
        self.btn_fetch.disabled = False
        self.btn_update.disabled = False
        self.btn_create.disabled = False
        self._tabs.selected_index = 0
        self.page.update()
        self._log("登录成功，加载选项...")
        threading.Thread(target=self._load_options, daemon=True).start()
        threading.Thread(target=self._do_refresh, daemon=True).start()

    async def _reset_login_btn(self):
        self.btn_login.disabled = False
        self.btn_login.text = "登录"
        self.page.update()

    # ─── Options Loading ─────────────────────────────────────────────────────
    def _load_options(self):
        try:
            self._dev_owners = self.client.get_cf_dev_owner_values()
            self._products = self.client.get_product_values()
            self.page.run_task(self._populate_options)
        except Exception as ex:
            self._log(f"加载选项失败: {ex}")

    async def _populate_options(self):
        self._set_dropdown_opts(self.upd_dev_owner, self._dev_owners)
        self._set_dropdown_opts(self.crt_dev_owner, self._dev_owners)
        self._set_dropdown_opts(self.upd_product, self._products)
        self._set_dropdown_opts(self.crt_product, self._products)
        if self._products:
            threading.Thread(target=self._load_components,
                             args=(self._products[0], self.crt_component, None), daemon=True).start()
        inferred = self._infer_dev_owner()
        if inferred:
            self.crt_dev_owner.value = inferred
            email = _extract_email(inferred)
            self.crt_assigned_to.value = email
            self.crt_qa_contact.value = email
        self.page.update()
        self._log("选项加载完成")

    # ─── My Bugs ─────────────────────────────────────────────────────────────
    def _on_refresh(self, e):
        if not self._logged_in:
            self._snack("请先登录")
            return
        self.btn_refresh.disabled = True
        self.page.update()
        self._log("刷新列表...")
        threading.Thread(target=self._do_refresh, daemon=True).start()

    def _do_refresh(self):
        try:
            bugs = self.client.get_my_bugs()
            self.page.run_task(self._show_bugs, bugs)
        except Exception as ex:
            self._log(f"获取列表失败: {ex}")
            self.page.run_task(self._refresh_done)

    async def _show_bugs(self, bugs):
        self.bugs_table.rows.clear()
        if not bugs:
            self.bugs_empty.visible = True
            self._bugs_scroll.visible = False
        else:
            self.bugs_empty.visible = False
            self._bugs_scroll.visible = True
            for i, bug in enumerate(bugs):
                bid = str(bug.get("id", ""))
                self.bugs_table.rows.append(ft.DataRow(
                    cells=[
                        ft.DataCell(ft.Text(bid, size=13, color=PRIMARY, weight=ft.FontWeight.W_500)),
                        ft.DataCell(ft.Text(str(bug.get("title", "")), size=13, color=TEXT_1,
                                            max_lines=1, overflow=ft.TextOverflow.ELLIPSIS)),
                        ft.DataCell(ft.Text(str(bug.get("product", "")), size=12, color=TEXT_2)),
                        ft.DataCell(ft.Text(str(bug.get("status", "")), size=12, color=TEXT_2)),
                    ],
                    color=BG_SIDEBAR if i % 2 == 1 else None,
                    on_select_change=self._make_row_click(bid),
                ))
        self.btn_refresh.disabled = False
        self.page.update()
        self._log(f"已加载 {len(bugs)} 个 Bug")

    async def _refresh_done(self):
        self.btn_refresh.disabled = False
        self.page.update()

    def _make_row_click(self, bug_id: str):
        def handler(e):
            self.tf_bug_id.value = bug_id
            self._tabs.selected_index = 1
            self.page.update()
            self._on_fetch(None)
        return handler

    # ─── Fetch & Update ──────────────────────────────────────────────────────
    def _on_fetch(self, e):
        bid = (self.tf_bug_id.value or "").strip()
        if not bid.isdigit():
            self._snack("请输入有效的 Bug ID")
            return
        if not self._logged_in:
            self._snack("请先登录")
            return
        self.btn_fetch.disabled = True
        self.page.update()
        self._log(f"获取 Bug {bid}...")
        threading.Thread(target=self._do_fetch, args=(int(bid),), daemon=True).start()

    def _do_fetch(self, bug_id: int):
        try:
            detail = self.client.get_bug_detail(bug_id)
            self.page.run_task(self._fill_detail, detail)
        except Exception as ex:
            self._log(f"获取失败: {ex}")
            self.page.run_task(self._fetch_done)

    async def _fill_detail(self, detail: dict):
        self._detail = detail
        self.upd_short_desc.value = detail.get("short_desc", "")
        status = detail.get("bug_status", "ASSIGNED")
        self.upd_status.value = status if status in STATUS_RESOLUTION_MAP else "ASSIGNED"
        self._refresh_resolutions(self.upd_status.value, detail.get("resolution"))
        self.upd_assigned_to.value = detail.get("assigned_to", "")
        self.upd_qa_contact.value = detail.get("qa_contact", "")
        self._select_dd(self.upd_dev_owner, detail.get("cf_dev_owner", ""), editable=True)
        self.upd_fix_version.value = detail.get("cf_fix_version", "")
        product = detail.get("product", "")
        self._select_dd(self.upd_product, product)
        if product:
            threading.Thread(target=self._load_components,
                             args=(product, self.upd_component, detail.get("component", "")),
                             daemon=True).start()
        self.btn_fetch.disabled = False
        self.page.update()
        self._log(f"Bug {detail.get('bug_id', '')} 已加载")

    async def _fetch_done(self):
        self.btn_fetch.disabled = False
        self.page.update()

    def _on_update(self, e):
        bid = (self.tf_bug_id.value or "").strip()
        if not bid.isdigit():
            self._snack("请先获取 Bug 详情")
            return
        self.confirm_dlg.content = ft.Text(f"将 Bug {bid} 的修改写入系统，不可撤销。")
        self.page.show_dialog(self.confirm_dlg)

    def _do_update(self):
        self.page.pop_dialog()
        bid = (self.tf_bug_id.value or "").strip()
        if not bid.isdigit():
            return
        self.btn_update.disabled = True
        self.btn_update.text = "更新中..."
        self.page.update()
        self._log(f"更新 Bug {bid}...")
        extra = {k: v for k, v in self._detail.items()
                 if k not in {"bug_id", "short_desc", "bug_status", "resolution",
                              "assigned_to", "qa_contact", "cf_dev_owner",
                              "cf_fix_version", "product", "component"}}
        dev_owner = _extract_email(self.upd_dev_owner.value or self.upd_dev_owner.text or "")
        threading.Thread(target=self._run_update, args=(int(bid), dev_owner, extra), daemon=True).start()

    def _run_update(self, bug_id, dev_owner, extra):
        try:
            self.client.update_bug(
                bug_id=bug_id,
                status=self.upd_status.value,
                resolution=self.upd_resolution.value,
                short_desc=self.upd_short_desc.value,
                assigned_to=self.upd_assigned_to.value,
                qa_contact=self.upd_qa_contact.value,
                cf_dev_owner=dev_owner,
                cf_fix_version=self.upd_fix_version.value,
                product=self.upd_product.value or None,
                component=self.upd_component.value or None,
                **extra,
            )
            self._log(f"Bug {bug_id} 更新成功")
            self.page.run_task(self._update_done, True)
        except Exception as ex:
            self._log(f"更新失败: {ex}")
            self.page.run_task(self._update_done, False)

    async def _update_done(self, success: bool):
        self.btn_update.disabled = False
        self.btn_update.text = "提交更新"
        self.page.update()
        if success:
            self._snack("更新成功")

    # ─── Create Bug ──────────────────────────────────────────────────────────
    def _on_create(self, e):
        if not self._logged_in:
            self._snack("请先登录")
            return
        product = (self.crt_product.value or "").strip()
        short_desc = (self.crt_short_desc.value or "").strip()
        component = (self.crt_component.value or "").strip()
        if not all([product, short_desc, component]):
            self._snack("Product、Description、Component 为必填项")
            return
        self.btn_create.disabled = True
        self.btn_create.text = "创建中..."
        self.page.update()
        self._log("创建 Bug...")
        dev_owner = _extract_email(self.crt_dev_owner.value or self.crt_dev_owner.text or "")
        payload = {
            "product": product, "short_desc": short_desc, "component": component,
            "cf_class": self.crt_cf_class.value or "Bug Fix",
            "priority": self.crt_priority.value or "P5",
            "cf_branch_name": self.crt_branch.value or "CT_REL",
            "assigned_to": self.crt_assigned_to.value or "",
            "cf_dev_owner": dev_owner,
            "qa_contact": self.crt_qa_contact.value or "",
            "comment": self.crt_comment.value or "",
        }
        threading.Thread(target=self._run_create, args=(payload,), daemon=True).start()

    def _run_create(self, payload: dict):
        try:
            new_id = self.client.create_bug(**payload)
            self._log(f"创建成功，Bug ID: {new_id}")
            self.page.run_task(self._create_done, new_id)
        except Exception as ex:
            self._log(f"创建失败: {ex}")
            self.page.run_task(self._create_reset)

    async def _create_done(self, new_id):
        self.btn_create.disabled = False
        self.btn_create.text = "创建 Bug"
        self._snack(f"创建成功！Bug ID: {new_id}")
        self.page.update()

    async def _create_reset(self):
        self.btn_create.disabled = False
        self.btn_create.text = "创建 Bug"
        self.page.update()

    # ─── Field Linking Logic ─────────────────────────────────────────────────
    def _on_status_changed(self, e):
        self._refresh_resolutions(e.control.value, None)

    def _refresh_resolutions(self, status: str, current: Optional[str]):
        opts = STATUS_RESOLUTION_MAP.get(status, ["---"])
        self.upd_resolution.options = [ft.DropdownOption(key=r, text=r) for r in opts]
        if current and current in opts:
            self.upd_resolution.value = current
        elif current:
            match = next((o for o in opts if o.lower() == current.lower()), None)
            self.upd_resolution.value = match or opts[0]
        else:
            self.upd_resolution.value = opts[0]
        self.page.update()

    def _on_upd_dev_owner(self, e):
        email = _extract_email(e.control.value or "")
        if email and email != "---":
            self.upd_assigned_to.value = email
            self.upd_qa_contact.value = email
            self.page.update()

    def _on_crt_dev_owner(self, e):
        email = _extract_email(e.control.value or "")
        if email and email != "---":
            self.crt_assigned_to.value = email
            self.crt_qa_contact.value = email
            self.page.update()

    def _on_upd_product(self, e):
        product = e.control.value or ""
        if product:
            threading.Thread(target=self._load_components,
                             args=(product, self.upd_component, None), daemon=True).start()

    def _on_crt_product(self, e):
        product = e.control.value or ""
        if product:
            threading.Thread(target=self._load_components,
                             args=(product, self.crt_component, None), daemon=True).start()

    def _load_components(self, product: str, dropdown: ft.Dropdown, select: Optional[str]):
        try:
            values = self.client.get_component_values(product)
            self.page.run_task(self._set_components, dropdown, values, select)
        except Exception as ex:
            self._log(f"加载 Component 失败: {ex}")

    async def _set_components(self, dd: ft.Dropdown, values: List[str], select: Optional[str]):
        self._set_dropdown_opts(dd, values)
        if select:
            self._select_dd(dd, select)
        self.page.update()

    # ─── Utilities ───────────────────────────────────────────────────────────
    def _set_dropdown_opts(self, dd: ft.Dropdown, values: List[str]):
        dd.options = [ft.DropdownOption(key=v, text=v) for v in values]
        if values:
            dd.value = values[0]

    def _select_dd(self, dd: ft.Dropdown, value: str, editable=False):
        if not value:
            return
        if not dd.options:
            if editable:
                dd.text = value
            return
        for opt in dd.options:
            if opt.key == value:
                dd.value = value
                return
        target_email = _extract_email(value)
        for opt in dd.options:
            if _extract_email(opt.key) == target_email:
                dd.value = opt.key
                return
        if editable:
            dd.text = value

    def _infer_dev_owner(self) -> Optional[str]:
        username = getattr(self.client, "current_user", None)
        if not username:
            return None
        prefix = username.strip().lower().split("@")[0]
        for v in self._dev_owners:
            if prefix and prefix in v.lower():
                return v
        return None

    def _log(self, msg: str):
        now = datetime.datetime.now().strftime("%H:%M:%S")
        self.log_view.controls.append(
            ft.Text(f"{now}  {msg}", size=12, font_family="Menlo, monospace", color=TEXT_2)
        )
        self.page.update()

    def _clear_log(self):
        self.log_view.controls.clear()
        self.page.update()

    def _snack(self, msg: str):
        self.page.show_dialog(ft.SnackBar(content=ft.Text(msg)))


# ─── Entry Point ─────────────────────────────────────────────────────────────
def main(page: ft.Page):
    page.title = "Bugzilla Client"
    page.theme_mode = ft.ThemeMode.LIGHT
    page.theme = _theme()
    page.bgcolor = BG_PAGE
    page.window.width = 1360
    page.window.height = 820
    page.window.min_width = 900
    page.window.min_height = 600
    page.padding = 0
    page.add(BugApp(page).build())


if __name__ == "__main__":
    import sys
    if "--web" in sys.argv:
        ft.run(main, view=ft.AppView.WEB_BROWSER, port=8551, host="0.0.0.0")
    else:
        ft.run(main)
