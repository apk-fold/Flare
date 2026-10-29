<div align="center">

[English](README_en.md) · [简体中文](README_zh.md) · [繁體中文](README_zh-TW.md) · [日本語](README_ja.md) · [한국어](README_ko.md) · [Türkçe](README_tr.md) · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · **Deutsch** · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

Automationen für **Android**, im Geist der Kurzbefehle-App von Apple, aber umfangreicher als Samsungs „Modi und
Routinen“: **ein Auslöser** (Bluetooth-Gerät verbunden, WLAN, Ladegerät, Uhrzeit, App geöffnet, Handy aufgeklappt…),
optionale **Bedingungen**, dann **eine Folge von Aktionen** (Einstellungen, Apps, Musik, Befehle…) und Aktionen „wenn
es endet“, die im richtigen Moment alles rückgängig machen.

Mitgeliefertes Beispiel: Verbindet sich das Handy per Bluetooth mit dem Auto, schaltet Flare „Nicht stören“ ein,
stellt die Lautstärke ein, startet die Musik in [Opus](https://github.com/apkangelo/opus) und öffnet Maps; beim
Trennen pausiert es die Musik und schaltet „Nicht stören“ aus.

Gemacht für das Galaxy Z Fold mit One UI: Auslöser „Handy zu- / aufgeklappt“, und dank Shizuku kein Root nötig
(Samsung erlaubt das Entsperren des Bootloaders nicht mehr).

## Herunterladen

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)** (Android 8 oder neuer). Datei
öffnen, Installation aus dieser Quelle erlauben, dann *Installieren*. Updates werden über die alte Version installiert,
ohne deine Automationen zu verlieren. Alle Versionen findest du unter
[*Releases*](https://github.com/apkangelo/Flare/releases), bei jeder Änderung von GitHub Actions gebaut.

## Bildschirmfotos

| Automationen | Editor | Protokoll | Berechtigungen |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## Was Flare kann

**Auslöser (27)**: Bluetooth-Gerät verbunden / getrennt (ein Teil des Namens genügt: „Media Nav“ erkennt
„MEDIA-NAV“), WLAN verbunden / getrennt, Flugmodus, Ladegerät angeschlossen / abgezogen, Akkustand, Handy gestartet,
Bildschirm an / aus, entsperrt, **Handy zu- / aufgeklappt**, Kopfhörer angeschlossen / abgezogen, Uhrzeit und Tage,
alle X Minuten, App geöffnet / geschlossen, Benachrichtigung erhalten (App, Text), Anruf eingehend / begonnen /
beendet, SMS erhalten, Ankunft an / Verlassen eines Orts.

**Bedingungen (11)**: Zeitraum, Wochentage, Akku, wird geladen, Bluetooth-Gerät verbunden, WLAN-Netz, zu- /
aufgeklappt, Kopfhörer, Bildschirm an, Klingelmodus, App auf dem Bildschirm.

**Aktionen (40)**
- *Verbindungen*: WLAN, mobile Daten, Bluetooth, Flugmodus, NFC, Standort (an, aus, umschalten).
- *Bildschirm*: Helligkeit, Bildschirm-Timeout, Drehen, dunkles Design, beim Laden aktiv bleiben, Energiesparmodus,
  Taschenlampe, sperren, Bildschirm einschalten, Screenshot.
- *Ton und Medien*: Klingelmodus, „Nicht stören“, Lautstärke jedes Kanals, Wiedergabe / Pause / Weiter, Musik in einer
  bestimmten App starten, Text vorlesen, vibrieren.
- *Apps*: App, Link oder Deep Link öffnen (`opus://flow`, `tel:`, `geo:`…), Maps-Navigation, beenden erzwingen,
  Einstellungsseite öffnen, Intent senden.
- *Bildschirmsteuerung*: Systemtasten (Zurück, Start, Letzte Apps, Benachrichtigungsfeld, Schnelleinstellungen,
  Ein/Aus-Menü, geteilter Bildschirm), auf einen Text tippen, Benachrichtigungen löschen.
- *Anrufe und SMS*: SMS senden, anrufen.
- *System*: Benachrichtigung, **Shell-Befehl**, **jede Systemeinstellung** (`system`, `secure`, `global`), womit auch
  die One-UI-eigenen Einstellungen abgedeckt sind.
- *Ablauf*: warten, Verknüpfung ausführen, Automation ein- / ausschalten.

**Verknüpfungen**: Aktionslisten ohne Auslöser, mit einem Tippen gestartet – in der App, vom **Startbildschirm** oder
über eine der **3 Schnelleinstellungs-Kacheln**.

**Protokoll** aller Ausführungen (mit dem Ergebnis jeder Aktion), **Export / Import** als JSON, Oberfläche in
**23 Sprachen**.

## Berechtigungen Schritt für Schritt

Flare braucht nie Root. Jede Aktion zeigt, welcher Zugriff fehlt, und der Tab **Berechtigungen** bündelt alles.

1. **Entwickleroptionen**: Einstellungen › Telefoninfo › Softwareinformationen, 7-mal auf *Buildnummer* tippen.
2. **Shizuku**: [Shizuku](https://shizuku.rikka.app/download/) installieren, in den Entwickleroptionen *Kabelloses
   Debugging* einschalten (im WLAN), dann in Shizuku: *Über kabelloses Debugging starten* › *Koppeln*, und den
   Kopplungscode in der Benachrichtigung eingeben.
3. In Flare › **Berechtigungen**: *Shizuku › Erteilen*, dann **Alles erteilen**. Flare erteilt sich selbst die
   dauerhaften Systemrechte, Bedienungshilfen, Benachrichtigungszugriff, „Nicht stören“, Einblenden über anderen Apps,
   keine Akku-Einschränkungen und die Android-Berechtigungen.
4. Auf Samsung: Einstellungen › Apps › Flare › Akku › **Nicht eingeschränkt**.

Nach einem Neustart muss Shizuku erneut gestartet werden (Schritt 2, ohne neues Koppeln), für die Aktionen WLAN, mobile
Daten, Bluetooth, Flugmodus, dunkles Design und Befehle. Alles andere funktioniert dank der dauerhaften Systemrechte
weiter.

Ohne Shizuku, von einem Computer:

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

Sind Bedienungshilfen oder Benachrichtigungszugriff ausgegraut (App nicht aus dem Play Store): App-Info › ⋮ ›
*Eingeschränkte Einstellungen zulassen*.

## Mit Opus

Das Beispiel „Automodus“ öffnet `opus://flow`, das den Flow in [Opus](https://github.com/apkangelo/opus) startet. Die
Aktion *Musik in einer App abspielen* funktioniert auch mit jedem anderen Player (Spotify, YouTube Music, Deezer…): Sie
nutzt die Mediensitzung der App oder sendet ihr die Taste „Wiedergabe“.

## Selbst bauen

Du brauchst JDK 21 und das Android SDK (oder Android Studio).

```sh
python3 scripts/i18n.py        # erzeugt die Android-Texte aus i18n/*.json
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

Bei jeder Änderung prüft GitHub Actions die 23 Übersetzungen, baut und veröffentlicht die APK. Ein zweiter Workflow
startet die App in einem Android-15-Emulator, simuliert das Anschließen des Ladegeräts, um zu prüfen, dass die
Automationen reagieren, und macht die Bildschirmfotos oben.

## Signatur der APK

Alle Versionen sind mit **demselben Schlüssel** signiert (`signing/flare.jks`), damit Updates über die alte Version
installiert werden. Dieser Schlüssel ist öffentlich im Repository: Installiere Flare nur aus diesem Repository. Für
einen privaten Schlüssel füge unter *Settings › Secrets › Actions* die Secrets `FLARE_KEYSTORE_B64` (die .jks-Datei in
Base64), `FLARE_KEYSTORE_PASSWORD`, `FLARE_KEY_ALIAS` und `FLARE_KEY_PASSWORD` hinzu.

## Gut zu wissen

- Automationen, Protokoll und Einstellungen bleiben **auf dem Handy**. Flare hat keinen Internetzugriff.
- Shell-Befehle laufen mit den ADB-Rechten von Shizuku: Füge nur Befehle ein, die du verstehst.
- Der WLAN-Name ist nur mit der Standortberechtigung lesbar (eine Android-Regel).

## Aufbau des Repositorys

| Ordner | Inhalt |
|---|---|
| `android/` | Die App (Kotlin, Jetpack Compose): Logik in `core/`, Dienste in `service/`, Bildschirme in `ui/` |
| `i18n/` | Texte der Oberfläche in den 23 Sprachen |
| `scripts/` | Texterzeugung, Emulator-Test, Build-Hilfe |
| `docs/` | Bildschirmfotos und übersetzte READMEs |
| `signing/` | Signaturschlüssel der APK |

## Weitere apk-fold-Apps

- [Haiku](https://github.com/apkangelo/haiku): Offline-Passwortmanager
- [Opus](https://github.com/apkangelo/opus): Musikplayer für Navidrome
- [Sonnet](https://github.com/apkangelo/sonnet): Jellyfin-Client
- [Mythos](https://github.com/apkangelo/mythos): Office-Suite, die ohne Server synchronisiert
- [Fable](https://github.com/apkangelo/fable): Arbeitszeiterfassung
- [Cyber](https://github.com/apkangelo/Cyber): SSH-Terminal und lokale Shell
