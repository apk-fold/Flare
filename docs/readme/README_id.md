<div align="center">

[English](README_en.md) · [简体中文](README_zh.md) · [繁體中文](README_zh-TW.md) · [日本語](README_ja.md) · [한국어](README_ko.md) · [Türkçe](README_tr.md) · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · [Deutsch](README_de.md) · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · **Bahasa Indonesia** · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

Otomatisasi untuk **Android**, terinspirasi aplikasi Pintasan dari Apple tetapi lebih lengkap daripada "Mode dan
Rutinitas" Samsung: **sebuah pemicu** (perangkat Bluetooth terhubung, Wi-Fi, pengisi daya, waktu, aplikasi dibuka,
ponsel dibuka lipatannya…), **syarat** opsional, lalu **serangkaian tindakan** (setelan, aplikasi, musik, perintah…),
ditambah tindakan "saat berakhir" yang mengembalikan semuanya pada saat yang tepat.

Contoh bawaan aplikasi: saat ponsel terhubung ke mobil lewat Bluetooth, Flare mengaktifkan Jangan Ganggu, mengatur
volume, memutar musik di [Opus](https://github.com/apkangelo/opus), dan membuka Maps; saat terputus, musik dijeda dan
Jangan Ganggu dimatikan.

Dibuat untuk Galaxy Z Fold dengan One UI: pemicu "ponsel dilipat / dibuka", dan tanpa root (Samsung tidak lagi
mengizinkan membuka kunci bootloader) berkat Shizuku.

## Unduh

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)** (Android 8 atau lebih baru).
Buka file, izinkan pemasangan dari sumber ini, lalu *Instal*. Pembaruan dipasang di atas versi lama tanpa kehilangan
otomatisasimu. Semua versi ada di tab [*Releases*](https://github.com/apkangelo/Flare/releases), dibuat oleh GitHub
Actions setiap ada perubahan.

## Tangkapan layar

| Otomatisasi | Editor | Log | Izin |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## Yang dilakukan Flare

**Pemicu (27)**: perangkat Bluetooth terhubung / terputus (cukup sebagian nama: "Media Nav" mengenali "MEDIA-NAV"),
Wi-Fi terhubung / terputus, mode pesawat, pengisi daya dicolokkan / dicabut, level baterai, ponsel dinyalakan, layar
menyala / mati, buka kunci, **ponsel dilipat / dibuka**, headphone dicolokkan / dicabut, jam dan hari tertentu, setiap
X menit, aplikasi dibuka / ditutup, notifikasi diterima (aplikasi, teks), panggilan masuk / dimulai / berakhir, SMS
diterima, tiba di / meninggalkan suatu tempat.

**Syarat (11)**: rentang waktu, hari dalam seminggu, baterai, sedang diisi daya, perangkat Bluetooth terhubung,
jaringan Wi-Fi, dilipat / dibuka, headphone, layar menyala, mode dering, aplikasi di layar.

**Tindakan (40)**
- *Koneksi*: Wi-Fi, data seluler, Bluetooth, mode pesawat, NFC, lokasi (aktif, nonaktif, ubah).
- *Layar*: kecerahan, waktu tunggu layar, putar, mode gelap, tetap menyala saat mengisi daya, penghemat daya, senter,
  kunci, nyalakan layar, screenshot.
- *Suara dan media*: mode dering, Jangan Ganggu, volume tiap saluran, putar / jeda / berikutnya, memutar musik di
  aplikasi tertentu, membacakan teks, getar.
- *Aplikasi*: membuka aplikasi, tautan, atau deep link (`opus://flow`, `tel:`, `geo:`…), navigasi Maps, paksa berhenti,
  membuka halaman setelan, mengirim intent.
- *Kontrol layar*: tombol sistem (kembali, layar utama, terbaru, panel notifikasi, setelan cepat, menu daya, layar
  terpisah), mengetuk teks di layar, menghapus notifikasi.
- *Panggilan dan SMS*: mengirim SMS, menelepon.
- *Sistem*: notifikasi, **perintah shell**, **setelan sistem apa pun** (`system`, `secure`, `global`), termasuk
  setelan khusus One UI.
- *Alur*: tunggu, jalankan pintasan, aktifkan / nonaktifkan otomatisasi.

**Pintasan**: daftar tindakan tanpa pemicu, dijalankan dengan sekali ketuk dari aplikasi, dari **layar utama**, atau
dari salah satu **3 ubin setelan cepat**.

**Log** semua yang dijalankan (dengan hasil setiap tindakan), **ekspor / impor** JSON, antarmuka dalam **23 bahasa**.

## Izin, langkah demi langkah

Flare tidak pernah memerlukan root. Setiap tindakan menunjukkan akses yang kurang, dan tab **Izin** mengumpulkan
semuanya.

1. **Opsi developer**: Setelan › Tentang ponsel › Informasi perangkat lunak, ketuk *Nomor versi* 7 kali.
2. **Shizuku**: instal [Shizuku](https://shizuku.rikka.app/download/), aktifkan *Debugging nirkabel* di opsi developer
   (saat terhubung Wi-Fi), lalu di Shizuku: *Mulai melalui debugging nirkabel* › *Sandingkan*, dan masukkan kode
   penyandingan di notifikasi.
3. Di Flare › **Izin**: *Shizuku › Berikan*, lalu **Berikan semua**. Flare memberikan sendiri hak sistem permanen,
   aksesibilitas, akses notifikasi, Jangan Ganggu, tampil di atas aplikasi lain, tanpa batasan baterai, dan izin
   Android.
4. Di Samsung: Setelan › Aplikasi › Flare › Baterai › **Tidak dibatasi**.

Setelah ponsel dimulai ulang, Shizuku perlu dijalankan lagi (langkah 2, tanpa penyandingan ulang) untuk tindakan Wi-Fi,
data seluler, Bluetooth, mode pesawat, mode gelap, dan perintah. Selebihnya tetap berjalan berkat hak sistem permanen.

Tanpa Shizuku, dari komputer:

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

Jika aksesibilitas atau akses notifikasi berwarna abu-abu (aplikasi dipasang di luar Play Store): Info aplikasi › ⋮ ›
*Izinkan setelan terbatas*.

## Dengan Opus

Contoh "Mode mobil" membuka `opus://flow`, yang memulai Flow di [Opus](https://github.com/apkangelo/opus). Tindakan
*Putar musik di aplikasi* juga bekerja dengan pemutar apa pun (Spotify, YouTube Music, Deezer…): lewat sesi media
aplikasi, atau dengan mengirim tombol "putar".

## Membangun sendiri

Kamu memerlukan JDK 21 dan Android SDK (atau Android Studio).

```sh
python3 scripts/i18n.py        # membuat teks Android dari i18n/*.json
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

Setiap ada perubahan, GitHub Actions memeriksa 23 terjemahan, membangun, dan menerbitkan APK. Workflow kedua
menjalankan aplikasi di emulator Android 15, menyimulasikan pengisi daya dicolokkan untuk memastikan mesinnya bereaksi,
dan mengambil tangkapan layar di atas.

## Tanda tangan APK

Semua versi ditandatangani dengan **kunci yang sama** (`signing/flare.jks`) agar pembaruan bisa dipasang di atas versi
lama. Kunci ini publik di repositori: pasang Flare hanya dari repositori ini. Untuk memakai kunci pribadi, tambahkan
di *Settings › Secrets › Actions* secret `FLARE_KEYSTORE_B64` (file .jks dalam base64), `FLARE_KEYSTORE_PASSWORD`,
`FLARE_KEY_ALIAS`, dan `FLARE_KEY_PASSWORD`.

## Perlu diketahui

- Otomatisasi, log, dan setelan tetap **di ponsel**. Flare tidak punya akses internet.
- Perintah shell berjalan dengan hak ADB dari Shizuku: tempel hanya perintah yang kamu pahami.
- Nama jaringan Wi-Fi hanya bisa dibaca dengan izin lokasi (aturan Android).

## Struktur repositori

| Folder | Isi |
|---|---|
| `android/` | Aplikasi (Kotlin, Jetpack Compose): mesin di `core/`, layanan di `service/`, layar di `ui/` |
| `i18n/` | Teks antarmuka dalam 23 bahasa |
| `scripts/` | Pembuatan teks, uji di emulator, bantuan build |
| `docs/` | Tangkapan layar dan README terjemahan |
| `signing/` | Kunci tanda tangan APK |

## Aplikasi apk-fold lainnya

- [Haiku](https://github.com/apkangelo/haiku): pengelola kata sandi offline
- [Opus](https://github.com/apkangelo/opus): pemutar musik untuk Navidrome
- [Sonnet](https://github.com/apkangelo/sonnet): klien Jellyfin
- [Mythos](https://github.com/apkangelo/mythos): rangkaian aplikasi kantor yang tersinkron tanpa server
- [Fable](https://github.com/apkangelo/fable): pencatat jam kerja
- [Cyber](https://github.com/apkangelo/Cyber): terminal SSH dan shell lokal
