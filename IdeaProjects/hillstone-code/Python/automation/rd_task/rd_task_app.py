#!/usr/bin/env python3
"""
RD 任务排期助手 - Flet Web 应用 (v4.1)
基于 Flet 0.85+，极简高密度风格
"""

import datetime
import re
import threading
import warnings
from datetime import date
from typing import Dict, Any, List, Optional

import flet as ft

warnings.filterwarnings("ignore", category=DeprecationWarning, module="flet")

import rd_task_core

DATE_PATTERN = re.compile(r"^\d{4}-\d{2}-\d{2}$")

# ============================================================
# Design Tokens — Notion/Linear inspired
# ============================================================
PRIMARY = "#2563EB"
BG_PAGE = "#F8FAFC"
BG_CARD = "#FFFFFF"
BG_ROW_ALT = "#F1F5F9"
BORDER = "#E2E8F0"
TEXT_1 = "#0F172A"
TEXT_2 = "#475569"
TEXT_3 = "#94A3B8"
GREEN = "#059669"
ERROR = "#DC2626"
AMBER = "#D97706"
PURPLE = "#7C3AED"
RADIUS = 12
SHADOW = ft.BoxShadow(spread_radius=0, blur_radius=16,
    color=ft.Colors.with_opacity(0.04, "#000000"), offset=ft.Offset(0, 2))


def _build_theme() -> ft.Theme:
    return ft.Theme(
        color_scheme_seed=PRIMARY, use_material3=True,
        font_family="Inter, -apple-system, sans-serif",
        text_theme=ft.TextTheme(
            title_medium=ft.TextStyle(size=16, weight=ft.FontWeight.W_600),
            body_medium=ft.TextStyle(size=14, weight=ft.FontWeight.W_400),
            body_small=ft.TextStyle(size=13, weight=ft.FontWeight.W_400),
            label_large=ft.TextStyle(size=14, weight=ft.FontWeight.W_500),
        ),
    )




