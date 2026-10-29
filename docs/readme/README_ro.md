<div align="center">

[English](README_en.md) · [简体中文](README_zh.md) · [繁體中文](README_zh-TW.md) · [日本語](README_ja.md) · [한국어](README_ko.md) · [Türkçe](README_tr.md) · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · [Deutsch](README_de.md) · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · **Română**

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

Automatizări pentru **Android**, în spiritul aplicației Comenzi rapide de la Apple, dar mai complete decât „Moduri și
rutine” de la Samsung: **un declanșator** (dispozitiv Bluetooth conectat, Wi-Fi, încărcător, oră, aplicație deschisă,
telefon desfăcut…), **condiții** opționale, apoi **o serie de acțiuni** (setări, aplicații, muzică, comenzi…), plus
acțiuni „când se termină” care anulează totul la momentul potrivit.

Exemplu inclus în aplicație: când telefonul se conectează prin Bluetooth la mașină, Flare pornește Nu deranja, setează
volumul, pornește muzica în [Opus](https://github.com/apkangelo/opus) și deschide Maps; la deconectare, pune muzica pe
pauză și oprește Nu deranja.

Gândit pentru Galaxy Z Fold cu One UI: declanșatoare „telefon pliat / desfăcut”, fără root (Samsung nu mai permite
deblocarea bootloaderului) datorită Shizuku.

## Descărcare

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)** (Android 8 sau mai nou).
Deschide fișierul, permite instalarea din această sursă, apoi *Instalează*. Actualizările se instalează peste versiunea
veche fără să-ți pierzi automatizările. Toate versiunile sunt în fila
[*Releases*](https://github.com/apkangelo/Flare/releases), compilate de GitHub Actions la fiecare modificare.

## Capturi de ecran

| Automatizări | Editor | Jurnal | Permisiuni |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## Ce face Flare

**Declanșatoare (27)**: dispozitiv Bluetooth conectat / deconectat (e suficientă o parte din nume: „Media Nav”
recunoaște „MEDIA-NAV”), Wi-Fi conectat / deconectat, mod avion, încărcător conectat / scos, nivelul bateriei, pornirea
telefonului, ecran aprins / stins, deblocare, **telefon pliat / desfăcut**, căști conectate / scoase, oră exactă și
zile, la fiecare X minute, aplicație deschisă / închisă, notificare primită (aplicație, text), apel primit / început /
încheiat, SMS primit, sosire într-un loc / plecare.

**Condiții (11)**: interval orar, zilele săptămânii, baterie, se încarcă, dispozitiv Bluetooth conectat, rețea Wi-Fi,
pliat / desfăcut, căști, ecran aprins, modul soneriei, aplicație pe ecran.

**Acțiuni (40)**
- *Conexiuni*: Wi-Fi, date mobile, Bluetooth, mod avion, NFC, locație (pornește, oprește, comută).
- *Ecran*: luminozitate, stingerea ecranului, rotire, mod întunecat, ecran aprins la încărcare, economisirea energiei,
  lanternă, blocare, aprinderea ecranului, captură de ecran.
- *Sunet și media*: modul soneriei, Nu deranja, volumul fiecărui canal, redare / pauză / următorul, pornirea muzicii
  într-o aplicație anume, citirea unui text cu voce tare, vibrații.
- *Aplicații*: deschiderea unei aplicații, a unui link sau deep link (`opus://flow`, `tel:`, `geo:`…), navigare în
  Maps, oprire forțată, deschiderea unei pagini de setări, trimiterea unui intent.
- *Controlul ecranului*: butoane de sistem (înapoi, acasă, recente, panoul de notificări, setări rapide, meniul de
  pornire, ecran împărțit), atingerea unui text de pe ecran, ștergerea notificărilor.
- *Apeluri și SMS*: trimiterea unui SMS, apelare.
- *Sistem*: notificare, **comandă shell**, **orice setare de sistem** (`system`, `secure`, `global`), ceea ce acoperă
  și setările proprii One UI.
- *Desfășurare*: așteptare, rularea unei comenzi rapide, activarea / dezactivarea unei automatizări.

**Comenzi rapide**: liste de acțiuni fără declanșator, pornite dintr-o atingere din aplicație, de pe **ecranul de
pornire** sau dintr-una dintre cele **3 casete din setările rapide**.

**Jurnal** al tuturor rulărilor (cu rezultatul fiecărei acțiuni), **export / import** în JSON, interfață în
**23 de limbi**.

## Permisiunile, pas cu pas

Flare nu are nevoie niciodată de root. Fiecare acțiune arată ce acces îi lipsește, iar fila **Permisiuni** le adună pe
toate.

1. **Opțiuni dezvoltator**: Setări › Despre telefon › Informații software, atinge de 7 ori *Număr versiune*.
2. **Shizuku**: instalează [Shizuku](https://shizuku.rikka.app/download/), activează *Depanare wireless* în opțiunile
   pentru dezvoltatori (pe Wi-Fi), apoi în Shizuku: *Pornește prin depanare wireless* › *Asociere*, și introdu codul
   de asociere în notificare.
3. În Flare › **Permisiuni**: *Shizuku › Acordă*, apoi **Acordă totul**. Flare își acordă singur drepturile de sistem
   permanente, accesibilitatea, accesul la notificări, Nu deranja, afișarea peste alte aplicații, lipsa restricțiilor
   de baterie și permisiunile Android.
4. Pe Samsung: Setări › Aplicații › Flare › Baterie › **Fără restricții**.

După o repornire, Shizuku trebuie pornit din nou (pasul 2, fără o nouă asociere) pentru acțiunile Wi-Fi, date mobile,
Bluetooth, mod avion, mod întunecat și comenzi. Restul continuă să funcționeze datorită drepturilor de sistem
permanente.

Fără Shizuku, de pe un computer:

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

Dacă accesibilitatea sau accesul la notificări apar cu gri (aplicație instalată din afara Play Store): Informații
despre aplicație › ⋮ › *Permite setările restricționate*.

## Cu Opus

Exemplul „Mod mașină” deschide `opus://flow`, care pornește Flow-ul din [Opus](https://github.com/apkangelo/opus).
Acțiunea *Pornește muzica într-o aplicație* funcționează cu orice player (Spotify, YouTube Music, Deezer…): folosește
sesiunea media a aplicației sau îi trimite tasta „redare”.

## Compilare proprie

Ai nevoie de JDK 21 și de SDK-ul Android (sau Android Studio).

```sh
python3 scripts/i18n.py        # regenerează textele Android din i18n/*.json
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

La fiecare modificare, GitHub Actions verifică cele 23 de traduceri, compilează și publică APK-ul. Un al doilea workflow
rulează aplicația într-un emulator Android 15, simulează conectarea încărcătorului ca să verifice că motorul reacționează
și face capturile de mai sus.

## Semnătura APK-ului

Toate versiunile sunt semnate cu **aceeași cheie** (`signing/flare.jks`) pentru ca actualizările să se instaleze peste
cea veche. Cheia este publică în depozit: instalează Flare doar din acest depozit. Pentru o cheie privată, adaugă în
*Settings › Secrets › Actions* secretele `FLARE_KEYSTORE_B64` (fișierul .jks în base64), `FLARE_KEYSTORE_PASSWORD`,
`FLARE_KEY_ALIAS` și `FLARE_KEY_PASSWORD`.

## Bine de știut

- Automatizările, jurnalul și setările rămân **pe telefon**. Flare nu are acces la internet.
- Comenzile shell rulează cu drepturile ADB ale Shizuku: lipește doar comenzi pe care le înțelegi.
- Numele rețelei Wi-Fi poate fi citit doar cu permisiunea de locație (regulă Android).

## Structura depozitului

| Dosar | Conținut |
|---|---|
| `android/` | Aplicația (Kotlin, Jetpack Compose): motorul în `core/`, serviciile în `service/`, ecranele în `ui/` |
| `i18n/` | Textele interfeței în cele 23 de limbi |
| `scripts/` | Generarea textelor, testul pe emulator, ajutor la compilare |
| `docs/` | Capturi de ecran și README traduse |
| `signing/` | Cheia de semnare a APK-ului |

## Celelalte aplicații apk-fold

- [Haiku](https://github.com/apkangelo/haiku): manager de parole offline
- [Opus](https://github.com/apkangelo/opus): player muzical pentru Navidrome
- [Sonnet](https://github.com/apkangelo/sonnet): client Jellyfin
- [Mythos](https://github.com/apkangelo/mythos): suită office care se sincronizează fără server
- [Fable](https://github.com/apkangelo/fable): evidența orelor de lucru
- [Cyber](https://github.com/apkangelo/Cyber): terminal SSH și shell local
