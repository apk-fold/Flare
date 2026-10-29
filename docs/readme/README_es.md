<div align="center">

[English](README_en.md) · [简体中文](README_zh.md) · [繁體中文](README_zh-TW.md) · [日本語](README_ja.md) · [한국어](README_ko.md) · [Türkçe](README_tr.md) · [Русский](README_ru.md) · [Tiếng Việt](README_vi.md) · [ไทย](README_th.md) · [Deutsch](README_de.md) · **Español** · [Français](../../README.md) · [Українська](README_uk.md) · [Polski](README_pl.md) · [Português (Brasil)](README_pt-BR.md) · [العربية](README_ar.md) · [فارسی](README_fa.md) · [Bahasa Indonesia](README_id.md) · [Italiano](README_it.md)
· [Ελληνικά](README_el.md) · [മലയാളം](README_ml.md) · [Roman Urdu](README_ur-Latn.md) · [Română](README_ro.md)

</div>

<p align="center"><img src="../../build/icon.png" width="120" alt="Flare"></p>

# Flare

Automatizaciones para **Android**, en la línea de la app Atajos de Apple pero más completas que «Modos y rutinas» de
Samsung: **un activador** (dispositivo Bluetooth conectado, Wi-Fi, cargador, hora, app abierta, teléfono
desplegado…), **condiciones** opcionales y luego **una serie de acciones** (ajustes, apps, música, comandos…), además
de acciones «cuando termine» que lo deshacen todo en el momento justo.

