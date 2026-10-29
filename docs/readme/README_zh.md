<div align="center">

[English](README_en.md) · **简体中文** · [繁體中文](README_zh-TW.md) · [日本語](README_ja.md) · [한국어](README_ko.md) · [Türkçe](README_tr.md) · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · [Deutsch](README_de.md) · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

适用于 **Android** 的自动化工具，思路类似 Apple 的“快捷指令”，但比三星的“模式和例程”更强大：**一个触发条件**（蓝牙设备
已连接、Wi-Fi、充电器、时间、应用已打开、手机已展开……）、可选的**条件**，然后是**一系列操作**（设置、应用、音乐、命令……），
还有在合适时机撤销一切的“结束时”操作。

应用自带的示例：手机通过蓝牙连接汽车时，Flare 会开启勿扰模式、调整音量、在 [Opus](https://github.com/apkangelo/opus)
中播放音乐并打开地图；断开连接时，它会暂停音乐并关闭勿扰模式。

专为运行 One UI 的 Galaxy Z Fold 打造：支持“手机折叠 / 展开”触发条件，并且借助 Shizuku 无需 root（三星已不再允许解锁
Bootloader）。

## 下载

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)**（Android 8 或更高版本）。打开
文件，允许从此来源安装，然后点按*安装*。更新会直接覆盖安装，不会丢失你的自动化。所有版本都在
[*Releases*](https://github.com/apkangelo/Flare/releases) 标签页中，每次修改后由 GitHub Actions 自动编译。

## 截图

| 自动化 | 编辑器 | 日志 | 权限 |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## Flare 能做什么

**触发条件（27 个）**：蓝牙设备连接 / 断开（输入部分名称即可：“Media Nav”能识别“MEDIA-NAV”）、Wi-Fi 连接 / 断开、
飞行模式、接入 / 拔下充电器、电池电量、手机开机、屏幕点亮 / 关闭、解锁、**手机折叠 / 展开**、插入 / 拔出耳机、指定时间和
星期、每隔 X 分钟、应用打开 / 关闭、收到通知（应用、文字）、来电 / 通话开始 / 通话结束、收到短信、到达 / 离开某地。

**条件（11 个）**：时间段、星期几、电量、充电中、蓝牙设备已连接、Wi-Fi 网络、折叠 / 展开、耳机、屏幕点亮、响铃模式、
屏幕上的应用。

**操作（40 个）**
- *连接*：Wi-Fi、移动数据、蓝牙、飞行模式、NFC、位置信息（开启、关闭、切换）。
- *屏幕*：亮度、屏幕超时、自动旋转、深色模式、充电时保持亮屏、省电模式、手电筒、锁屏、点亮屏幕、截屏。
- *声音和媒体*：响铃模式、勿扰、各类音量、播放 / 暂停 / 下一首、在指定应用中播放音乐、朗读文字、振动。
- *应用*：打开应用、链接或深层链接（`opus://flow`、`tel:`、`geo:`……）、地图导航、强行停止、打开设置页面、发送 intent。
- *屏幕控制*：系统按钮（返回、主屏幕、最近应用、通知面板、快捷设置、电源菜单、分屏）、点按屏幕上的文字、清除通知。
- *通话和短信*：发送短信、拨打电话。
- *系统*：通知、**shell 命令**、**任意系统设置**（`system`、`secure`、`global`），也包括 One UI 特有的设置。
- *流程*：等待、运行快捷方式、开启 / 关闭某个自动化。

**快捷方式**：没有触发条件的操作列表，可在应用内、**主屏幕**或 **3 个快捷设置图块**之一中一键运行。

记录所有运行情况的**日志**（含每个操作的结果）、JSON **导出 / 导入**、界面支持 **23 种语言**。

## 权限设置步骤

Flare 从不需要 root。每个操作都会显示缺少的权限，**权限**标签页集中管理所有权限。

1. **开发者选项**：设置 › 关于手机 › 软件信息，连续点按 7 次*编译编号*。
2. **Shizuku**：安装 [Shizuku](https://shizuku.rikka.app/download/)，在开发者选项中开启*无线调试*（需连接 Wi-Fi），
   然后在 Shizuku 中：*通过无线调试启动* › *配对*，并在通知中输入配对码。
3. 在 Flare › **权限**中：*Shizuku › 授予*，然后点按**全部授予**。Flare 会自动为自己授予永久系统权限、无障碍、通知使用权、
   勿扰、显示在其他应用上层、不限制电池以及 Android 权限。
4. 三星手机：设置 › 应用程序 › Flare › 电池 › **不受限制**。

重启后，需要重新启动 Shizuku（第 2 步，无需重新配对）才能使用 Wi-Fi、移动数据、蓝牙、飞行模式、深色模式和命令类操作。
其他功能依靠永久系统权限继续工作。

没有 Shizuku 时，可在电脑上运行：

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

如果无障碍或通知使用权显示为灰色（非 Play 商店安装的应用）：应用信息 › ⋮ › *允许受限制的设置*。

## 搭配 Opus

“车载模式”示例会打开 `opus://flow`，启动 [Opus](https://github.com/apkangelo/opus) 的 Flow。*在应用中播放音乐*操作
也适用于任何播放器（Spotify、YouTube Music、Deezer……）：通过应用的媒体会话，或向它发送“播放”键。

## 自行编译

需要 JDK 21 和 Android SDK（或 Android Studio）。

```sh
python3 scripts/i18n.py        # 根据 i18n/*.json 生成 Android 文字资源
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

每次修改后，GitHub Actions 会检查 23 种翻译、编译并发布 APK。另一个 workflow 会在 Android 15 模拟器中运行应用，模拟接入
充电器以确认引擎会响应，并生成上面的截图。

## APK 签名

所有版本都使用**同一个密钥**（`signing/flare.jks`）签名，这样更新可以直接覆盖安装。此密钥在仓库中公开：请只从本仓库安装
Flare。如需使用私有密钥，请在 *Settings › Secrets › Actions* 中添加 `FLARE_KEYSTORE_B64`（base64 格式的 .jks 文件）、
`FLARE_KEYSTORE_PASSWORD`、`FLARE_KEY_ALIAS` 和 `FLARE_KEY_PASSWORD`。

## 注意事项

- 自动化、日志和设置都只保存在**手机上**。Flare 没有网络访问权限。
- shell 命令以 Shizuku 的 ADB 权限运行：只粘贴你理解的命令。
- 读取 Wi-Fi 网络名称需要位置权限（Android 的规定）。

## 仓库结构

| 文件夹 | 内容 |
|---|---|
| `android/` | 应用（Kotlin、Jetpack Compose）：引擎在 `core/`，服务在 `service/`，界面在 `ui/` |
| `i18n/` | 23 种语言的界面文字 |
| `scripts/` | 文字生成、模拟器测试、编译辅助 |
| `docs/` | 截图和翻译后的 README |
| `signing/` | APK 签名密钥 |

## apk-fold 的其他应用

- [Haiku](https://github.com/apkangelo/haiku)：离线密码管理器
- [Opus](https://github.com/apkangelo/opus)：Navidrome 音乐播放器
- [Sonnet](https://github.com/apkangelo/sonnet)：Jellyfin 客户端
- [Mythos](https://github.com/apkangelo/mythos)：无需服务器即可同步的办公套件
- [Fable](https://github.com/apkangelo/fable)：工时记录
- [Cyber](https://github.com/apkangelo/Cyber)：SSH 终端和本地 shell
