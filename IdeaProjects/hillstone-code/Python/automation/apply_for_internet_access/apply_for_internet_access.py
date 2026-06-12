# -*- coding: utf-8 -*-
import argparse
import datetime
import getpass
import json
import sys

import requests
from PyQt6.QtCore import Qt, QSettings
from PyQt6.QtGui import QFont, QPalette
from PyQt6.QtWidgets import (
    QApplication, QMainWindow, QWidget, QVBoxLayout,
    QHBoxLayout, QLabel, QLineEdit, QTextEdit, QPushButton,
    QMessageBox, QGroupBox, QFormLayout, QSplitter
)


# author JunXian Wu
# created 2025-05-29
# consolidated 2026-02-10

# --- Core Logic (Platform Agnostic) ---

def _create_order_info(ip: str, hours: int) -> list:
    """Helper to generate the complex 'orderInfo' payload."""
    return [
        {"label": "标题", "required": True, "type": "text", "typeName": "单行文本", "placeholder": "示例：KVM服务器安装/升级工具", "value": "申请上网", "change": False},
        {"label": "机房", "key": "DanXuanXiaLaKuang", "required": True, "type": "select", "typeName": "单选下拉框", "placeholder": "请选择机房", "value": 0, "change": True, "minValue": 0, "maxValue": 10, "rule": 0, "managerInput": False, "options": ["苏州研发新机房", "苏州研发旧机房", "北京机房"]},
        {"label": "上网设备IP", "key": "DanXingWenBen", "required": True, "type": "text", "typeName": "单行文本", "placeholder": "例：10.X.X.X（每次只能输一个IP）", "value": ip, "change": True, "minValue": 0, "maxValue": 10, "rule": 1},
        {"label": "上网原因", "key": "DuoXuanXiaLaKuang", "required": True, "type": "multiple", "typeName": "多选下拉框", "placeholder": "请选择上网原因", "value": ["安装/升级工具"], "change": True, "minValue": 0, "maxValue": 10, "options": ["安装/升级工具", "安装/更新容器镜像", "下载第三方数据", "下载/更新驱动", "下载/更新第三方代码包", "测试公网系统", "其它"]},
        {"label": "上网时长", "key": "ShuZiShuRuKuang", "required": True, "type": "number", "typeName": "数字输入框", "placeholder": "请输入上网时长", "value": hours, "change": True, "minValue": 0, "maxValue": str(hours)}
    ]


def apply_for_internet_access(username, password, ips, hours=24, logger=print):
    """
    Sends requests to apply for internet access in the test environment.
    :param username: Username for login.
    :param password: Encoded password from browser login.
    :param ips: List of IP addresses to apply for.
    :param hours: Duration of internet access in hours.
    :param logger: A callable (like print or a UI log method) to output progress.
    :return: A list of dictionaries with results for each IP.
    """
    logger("正在登录系统...")
    session = requests.Session()
    session.trust_env = False

    try:
        session.post(
            url="https://hspauth.hillstonenet.com/oauth2/login",
            params={"username": username, "password": password, "remember-me": "true"},
            verify=True,
            timeout=10
        ).raise_for_status()
        session.post(
            "https://ils.hillstonenet.com/work-order_svc/oauth2/authorization/workorder-client-oidc?redirectTo=https://ils.hillstonenet.com/work-order/",
            verify=True,
            timeout=10
        ).raise_for_status()
        logger("✓ 登录成功！")
    except requests.RequestException as e:
        error_message = f"登录失败: {e}"
        logger(f"✗ {error_message}")
        raise Exception(error_message)

    results = []
    total_ips = len(ips)
    for i, ip in enumerate(ips, 1):
        response = None  # Ensure response is defined for the finally block
        try:
            logger(f"正在处理第 {i}/{total_ips} 个IP: {ip}")

            network_params = json.dumps({"ipAddress": ip, "hours": hours})
            order_info = _create_order_info(ip, hours)
            order_info_str = json.dumps(order_info, ensure_ascii=False)

            payload = {
                "createUser": 944,
                "orderTitle": f"{ip}申请上网",
                "orderType": "108",
                "networkParams": network_params,
                "lab": "苏州研发新机房",
                "orderInfo": order_info_str
            }

            response = session.post(
                "https://ils.hillstonenet.com/work-order_svc/order/order/submit",
                json=payload,
                timeout=10
            )
            response.raise_for_status()

            result = {"ip": ip, "success": True, "message": response.json()}
            results.append(result)
            logger(f"✓ IP {ip} 提交成功")

        except requests.RequestException as e:
            msg = str(e)
            if e.response is not None:
                msg += f" | 响应: {e.response.text}"
            result = {"ip": ip, "success": False, "message": msg}
            results.append(result)
            logger(f"✗ IP {ip} 提交失败: {msg}")
        except Exception as e:
            msg = str(e)
            result = {"ip": ip, "success": False, "message": msg}
            results.append(result)
            logger(f"✗ IP {ip} 提交失败: {msg}")

    return results


