<div align="center">

[English](README_en.md) · [简体中文](README_zh.md) · [繁體中文](README_zh-TW.md) · **日本語** · [한국어](README_ko.md) · [Türkçe](README_tr.md) · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · [Deutsch](README_de.md) · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

**Android** 向けのオートメーションアプリ。Apple の「ショートカット」と同じ発想で、Samsung の「モードとルーチン」よりも
高度です。**トリガー**（Bluetooth デバイスの接続、Wi-Fi、充電器、時刻、アプリの起動、スマホを開いたとき…）、任意の
**条件**、そして**一連のアクション**（設定、アプリ、音楽、コマンド…）に加え、適切なタイミングで元に戻す「終了したとき」の
アクションも設定できます。

アプリに同梱の例：スマホが車と Bluetooth でつながると、Flare はサイレントモードをオンにし、音量を調整し、
[Opus](https://github.com/apkangelo/opus) で音楽を再生してマップを開きます。切断されると、音楽を一時停止してサイレント
モードをオフにします。

One UI の Galaxy Z Fold 向けに作られています。「スマホを折りたたんだ / 開いた」トリガーがあり、Shizuku のおかげで root は
不要です（Samsung はブートローダーのロック解除をもう許可していません）。

## ダウンロード

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)**（Android 8 以降）。ファイルを
開き、このソースからのインストールを許可して*インストール*をタップします。アップデートは上書きインストールされ、
オートメーションは消えません。すべてのバージョンは [*Releases*](https://github.com/apkangelo/Flare/releases) タブにあり、
変更のたびに GitHub Actions でビルドされます。

## スクリーンショット

| オートメーション | エディタ | ログ | 権限 |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## Flare でできること

**トリガー（27 種）**：Bluetooth デバイスの接続 / 切断（名前の一部で OK。「Media Nav」で「MEDIA-NAV」も認識）、Wi-Fi の
接続 / 切断、機内モード、充電器の接続 / 取り外し、バッテリー残量、スマホの起動、画面オン / オフ、ロック解除、**スマホを
折りたたんだ / 開いた**、イヤホンの接続 / 取り外し、決まった時刻と曜日、X 分ごと、アプリを開いた / 閉じた、通知の受信
（アプリ、テキスト）、着信 / 通話開始 / 通話終了、SMS の受信、場所への到着 / 出発。

**条件（11 種）**：時間帯、曜日、バッテリー、充電中、Bluetooth デバイス接続中、Wi-Fi ネットワーク、折りたたみ / 開いた
状態、イヤホン、画面オン、着信音モード、画面上のアプリ。

**アクション（40 種）**
- *接続*：Wi-Fi、モバイルデータ、Bluetooth、機内モード、NFC、位置情報（オン、オフ、切り替え）。
- *画面*：明るさ、画面のタイムアウト、自動回転、ダークモード、充電中は画面オン、省電力、ライト、ロック、画面をオン、
  スクリーンショット。
- *サウンドとメディア*：着信音モード、サイレントモード、各音量、再生 / 一時停止 / 次へ、指定したアプリで音楽を再生、
  テキストの読み上げ、バイブレーション。
- *アプリ*：アプリ・リンク・ディープリンク（`opus://flow`、`tel:`、`geo:`…）を開く、マップでナビ、強制停止、設定ページを
  開く、インテントの送信。
- *画面の操作*：システムボタン（戻る、ホーム、履歴、通知パネル、クイック設定、電源メニュー、分割画面）、画面上の
  テキストをタップ、通知の消去。
- *通話と SMS*：SMS の送信、発信。
- *システム*：通知、**シェルコマンド**、**あらゆるシステム設定**（`system`、`secure`、`global`）。One UI 独自の設定にも
  対応します。
- *フロー*：待機、ショートカットの実行、オートメーションのオン / オフ。

**ショートカット**：トリガーなしのアクションリスト。アプリ、**ホーム画面**、**3 つのクイック設定タイル**のいずれかから
ワンタップで実行できます。

実行したすべての**ログ**（各アクションの結果つき）、JSON での**エクスポート / インポート**、**23 言語**のインターフェース。

## 権限の設定手順

Flare は root を一切必要としません。各アクションには不足している権限が表示され、**権限**タブですべてを管理できます。

1. **開発者向けオプション**：設定 › 端末情報 › ソフトウェア情報で、*ビルド番号*を 7 回タップします。
2. **Shizuku**：[Shizuku](https://shizuku.rikka.app/download/) をインストールし、開発者向けオプションで*ワイヤレス
   デバッグ*をオンにします（Wi-Fi 接続時）。次に Shizuku で *ワイヤレスデバッグで開始* › *ペア設定* を選び、通知に
   ペア設定コードを入力します。
3. Flare › **権限**で *Shizuku › 許可* をタップし、続けて**すべて許可**をタップします。Flare が永続的なシステム権限、
   ユーザー補助、通知へのアクセス、サイレントモード、他のアプリの上に重ねて表示、バッテリー制限なし、Android の各権限を
   自分で許可します。
4. Samsung の場合：設定 › アプリ › Flare › バッテリー › **制限なし**。

再起動後は、Wi-Fi、モバイルデータ、Bluetooth、機内モード、ダークモード、コマンドのアクションのために Shizuku を再度
起動する必要があります（手順 2。ペア設定のやり直しは不要）。それ以外は永続的なシステム権限のおかげで動き続けます。

Shizuku を使わない場合は、パソコンから：

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

ユーザー補助や通知へのアクセスがグレー表示の場合（Play ストア以外からインストールしたアプリ）：アプリ情報 › ⋮ ›
*制限付き設定を許可*。

## Opus と一緒に

「車モード」の例は `opus://flow` を開き、[Opus](https://github.com/apkangelo/opus) の Flow を開始します。
*アプリで音楽を再生*アクションは、ほかのプレーヤー（Spotify、YouTube Music、Deezer…）でも使えます。アプリのメディア
セッションを利用するか、「再生」キーを送ります。

## 自分でビルドする

JDK 21 と Android SDK（または Android Studio）が必要です。

```sh
python3 scripts/i18n.py        # i18n/*.json から Android の文字列を生成
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

変更のたびに GitHub Actions が 23 言語の翻訳をチェックし、APK をビルドして公開します。別の workflow が Android 15 の
エミュレーターでアプリを起動し、充電器の接続をシミュレートしてエンジンが反応するか確認し、上のスクリーンショットを
撮ります。

## APK の署名

すべてのバージョンは**同じ鍵**（`signing/flare.jks`）で署名されているため、アップデートを上書きインストールできます。
この鍵はリポジトリで公開されています。Flare はこのリポジトリからのみインストールしてください。独自の非公開鍵を使うには、
*Settings › Secrets › Actions* に `FLARE_KEYSTORE_B64`（base64 化した .jks ファイル）、`FLARE_KEYSTORE_PASSWORD`、
`FLARE_KEY_ALIAS`、`FLARE_KEY_PASSWORD` を追加します。

## 知っておきたいこと

- オートメーション、ログ、設定はすべて**スマホの中**に保存されます。Flare はインターネットにアクセスしません。
- シェルコマンドは Shizuku の ADB 権限で実行されます。内容を理解しているコマンドだけを貼り付けてください。
- Wi-Fi ネットワーク名は位置情報の権限がないと読み取れません（Android の仕様）。

## リポジトリの構成

| フォルダ | 内容 |
|---|---|
| `android/` | アプリ（Kotlin、Jetpack Compose）：エンジンは `core/`、サービスは `service/`、画面は `ui/` |
| `i18n/` | 23 言語のインターフェース文字列 |
| `scripts/` | 文字列の生成、エミュレーターテスト、ビルド補助 |
| `docs/` | スクリーンショットと翻訳した README |
| `signing/` | APK の署名鍵 |

## apk-fold のほかのアプリ

- [Haiku](https://github.com/apkangelo/haiku)：オフラインのパスワードマネージャー
- [Opus](https://github.com/apkangelo/opus)：Navidrome 用の音楽プレーヤー
- [Sonnet](https://github.com/apkangelo/sonnet)：Jellyfin クライアント
- [Mythos](https://github.com/apkangelo/mythos)：サーバーなしで同期できるオフィススイート
- [Fable](https://github.com/apkangelo/fable)：勤務時間の記録
- [Cyber](https://github.com/apkangelo/Cyber)：SSH ターミナルとローカルシェル
