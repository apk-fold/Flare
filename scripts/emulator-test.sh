#!/usr/bin/env bash
# Test sur émulateur : installe la version de développement, accorde les accès comme le ferait
# « Tout accorder », parcourt les écrans (captures dans docs/screenshots), simule un branchement du
# chargeur pour vérifier que le moteur réagit, puis cherche un éventuel plantage.
set -u
P=fr.apkfold.flare
APK=android/app/build/outputs/apk/debug/app-debug.apk
OUT=docs/screenshots
mkdir -p "$OUT"

adb wait-for-device
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
adb install -r -g "$APK" || { echo "::error title=Installation::échec de adb install"; exit 1; }

adb shell pm grant $P android.permission.WRITE_SECURE_SETTINGS
adb shell appops set $P SYSTEM_ALERT_WINDOW allow
adb shell appops set $P WRITE_SETTINGS allow
adb shell cmd notification allow_dnd $P || true
adb shell cmd notification allow_listener $P/$P.service.FlareNotificationListener || true
adb shell settings put secure enabled_accessibility_services $P/$P.service.FlareAccessibility
adb shell settings put secure accessibility_enabled 1
adb shell dumpsys deviceidle whitelist +$P || true
adb shell cmd uimode night no
adb logcat -c

start() { adb shell am start -n $P/.ui.MainActivity "$@" >/dev/null; }
shot() { sleep 4; adb exec-out screencap -p > "$OUT/$1.png"; echo "capture $1 : $(wc -c < "$OUT/$1.png") octets"; }

start --ez demo true --ei tab 0
sleep 6
shot automations
start --ei tab 1
shot shortcuts
start --ez edit_first true
shot editor

# Lancement d'un raccourci + simulation du chargeur (déclencheur « Chargeur branché »)
start --ez run_first_shortcut true --ei tab 2
sleep 3
adb shell dumpsys battery unplug
sleep 3
adb shell dumpsys battery set ac 1
sleep 5
adb shell dumpsys battery reset
start --ei tab 2
shot log
start --ei tab 3
shot rights

adb shell cmd uimode night yes
sleep 2
start --ei tab 0
shot automations-dark

# Le moteur a-t-il réagi au chargeur ? (notification « Chargeur branché » publiée par l'action)
NOTIF=$(adb shell dumpsys notification --noredact | grep -c "android.title=String (Flare)" || true)
echo "::notice title=Moteur::notifications Flare publiées pendant le test : $NOTIF"

SVC=$(adb shell dumpsys activity services $P | grep -c "FlareService" || true)
echo "::notice title=Service::FlareService présent : $SVC"

CRASH=$(adb logcat -d -b crash)
if [ -n "$CRASH" ]; then
  echo "::error title=Plantage::$(echo "$CRASH" | head -c 3500 | tr '\n' ' ')"
  exit 1
fi
ERRS=$(adb logcat -d | grep -E "E AndroidRuntime|FATAL EXCEPTION|$P.*Exception" | head -20)
if [ -n "$ERRS" ]; then echo "::warning title=Erreurs logcat::$(echo "$ERRS" | head -c 3000 | tr '\n' ' ')"; fi
echo "::notice title=Émulateur::aucun plantage"