# --- GUI Application ---

class InternetAccessApp(QMainWindow):
    def __init__(self):
        super().__init__()
        self.settings = QSettings("Hillstone", "InternetAccessApp")
        self.init_ui()
        self.load_settings()
        self.update_log_style()

    def init_ui(self):
        self.setWindowTitle("测试环境上网申请工具")
        self.setMinimumSize(700, 600)
        central_widget = QWidget()
        self.setCentralWidget(central_widget)

        main_layout = QVBoxLayout(central_widget)
        main_layout.setSpacing(10)
        main_layout.setContentsMargins(15, 15, 15, 15)

        # --- Inputs ---
        input_widget = QWidget()
        input_layout = QVBoxLayout(input_widget)
        input_layout.setSpacing(10)

        login_group = QGroupBox("登录信息")
        login_layout = QFormLayout(login_group)
        login_layout.setSpacing(8)
        self.username_input = QLineEdit(placeholderText="请输入用户名")
        self.password_input = QLineEdit(placeholderText="请输入密码", echoMode=QLineEdit.EchoMode.Password)
        login_layout.addRow("用户名:", self.username_input)
        login_layout.addRow("密码:", self.password_input)

        ip_group = QGroupBox("IP地址列表")
        ip_layout = QVBoxLayout(ip_group)
        self.ip_textedit = QTextEdit(placeholderText="请输入IP地址，每行一个\n示例：\n10.180.139.97\n10.180.139.98")
        self.ip_textedit.setMinimumHeight(120)
        ip_layout.addWidget(self.ip_textedit)

        # --- Controls ---
        controls_layout = QHBoxLayout()
        duration_layout = QHBoxLayout()
        duration_layout.addWidget(QLabel("上网时长(小时):"))
        self.duration_input = QLineEdit("24", maximumWidth=80)
        duration_layout.addWidget(self.duration_input)
        duration_layout.addStretch()

        action_buttons_layout = QHBoxLayout()
        self.load_default_btn = QPushButton("加载默认IP")
        self.clear_btn = QPushButton("清空")
        self.save_btn = QPushButton("保存设置")
        action_buttons_layout.addWidget(self.load_default_btn)
        action_buttons_layout.addWidget(self.clear_btn)
        action_buttons_layout.addWidget(self.save_btn)

        controls_layout.addLayout(duration_layout)
        controls_layout.addLayout(action_buttons_layout)

        input_layout.addWidget(login_group)
        input_layout.addWidget(ip_group)
        input_layout.addLayout(controls_layout)

        # --- Logs and Submit ---
        log_widget = QWidget()
        log_layout = QVBoxLayout(log_widget)
        log_layout.setContentsMargins(0, 5, 0, 0)
        log_layout.addWidget(QLabel("操作日志", styleSheet="font-weight: bold;"))
        self.log_textedit = QTextEdit(readOnly=True)
        log_layout.addWidget(self.log_textedit)

        submit_layout = QHBoxLayout()
        submit_layout.addStretch()
        self.submit_btn = QPushButton("提交申请")
        submit_layout.addWidget(self.submit_btn)
        log_layout.addLayout(submit_layout)

        # --- Splitter ---
        splitter = QSplitter(Qt.Orientation.Vertical)
        splitter.addWidget(input_widget)
        splitter.addWidget(log_widget)
        splitter.setSizes([380, 220])
        main_layout.addWidget(splitter)

        # --- Connections ---
        self.load_default_btn.clicked.connect(self.load_default_ips)
        self.clear_btn.clicked.connect(self.clear_inputs)
        self.save_btn.clicked.connect(self.save_settings)
        self.submit_btn.clicked.connect(self.submit_application)

        # --- Styling ---
        self.setup_styles()
        font = QFont()
        font.setPointSize(10)
        self.setFont(font)

    def setup_styles(self):
        """Centralized styling for the application."""
        self.setStyleSheet("""
            QMainWindow { background-color: palette(window); }
            QGroupBox {
                font-weight: bold; border: 1px solid palette(mid);
                border-radius: 5px; margin-top: 10px; padding-top: 10px;
            }
            QGroupBox::title {
                subcontrol-origin: margin; left: 10px; padding: 0 5px;
            }
            QLineEdit, QTextEdit {
                border: 1px solid palette(mid); border-radius: 3px;
                padding: 5px; background-color: palette(base);
            }
            QLineEdit:focus, QTextEdit:focus { border: 2px solid #2196F3; }
            QPushButton {
                padding: 6px 12px; border: 1px solid palette(mid);
                border-radius: 4px; background-color: palette(button);
            }
            QPushButton:hover { background-color: palette(highlight); }
        """)
        self.submit_btn.setStyleSheet("""
            QPushButton {
                background-color: #2196F3; color: white; font-weight: bold;
                padding: 8px 20px; border-radius: 4px; border: none;
            }
            QPushButton:hover { background-color: #1976D2; }
            QPushButton:disabled { background-color: #cccccc; color: #666666; }
        """)

    def update_log_style(self):
        palette = self.log_textedit.palette()
        bg_color = palette.color(QPalette.ColorRole.Base)
        brightness = (bg_color.red() * 299 + bg_color.green() * 587 + bg_color.blue() * 114) / 1000

        self.log_colors = {
            'success': "#4CAF50" if brightness < 128 else "#2E7D32",
            'error': "#F44336" if brightness < 128 else "#C62828",
            'timestamp': "#B0BEC5" if brightness < 128 else "#546E7A",
            'text': "#E0E0E0" if brightness < 128 else "#212121",
        }
        self.log_textedit.setStyleSheet(f"""
            QTextEdit {{
                border: 1px solid palette(mid); border-radius: 3px;
                font-family: Consolas, 'Courier New', monospace; font-size: 9pt;
                background-color: palette(base); color: {self.log_colors['text']};
            }}""")

    def changeEvent(self, event):
        super().changeEvent(event)
        if event.type() == event.Type.PaletteChange:
            self.update_log_style()

    def log_message(self, message, msg_type=None):
        timestamp = datetime.datetime.now().strftime("%H:%M:%S")
        timestamp_html = f'<span style="color: {self.log_colors["timestamp"]};">[{timestamp}]</span>'

        if "✓" in message or "成功" in message:
            color = self.log_colors['success']
            styled_message = f'<span style="color: {color};">{message}</span>'
        elif "✗" in message or "失败" in message or "错误" in message:
            color = self.log_colors['error']
            styled_message = f'<span style="color: {color};">{message}</span>'
        else:
            styled_message = message.replace('<', '&lt;').replace('>', '&gt;')

        self.log_textedit.append(f"{timestamp_html} {styled_message}")
        QApplication.processEvents()

    def load_settings(self):
        self.username_input.setText(self.settings.value("username", ""))
        self.password_input.setText(self.settings.value("password", ""))
        self.ip_textedit.setText(self.settings.value("ip_list", ""))
        self.duration_input.setText(self.settings.value("duration", "24"))

    def save_settings(self, checked=False, show_popup=True):
        self.settings.setValue("username", self.username_input.text())
        self.settings.setValue("password", self.password_input.text())
        self.settings.setValue("ip_list", self.ip_textedit.toPlainText())
        self.settings.setValue("duration", self.duration_input.text())
        self.settings.sync()
        if self.settings.status() != QSettings.Status.NoError:
            self.log_message("设置保存失败")
            if show_popup:
                QMessageBox.warning(self, "错误", "设置保存失败，请重试。")
            return
        if show_popup:
            self.log_message("设置已保存")
            QMessageBox.information(self, "成功", "设置已保存！")

    def clear_inputs(self):
        if QMessageBox.question(self, "确认", "确定要清空所有输入内容吗？") == QMessageBox.StandardButton.Yes:
            self.username_input.clear()
            self.password_input.clear()
            self.ip_textedit.clear()
            self.duration_input.setText("24")
            self.log_message("已清空输入内容")

    def load_default_ips(self):
        default_ips = "10.186.34.100\n10.180.139.97\n10.180.139.98\n10.185.224.41\n10.185.232.21\n10.185.232.22\n10.185.232.23\n10.185.236.61\n10.185.236.62\n10.185.238.84\n10.185.238.85"
        self.ip_textedit.setText(default_ips)
        self.log_message("已加载默认IP列表")

    def parse_ips(self):
        return [line.strip() for line in self.ip_textedit.toPlainText().strip().split('\n') if line.strip()]

    def submit_application(self):
        username = self.username_input.text().strip()
        password = self.password_input.text().strip()
        ips = self.parse_ips()
        duration_str = self.duration_input.text().strip()

        if not all([username, password, ips, duration_str]):
            QMessageBox.warning(self, "警告", "请填写所有必填项（用户名、密码、IP、时长）！")
            return
        if not duration_str.isdigit() or int(duration_str) <= 0:
            QMessageBox.warning(self, "警告", "请输入有效的上网时长（正整数）！")
            return
        
        preview_ips = "\n".join(ips[:5]) + ("\n..." if len(ips) > 5 else "")
        if QMessageBox.question(self, "确认提交", f"确定要为以下 {len(ips)} 个IP地址申请上网吗？\n\n{preview_ips}") != QMessageBox.StandardButton.Yes:
            self.log_message("用户取消提交")
            return

        self.submit_btn.setEnabled(False)
        self.log_message(f"开始提交申请，共 {len(ips)} 个IP地址")

        try:
            results = apply_for_internet_access(username, password, ips, int(duration_str), logger=self.log_message)
            success_count = sum(1 for r in results if r["success"])
            fail_count = len(ips) - success_count
            self.log_message(f"申请完成！成功: {success_count}, 失败: {fail_count}")

            if success_count > 0:
                self.save_settings(show_popup=False)
                self.log_message("设置已自动保存。")

            if fail_count == 0:
                QMessageBox.information(self, "成功", f"所有 {len(ips)} 个IP地址申请已成功提交！")
            else:
                QMessageBox.warning(self, "部分成功", f"申请完成！\n成功: {success_count} 个\n失败: {fail_count} 个\n\n请查看日志了解详情。")
        except Exception as e:
            self.log_message(f"提交过程中发生严重错误: {e}")
            QMessageBox.critical(self, "错误", f"提交过程中发生严重错误: {e}")
        finally:
            self.submit_btn.setEnabled(True)


