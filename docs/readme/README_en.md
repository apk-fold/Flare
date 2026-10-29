<div align="center">

**English** · [简体中文](README_zh.md) · [繁體中文](README_zh-TW.md) · [日本語](README_ja.md) · [한국어](README_ko.md) · [Türkçe](README_tr.md) · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · [Deutsch](README_de.md) · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

Automations for **Android**, in the spirit of Apple's Shortcuts app but going further than Samsung's "Modes and
Routines": **a trigger** (Bluetooth device connected, Wi-Fi, charger, time, app opened, phone unfolded…), optional
**conditions**, then **a series of actions** (settings, apps, music, commands…), plus "when it stops" actions that
undo everything at the right moment.

Example shipped with the app: when the phone connects to the car over Bluetooth, Flare turns on Do Not Disturb, sets
the volume, starts the music in [Opus](https://github.com/apkangelo/opus) and opens Maps; when it disconnects, it
pauses the music and turns Do Not Disturb off.

Built for the Galaxy Z Fold on One UI: "phone folded / unfolded" triggers, and no root needed (Samsung no longer lets
you unlock the bootloader) thanks to Shizuku.

## Download

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)** (Android 8 or newer). Open the
file, allow installs from this source, then *Install*. Updates install over the previous version without losing your
automations. Every version is in the [*Releases*](https://github.com/apkangelo/Flare/releases) tab, built by GitHub
Actions on every change.

## Screenshots

| Automations | Editor | Log | Access |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## What Flare does

**Triggers (27)**: Bluetooth device connected / disconnected (part of the name is enough: "Media Nav" matches
"MEDIA-NAV"), Wi-Fi connected / disconnected, airplane mode, charger plugged in / unplugged, battery level, phone
started, screen on / off, unlocked, **phone folded / unfolded**, headphones plugged in / unplugged, set time and days,
every X minutes, app opened / closed, notification received (app, text), incoming / started / ended call, SMS
received, arriving at / leaving a place.

**Conditions (11)**: time range, days of the week, battery, charging, Bluetooth device connected, Wi-Fi network,
folded / unfolded, headphones, screen on, ringer mode, app on screen.

**Actions (40)**
- *Connections*: Wi-Fi, mobile data, Bluetooth, airplane mode, NFC, location (on, off, switch).
- *Screen*: brightness, screen timeout, auto-rotate, dark mode, stay awake while charging, power saving, flashlight,
  lock, wake the screen, screenshot.
- *Sound and media*: ringer mode, Do Not Disturb, volume of each stream, play / pause / next, start music in a given
  app, read a text aloud, vibrate.
- *Apps*: open an app, a link or a deep link (`opus://flow`, `tel:`, `geo:`…), Maps navigation, force stop, open a
  settings page, send an intent.
- *Screen control*: system buttons (back, home, recents, notification panel, quick settings, power menu, split screen),
  tap a text on screen, clear notifications.
- *Calls and SMS*: send an SMS, call.
- *System*: notification, **shell command**, **any system setting** (`system`, `secure`, `global`), which also covers
  One UI's own settings.
- *Flow*: wait, run a shortcut, turn an automation on / off.

**Shortcuts**: action lists without a trigger, started with one tap from the app, from the **home screen** or from one
of the **3 quick settings tiles**.

A **log** of everything that ran (with the result of each action), JSON **export / import**, interface in
**23 languages**.

## Permissions, step by step

Flare never needs root. Each action shows which access it's missing, and the **Access** tab gathers everything.

1. **Developer options**: Settings › About phone › Software information, tap *Build number* 7 times.
2. **Shizuku**: install [Shizuku](https://shizuku.rikka.app/download/), turn on *Wireless debugging* in developer
   options (on Wi-Fi), then in Shizuku: *Start via Wireless debugging* › *Pairing*, and type the pairing code in the
   notification.
3. In Flare › **Access**: *Shizuku › Grant*, then **Grant everything**. Flare grants itself the permanent system
   rights, accessibility, notification access, Do Not Disturb, display over other apps, no battery restrictions and the
   Android permissions.
4. On Samsung: Settings › Apps › Flare › Battery › **Unrestricted**.

After a restart, Shizuku must be started again (step 2, without pairing again) for the Wi-Fi, mobile data, Bluetooth,
airplane mode, dark mode and command actions. Everything else keeps working thanks to the permanent system rights.

Without Shizuku, from a computer:

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

If accessibility or notification access is greyed out (app installed outside the Play Store): App info › ⋮ ›
*Allow restricted settings*.

## With Opus

The "Car mode" example opens `opus://flow`, which starts the Flow in [Opus](https://github.com/apkangelo/opus). The
*Play music in an app* action also works with any player (Spotify, YouTube Music, Deezer…): it goes through the app's
media session, or sends it the "play" key.

## Build it yourself

You need JDK 21 and the Android SDK (or Android Studio).

```sh
python3 scripts/i18n.py        # regenerates the Android strings from i18n/*.json
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

On every change, GitHub Actions checks the 23 translations, builds and publishes the APK. A second workflow runs the
app in an Android 15 emulator, simulates plugging in the charger to check that the engine reacts, and takes the
screenshots above.

## APK signing

Every version is signed with **the same key** (`signing/flare.jks`) so updates install over the previous one. This key
is public in the repository: only install Flare from this repository. To use a private key, add the secrets
`FLARE_KEYSTORE_B64` (the .jks file in base64), `FLARE_KEYSTORE_PASSWORD`, `FLARE_KEY_ALIAS` and `FLARE_KEY_PASSWORD`
in *Settings › Secrets › Actions*.

## Good to know

- Automations, log and settings stay **on the phone**. Flare has no Internet access.
- Shell commands run with Shizuku's ADB rights: only paste commands you understand.
- The Wi-Fi network name can only be read with the location permission (an Android rule).

## Repository layout

| Folder | Contents |
|---|---|
| `android/` | The app (Kotlin, Jetpack Compose): engine in `core/`, services in `service/`, screens in `ui/` |
| `i18n/` | Interface texts in the 23 languages |
| `scripts/` | String generation, emulator test, build helper |
| `docs/` | Screenshots and translated READMEs |
| `signing/` | APK signing key |

## Other apk-fold apps

- [Haiku](https://github.com/apkangelo/haiku): offline password manager
- [Opus](https://github.com/apkangelo/opus): music player for Navidrome
- [Sonnet](https://github.com/apkangelo/sonnet): Jellyfin client
- [Mythos](https://github.com/apkangelo/mythos): office suite that syncs without a server
- [Fable](https://github.com/apkangelo/fable): work hours tracker
- [Cyber](https://github.com/apkangelo/Cyber): SSH terminal and local shell
