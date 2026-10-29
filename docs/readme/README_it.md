<div align="center">

[English](README_en.md) · [简体中文](README_zh.md) · [繁體中文](README_zh-TW.md) · [日本語](README_ja.md) · [한국어](README_ko.md) · [Türkçe](README_tr.md) · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · [Deutsch](README_de.md) · [Español](README_es.md) · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · **Italiano**
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

Automazioni per **Android**, nello spirito dell'app Comandi di Apple ma più complete di «Modalità e routine» di
Samsung: **un attivatore** (dispositivo Bluetooth connesso, Wi-Fi, caricabatterie, orario, app aperta, telefono
aperto…), **condizioni** facoltative e poi **una serie di azioni** (impostazioni, app, musica, comandi…), più azioni
«quando finisce» che annullano tutto al momento giusto.

Esempio incluso nell'app: quando il telefono si collega in Bluetooth all'auto, Flare attiva Non disturbare, regola il
volume, avvia la musica in [Opus](https://github.com/apkangelo/opus) e apre Maps; alla disconnessione mette in pausa la
musica e disattiva Non disturbare.

Pensato per il Galaxy Z Fold con One UI: attivatori «telefono chiuso / aperto», e niente root (Samsung non permette più
di sbloccare il bootloader) grazie a Shizuku.

## Scarica

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)** (Android 8 o successivo). Apri
il file, consenti l'installazione da questa fonte, poi *Installa*. Gli aggiornamenti si installano sopra senza perdere
le tue automazioni. Tutte le versioni sono nella scheda [*Releases*](https://github.com/apkangelo/Flare/releases),
compilate da GitHub Actions a ogni modifica.

## Schermate

| Automazioni | Editor | Registro | Permessi |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## Cosa fa Flare

**Attivatori (27)**: dispositivo Bluetooth connesso / disconnesso (basta una parte del nome: «Media Nav» riconosce
«MEDIA-NAV»), Wi-Fi connesso / disconnesso, modalità aereo, caricabatterie collegato / scollegato, livello batteria,
accensione del telefono, schermo acceso / spento, sblocco, **telefono chiuso / aperto**, cuffie collegate /
scollegate, ora precisa e giorni, ogni X minuti, app aperta / chiusa, notifica ricevuta (app, testo), chiamata in
arrivo / iniziata / terminata, SMS ricevuto, arrivo in un luogo / partenza.

**Condizioni (11)**: fascia oraria, giorni della settimana, batteria, in carica, dispositivo Bluetooth connesso, rete
Wi-Fi, chiuso / aperto, cuffie, schermo acceso, modalità suoneria, app sullo schermo.

**Azioni (40)**
- *Connessioni*: Wi-Fi, dati mobili, Bluetooth, modalità aereo, NFC, posizione (attiva, disattiva, inverti).
- *Schermo*: luminosità, spegnimento dello schermo, rotazione, tema scuro, schermo attivo in ricarica, risparmio
  energetico, torcia, blocco, accensione dello schermo, screenshot.
- *Suono e media*: modalità suoneria, Non disturbare, volume di ogni canale, riproduci / pausa / successivo, avvio
  della musica in un'app precisa, lettura di un testo ad alta voce, vibrazione.
- *App*: aprire un'app, un link o un deep link (`opus://flow`, `tel:`, `geo:`…), navigazione con Maps, arresto
  forzato, aprire una pagina delle impostazioni, inviare un intent.
- *Controllo dello schermo*: tasti di sistema (indietro, Home, recenti, pannello notifiche, impostazioni rapide, menu
  di spegnimento, schermo diviso), toccare un testo sullo schermo, cancellare le notifiche.
- *Chiamate e SMS*: inviare un SMS, chiamare.
- *Sistema*: notifica, **comando shell**, **qualsiasi impostazione di sistema** (`system`, `secure`, `global`), che
  copre anche le impostazioni proprie di One UI.
- *Flusso*: attendere, eseguire una scorciatoia, attivare / disattivare un'automazione.

**Scorciatoie**: elenchi di azioni senza attivatore, avviati con un tocco dall'app, dalla **schermata Home** o da uno
dei **3 riquadri delle impostazioni rapide**.

**Registro** di tutto ciò che è stato eseguito (con il risultato di ogni azione), **esportazione / importazione** in
JSON, interfaccia in **23 lingue**.

## I permessi, passo dopo passo

Flare non ha mai bisogno del root. Ogni azione mostra l'accesso che le manca, e la scheda **Permessi** raccoglie tutto.

1. **Opzioni sviluppatore**: Impostazioni › Info sul telefono › Informazioni sul software, tocca 7 volte *Numero
   build*.
2. **Shizuku**: installa [Shizuku](https://shizuku.rikka.app/download/), attiva *Debug wireless* nelle opzioni
   sviluppatore (in Wi-Fi), poi in Shizuku: *Avvia tramite debug wireless* › *Associa*, e inserisci il codice di
   associazione nella notifica.
3. In Flare › **Permessi**: *Shizuku › Concedi*, poi **Concedi tutto**. Flare si concede da solo i permessi di sistema
   permanenti, l'accessibilità, l'accesso alle notifiche, Non disturbare, la visualizzazione sopra le altre app,
   nessuna restrizione della batteria e i permessi Android.
4. Su Samsung: Impostazioni › App › Flare › Batteria › **Senza restrizioni**.

Dopo un riavvio, Shizuku va riavviato (passo 2, senza rifare l'associazione) per le azioni Wi-Fi, dati mobili,
Bluetooth, modalità aereo, tema scuro e comandi. Il resto continua a funzionare grazie ai permessi di sistema
permanenti.

Senza Shizuku, da un computer:

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

Se l'accessibilità o l'accesso alle notifiche sono in grigio (app installata fuori dal Play Store): Informazioni app ›
⋮ › *Consenti impostazioni con limitazioni*.

## Con Opus

L'esempio «Modalità auto» apre `opus://flow`, che avvia il Flow di [Opus](https://github.com/apkangelo/opus). L'azione
*Riproduci musica in un'app* funziona anche con qualsiasi lettore (Spotify, YouTube Music, Deezer…): passa dalla
sessione multimediale dell'app o le invia il tasto «riproduci».

## Compilarlo da sé

Servono il JDK 21 e l'SDK Android (o Android Studio).

```sh
python3 scripts/i18n.py        # rigenera i testi Android da i18n/*.json
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

A ogni modifica, GitHub Actions controlla le 23 traduzioni, compila e pubblica l'APK. Un secondo workflow avvia l'app
in un emulatore Android 15, simula il collegamento del caricabatterie per verificare che il motore reagisca e fa le
schermate qui sopra.

## Firma dell'APK

Tutte le versioni sono firmate con **la stessa chiave** (`signing/flare.jks`) perché gli aggiornamenti si installino
sopra. Questa chiave è pubblica nel repository: installa Flare solo da questo repository. Per usare una chiave privata,
aggiungi in *Settings › Secrets › Actions* i secret `FLARE_KEYSTORE_B64` (il file .jks in base64),
`FLARE_KEYSTORE_PASSWORD`, `FLARE_KEY_ALIAS` e `FLARE_KEY_PASSWORD`.

## Buono a sapersi

- Automazioni, registro e impostazioni restano **sul telefono**. Flare non ha accesso a Internet.
- I comandi shell girano con i permessi ADB di Shizuku: incolla solo comandi che capisci.
- Il nome della rete Wi-Fi è leggibile solo con il permesso di posizione (regola di Android).

## Organizzazione del repository

| Cartella | Contenuto |
|---|---|
| `android/` | L'app (Kotlin, Jetpack Compose): motore in `core/`, servizi in `service/`, schermate in `ui/` |
| `i18n/` | I testi dell'interfaccia nelle 23 lingue |
| `scripts/` | Generazione dei testi, test su emulatore, aiuto alla compilazione |
| `docs/` | Schermate e README tradotti |
| `signing/` | Chiave di firma dell'APK |

## Le altre app apk-fold

- [Haiku](https://github.com/apkangelo/haiku): gestore di password offline
- [Opus](https://github.com/apkangelo/opus): lettore musicale per Navidrome
- [Sonnet](https://github.com/apkangelo/sonnet): client Jellyfin
- [Mythos](https://github.com/apkangelo/mythos): suite per l'ufficio sincronizzata senza server
- [Fable](https://github.com/apkangelo/fable): registro delle ore di lavoro
- [Cyber](https://github.com/apkangelo/Cyber): terminale SSH e shell locale