Ejemplo incluido en la app: cuando el teléfono se conecta por Bluetooth al coche, Flare activa No molestar, ajusta el
volumen, inicia la música en [Opus](https://github.com/apkangelo/opus) y abre Maps; al desconectarse, pausa la música
y desactiva No molestar.

Pensado para el Galaxy Z Fold con One UI: activadores «teléfono plegado / desplegado», y sin root (Samsung ya no
permite desbloquear el bootloader) gracias a Shizuku.

## Descargar

**[Flare.apk](https://github.com/apkangelo/Flare/releases/latest/download/Flare.apk)** (Android 8 o posterior). Abre
el archivo, permite instalar desde esta fuente y luego *Instalar*. Las actualizaciones se instalan encima sin perder
tus automatizaciones. Todas las versiones están en la pestaña [*Releases*](https://github.com/apkangelo/Flare/releases),
compiladas por GitHub Actions con cada cambio.

## Capturas

| Automatizaciones | Editor | Registro | Permisos |
|---|---|---|---|
| <img src="../screenshots/automations.png" width="200"> | <img src="../screenshots/editor.png" width="200"> | <img src="../screenshots/log.png" width="200"> | <img src="../screenshots/rights.png" width="200"> |

## Qué hace Flare

**Activadores (27)**: dispositivo Bluetooth conectado / desconectado (basta parte del nombre: «Media Nav» reconoce
«MEDIA-NAV»), Wi-Fi conectado / desconectado, modo avión, cargador enchufado / desenchufado, nivel de batería,
teléfono encendido, pantalla encendida / apagada, desbloqueo, **teléfono plegado / desplegado**, auriculares
conectados / desconectados, hora concreta y días, cada X minutos, app abierta / cerrada, notificación recibida (app,
texto), llamada entrante / iniciada / terminada, SMS recibido, llegada a un lugar / salida.

**Condiciones (11)**: franja horaria, días de la semana, batería, cargando, dispositivo Bluetooth conectado, red Wi-Fi,
plegado / desplegado, auriculares, pantalla encendida, modo de sonido, app en pantalla.

**Acciones (40)**
- *Conexiones*: Wi-Fi, datos móviles, Bluetooth, modo avión, NFC, ubicación (activar, desactivar, invertir).
- *Pantalla*: brillo, tiempo de espera, giro, modo oscuro, mantener encendida al cargar, ahorro de energía, linterna,
  bloquear, encender la pantalla, captura de pantalla.
- *Sonido y multimedia*: modo de sonido, No molestar, volumen de cada canal, reproducir / pausa / siguiente, iniciar la
  música en una app concreta, leer un texto en voz alta, vibrar.
- *Apps*: abrir una app, un enlace o un enlace profundo (`opus://flow`, `tel:`, `geo:`…), navegación en Maps, forzar
  detención, abrir una página de ajustes, enviar un intent.
- *Control de la pantalla*: botones del sistema (atrás, inicio, recientes, panel de notificaciones, ajustes rápidos,
  menú de apagado, pantalla dividida), tocar un texto en pantalla, borrar notificaciones.
- *Llamadas y SMS*: enviar un SMS, llamar.
- *Sistema*: notificación, **comando shell**, **cualquier ajuste del sistema** (`system`, `secure`, `global`), que
  cubre también los ajustes propios de One UI.
- *Flujo*: esperar, ejecutar un atajo, activar / desactivar una automatización.

**Atajos**: listas de acciones sin activador, lanzadas con un toque desde la app, desde la **pantalla de inicio** o
desde uno de los **3 mosaicos de ajustes rápidos**.

**Registro** de todo lo ejecutado (con el resultado de cada acción), **exportar / importar** en JSON, interfaz en
**23 idiomas**.

## Los permisos, paso a paso

Flare nunca necesita root. Cada acción indica qué acceso le falta, y la pestaña **Permisos** lo reúne todo.

1. **Opciones para desarrolladores**: Ajustes › Información del teléfono › Información de software, toca 7 veces
   *Número de compilación*.
2. **Shizuku**: instala [Shizuku](https://shizuku.rikka.app/download/), activa *Depuración inalámbrica* en las opciones
   para desarrolladores (con Wi-Fi) y luego en Shizuku: *Iniciar mediante depuración inalámbrica* › *Vincular*, e
   introduce el código de vinculación en la notificación.
3. En Flare › **Permisos**: *Shizuku › Conceder* y luego **Concederlo todo**. Flare se concede a sí mismo los permisos
   de sistema permanentes, la accesibilidad, el acceso a las notificaciones, No molestar, mostrar sobre otras apps, sin
   restricciones de batería y los permisos de Android.
4. En Samsung: Ajustes › Aplicaciones › Flare › Batería › **Sin restricciones**.

Tras reiniciar, hay que volver a iniciar Shizuku (paso 2, sin volver a vincular) para las acciones de Wi-Fi, datos
móviles, Bluetooth, modo avión, modo oscuro y comandos. Lo demás sigue funcionando gracias a los permisos de sistema
permanentes.

Sin Shizuku, desde un ordenador:

```sh
adb shell pm grant fr.apkfold.flare android.permission.WRITE_SECURE_SETTINGS
```

Si la accesibilidad o el acceso a las notificaciones aparecen en gris (app instalada fuera de Play Store): Información
de la app › ⋮ › *Permitir ajustes restringidos*.

## Con Opus

El ejemplo «Modo coche» abre `opus://flow`, que inicia el Flow de [Opus](https://github.com/apkangelo/opus). La acción
*Reproducir música en una app* funciona también con cualquier reproductor (Spotify, YouTube Music, Deezer…): usa la
sesión multimedia de la app o le envía la tecla «reproducir».

## Compilarlo tú mismo

Necesitas el JDK 21 y el SDK de Android (o Android Studio).

```sh
python3 scripts/i18n.py        # regenera los textos de Android desde i18n/*.json
cd android
./gradlew assembleRelease      # → android/app/build/outputs/apk/release/app-release.apk
```

Con cada cambio, GitHub Actions comprueba las 23 traducciones, compila y publica el APK. Un segundo workflow ejecuta
la app en un emulador de Android 15, simula enchufar el cargador para comprobar que el motor reacciona y hace las
capturas de arriba.

## Firma del APK

Todas las versiones se firman con **la misma clave** (`signing/flare.jks`) para que las actualizaciones se instalen
encima. Esta clave es pública en el repositorio: instala Flare solo desde este repositorio. Para usar una clave
privada, añade en *Settings › Secrets › Actions* los secretos `FLARE_KEYSTORE_B64` (el archivo .jks en base64),
`FLARE_KEYSTORE_PASSWORD`, `FLARE_KEY_ALIAS` y `FLARE_KEY_PASSWORD`.

## Conviene saber

- Las automatizaciones, el registro y los ajustes se quedan **en el teléfono**. Flare no tiene acceso a Internet.
- Los comandos shell se ejecutan con los permisos ADB de Shizuku: pega solo comandos que entiendas.
- El nombre de la red Wi-Fi solo se puede leer con el permiso de ubicación (norma de Android).

## Organización del repositorio

| Carpeta | Contenido |
|---|---|
| `android/` | La app (Kotlin, Jetpack Compose): motor en `core/`, servicios en `service/`, pantallas en `ui/` |
| `i18n/` | Los textos de la interfaz en los 23 idiomas |
| `scripts/` | Generación de textos, prueba en emulador, ayuda para compilar |
| `docs/` | Capturas y README traducidos |
| `signing/` | Clave de firma del APK |

## Las otras apps de apk-fold

- [Haiku](https://github.com/apkangelo/haiku): gestor de contraseñas sin conexión
- [Opus](https://github.com/apkangelo/opus): reproductor de música para Navidrome
- [Sonnet](https://github.com/apkangelo/sonnet): cliente de Jellyfin
- [Mythos](https://github.com/apkangelo/mythos): suite ofimática que se sincroniza sin servidor
- [Fable](https://github.com/apkangelo/fable): registro de horas de trabajo
- [Cyber](https://github.com/apkangelo/Cyber): terminal SSH y shell local
