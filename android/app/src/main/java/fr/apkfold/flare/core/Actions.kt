package fr.apkfold.flare.core

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.UiModeManager
import android.bluetooth.BluetoothManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.browse.MediaBrowser
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.nfc.NfcAdapter
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.telephony.SmsManager
import android.view.KeyEvent
import fr.apkfold.flare.R
import fr.apkfold.flare.service.FlareAccessibility
import fr.apkfold.flare.service.FlareNotificationListener
import fr.apkfold.flare.ui.MainActivity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

/** Exécution des actions. Chaque action choisit le meilleur chemin selon les accès disponibles. */
object Actions {

    suspend fun exec(ctx: Context, s: Step, depth: Int): Outcome { return when (s.type) {
        // ---- Connexions ----
        "wifi" -> {
            val on = want(s) { ctx.applicationContext.getSystemService(WifiManager::class.java).isWifiEnabled }
            shell(ctx, "svc wifi ${en(on)}")
        }
        "data" -> {
            val on = want(s) { Shell.run(ctx, "settings get global mobile_data")?.out?.trim() == "1" }
            shell(ctx, "svc data ${en(on)}")
        }
        "bluetooth" -> {
            val on = want(s) { bluetoothOn(ctx) }
            shell(ctx, "cmd bluetooth_manager ${en(on)} || svc bluetooth ${en(on)}")
        }
        "airplane_set" -> {
            val on = want(s) { Settings.Global.getInt(ctx.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 1 }
            shell(
                ctx,
                "cmd connectivity airplane-mode ${en(on)} || (settings put global airplane_mode_on ${if (on) 1 else 0} && " +
                    "am broadcast -a android.intent.action.AIRPLANE_MODE --ez state $on)",
            )
        }
        "nfc" -> {
            val on = want(s) { NfcAdapter.getDefaultAdapter(ctx)?.isEnabled == true }
            shell(ctx, "svc nfc ${en(on)}")
        }
        "location" -> {
            val on = want(s) { locationOn(ctx) }
            shell(ctx, "cmd location set-location-enabled $on || settings put secure location_mode ${if (on) 3 else 0}")
        }

        // ---- Écran ----
        "brightness" -> {
            if (s.p("mode") == "auto") {
                putSystem(ctx, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC.toString())
            } else {
                val pct = (s.p("level").toIntOrNull() ?: 50).coerceIn(0, 100)
                val v = (pct * 255 / 100).coerceIn(1, 255)
                val r = putSystem(ctx, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL.toString())
                if (!r.ok) r else putSystem(ctx, Settings.System.SCREEN_BRIGHTNESS, v.toString())
            }
        }
        "timeout" -> {
            val sec = (s.p("seconds").toIntOrNull() ?: 60).coerceIn(5, 86_400)
            putSystem(ctx, Settings.System.SCREEN_OFF_TIMEOUT, (sec * 1000).toString())
        }
        "rotation" -> {
            val on = want(s) { Settings.System.getInt(ctx.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1 }
            putSystem(ctx, Settings.System.ACCELEROMETER_ROTATION, if (on) "1" else "0")
        }
        "dark" -> {
            val on = want(s) { darkOn(ctx) }
            shell(ctx, "cmd uimode night ${if (on) "yes" else "no"}")
        }
        "stay_awake" -> {
            val v = if (s.p("state") == "off") "0" else "7"
            putProtected(ctx, "global", "stay_on_while_plugged_in", v)
        }
        "battery_saver" -> {
            val v = if (s.p("state") == "off") "0" else "1"
            shell(ctx, "cmd power set-mode $v || settings put global low_power $v")
        }
        "flashlight" -> flashlight(ctx, want(s) { DeviceState.torchOn })
        "lock" -> {
            val acc = FlareAccessibility.instance
            if (acc != null && Build.VERSION.SDK_INT >= 28) {
                Outcome(acc.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN))
            } else shell(ctx, "input keyevent 223")
        }
        "wake" -> shell(ctx, "input keyevent 224")
        "screenshot" -> {
            val acc = FlareAccessibility.instance
            if (acc != null && Build.VERSION.SDK_INT >= 28) {
                Outcome(acc.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT))
            } else shell(ctx, "input keyevent 120")
        }

        // ---- Son et médias ----
        "ringer" -> {
            val am = ctx.getSystemService(AudioManager::class.java)
            am.ringerMode = when (s.p("mode")) {
                "silent" -> AudioManager.RINGER_MODE_SILENT
                "normal" -> AudioManager.RINGER_MODE_NORMAL
                else -> AudioManager.RINGER_MODE_VIBRATE
            }
            Outcome(true)
        }
        "dnd" -> dnd(ctx, s.p("mode").ifBlank { "priority" })
        "volume" -> {
            val am = ctx.getSystemService(AudioManager::class.java)
            val stream = when (s.p("stream")) {
                "ring" -> AudioManager.STREAM_RING
                "notification" -> AudioManager.STREAM_NOTIFICATION
                "alarm" -> AudioManager.STREAM_ALARM
                "call" -> AudioManager.STREAM_VOICE_CALL
                "system" -> AudioManager.STREAM_SYSTEM
                else -> AudioManager.STREAM_MUSIC
            }
            val pct = (s.p("level").toIntOrNull() ?: 50).coerceIn(0, 100)
            val max = am.getStreamMaxVolume(stream)
            am.setStreamVolume(stream, Math.round(max * pct / 100f), 0)
            Outcome(true)
        }
        "media" -> {
            mediaKey(ctx, null, mediaCode(s.p("command")))
            Outcome(true)
        }
        "play_app" -> playInApp(ctx, s.p("app"), s.p("open_first") != "no")
        "speak" -> speak(ctx, s.p("text"))
        "vibrate" -> {
            val ms = (s.p("ms").toLongOrNull() ?: 400L).coerceIn(10L, 10_000L)
            vibrator(ctx).vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
            Outcome(true)
        }

        // ---- Applis ----
        "launch" -> {
            val i = ctx.packageManager.getLaunchIntentForPackage(s.p("app"))
            if (i == null) Outcome(false, ctx.getString(R.string.err_not_found, s.p("app"))) else startUi(ctx, i)
        }
        "uri" -> {
            val u = s.p("uri").trim()
            val i = if (u.startsWith("intent:")) Intent.parseUri(u, Intent.URI_INTENT_SCHEME) else Intent(Intent.ACTION_VIEW, Uri.parse(u))
            startUi(ctx, i)
        }
        "navigate" -> {
            val dest = s.p("destination").trim()
            val mode = s.p("travel").ifBlank { "d" }
            val maps = "com.google.android.apps.maps"
            val i = if (Shell.isInstalled(ctx, maps)) {
                Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + Uri.encode(dest) + "&mode=" + mode)).setPackage(maps)
            } else {
                Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(dest)))
            }
            startUi(ctx, i)
        }
        "force_stop" -> shell(ctx, "am force-stop " + Shell.q(s.p("app")))
        "settings_page" -> startUi(ctx, Intent(settingsAction(s.p("page"))))
        "intent" -> sendIntent(ctx, s)

        // ---- Pilotage de l'écran ----
        "global" -> {
            val acc = FlareAccessibility.instance ?: return Outcome(false, missing(ctx, Need.ACCESSIBILITY))
            val action = when (s.p("button")) {
                "home" -> AccessibilityService.GLOBAL_ACTION_HOME
                "recents" -> AccessibilityService.GLOBAL_ACTION_RECENTS
                "notifications" -> AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS
                "quick" -> AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS
                "power" -> AccessibilityService.GLOBAL_ACTION_POWER_DIALOG
                "split" -> AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN
                else -> AccessibilityService.GLOBAL_ACTION_BACK
            }
            Outcome(acc.performGlobalAction(action))
        }
        "click" -> {
            val acc = FlareAccessibility.instance ?: return Outcome(false, missing(ctx, Need.ACCESSIBILITY))
            if (withContext(Dispatchers.Main) { acc.clickText(s.p("text")) }) Outcome(true)
            else Outcome(false, ctx.getString(R.string.err_not_found, s.p("text")))
        }
        "clear_notifs" -> {
            val l = FlareNotificationListener.instance ?: return Outcome(false, missing(ctx, Need.NOTIF_LISTENER))
            l.cancelAllNotifications()
            Outcome(true)
        }

        // ---- Appels et SMS ----
        "send_sms" -> {
            val sms = if (Build.VERSION.SDK_INT >= 31) ctx.getSystemService(SmsManager::class.java) else @Suppress("DEPRECATION") SmsManager.getDefault()
            val parts = sms.divideMessage(s.p("message"))
            sms.sendMultipartTextMessage(s.p("number").trim(), null, parts, null, null)
            Outcome(true)
        }
        "call" -> startUi(ctx, Intent(Intent.ACTION_CALL, Uri.fromParts("tel", s.p("number").trim(), null)))

        // ---- Système ----
        "notify" -> notify(ctx, s.p("title"), s.p("text"))
        "shell" -> {
            val r = Shell.run(ctx, s.p("command"), 60_000) ?: return Outcome(false, ctx.getString(R.string.err_shell))
            Outcome(r.ok, (if (r.ok) r.out else r.message).trim().take(400))
        }
        "setting" -> putProtected(ctx, s.p("namespace").ifBlank { "system" }, s.p("key").trim(), s.p("value"))

        // ---- Déroulement ----
        "wait" -> {
            val sec = (s.p("seconds").toDoubleOrNull() ?: 1.0).coerceIn(0.0, 3600.0)
            delay((sec * 1000).toLong())
            Outcome(true)
        }
        "run" -> {
            val target = Store.get(s.p("routine"))
            when {
                target == null -> Outcome(false, ctx.getString(R.string.err_not_found, s.p("routine")))
                depth >= 4 -> Outcome(false, ctx.getString(R.string.err_loop))
                else -> {
                    Engine.execute(ctx, target, target.actions, ctx.getString(R.string.log_manual), depth + 1)
                    Outcome(true, target.name)
                }
            }
        }
        "toggle_auto" -> {
            val target = Store.get(s.p("routine"))
            if (target == null) Outcome(false, ctx.getString(R.string.err_not_found, s.p("routine")))
            else {
                val on = want(s) { target.enabled }
                Store.setEnabled(target.id, on)
                Outcome(true, target.name)
            }
        }
        else -> Outcome(false, s.type)
    } }

    // ---- Outils ----

    private fun want(s: Step, current: () -> Boolean): Boolean = when (s.p("state")) {
        "off" -> false
        "toggle" -> !current()
        else -> true
    }

    private fun en(on: Boolean) = if (on) "enable" else "disable"

    private fun missing(ctx: Context, n: Need) = ctx.getString(R.string.err_access, ctx.getString(n.label))

    private fun shell(ctx: Context, cmd: String): Outcome {
        val r = Shell.run(ctx, cmd) ?: return Outcome(false, ctx.getString(R.string.err_shell))
        return if (r.ok) Outcome(true) else Outcome(false, r.message.ifBlank { "code ${r.code}" })
    }

    private fun bluetoothOn(ctx: Context): Boolean = try {
        ctx.getSystemService(BluetoothManager::class.java)?.adapter?.isEnabled == true
    } catch (_: SecurityException) {
        Shell.run(ctx, "settings get global bluetooth_on")?.out?.trim() == "1"
    }

    private fun locationOn(ctx: Context): Boolean {
        val lm = ctx.getSystemService(LocationManager::class.java)
        return if (Build.VERSION.SDK_INT >= 28) lm.isLocationEnabled
        else @Suppress("DEPRECATION") Settings.Secure.getInt(ctx.contentResolver, Settings.Secure.LOCATION_MODE, 0) != 0
    }

    private fun darkOn(ctx: Context): Boolean {
        val um = ctx.getSystemService(UiModeManager::class.java)
        return when (um.nightMode) {
            UiModeManager.MODE_NIGHT_YES -> true
            UiModeManager.MODE_NIGHT_NO -> false
            else -> (ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }
    }

    /** Réglage « system » : API si l'appli a le droit de modifier les paramètres, sinon Shizuku/root. */
    private fun putSystem(ctx: Context, key: String, value: String): Outcome {
        if (Settings.System.canWrite(ctx)) {
            try {
                Settings.System.putString(ctx.contentResolver, key, value)
                return Outcome(true)
            } catch (_: Exception) {
            }
        }
        val r = Shell.run(ctx, "settings put system ${Shell.q(key)} ${Shell.q(value)}")
            ?: return Outcome(false, missing(ctx, Need.WRITE_SETTINGS))
        return if (r.ok) Outcome(true) else Outcome(false, r.message)
    }

    /** Réglage de n'importe quelle table : Shizuku/root d'abord, puis droits système permanents. */
    private fun putProtected(ctx: Context, ns: String, key: String, value: String): Outcome {
        if (key.isBlank()) return Outcome(false, ctx.getString(R.string.err_not_found, "key"))
        val table = if (ns in setOf("system", "secure", "global")) ns else "system"
        Shell.run(ctx, "settings put $table ${Shell.q(key)} ${Shell.q(value)}")?.let {
            return if (it.ok) Outcome(true) else Outcome(false, it.message)
        }
        return try {
            when (table) {
                "secure" -> Settings.Secure.putString(ctx.contentResolver, key, value)
                "global" -> Settings.Global.putString(ctx.contentResolver, key, value)
                else -> return putSystem(ctx, key, value)
            }
            Outcome(true)
        } catch (_: Exception) {
            Outcome(false, ctx.getString(R.string.err_shell))
        }
    }

    private fun flashlight(ctx: Context, on: Boolean): Outcome {
        val cm = ctx.getSystemService(CameraManager::class.java)
        val ids = cm.cameraIdList.filter {
            cm.getCameraCharacteristics(it).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
        val id = ids.firstOrNull {
            cm.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
        } ?: ids.firstOrNull() ?: return Outcome(false, ctx.getString(R.string.err_not_found, "flash"))
        cm.setTorchMode(id, on)
        DeviceState.torchOn = on
        return Outcome(true)
    }

    private fun dnd(ctx: Context, mode: String): Outcome {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        if (nm.isNotificationPolicyAccessGranted) {
            nm.setInterruptionFilter(
                when (mode) {
                    "off" -> NotificationManager.INTERRUPTION_FILTER_ALL
                    "alarms" -> NotificationManager.INTERRUPTION_FILTER_ALARMS
                    "none" -> NotificationManager.INTERRUPTION_FILTER_NONE
                    else -> NotificationManager.INTERRUPTION_FILTER_PRIORITY
                },
            )
            return Outcome(true)
        }
        return shell(ctx, "cmd notification set_dnd $mode")
    }

    private fun mediaCode(cmd: String) = when (cmd) {
        "play" -> KeyEvent.KEYCODE_MEDIA_PLAY
        "pause" -> KeyEvent.KEYCODE_MEDIA_PAUSE
        "next" -> KeyEvent.KEYCODE_MEDIA_NEXT
        "previous" -> KeyEvent.KEYCODE_MEDIA_PREVIOUS
        "stop" -> KeyEvent.KEYCODE_MEDIA_STOP
        else -> KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
    }

    /** Touche média vers la lecture active, ou vers une appli précise si [pkg] est donné. */
    private fun mediaKey(ctx: Context, pkg: String?, code: Int) {
        if (pkg == null) {
            val am = ctx.getSystemService(AudioManager::class.java)
            am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, code))
            am.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, code))
        } else {
            for (action in intArrayOf(KeyEvent.ACTION_DOWN, KeyEvent.ACTION_UP)) {
                ctx.sendBroadcast(
                    Intent(Intent.ACTION_MEDIA_BUTTON).setPackage(pkg).putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(action, code)),
                )
            }
        }
    }

    /**
     * Lance la lecture dans une appli : session active (accès aux notifications), sinon connexion à son
     * service média, sinon touche « lecture » envoyée à l'appli.
     */
    private suspend fun playInApp(ctx: Context, pkg: String, openFirst: Boolean): Outcome {
        if (pkg.isBlank()) return Outcome(false, ctx.getString(R.string.err_not_found, "app"))
        if (openFirst) {
            val i = ctx.packageManager.getLaunchIntentForPackage(pkg)
                ?: return Outcome(false, ctx.getString(R.string.err_not_found, pkg))
            startUi(ctx, i)
            delay(2500)
        }
        if (Perms.notificationListenerEnabled(ctx)) {
            try {
                val msm = ctx.getSystemService(MediaSessionManager::class.java)
                val c = msm.getActiveSessions(Perms.listenerComponent(ctx)).firstOrNull { it.packageName == pkg }
                if (c != null) {
                    c.transportControls.play()
                    return Outcome(true)
                }
            } catch (_: SecurityException) {
            }
        }
        if (browserPlay(ctx, pkg)) return Outcome(true)
        mediaKey(ctx, pkg, KeyEvent.KEYCODE_MEDIA_PLAY)
        return Outcome(true)
    }

    private suspend fun browserPlay(ctx: Context, pkg: String): Boolean = withContext(Dispatchers.Main) {
        val services = ctx.packageManager.queryIntentServices(Intent("android.media.browse.MediaBrowserService").setPackage(pkg), 0)
        val info = services.firstOrNull()?.serviceInfo ?: return@withContext false
        val comp = ComponentName(pkg, info.name)
        val holder = arrayOfNulls<MediaBrowser>(1)
        val result = withTimeoutOrNull(6000) {
            suspendCancellableCoroutine<Boolean> { cont ->
                val cb = object : MediaBrowser.ConnectionCallback() {
                    override fun onConnected() {
                        val ok = try {
                            val token = holder[0]?.sessionToken
                            if (token != null) {
                                MediaController(ctx, token).transportControls.play()
                                true
                            } else false
                        } catch (_: Exception) {
                            false
                        }
                        if (cont.isActive) cont.resume(ok)
                    }

                    override fun onConnectionFailed() {
                        if (cont.isActive) cont.resume(false)
                    }
                }
                val b = MediaBrowser(ctx, comp, cb, null)
                holder[0] = b
                cont.invokeOnCancellation {
                    try {
                        b.disconnect()
                    } catch (_: Exception) {
                    }
                }
                b.connect()
            }
        }
        result ?: false
    }

    private var tts: TextToSpeech? = null
    private var ttsReady = CompletableDeferred<Boolean>()

    private suspend fun speak(ctx: Context, text: String): Outcome {
        if (text.isBlank()) return Outcome(true)
        if (tts == null) {
            withContext(Dispatchers.Main) {
                val ready = CompletableDeferred<Boolean>()
                ttsReady = ready
                tts = TextToSpeech(ctx.applicationContext) { status -> ready.complete(status == TextToSpeech.SUCCESS) }
            }
        }
        val ok = withTimeoutOrNull(10_000) { ttsReady.await() } ?: false
        val engine = tts
        if (!ok || engine == null) {
            tts = null
            return Outcome(false, "TTS")
        }
        engine.setLanguage(Locale.getDefault())
        engine.speak(text, TextToSpeech.QUEUE_ADD, null, "flare-" + System.nanoTime())
        return Outcome(true)
    }

    private fun vibrator(ctx: Context): Vibrator = if (Build.VERSION.SDK_INT >= 31) {
        ctx.getSystemService(VibratorManager::class.java).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        ctx.getSystemService(Vibrator::class.java)
    }

    /** Ouvre une activité depuis l'arrière-plan (via l'accessibilité si elle est active). */
    fun startUi(ctx: Context, intent: Intent): Outcome {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            val acc = FlareAccessibility.instance
            if (acc != null) acc.startActivity(intent) else ctx.startActivity(intent)
            Outcome(true)
        } catch (e: ActivityNotFoundException) {
            Outcome(false, ctx.getString(R.string.err_not_found, intent.data?.toString() ?: intent.`package` ?: intent.action.orEmpty()))
        }
    }

    private fun settingsAction(page: String) = when (page) {
        "bluetooth" -> Settings.ACTION_BLUETOOTH_SETTINGS
        "display" -> Settings.ACTION_DISPLAY_SETTINGS
        "sound" -> Settings.ACTION_SOUND_SETTINGS
        "battery" -> Intent.ACTION_POWER_USAGE_SUMMARY
        "location" -> Settings.ACTION_LOCATION_SOURCE_SETTINGS
        "apps" -> Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS
        "nfc" -> Settings.ACTION_NFC_SETTINGS
        "accessibility" -> Settings.ACTION_ACCESSIBILITY_SETTINGS
        "developer" -> Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
        else -> Settings.ACTION_WIFI_SETTINGS
    }

    private fun sendIntent(ctx: Context, s: Step): Outcome {
        val i = Intent()
        if (s.p("action").isNotBlank()) i.action = s.p("action").trim()
        if (s.p("data").isNotBlank()) i.data = Uri.parse(s.p("data").trim())
        if (s.p("package").isNotBlank()) i.setPackage(s.p("package").trim())
        s.p("extras").split(';', '\n').forEach { kv ->
            val k = kv.substringBefore('=', "").trim()
            if (k.isNotEmpty()) i.putExtra(k, kv.substringAfter('=').trim())
        }
        return if (s.p("kind") == "broadcast") {
            ctx.sendBroadcast(i)
            Outcome(true)
        } else startUi(ctx, i)
    }

    @SuppressLint("MissingPermission")
    private fun notify(ctx: Context, title: String, text: String): Outcome {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        val open = PendingIntent.getActivity(
            ctx, 0, Intent(ctx, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val n = Notification.Builder(ctx, FlareChannels.ALERTS)
            .setSmallIcon(R.drawable.ic_stat_flare)
            .setContentTitle(title.ifBlank { ctx.getString(R.string.app_name) })
            .setContentText(text)
            .setStyle(Notification.BigTextStyle().bigText(text))
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        nm.notify((System.currentTimeMillis() % 100_000).toInt() + 100, n)
        return Outcome(true)
    }
}