class RDTaskApp:
    def __init__(self, page: ft.Page):
        self.page = page
        self.rd_client = rd_task_core.RDClient()
        self.current_tasks: Dict[str, Dict[str, Any]] = {}
        self.calculated_data: Dict[str, Dict[str, Any]] = {}
        self._merge_states: Dict[str, bool] = {}
        self._fetch_thread: Optional[threading.Thread] = None
        self._calc_thread: Optional[threading.Thread] = None
        self._update_thread: Optional[threading.Thread] = None
        self._pending_update_list: List[Dict[str, Any]] = []
        self._create_controls()

    def _create_controls(self):
        self.input_user = ft.TextField(
            label="用户名", dense=True, autofocus=True,
            border=ft.InputBorder.UNDERLINE, content_padding=ft.Padding(0, 8, 0, 8),
            text_style=ft.TextStyle(size=14), label_style=ft.TextStyle(size=12),
            autofill_hints=ft.AutofillHint.USERNAME, expand=True,
        )
        self.input_pass = ft.TextField(
            label="密码", password=True, can_reveal_password=True, dense=True,
            border=ft.InputBorder.UNDERLINE, content_padding=ft.Padding(0, 8, 0, 8),
            text_style=ft.TextStyle(size=14), label_style=ft.TextStyle(size=12),
            autofill_hints=ft.AutofillHint.PASSWORD, expand=True,
        )
        self.input_frid = ft.TextField(
            label="FR ID", dense=True, width=90,
            border=ft.InputBorder.UNDERLINE, content_padding=ft.Padding(0, 8, 0, 8),
            text_style=ft.TextStyle(size=14), label_style=ft.TextStyle(size=12),
            input_filter=ft.NumbersOnlyInputFilter(),
        )
        self.btn_fetch = ft.FilledButton(
            content="获取任务", icon=ft.Icons.DOWNLOAD_ROUNDED,
            on_click=self.on_fetch_clicked,
            style=ft.ButtonStyle(shape=ft.RoundedRectangleBorder(radius=8), padding=ft.Padding(16, 8, 16, 8)),
        )
        self.date_picker = ft.DatePicker(
            first_date=date(2020, 1, 1), last_date=date(2035, 12, 31), on_change=self._on_date_picked,
        )
        self.date_text = ft.TextField(
            label="起始日期", value=date.today().strftime("%Y-%m-%d"), width=130, dense=True,
            border=ft.InputBorder.UNDERLINE,
            content_padding=ft.Padding(0, 8, 0, 8),
            text_style=ft.TextStyle(size=14), label_style=ft.TextStyle(size=12),
            suffix=ft.IconButton(icon=ft.Icons.CALENDAR_TODAY_ROUNDED, icon_size=16,
                                 on_click=self._open_date_picker),
        )

        self.btn_calc = ft.OutlinedButton(
            content="计算排期", icon=ft.Icons.CALCULATE_ROUNDED,
            on_click=self.on_calc_clicked,
            style=ft.ButtonStyle(shape=ft.RoundedRectangleBorder(radius=8), padding=ft.Padding(14, 8, 14, 8)),
        )
        self.btn_commit = ft.FilledButton(
            content="更新系统", icon=ft.Icons.CLOUD_UPLOAD_ROUNDED,
            on_click=self.on_commit_clicked, disabled=True,
            style=ft.ButtonStyle(shape=ft.RoundedRectangleBorder(radius=8), padding=ft.Padding(14, 8, 14, 8)),
        )
        self.pbar = ft.ProgressBar(value=0, expand=True, bar_height=4, border_radius=2)
        self.progress_row = ft.Row(
            [ft.Text("进度:", size=12, color=TEXT_3), self.pbar],
            spacing=8, vertical_alignment=ft.CrossAxisAlignment.CENTER, visible=False,
        )
        self.task_table = ft.DataTable(
            columns=[
                ft.DataColumn(ft.Text("任务ID", size=13, color=TEXT_3, weight=ft.FontWeight.W_500)),
                ft.DataColumn(ft.Text("任务标题", size=13, color=TEXT_3, weight=ft.FontWeight.W_500)),
                ft.DataColumn(ft.Text("工作量（人天）", size=13, color=TEXT_3, weight=ft.FontWeight.W_500), numeric=True),
                ft.DataColumn(ft.Text("其他工作量（人天）", size=13, color=TEXT_3, weight=ft.FontWeight.W_500), numeric=True),
                ft.DataColumn(ft.Text("开始日期", size=13, color=TEXT_3, weight=ft.FontWeight.W_500)),
                ft.DataColumn(ft.Text("结束日期", size=13, color=TEXT_3, weight=ft.FontWeight.W_500)),
                ft.DataColumn(ft.Text("是否合并", size=13, color=TEXT_3, weight=ft.FontWeight.W_500)),
            ],
            heading_row_height=36, data_row_min_height=40, data_row_max_height=40,
            heading_row_color=ft.Colors.with_opacity(0.03, "#0F172A"),
            border_radius=10, horizontal_margin=12, column_spacing=8,
            divider_thickness=0, expand=True,
        )
        self.table_scroll = ft.Column([self.task_table], scroll=ft.ScrollMode.AUTO, expand=True, horizontal_alignment=ft.CrossAxisAlignment.STRETCH)
        self._empty_state_hint = ft.Text("登录后获取 FR 任务列表", size=12, color=TEXT_3)
        self.table_empty = ft.Container(
            content=ft.Column([
                ft.Icon(ft.Icons.INBOX_ROUNDED, size=56, color=TEXT_3),
                ft.Text("暂无任务", size=15, weight=ft.FontWeight.W_500, color=TEXT_2),
                self._empty_state_hint,
            ], horizontal_alignment=ft.CrossAxisAlignment.CENTER,
               alignment=ft.MainAxisAlignment.CENTER, spacing=8, expand=True),
            alignment=ft.Alignment(0, 0), expand=True,
        )
        self.log_view = ft.ListView(spacing=1, expand=True, auto_scroll=True,
                                    padding=ft.Padding(left=10, top=6, right=10, bottom=6))
        self.status_text = ft.Text("就绪", size=12, color=TEXT_3)
        # Shared DatePicker for table date cells
        self._cell_date_target = None  # (tid, field, tf_ref)
        self.cell_date_picker = ft.DatePicker(
            first_date=date(2020, 1, 1), last_date=date(2035, 12, 31),
            on_change=self._on_cell_date_picked,
        )
        self.confirm_dlg = ft.AlertDialog(
            modal=True, title=ft.Text("确认更新"),
            content=ft.Text("将工作量和排期时间写入系统，此操作不可撤销。"),
            actions=[
                ft.TextButton(content="取消", on_click=lambda _: self._close_dialog()),
                ft.FilledButton(content="确认", on_click=lambda _: self._do_commit_update()),
            ], actions_alignment=ft.MainAxisAlignment.END,
        )


    def build(self) -> ft.Container:
        toolbar = ft.Container(
            content=ft.Row([
                ft.Row([self.input_user, self.input_pass, self.input_frid, self.btn_fetch], spacing=12, expand=True),
                ft.Container(width=1, height=28, bgcolor=BORDER),
                ft.Row([self.date_text, self.btn_calc, self.btn_commit], spacing=10),
            ], spacing=16, vertical_alignment=ft.CrossAxisAlignment.CENTER),
            padding=ft.Padding(left=20, top=10, right=20, bottom=10),
            bgcolor=BG_CARD,
            border=ft.Border(bottom=ft.BorderSide(1, BORDER)),
            shadow=ft.BoxShadow(spread_radius=0, blur_radius=4,
                color=ft.Colors.with_opacity(0.04, "#000000"), offset=ft.Offset(0, 1)),
        )

        table_panel = ft.Container(
            content=ft.Column([
                ft.Row([
                    ft.Text("任务列表", size=15, weight=ft.FontWeight.W_600, color=TEXT_1),
                    ft.Container(expand=True),
                    self.status_text,
                ], vertical_alignment=ft.CrossAxisAlignment.CENTER),
                ft.Container(height=1, bgcolor=BORDER),
                ft.Stack([self.table_empty, self.table_scroll], expand=True),
            ], spacing=8, expand=True),
            bgcolor=BG_PAGE, padding=ft.Padding(left=20, top=12, right=20, bottom=12),
            expand=3,
        )

        log_panel = ft.Container(
            content=ft.Column([
                ft.Row([
                    ft.Text("日志", size=12, weight=ft.FontWeight.W_500, color=TEXT_2),
                    self.progress_row,
                    ft.Container(expand=True),
                    ft.TextButton(content="清空", on_click=lambda _: self._clear_log(),
                                  style=ft.ButtonStyle(color=TEXT_3, padding=ft.Padding(8, 4, 8, 4))),
                ], spacing=8, vertical_alignment=ft.CrossAxisAlignment.CENTER),
                ft.Container(
                    content=self.log_view, expand=True, border_radius=8,
                    bgcolor="#FFFFFF", border=ft.Border(
                        top=ft.BorderSide(1, BORDER), right=ft.BorderSide(1, BORDER),
                        bottom=ft.BorderSide(1, BORDER), left=ft.BorderSide(1, BORDER)),
                ),
            ], spacing=6, expand=True),
            expand=1,
            padding=ft.Padding(left=12, top=12, right=12, bottom=12),
            bgcolor="#F8FAFC", border=ft.Border(left=ft.BorderSide(1, BORDER)),
        )

        main_row = ft.Row([table_panel, log_panel], spacing=0, expand=True)

        return ft.Column([toolbar, main_row], expand=True, spacing=0)


    def _open_date_picker(self, e):
        self.page.show_dialog(self.date_picker)

    def _on_date_picked(self, e):
        if self.date_picker.value:
            local_date = self.date_picker.value.astimezone().date()
            self.date_text.value = local_date.strftime("%Y-%m-%d")
            self.page.update()

    def _refresh_table(self):
        self.task_table.rows.clear()
        if not self.current_tasks:
            self.table_empty.visible = True
            self.table_scroll.visible = False
            self.btn_commit.disabled = True
            self._empty_state_hint.value = "登录后获取 FR 任务列表"
            self.page.update()
            return
        self.table_empty.visible = False
        self.table_scroll.visible = True
        task_ids = list(self.current_tasks.keys())
        prev_start = prev_end = None
        for idx, tid in enumerate(task_ids):
            t = self.current_tasks[tid]
            cur_start, cur_end = t.get('start_time'), t.get('end_time')
            auto_merge = bool(cur_start and cur_end and cur_start == prev_start and cur_end == prev_end)
            if tid not in self._merge_states:
                self._merge_states[tid] = auto_merge
            wd = t.get('workdays', 0.0)
            wd_str = f"{int(wd)}" if wd == int(wd) else f"{wd:.1f}"
            owd = t.get('other_workdays', 0.0)
            owd_str = f"{int(owd)}" if owd == int(owd) else f"{owd:.1f}"
            start_str = str(cur_start) if cur_start else ""
            end_str = str(cur_end) if cur_end else ""
            # Inline-edit fields: underline border, minimal padding
            wd_tf = ft.TextField(
                value=wd_str, dense=True, text_align=ft.TextAlign.RIGHT, width=55,
                border=ft.InputBorder.NONE, content_padding=ft.Padding(4, 2, 4, 2),
                text_style=ft.TextStyle(size=14, color=AMBER, weight=ft.FontWeight.W_600),
            )
            wd_tf.on_change = self._make_wd_handler(tid, wd_tf, 'workdays')
            owd_tf = ft.TextField(
                value=owd_str, dense=True, text_align=ft.TextAlign.RIGHT, width=55,
                border=ft.InputBorder.NONE, content_padding=ft.Padding(4, 2, 4, 2),
                text_style=ft.TextStyle(size=14, color=PURPLE, weight=ft.FontWeight.W_500),
            )
            owd_tf.on_change = self._make_wd_handler(tid, owd_tf, 'other_workdays')
            start_tf = ft.TextField(
                value=start_str, dense=True, width=95,
                border=ft.InputBorder.NONE, content_padding=ft.Padding(4, 2, 4, 2),
                text_style=ft.TextStyle(size=14, color=TEXT_2),
            )
            start_tf.on_change = self._make_date_handler(tid, start_tf, 'start_time')
            start_cell = ft.Row([start_tf, ft.IconButton(
                icon=ft.Icons.CALENDAR_TODAY_ROUNDED, icon_size=14, icon_color=TEXT_3,
                padding=0, width=24, height=24,
                on_click=self._make_cell_date_opener(tid, 'start_time', start_tf),
            )], spacing=0, vertical_alignment=ft.CrossAxisAlignment.CENTER)
            end_tf = ft.TextField(
                value=end_str, dense=True, width=95,
                border=ft.InputBorder.NONE, content_padding=ft.Padding(4, 2, 4, 2),
                text_style=ft.TextStyle(size=14, color=TEXT_2),
            )
            end_tf.on_change = self._make_date_handler(tid, end_tf, 'end_time')
            end_cell = ft.Row([end_tf, ft.IconButton(
                icon=ft.Icons.CALENDAR_TODAY_ROUNDED, icon_size=14, icon_color=TEXT_3,
                padding=0, width=24, height=24,
                on_click=self._make_cell_date_opener(tid, 'end_time', end_tf),
            )], spacing=0, vertical_alignment=ft.CrossAxisAlignment.CENTER)
            merge_cb = ft.Checkbox(value=self._merge_states.get(tid, False))
            merge_cb.on_change = self._make_merge_handler(tid, merge_cb)
            row_color = BG_ROW_ALT if idx % 2 == 1 else None
            self.task_table.rows.append(ft.DataRow(
                cells=[
                    ft.DataCell(ft.Text(str(t.get('id', '')), size=13, color=TEXT_3)),
                    ft.DataCell(ft.Text(str(t.get('title', '')), size=14, color=TEXT_1, max_lines=1, overflow=ft.TextOverflow.ELLIPSIS)),
                    ft.DataCell(wd_tf), ft.DataCell(owd_tf),
                    ft.DataCell(start_cell), ft.DataCell(end_cell), ft.DataCell(merge_cb),
                ],
                color=row_color,
            ))
            prev_start, prev_end = cur_start, cur_end
        self.btn_commit.disabled = False
        self.page.update()


    def _make_wd_handler(self, tid, tf, field):
        def handler(e):
            try:
                val = float(e.control.value)
                if val < 0:
                    raise ValueError
                if tid in self.current_tasks:
                    self.current_tasks[tid][field] = val
                e.control.value = f"{int(val)}" if val == int(val) else f"{val:.1f}"
            except (ValueError, TypeError):
                prev = self.current_tasks.get(tid, {}).get(field, 0.0)
                e.control.value = f"{int(prev)}" if prev == int(prev) else f"{prev:.1f}"
            e.control.update()
        return handler

    def _make_date_handler(self, tid, tf, field):
        def handler(e):
            if tid in self.current_tasks:
                self.current_tasks[tid][field] = (e.control.value or "").strip()
        return handler

    def _make_merge_handler(self, tid, cb):
        def handler(e):
            self._merge_states[tid] = e.control.value
        return handler

    def _make_cell_date_opener(self, tid, field, tf):
        def handler(e):
            self._cell_date_target = (tid, field, tf)
            try:
                self.cell_date_picker.value = date.fromisoformat((tf.value or "").strip())
            except (ValueError, TypeError):
                self.cell_date_picker.value = date.today()
            self.page.show_dialog(self.cell_date_picker)
        return handler

    def _on_cell_date_picked(self, e):
        if not self.cell_date_picker.value or not self._cell_date_target:
            return
        tid, field, tf = self._cell_date_target
        local_date = self.cell_date_picker.value.astimezone().date()
        date_str = local_date.strftime("%Y-%m-%d")
        tf.value = date_str
        if tid in self.current_tasks:
            self.current_tasks[tid][field] = date_str
        self._cell_date_target = None
        self.page.update()

    def _apply_calculation_results(self, results):
        self.calculated_data = {item['id']: item for item in results}
        for row in self.task_table.rows:
            cells = row.cells
            tid = cells[0].content.value if isinstance(cells[0].content, ft.Text) else ""
            if tid not in self.calculated_data:
                continue
            res = self.calculated_data[tid]
            start_container = cells[4].content
            start_tf = start_container.controls[0] if isinstance(start_container, ft.Row) else start_container
            if isinstance(start_tf, ft.TextField):
                start_tf.value = str(res['new_start'])
                start_tf.text_style = ft.TextStyle(size=14, color=GREEN, weight=ft.FontWeight.W_600)
            end_container = cells[5].content
            end_tf = end_container.controls[0] if isinstance(end_container, ft.Row) else end_container
            if isinstance(end_tf, ft.TextField):
                end_tf.value = str(res['new_end'])
                end_tf.text_style = ft.TextStyle(size=14, color=GREEN, weight=ft.FontWeight.W_600)
            if 'other_workdays' in res:
                owd = res['other_workdays']
                owd_tf = cells[3].content
                if isinstance(owd_tf, ft.TextField):
                    owd_tf.value = f"{int(owd)}" if owd == int(owd) else f"{owd:.1f}"
            if tid in self.current_tasks:
                self.current_tasks[tid]['start_time'] = str(res['new_start'])
                self.current_tasks[tid]['end_time'] = str(res['new_end'])
                if 'other_workdays' in res:
                    self.current_tasks[tid]['other_workdays'] = res['other_workdays']
        self.btn_commit.disabled = False
        self.page.update()


    def on_fetch_clicked(self, e):
        u = (self.input_user.value or "").strip()
        p = (self.input_pass.value or "").strip()
        f = (self.input_frid.value or "").strip()
        if not all([u, p, f]):
            self._show_snack("请填写完整登录信息", is_error=True)
            return
        if not f.isdigit():
            self._show_snack("FR ID 必须为纯数字", is_error=True)
            return
        self._set_busy(True, "fetch")
        self.task_table.rows.clear()
        self.current_tasks.clear()
        self.table_empty.visible = True
        self.table_scroll.visible = False
        self.btn_commit.disabled = True
        self.page.update()
        self._log("开始获取任务...")
        self._fetch_thread = threading.Thread(target=self._do_fetch, args=(u, p, int(f)), daemon=True)
        self._fetch_thread.start()

    def on_calc_clicked(self, e):
        if not self.current_tasks:
            self._show_snack("请先获取任务", is_error=True)
            return
        try:
            start_date = date.fromisoformat(self.date_text.value or "")
        except (ValueError, TypeError):
            self._show_snack("起始日期格式无效", is_error=True)
            return
        self._log("计算排期中...")
        task_order = []
        for row in self.task_table.rows:
            cells = row.cells
            tid = cells[0].content.value if isinstance(cells[0].content, ft.Text) else ""
            if not tid:
                continue
            try:
                owd_tf = cells[3].content
                owd = float(owd_tf.value.strip()) if isinstance(owd_tf, ft.TextField) else 0.0
            except (ValueError, TypeError):
                owd = 0.0
            merge_cb = cells[6].content
            merge_flag = merge_cb.value if isinstance(merge_cb, ft.Checkbox) else False
            task_order.append((tid, owd, merge_flag))
        self._set_busy(True, "calc")
        self._calc_thread = threading.Thread(target=self._do_calculate, args=(start_date, task_order), daemon=True)
        self._calc_thread.start()

    def on_commit_clicked(self, e):
        if not self.task_table.rows:
            return
        update_list: List[Dict[str, Any]] = []
        for row in self.task_table.rows:
            cells = row.cells
            tid = cells[0].content.value if isinstance(cells[0].content, ft.Text) else ""
            if not tid:
                continue
            task_data = {"id": tid, "title": self.current_tasks.get(tid, {}).get('title', '')}
            has_changes = False
            wd_tf = cells[2].content
            if isinstance(wd_tf, ft.TextField):
                try:
                    task_data["workdays"] = float((wd_tf.value or "").strip())
                    has_changes = True
                except (ValueError, TypeError):
                    pass
            start_c, end_c = cells[4].content, cells[5].content
            start_tf = start_c.controls[0] if isinstance(start_c, ft.Row) else start_c
            end_tf = end_c.controls[0] if isinstance(end_c, ft.Row) else end_c
            if isinstance(start_tf, ft.TextField) and isinstance(end_tf, ft.TextField):
                ns, ne = (start_tf.value or "").strip(), (end_tf.value or "").strip()
                if DATE_PATTERN.match(ns) and DATE_PATTERN.match(ne):
                    task_data["new_start"], task_data["new_end"] = ns, ne
                    has_changes = True
            if has_changes:
                update_list.append(task_data)
        if not update_list:
            self._show_snack("没有有效数据可提交")
            return
        self._pending_update_list = update_list
        self.confirm_dlg.content = ft.Text(f"将 {len(update_list)} 个任务写入系统，不可撤销。")
        self.page.show_dialog(self.confirm_dlg)


    def _do_commit_update(self):
        self._close_dialog()
        if not self._pending_update_list:
            return
        self._set_busy(True, "commit")
        self.pbar.value = 0
        self.progress_row.visible = True
        self.page.update()
        self._log(f"批量更新 {len(self._pending_update_list)} 个任务...")
        update_list = self._pending_update_list
        self._pending_update_list = []
        self._update_thread = threading.Thread(target=self._do_update, args=(update_list,), daemon=True)
        self._update_thread.start()

    def _do_fetch(self, user, pwd, fr_id):
        try:
            self._log(f"登录 ({user})...")
            self.rd_client.login(user, pwd)
            uid = self.rd_client.fetch_user_uid()
            self._log(f"UID: {uid}")
            self._log(f"拉取 FR-{fr_id}...")
            tasks = self.rd_client.get_my_tasks(fr_id)
            if tasks:
                self._log(f"获取 {len(tasks)} 个任务")
            else:
                self._empty_state_hint.value = "未找到属于您的任务，请检查 FR ID"
                self._log("未找到任务")
            self._compute_other_workdays(tasks)
            self.current_tasks = tasks
            self.page.run_task(self._async_refresh_after_fetch)
        except Exception as ex:
            self._log(f"失败: {ex}")
            self.page.run_task(self._async_reset_busy, "fetch")

    async def _async_refresh_after_fetch(self):
        self._prepare_merge_states()
        self._refresh_table()
        self._set_busy(False, "fetch")
        self._log("就绪，可编辑后计算排期。")

    async def _async_reset_busy(self, action):
        self._set_busy(False, action)

    def _compute_other_workdays(self, tasks):
        calc = rd_task_core.WorkdayCalculator()
        date_groups: Dict[str, List[str]] = {}
        group_order: List[str] = []
        for tid, task in tasks.items():
            st, et = task.get('start_time'), task.get('end_time')
            if st and et:
                key = f"{st}|{et}"
                if key not in date_groups:
                    date_groups[key] = []
                    group_order.append(key)
                date_groups[key].append(tid)
        for key in group_order:
            tids = date_groups[key]
            start_str, end_str = key.split('|', 1)
            span_wd = 0
            try:
                span_wd = calc.workdays_between(date.fromisoformat(start_str), date.fromisoformat(end_str))
            except (ValueError, TypeError):
                pass
            total_work = sum(tasks[tid]['workdays'] for tid in tids)
            for i, tid in enumerate(tids):
                tasks[tid]['other_workdays'] = max(0.0, span_wd - total_work) if i == 0 else 0.0
        for task in tasks.values():
            task.setdefault('other_workdays', 0.0)
        prev_end = None
        for tid in tasks:
            task = tasks[tid]
            st, et = task.get('start_time'), task.get('end_time')
            if st and et and st == et and prev_end and st == prev_end:
                task['workdays'] = task['other_workdays'] = 0.0
            prev_end = et or prev_end

    def _prepare_merge_states(self):
        self._merge_states.clear()
        prev_start = prev_end = None
        for tid in self.current_tasks:
            t = self.current_tasks[tid]
            cs, ce = t.get('start_time'), t.get('end_time')
            self._merge_states[tid] = bool(cs and ce and cs == prev_start and ce == prev_end)
            prev_start, prev_end = cs, ce


    def _do_calculate(self, start_date, task_order):
        try:
            calculator = rd_task_core.WorkdayCalculator()
            results = []
            groups, current_group = [], []
            for tid, other_wd, merge_flag in task_order:
                if merge_flag and current_group:
                    current_group.append((tid, other_wd))
                else:
                    if current_group:
                        groups.append(current_group)
                    current_group = [(tid, other_wd)]
            if current_group:
                groups.append(current_group)
            current_start, prev_end = start_date, None
            for group in groups:
                total_workdays = sum(
                    float(self.current_tasks[tid]['workdays'])
                    for tid, _ in group
                    if self.current_tasks.get(tid, {}).get('workdays') is not None
                )
                group_other = sum(ow for _, ow in group)
                total = total_workdays + group_other
                if total < 0:
                    continue
                if total == 0 and prev_end is not None:
                    real_start = real_end = prev_end
                else:
                    real_start = calculator.init_start_date(current_start)
                    real_end = calculator.calculate_end_date(real_start, total)
                for i, (tid, _) in enumerate(group):
                    results.append({"id": tid, "new_start": real_start, "new_end": real_end,
                                    "other_workdays": group_other if i == 0 else 0.0})
                prev_end = real_end
                current_start = real_end + datetime.timedelta(days=1)
            self._log("排期计算完成")
            self.page.run_task(self._async_calc_finished, results)
        except Exception as ex:
            self._log(f"计算出错: {ex}")
            self.page.run_task(self._async_reset_busy, "calc")

    async def _async_calc_finished(self, results):
        self._apply_calculation_results(results)
        self._set_busy(False, "calc")

    def _do_update(self, update_list):
        total = len(update_list)
        success = 0
        for idx, item in enumerate(update_list):
            try:
                if 'workdays' in item:
                    self.rd_client.update_task_worktime(item['id'], item['workdays'])
                if 'new_start' in item and 'new_end' in item:
                    self.rd_client.update_task_time(item['id'], str(item['new_start']), str(item['new_end']))
                self._log(f"OK {item['title']}")
                success += 1
            except Exception as ex:
                self._log(f"FAIL {item['title']}: {ex}")
            self.pbar.value = (idx + 1) / total
            self.page.update()
        self._log(f"完成 {success}/{total}")
        self.page.run_task(self._async_update_done)

    async def _async_update_done(self):
        self._set_busy(False, "commit")
        self.pbar.value = 1.0
        self.progress_row.visible = False
        self.page.update()
        self._show_snack("更新完成，重新拉取中...")
        self.on_fetch_clicked(None)

    def _set_busy(self, busy, action):
        if action == "fetch":
            self.btn_fetch.disabled = busy
            self.btn_fetch.content = "获取中..." if busy else "获取任务"
            self.btn_fetch.icon = ft.Icons.HOURGLASS_EMPTY_ROUNDED if busy else ft.Icons.DOWNLOAD_ROUNDED
            self.btn_calc.disabled = busy
            self.btn_commit.disabled = busy or not self.current_tasks
        elif action == "calc":
            self.btn_calc.disabled = busy
            self.btn_calc.content = "计算中..." if busy else "计算排期"
            self.btn_calc.icon = ft.Icons.HOURGLASS_EMPTY_ROUNDED if busy else ft.Icons.CALCULATE_ROUNDED
            self.btn_fetch.disabled = busy
            self.btn_commit.disabled = busy or not self.current_tasks
        elif action == "commit":
            self.btn_commit.disabled = busy
            self.btn_commit.content = "更新中..." if busy else "更新系统"
            self.btn_commit.icon = ft.Icons.HOURGLASS_EMPTY_ROUNDED if busy else ft.Icons.CLOUD_UPLOAD_ROUNDED
            self.btn_fetch.disabled = busy
            self.btn_calc.disabled = busy
        self.page.update()

    def _log(self, msg):
        now = datetime.datetime.now().strftime("%H:%M:%S")
        msg_lower = msg.lower()
        if msg_lower.startswith("fail") or msg_lower.startswith("失败") or msg_lower.startswith("计算出错"):
            color = ERROR
        elif msg_lower.startswith("ok ") or msg_lower.startswith("完成") or msg_lower.startswith("就绪") or msg_lower.startswith("排期计算完成"):
            color = GREEN
        else:
            color = TEXT_2
        self.log_view.controls.append(
            ft.Text(f"{now}  {msg}", size=12, font_family="monospace", color=color))
        self.status_text.value = msg
        self.page.update()

    def _clear_log(self):
        self.log_view.controls.clear()
        self.page.update()

    def _show_snack(self, msg, is_error: bool = False):
        snack = ft.SnackBar(
            content=ft.Text(msg, color=ft.Colors.WHITE),
            bgcolor=ERROR if is_error else ft.Colors.with_opacity(0.9, TEXT_1),
            duration=4000,
        )
        self.page.overlay.append(snack)
        snack.open = True
        self.page.update()

    def _close_dialog(self):
        self.page.pop_dialog()


def main(page: ft.Page):
    page.title = "RD 任务排期助手"
    page.theme_mode = ft.ThemeMode.LIGHT
    page.theme = _build_theme()
    page.bgcolor = BG_PAGE
    page.window.width = 1400
    page.window.height = 850
    page.window.min_width = 900
    page.window.min_height = 600
    page.padding = 0

    app = RDTaskApp(page)
    page.add(app.build())


if __name__ == "__main__":
    import sys
    if "--web" in sys.argv:
        ft.run(main, view=ft.AppView.WEB_BROWSER, port=8550, host="0.0.0.0")
    else:
        ft.run(main)
