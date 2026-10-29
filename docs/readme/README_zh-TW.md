<div align="center">

[English](README_en.md) · [简体中文](README_zh.md) · **繁體中文** · [日本語](README_ja.md) · [한국어](README_ko.md) · [Türkçe](README_tr.md) · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · [Deutsch](README_de.md) · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

適用於 **Android** 的自動化工具，概念類似 Apple 的「捷徑」，但比三星的「模式與例行程序」更強大：**一個觸發條件**（藍牙裝置
已連線、Wi-Fi、充電器、時間、應用程式已開啟、手機已展開……）、可選的**條件**，接著是**一連串動作**（設定、應用程式、音樂、
指令……），還有在適當時機還原一切的「結束時」動作。

應用程式內建的範例：手機透過藍牙連上汽車時，Flare 會開啟零打擾、調整音量、在 [Opus](https://github.com/apkangelo/opus)
中播放音樂並開啟地圖；中斷連線時，會暫停音樂並關閉零打擾。

專為執行 One UI 的 Galaxy Z Fold 設計：支援「手機摺疊 / 展開」觸發條件，並且透過 Shizuku 不需要 root（三星已不再允許解鎖
Bootloader）。

## 下載

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)**（Android 8 或更新版本）。開啟
檔案，允許從此來源安裝，然後輕觸*安裝*。更新會直接覆蓋安裝，不會遺失你的自動化。所有版本都在
[*Releases*](https://github.com/apkangelo/Flare/releases) 分頁中，每次修改後由 GitHub Actions 自動編譯。

## 螢幕截圖

| 自動化 | 編輯器 | 記錄 | 權限 |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## Flare 能做什麼

**觸發條件（27 個）**：藍牙裝置連線 / 中斷連線（輸入部分名稱即可：「Media Nav」可辨識「MEDIA-NAV」）、Wi-Fi 連線 / 中斷、
飛航模式、接上 / 拔除充電器、電池電量、手機開機、螢幕開啟 / 關閉、解鎖、**手機摺疊 / 展開**、接上 / 拔除耳機、指定時間和
星期、每隔 X 分鐘、應用程式開啟 / 關閉、收到通知（應用程式、文字）、來電 / 通話開始 / 通話結束、收到簡訊、抵達 / 離開某地。

**條件（11 個）**：時段、星期幾、電量、充電中、已連線的藍牙裝置、Wi-Fi 網路、摺疊 / 展開、耳機、螢幕開啟、鈴聲模式、
畫面上的應用程式。

**動作（40 個）**
- *連線*：Wi-Fi、行動數據、藍牙、飛航模式、NFC、位置資訊（開啟、關閉、切換）。
- *螢幕*：亮度、螢幕逾時、自動旋轉、深色模式、充電時保持螢幕開啟、省電模式、手電筒、鎖定、開啟螢幕、擷取畫面。
- *音效和媒體*：鈴聲模式、零打擾、各類音量、播放 / 暫停 / 下一首、在指定應用程式中播放音樂、朗讀文字、震動。
- *應用程式*：開啟應用程式、連結或深層連結（`opus://flow`、`tel:`、`geo:`……）、地圖導航、強制停止、開啟設定頁面、傳送
  intent。
- *螢幕控制*：系統按鈕（返回、主畫面、最近使用、通知面板、快速設定、電源選單、分割畫面）、輕觸畫面上的文字、清除通知。
- *通話和簡訊*：傳送簡訊、撥打電話。
- *系統*：通知、**shell 指令**、**任何系統設定**（`system`、`secure`、`global`），也涵蓋 One UI 專屬的設定。
- *流程*：等待、執行捷徑、開啟 / 關閉某個自動化。

**捷徑**：沒有觸發條件的動作清單，可在應用程式內、**主畫面**或 **3 個快速設定圖塊**之一中一鍵執行。

記錄所有執行情形的**記錄**（含每個動作的結果）、JSON **匯出 / 匯入**、介面支援 **23 種語言**。

## 權限設定步驟

Flare 從不需要 root。每個動作都會顯示缺少的權限，**權限**分頁則集中管理所有權限。

1. **開發人員選項**：設定 › 關於手機 › 軟體資訊，連續輕觸 7 次*版本號碼*。
2. **Shizuku**：安裝 [Shizuku](https://shizuku.rikka.app/download/)，在開發人員選項中開啟*無線偵錯*（需連上 Wi-Fi），
   接著在 Shizuku 中：*透過無線偵錯啟動* › *配對*，並在通知中輸入配對碼。
3. 在 Flare › **權限**中：*Shizuku › 授予*，然後輕觸**全部授予**。Flare 會自動為自己授予永久系統權限、無障礙、通知存取權、
   零打擾、顯示在其他應用程式上層、不限制電池以及 Android 權限。
4. 三星手機：設定 › 應用程式 › Flare › 電池 › **不受限制**。

重新啟動後，需再次啟動 Shizuku（第 2 步，不必重新配對）才能使用 Wi-Fi、行動數據、藍牙、飛航模式、深色模式和指令類動作。
其他功能則靠永久系統權限繼續運作。

沒有 Shizuku 時，可在電腦上執行：

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

如果無障礙或通知存取權顯示為灰色（非從 Play 商店安裝的應用程式）：應用程式資訊 › ⋮ › *允許受限制的設定*。

## 搭配 Opus

「車用模式」範例會開啟 `opus://flow`，啟動 [Opus](https://github.com/apkangelo/opus) 的 Flow。*在應用程式中播放音樂*
動作也適用於任何播放器（Spotify、YouTube Music、Deezer……）：透過應用程式的媒體工作階段，或傳送「播放」鍵給它。

## 自行編譯

需要 JDK 21 和 Android SDK（或 Android Studio）。

```sh
python3 scripts/i18n.py        # 從 i18n/*.json 產生 Android 文字資源
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

每次修改後，GitHub Actions 會檢查 23 種翻譯、編譯並發布 APK。另一個 workflow 會在 Android 15 模擬器中執行應用程式，模擬
接上充電器以確認引擎會回應，並產生上方的截圖。

## APK 簽署

所有版本都使用**同一把金鑰**（`signing/flare.jks`）簽署，讓更新可以直接覆蓋安裝。這把金鑰在存放區中公開：請只從本存放區
安裝 Flare。如要使用私人金鑰，請在 *Settings › Secrets › Actions* 中新增 `FLARE_KEYSTORE_B64`（base64 格式的 .jks
檔案）、`FLARE_KEYSTORE_PASSWORD`、`FLARE_KEY_ALIAS` 和 `FLARE_KEY_PASSWORD`。

## 注意事項

- 自動化、記錄和設定都只存在**手機上**。Flare 沒有網路存取權。
- shell 指令以 Shizuku 的 ADB 權限執行：只貼上你看得懂的指令。
- 讀取 Wi-Fi 網路名稱需要位置權限（Android 的規定）。

## 存放區結構

| 資料夾 | 內容 |
|---|---|
| `android/` | 應用程式（Kotlin、Jetpack Compose）：引擎在 `core/`，服務在 `service/`，畫面在 `ui/` |
| `i18n/` | 23 種語言的介面文字 |
| `scripts/` | 文字產生、模擬器測試、編譯輔助 |
| `docs/` | 截圖和翻譯後的 README |
| `signing/` | APK 簽署金鑰 |

## apk-fold 的其他應用程式

- [Haiku](https://github.com/apkangelo/haiku)：離線密碼管理工具
- [Opus](https://github.com/apkangelo/opus)：Navidrome 音樂播放器
- [Sonnet](https://github.com/apkangelo/sonnet)：Jellyfin 用戶端
- [Mythos](https://github.com/apkangelo/mythos)：免伺服器同步的辦公室套件
- [Fable](https://github.com/apkangelo/fable)：工時記錄
- [Cyber](https://github.com/apkangelo/Cyber)：SSH 終端機和本機 shell
