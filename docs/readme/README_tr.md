<div align="center">

[English](README_en.md) · [简体中文](README_zh.md) · [繁體中文](README_zh-TW.md) · [日本語](README_ja.md) · [한국어](README_ko.md) · **Türkçe** · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · [Deutsch](README_de.md) · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

**Android** için otomasyonlar: Apple'ın Kestirmeler uygulaması gibi, ama Samsung'un "Modlar ve Rutinler"inden çok daha
kapsamlı. **Bir tetikleyici** (Bluetooth cihazı bağlandı, Wi-Fi, şarj aleti, saat, uygulama açıldı, telefon açıldı…),
isteğe bağlı **koşullar**, ardından **bir eylem dizisi** (ayarlar, uygulamalar, müzik, komutlar…) ve doğru anda her
şeyi geri alan "sona erdiğinde" eylemleri.

Uygulamayla gelen örnek: telefon arabaya Bluetooth ile bağlandığında Flare Rahatsız Etmeyin'i açar, ses düzeyini
ayarlar, [Opus](https://github.com/apkangelo/opus)'ta müziği başlatır ve Haritalar'ı açar; bağlantı kesilince müziği
duraklatır ve Rahatsız Etmeyin'i kapatır.

One UI çalıştıran Galaxy Z Fold için tasarlandı: "telefon katlandı / açıldı" tetikleyicileri ve Shizuku sayesinde root
gerekmez (Samsung artık bootloader kilidini açmaya izin vermiyor).

## İndir

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)** (Android 8 veya üstü). Dosyayı
aç, bu kaynaktan yüklemeye izin ver, sonra *Yükle*. Güncellemeler otomasyonlarını kaybetmeden eski sürümün üzerine
yüklenir. Tüm sürümler [*Releases*](https://github.com/apkangelo/Flare/releases) sekmesinde; her değişiklikte GitHub
Actions tarafından derlenir.

## Ekran görüntüleri

| Otomasyonlar | Düzenleyici | Günlük | İzinler |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## Flare neler yapar

**Tetikleyiciler (27)**: Bluetooth cihazı bağlandı / bağlantısı kesildi (adın bir kısmı yeterli: "Media Nav",
"MEDIA-NAV"ı tanır), Wi-Fi bağlandı / kesildi, uçak modu, şarj aleti takıldı / çıkarıldı, pil seviyesi, telefon açılışı,
ekran açık / kapalı, kilit açma, **telefon katlandı / açıldı**, kulaklık takıldı / çıkarıldı, belirli saat ve günler,
her X dakikada bir, uygulama açıldı / kapandı, bildirim alındı (uygulama, metin), gelen / başlayan / biten arama, SMS
alındı, bir yere varış / ayrılış.

**Koşullar (11)**: saat aralığı, haftanın günleri, pil, şarj oluyor, bağlı Bluetooth cihazı, Wi-Fi ağı, katlı / açık,
kulaklık, ekran açık, zil modu, ekrandaki uygulama.

**Eylemler (40)**
- *Bağlantılar*: Wi-Fi, mobil veri, Bluetooth, uçak modu, NFC, konum (aç, kapat, değiştir).
- *Ekran*: parlaklık, ekran zaman aşımı, döndürme, karanlık mod, şarjda uyanık kal, güç tasarrufu, el feneri, kilitle,
  ekranı aç, ekran görüntüsü.
- *Ses ve medya*: zil modu, Rahatsız Etmeyin, her kanalın ses düzeyi, oynat / duraklat / sonraki, belirli bir
  uygulamada müzik başlat, metni sesli oku, titreşim.
- *Uygulamalar*: uygulama, bağlantı veya derin bağlantı aç (`opus://flow`, `tel:`, `geo:`…), Haritalar yol tarifi,
  zorla durdur, bir ayarlar sayfası aç, intent gönder.
- *Ekran kontrolü*: sistem düğmeleri (geri, ana ekran, son uygulamalar, bildirim paneli, hızlı ayarlar, güç menüsü,
  bölünmüş ekran), ekrandaki bir metne dokun, bildirimleri temizle.
- *Aramalar ve SMS*: SMS gönder, ara.
- *Sistem*: bildirim, **shell komutu**, **herhangi bir sistem ayarı** (`system`, `secure`, `global`); bu, One UI'ye özgü
  ayarları da kapsar.
- *Akış*: bekle, kısayol çalıştır, otomasyonu aç / kapat.

**Kısayollar**: tetikleyicisiz eylem listeleri; uygulamadan, **ana ekrandan** veya **3 hızlı ayarlar kutucuğundan**
birinden tek dokunuşla başlatılır.

Çalışan her şeyin **günlüğü** (her eylemin sonucuyla), JSON olarak **dışa / içe aktarma**, **23 dilde** arayüz.

## Adım adım izinler

Flare hiçbir zaman root gerektirmez. Her eylem eksik olan erişimi gösterir; **İzinler** sekmesi hepsini bir araya
getirir.

1. **Geliştirici seçenekleri**: Ayarlar › Telefon hakkında › Yazılım bilgileri, *Yapım numarası*na 7 kez dokun.
2. **Shizuku**: [Shizuku](https://shizuku.rikka.app/download/)'yu yükle, geliştirici seçeneklerinde *Kablosuz hata
   ayıklama*yı aç (Wi-Fi'de), sonra Shizuku'da: *Kablosuz hata ayıklama ile başlat* › *Eşleştir*, ve eşleştirme kodunu
   bildirime gir.
3. Flare › **İzinler**: *Shizuku › Ver*, ardından **Hepsini ver**. Flare kalıcı sistem haklarını, erişilebilirliği,
   bildirim erişimini, Rahatsız Etmeyin'i, diğer uygulamaların üzerinde gösterme iznini, pil kısıtlamasızlığını ve
   Android izinlerini kendine verir.
4. Samsung'da: Ayarlar › Uygulamalar › Flare › Pil › **Kısıtlanmamış**.

Yeniden başlatmadan sonra Wi-Fi, mobil veri, Bluetooth, uçak modu, karanlık mod ve komut eylemleri için Shizuku'yu
yeniden başlatmak gerekir (2. adım, yeniden eşleştirme olmadan). Geri kalanı kalıcı sistem hakları sayesinde çalışmaya
devam eder.

Shizuku olmadan, bir bilgisayardan:

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

Erişilebilirlik veya bildirim erişimi griyse (Play Store dışından yüklenen uygulama): Uygulama bilgileri › ⋮ ›
*Kısıtlanmış ayarlara izin ver*.

## Opus ile

"Araba modu" örneği `opus://flow`'u açar; bu, [Opus](https://github.com/apkangelo/opus)'taki Flow'u başlatır.
*Bir uygulamada müzik çal* eylemi her oynatıcıyla da çalışır (Spotify, YouTube Music, Deezer…): uygulamanın medya
oturumunu kullanır ya da ona "oynat" tuşunu gönderir.

## Kendin derle

JDK 21 ve Android SDK (veya Android Studio) gerekir.

```sh
python3 scripts/i18n.py        # Android metinlerini i18n/*.json'dan üretir
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

Her değişiklikte GitHub Actions 23 çeviriyi denetler, APK'yı derler ve yayımlar. İkinci bir workflow uygulamayı
Android 15 emülatöründe çalıştırır, motorun tepki verdiğini doğrulamak için şarj aletinin takılmasını taklit eder ve
yukarıdaki ekran görüntülerini alır.

## APK imzası

Tüm sürümler güncellemelerin üzerine kurulabilmesi için **aynı anahtarla** (`signing/flare.jks`) imzalanır. Bu anahtar
depoda herkese açıktır: Flare'i yalnızca bu depodan yükle. Özel bir anahtar için *Settings › Secrets › Actions*
bölümüne `FLARE_KEYSTORE_B64` (base64'te .jks dosyası), `FLARE_KEYSTORE_PASSWORD`, `FLARE_KEY_ALIAS` ve
`FLARE_KEY_PASSWORD` gizli değerlerini ekle.

## Bilmekte fayda var

- Otomasyonlar, günlük ve ayarlar **telefonda** kalır. Flare'in internet erişimi yoktur.
- Shell komutları Shizuku'nun ADB haklarıyla çalışır: yalnızca anladığın komutları yapıştır.
- Wi-Fi ağ adı yalnızca konum izniyle okunabilir (Android kuralı).

## Deponun düzeni

| Klasör | İçerik |
|---|---|
| `android/` | Uygulama (Kotlin, Jetpack Compose): motor `core/`, hizmetler `service/`, ekranlar `ui/` içinde |
| `i18n/` | 23 dilde arayüz metinleri |
| `scripts/` | Metin üretimi, emülatör testi, derleme yardımcısı |
| `docs/` | Ekran görüntüleri ve çevrilmiş README'ler |
| `signing/` | APK imza anahtarı |

## Diğer apk-fold uygulamaları

- [Haiku](https://github.com/apkangelo/haiku): çevrimdışı parola yöneticisi
- [Opus](https://github.com/apkangelo/opus): Navidrome için müzik çalar
- [Sonnet](https://github.com/apkangelo/sonnet): Jellyfin istemcisi
- [Mythos](https://github.com/apkangelo/mythos): sunucusuz eşitlenen ofis paketi
- [Fable](https://github.com/apkangelo/fable): çalışma saatleri takibi
- [Cyber](https://github.com/apkangelo/Cyber): SSH terminali ve yerel kabuk
