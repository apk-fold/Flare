<div align="center">

[English](README_en.md) · [简体中文](README_zh.md) · [繁體中文](README_zh-TW.md) · [日本語](README_ja.md) · [한국어](README_ko.md) · [Türkçe](README_tr.md) · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · [Deutsch](README_de.md) · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · **Polski** · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

Automatyzacje dla **Androida** w duchu aplikacji Skróty od Apple, ale bardziej rozbudowane niż „Tryby i procedury”
Samsunga: **wyzwalacz** (połączone urządzenie Bluetooth, Wi-Fi, ładowarka, godzina, otwarta aplikacja, rozłożony
telefon…), opcjonalne **warunki**, a potem **seria działań** (ustawienia, aplikacje, muzyka, polecenia…), do tego
działania „gdy się skończy”, które we właściwym momencie wszystko cofają.

Przykład dołączony do aplikacji: gdy telefon połączy się z samochodem przez Bluetooth, Flare włącza Nie przeszkadzać,
ustawia głośność, uruchamia muzykę w [Opus](https://github.com/apkangelo/opus) i otwiera Mapy; po rozłączeniu
wstrzymuje muzykę i wyłącza Nie przeszkadzać.

Stworzony z myślą o Galaxy Z Fold z One UI: wyzwalacze „telefon złożony / rozłożony” i bez roota (Samsung nie pozwala
już odblokować bootloadera) dzięki Shizuku.

## Pobieranie

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)** (Android 8 lub nowszy). Otwórz
plik, zezwól na instalację z tego źródła, a potem *Zainstaluj*. Aktualizacje instalują się na poprzedniej wersji bez
utraty automatyzacji. Wszystkie wersje są w zakładce [*Releases*](https://github.com/apkangelo/Flare/releases),
budowane przez GitHub Actions przy każdej zmianie.

## Zrzuty ekranu

| Automatyzacje | Edytor | Dziennik | Uprawnienia |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## Co potrafi Flare

**Wyzwalacze (27)**: urządzenie Bluetooth połączone / rozłączone (wystarczy część nazwy: „Media Nav” rozpozna
„MEDIA-NAV”), Wi-Fi połączone / rozłączone, tryb samolotowy, ładowarka podłączona / odłączona, poziom baterii,
włączenie telefonu, ekran włączony / wyłączony, odblokowanie, **telefon złożony / rozłożony**, słuchawki podłączone /
odłączone, konkretna godzina i dni, co X minut, aplikacja otwarta / zamknięta, otrzymane powiadomienie (aplikacja,
tekst), połączenie przychodzące / rozpoczęte / zakończone, otrzymany SMS, przybycie na miejsce / wyjście.

**Warunki (11)**: przedział godzin, dni tygodnia, bateria, ładowanie, połączone urządzenie Bluetooth, sieć Wi-Fi,
złożony / rozłożony, słuchawki, ekran włączony, tryb dzwonka, aplikacja na ekranie.

**Działania (40)**
- *Połączenia*: Wi-Fi, dane mobilne, Bluetooth, tryb samolotowy, NFC, lokalizacja (włącz, wyłącz, przełącz).
- *Ekran*: jasność, wygaszanie, obracanie, tryb ciemny, nie wygaszaj podczas ładowania, oszczędzanie energii, latarka,
  blokada, włączenie ekranu, zrzut ekranu.
- *Dźwięk i multimedia*: tryb dzwonka, Nie przeszkadzać, głośność każdego kanału, odtwórz / wstrzymaj / następny,
  uruchomienie muzyki w wybranej aplikacji, czytanie tekstu na głos, wibracje.
- *Aplikacje*: otwarcie aplikacji, linku lub deep linku (`opus://flow`, `tel:`, `geo:`…), nawigacja w Mapach,
  wymuszenie zatrzymania, otwarcie strony ustawień, wysłanie intentu.
- *Sterowanie ekranem*: przyciski systemowe (wstecz, ekran główny, ostatnie, panel powiadomień, szybkie ustawienia,
  menu zasilania, podzielony ekran), dotknięcie tekstu na ekranie, czyszczenie powiadomień.
- *Połączenia i SMS*: wysłanie SMS-a, dzwonienie.
- *System*: powiadomienie, **polecenie shell**, **dowolne ustawienie systemu** (`system`, `secure`, `global`), co
  obejmuje też ustawienia specyficzne dla One UI.
- *Przebieg*: czekanie, uruchomienie skrótu, włączenie / wyłączenie automatyzacji.

**Skróty**: listy działań bez wyzwalacza, uruchamiane jednym dotknięciem w aplikacji, z **ekranu głównego** albo
z jednego z **3 kafelków szybkich ustawień**.

**Dziennik** wszystkiego, co się uruchomiło (z wynikiem każdego działania), **eksport / import** w JSON, interfejs
w **23 językach**.

## Uprawnienia krok po kroku

Flare nigdy nie potrzebuje roota. Każde działanie pokazuje, jakiego dostępu mu brakuje, a zakładka **Uprawnienia**
zbiera wszystko.

1. **Opcje programisty**: Ustawienia › Telefon — informacje › Informacje o oprogramowaniu, dotknij 7 razy *Numer
   wersji*.
2. **Shizuku**: zainstaluj [Shizuku](https://shizuku.rikka.app/download/), włącz *Debugowanie bezprzewodowe* w opcjach
   programisty (przy Wi-Fi), a w Shizuku: *Uruchom przez debugowanie bezprzewodowe* › *Parowanie* i wpisz kod
   parowania w powiadomieniu.
3. W Flare › **Uprawnienia**: *Shizuku › Przyznaj*, potem **Przyznaj wszystko**. Flare sam przyznaje sobie stałe
   uprawnienia systemowe, ułatwienia dostępu, dostęp do powiadomień, Nie przeszkadzać, wyświetlanie nad innymi
   aplikacjami, brak ograniczeń baterii i uprawnienia Androida.
4. Na Samsungu: Ustawienia › Aplikacje › Flare › Bateria › **Bez ograniczeń**.

Po ponownym uruchomieniu trzeba znowu uruchomić Shizuku (krok 2, bez ponownego parowania) dla działań Wi-Fi, dane
mobilne, Bluetooth, tryb samolotowy, tryb ciemny i polecenia. Reszta działa dalej dzięki stałym uprawnieniom
systemowym.

Bez Shizuku, z komputera:

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

Jeśli ułatwienia dostępu lub dostęp do powiadomień są wyszarzone (aplikacja spoza Sklepu Play): Informacje
o aplikacji › ⋮ › *Zezwól na ustawienia z ograniczonym dostępem*.

## Z Opusem

Przykład „Tryb samochodowy” otwiera `opus://flow`, co uruchamia Flow w [Opusie](https://github.com/apkangelo/opus).
Działanie *Odtwórz muzykę w aplikacji* działa też z każdym innym odtwarzaczem (Spotify, YouTube Music, Deezer…):
korzysta z sesji multimedialnej aplikacji albo wysyła jej klawisz „odtwórz”.

## Samodzielna kompilacja

Potrzebujesz JDK 21 i Android SDK (lub Android Studio).

```sh
python3 scripts/i18n.py        # generuje teksty Androida z i18n/*.json
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

Przy każdej zmianie GitHub Actions sprawdza 23 tłumaczenia, kompiluje i publikuje APK. Drugi workflow uruchamia
aplikację w emulatorze Androida 15, symuluje podłączenie ładowarki, żeby sprawdzić reakcję automatyzacji, i robi
powyższe zrzuty ekranu.

## Podpis APK

Wszystkie wersje są podpisane **tym samym kluczem** (`signing/flare.jks`), żeby aktualizacje instalowały się na
poprzedniej wersji. Klucz jest publiczny w repozytorium: instaluj Flare tylko z tego repozytorium. Aby użyć
prywatnego klucza, dodaj w *Settings › Secrets › Actions* sekrety `FLARE_KEYSTORE_B64` (plik .jks w base64),
`FLARE_KEYSTORE_PASSWORD`, `FLARE_KEY_ALIAS` i `FLARE_KEY_PASSWORD`.

## Dobrze wiedzieć

- Automatyzacje, dziennik i ustawienia zostają **w telefonie**. Flare nie ma dostępu do internetu.
- Polecenia shell działają z uprawnieniami ADB Shizuku: wklejaj tylko polecenia, które rozumiesz.
- Nazwę sieci Wi-Fi można odczytać tylko z uprawnieniem do lokalizacji (zasada Androida).

## Struktura repozytorium

| Folder | Zawartość |
|---|---|
| `android/` | Aplikacja (Kotlin, Jetpack Compose): silnik w `core/`, usługi w `service/`, ekrany w `ui/` |
| `i18n/` | Teksty interfejsu w 23 językach |
| `scripts/` | Generowanie tekstów, test na emulatorze, pomoc przy kompilacji |
| `docs/` | Zrzuty ekranu i przetłumaczone README |
| `signing/` | Klucz podpisu APK |

## Inne aplikacje apk-fold

- [Haiku](https://github.com/apkangelo/haiku): menedżer haseł offline
- [Opus](https://github.com/apkangelo/opus): odtwarzacz muzyki dla Navidrome
- [Sonnet](https://github.com/apkangelo/sonnet): klient Jellyfin
- [Mythos](https://github.com/apkangelo/mythos): pakiet biurowy synchronizowany bez serwera
- [Fable](https://github.com/apkangelo/fable): ewidencja czasu pracy
- [Cyber](https://github.com/apkangelo/Cyber): terminal SSH i lokalna powłoka
