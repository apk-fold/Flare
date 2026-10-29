package fr.apkfold.flare.core

import android.content.Context
import android.hardware.display.DisplayManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.PowerManager
import android.view.Display
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.min

/** État de l'appareil suivi par le service (Bluetooth, Wi-Fi, appli à l'écran…). */
object DeviceState {
    /** Appareils Bluetooth connectés : adresse → nom. */
    val btConnected = ConcurrentHashMap<String, String>()

    @Volatile var wifiConnected = false
    @Volatile var ssid: String? = null
    @Volatile var foregroundApp: String? = null
    @Volatile var batteryLevel = -1
    @Volatile var charging = false
    @Volatile var torchOn = false

    /**
     * Galaxy Z Fold : l'écran principal (presque carré) est actif quand le téléphone est déplié,
     * l'écran externe (très allongé) quand il est plié.
     */
    fun isUnfolded(ctx: Context): Boolean {
        val d = ctx.getSystemService(DisplayManager::class.java).getDisplay(Display.DEFAULT_DISPLAY) ?: return false
        val m = d.mode
        val w = m.physicalWidth.toFloat()
        val h = m.physicalHeight.toFloat()
        if (w <= 0f || h <= 0f) return false
        return max(w, h) / min(w, h) < 1.6f
    }

    fun headsetConnected(ctx: Context): Boolean {
        val am = ctx.getSystemService(AudioManager::class.java)
        return am.getDevices(AudioManager.GET_DEVICES_OUTPUTS).any { isHeadset(it) }
    }

    fun isHeadset(d: AudioDeviceInfo): Boolean = d.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
        d.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
        d.type == AudioDeviceInfo.TYPE_USB_HEADSET

    fun screenOn(ctx: Context): Boolean = ctx.getSystemService(PowerManager::class.java).isInteractive

    /** « MEDIA-NAV », « Media Nav » et « medianav » se valent. */
    fun norm(s: String): String = s.lowercase().filter { it.isLetterOrDigit() }

    fun tokens(list: String): List<String> = list.split(',', ';', '\n').map { it.trim() }.filter { it.isNotEmpty() }

    /** Vrai si le nom ou l'adresse correspond à l'un des noms de la liste (liste vide : n'importe lequel). */
    fun deviceMatches(list: String, name: String?, address: String?): Boolean {
        val t = tokens(list)
        if (t.isEmpty()) return true
        val n = norm(name.orEmpty())
        return t.any { tok ->
            (address != null && tok.equals(address, ignoreCase = true)) || (norm(tok).isNotEmpty() && n.contains(norm(tok)))
        }
    }

    fun ssidMatches(list: String, current: String?): Boolean {
        val t = tokens(list)
        if (t.isEmpty()) return true
        val c = current?.trim('"') ?: return false
        return t.any { it.trim('"').equals(c, ignoreCase = true) }
    }
}