# --- CLI Runner ---

def run_cli(args):
    """Executes the application in Command-Line Interface mode."""
    settings = QSettings("Hillstone", "InternetAccessApp")
    
    username = args.username or settings.value("username", "")
    if not username:
        try:
            username = input("请输入用户名: ")
        except EOFError:
            print("\n用户名未提供，退出。", file=sys.stderr)
            sys.exit(1)

    password = args.password or settings.value("password", "")
    if not password:
        try:
            password = getpass.getpass("请输入密码 (输入内容不会显示): ")
        except EOFError:
            print("\n密码未提供，退出。", file=sys.stderr)
            sys.exit(1)
            
    if not args.ips:
        print("错误：未提供任何IP地址。", file=sys.stderr)
        print("用法: python apply_for_internet_access.py <ip1> <ip2> ... [--username ...] [--password ...]", file=sys.stderr)
        sys.exit(1)

    print(f"准备为 {len(args.ips)} 个IP地址申请 {args.hours} 小时上网...")
    try:
        results = apply_for_internet_access(username, password, args.ips, args.hours, logger=print)
        success_count = sum(1 for r in results if r["success"])
        fail_count = len(args.ips) - success_count

        print(f"\n申请完成！成功: {success_count}, 失败: {fail_count}")
        if fail_count > 0:
            print("\n--- 失败详情 ---")
            for r in results:
                if not r['success']:
                    print(f"- IP: {r['ip']}\n  原因: {r['message']}")
            print("-----------------")
            sys.exit(1)
            
    except Exception as e:
        print(f"\n发生严重错误: {e}", file=sys.stderr)
        sys.exit(1)

# --- Main Entry Point ---

def main():
    """Main entry point for the script."""
    parser = argparse.ArgumentParser(
        description="测试环境上网申请工具。默认启动GUI，提供IP参数时执行CLI。",
        formatter_class=argparse.RawTextHelpFormatter
    )
    parser.add_argument('ips', nargs='*', help="要申请上网的IP地址列表（CLI模式）。")
    parser.add_argument('--username', help="登录用户名（CLI模式）。如果未提供，则尝试从已保存的设置中读取或提示输入。")
    parser.add_argument('--password', help="登录密码（CLI模式）。如果未提供，则尝试从已保存的设置中读取或提示输入。")
    parser.add_argument('--hours', type=int, default=24, help="上网时长（小时），默认为24（CLI模式）。")
    parser.add_argument('--gui', action='store_true', help="强制启动GUI界面。")

    args = parser.parse_args()

    # Launch GUI if --gui is specified or if no IPs are given in CLI mode
    if args.gui or not args.ips:
        app = QApplication(sys.argv)
        app.setApplicationName("测试环境上网申请工具")
        window = InternetAccessApp()
        window.show()
        sys.exit(app.exec())
    else:
        run_cli(args)


if __name__ == '__main__':
    main()
