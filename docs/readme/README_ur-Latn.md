<div align="center">

[English](README_en.md) · [简体中文](README_zh.md) · [繁體中文](README_zh-TW.md) · [日本語](README_ja.md) · [한국어](README_ko.md) · [Türkçe](README_tr.md) · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · [Deutsch](README_de.md) · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · **Roman Urdu** · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

**Android** ke liye automations, Apple ki Shortcuts app jaisi soch ke saath, lekin Samsung ke "Modes and Routines" se
kahin zyada taqatwar: **aik trigger** (Bluetooth device connect, Wi-Fi, charger, waqt, app khuli, phone khola…),
ikhtiyari **sharait**, phir **actions ka aik silsila** (settings, apps, music, commands…), aur "jab khatam ho" wale
actions jo sahi waqt par sab kuch wapas kar dete hain.

App ke saath aane wali misal: jab phone Bluetooth se gaari se connect hota hai, Flare Do Not Disturb on karta hai, awaaz
set karta hai, [Opus](https://github.com/apkangelo/opus) mein music chalata hai aur Maps kholta hai; disconnect hone par
music pause kar ke Do Not Disturb band kar deta hai.

One UI wale Galaxy Z Fold ke liye banaya gaya: "phone fold / khola" triggers, aur Shizuku ki badaulat root ki zaroorat
nahin (Samsung ab bootloader unlock karne ki ijazat nahin deta).

## Download

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)** (Android 8 ya naya). File
kholein, is source se install ki ijazat dein, phir *Install*. Updates purane version ke upar install hoti hain aur aap ki
automations zaya nahin hotin. Tamam versions [*Releases*](https://github.com/apkangelo/Flare/releases) tab mein hain, jo
har tabdeeli par GitHub Actions banata hai.

## Screenshots

| Automations | Editor | Log | Ijazatein |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## Flare kya karta hai

**Triggers (27)**: Bluetooth device connect / disconnect (naam ka kuch hissa kaafi hai: "Media Nav" "MEDIA-NAV" ko
pehchan leta hai), Wi-Fi connect / disconnect, airplane mode, charger laga / nikla, battery level, phone on hua, screen
on / off, unlock, **phone fold / khola**, headphones lage / nikle, muqarrara waqt aur din, har X minute, app khuli /
band, notification mili (app, text), aane wali call / call shuru / call khatam, SMS mila, kisi jagah pohanchna / jana.

**Sharait (11)**: waqt ka daira, hafte ke din, battery, charge ho raha hai, Bluetooth device connected, Wi-Fi network,
fold / khula, headphones, screen on, ringer mode, screen par app.

**Actions (40)**
- *Connections*: Wi-Fi, mobile data, Bluetooth, airplane mode, NFC, location (on, off, ulat dein).
- *Screen*: brightness, screen timeout, rotation, dark mode, charging mein screen on, power saving, torch, lock, screen
  on, screenshot.
- *Awaaz aur media*: ringer mode, Do Not Disturb, har qism ki awaaz, play / pause / agla, kisi khaas app mein music
  chalana, text bol kar parhna, vibrate.
- *Apps*: app, link ya deep link kholna (`opus://flow`, `tel:`, `geo:`…), Maps navigation, force stop, settings ka page
  kholna, intent bhejna.
- *Screen control*: system buttons (back, home, recents, notification panel, quick settings, power menu, split screen),
  screen par text ko tap karna, notifications saaf karna.
- *Calls aur SMS*: SMS bhejna, call karna.
- *System*: notification, **shell command**, **koi bhi system setting** (`system`, `secure`, `global`), jis mein One UI
  ki apni settings bhi shamil hain.
- *Tarteeb*: intezar, shortcut chalana, automation on / off karna.

**Shortcuts**: baghair trigger ke actions ki fehrist, jo app se, **home screen** se ya **3 quick settings tiles** mein
se kisi aik se aik tap mein chalti hai.

Har chalne wali cheez ka **log** (har action ke nateeje ke saath), JSON mein **export / import**, aur **23 zubanon** mein
interface.

## Ijazatein, qadam ba qadam

Flare ko kabhi root nahin chahiye. Har action batata hai ke kaunsi ijazat kam hai, aur **Ijazatein** tab sab kuch aik
jagah dikhata hai.

1. **Developer options**: Settings › About phone › Software information, *Build number* par 7 baar tap karein.
2. **Shizuku**: [Shizuku](https://shizuku.rikka.app/download/) install karein, developer options mein *Wireless
   debugging* on karein (Wi-Fi par), phir Shizuku mein: *Start via Wireless debugging* › *Pairing*, aur notification
   mein pairing code likhein.
3. Flare › **Ijazatein** mein: *Shizuku › Dein*, phir **Sab dein**. Flare khud ko mustaqil system huqooq,
   accessibility, notification access, Do Not Disturb, doosri apps ke upar dikhana, battery ki koi pabandi na hona aur
   Android ki ijazatein de deta hai.
4. Samsung par: Settings › Apps › Flare › Battery › **Unrestricted**.

Restart ke baad Wi-Fi, mobile data, Bluetooth, airplane mode, dark mode aur commands wale actions ke liye Shizuku dobara
shuru karna hoga (qadam 2, dobara pairing ke baghair). Baqi sab mustaqil system huqooq ki wajah se chalta rehta hai.

Shizuku ke baghair, computer se:

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

Agar accessibility ya notification access grey hai (Play Store ke bahar se install ki gayi app): App info › ⋮ ›
*Allow restricted settings*.

## Opus ke saath

"Car mode" misal `opus://flow` kholti hai, jo [Opus](https://github.com/apkangelo/opus) mein Flow shuru karta hai.
*Kisi app mein music chalayein* action har player ke saath chalta hai (Spotify, YouTube Music, Deezer…): app ke media
session ke zariye, ya use "play" key bhej kar.

## Khud build karein

JDK 21 aur Android SDK (ya Android Studio) chahiye.

```sh
python3 scripts/i18n.py        # i18n/*.json se Android strings banata hai
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

Har tabdeeli par GitHub Actions 23 tarjume check karta hai, APK banata aur publish karta hai. Doosra workflow app ko
Android 15 emulator mein chalata hai, charger lagne ki naqal kar ke dekhta hai ke engine react karta hai, aur upar wale
screenshots leta hai.

## APK ka signature

Tamam versions **aik hi key** (`signing/flare.jks`) se sign hote hain taake updates purane version ke upar install hon.
Yeh key repository mein public hai: Flare sirf isi repository se install karein. Private key ke liye *Settings ›
Secrets › Actions* mein `FLARE_KEYSTORE_B64` (.jks file base64 mein), `FLARE_KEYSTORE_PASSWORD`, `FLARE_KEY_ALIAS` aur
`FLARE_KEY_PASSWORD` secrets shamil karein.

## Jaan lena acha hai

- Automations, log aur settings **phone par hi** rehti hain. Flare ko internet ki rasai nahin.
- Shell commands Shizuku ke ADB huqooq ke saath chalti hain: sirf wohi commands paste karein jo aap samajhte hain.
- Wi-Fi network ka naam sirf location ki ijazat se parha ja sakta hai (Android ka usool).

## Repository ki tarteeb

| Folder | Mawad |
|---|---|
| `android/` | App (Kotlin, Jetpack Compose): engine `core/` mein, services `service/` mein, screens `ui/` mein |
| `i18n/` | 23 zubanon mein interface ke text |
| `scripts/` | Strings banana, emulator test, build madad |
| `docs/` | Screenshots aur tarjuma shuda README |
| `signing/` | APK signing key |

## apk-fold ki doosri apps

- [Haiku](https://github.com/apkangelo/haiku): offline password manager
- [Opus](https://github.com/apkangelo/opus): Navidrome ke liye music player
- [Sonnet](https://github.com/apkangelo/sonnet): Jellyfin client
- [Mythos](https://github.com/apkangelo/mythos): baghair server sync hone wala office suite
- [Fable](https://github.com/apkangelo/fable): kaam ke ghante ka hisaab
- [Cyber](https://github.com/apkangelo/Cyber): SSH terminal aur local shell
