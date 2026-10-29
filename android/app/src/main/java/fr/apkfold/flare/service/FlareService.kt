package fr.apkfold.flare.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.view.Display
import fr.apkfold.flare.R
import fr.apkfold.flare.core.DeviceState
import fr.apkfold.flare.core.Engine
import fr.apkfold.flare.core.FlareChannels
import fr.apkfold.flare.core.Scheduler
import fr.apkfold.flare.core.Store
import fr.apkfold.flare.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Service au premier plan : écoute Bluetooth, Wi-Fi, alimentation, écran, pliage, casque, appels, SMS…
 * et transmet chaque événement au moteur.
 */
class FlareService : Service() {

    companion object {
        private const val NOTIF_ID = 1

        @Volatile
        var running = false
            private set

        fun start(ctx: Context, boot: Boolean = false) {
            if (!Store.serviceEnabled) return
            try {
                ctx.startForegroundService(Intent(ctx, FlareService::class.java).putExtra("boot", boot))
            } catch (_: Exception) {
            }
        }

        fun stop(ctx: Context) {
            ctx.stopService(Intent(ctx, FlareService::class.java))
        }
    }

    private val main = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var receiverOn = false
    private var lastUnfolded: Boolean? = null
    private var lastHeadset = false
    private var lastCall = TelephonyManager.EXTRA_STATE_IDLE
    private var wifiNetwork: Network? = null
    private var pendingWifi: Runnable? = null
    private var netCallback: ConnectivityManager.NetworkCallback? = null
    private var torchCallback: CameraManager.TorchCallback? = null

    private fun ev(type: String, data: Map<String, String> = emptyMap()) = Engine.event(this, type, data)

    // ---- Diffusions système ----

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, i: Intent) {
            when (i.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED, BluetoothDevice.ACTION_ACL_DISCONNECTED -> onBluetooth(i)
                Intent.ACTION_POWER_CONNECTED -> {
                    DeviceState.charging = true
                    ev("power_on")
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    DeviceState.charging = false
                    ev("power_off")
                }
                Intent.ACTION_BATTERY_CHANGED -> onBattery(i)
                Intent.ACTION_SCREEN_ON -> ev("screen_on")
                Intent.ACTION_SCREEN_OFF -> ev("screen_off")
                Intent.ACTION_USER_PRESENT -> ev("unlock")
                Intent.ACTION_AIRPLANE_MODE_CHANGED ->
                    ev("airplane", mapOf("state" to if (i.getBooleanExtra("state", false)) "on" else "off"))
                TelephonyManager.ACTION_PHONE_STATE_CHANGED -> onCall(i)
                Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> onSms(i)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun onBluetooth(i: Intent) {
        val d: BluetoothDevice? = if (Build.VERSION.SDK_INT >= 33) {
            i.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        } else {
            @Suppress("DEPRECATION")
            i.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }
        val address = d?.address ?: return
        val name = try {
            d.name
        } catch (_: SecurityException) {
            null
        } ?: address
        if (i.action == BluetoothDevice.ACTION_ACL_CONNECTED) {
            if (DeviceState.btConnected.put(address, name) == null) ev("bt_on", mapOf("name" to name, "address" to address))
        } else {
            DeviceState.btConnected.remove(address)
            ev("bt_off", mapOf("name" to name, "address" to address))
        }
    }

    private fun onBattery(i: Intent) {
        val level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
        val pct = if (level < 0) -1 else level * 100 / scale
        DeviceState.charging = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0
        val prev = DeviceState.batteryLevel
        DeviceState.batteryLevel = pct
        if (prev >= 0 && pct >= 0 && prev != pct) ev("battery", mapOf("level" to "$pct", "prev" to "$prev"))
    }

    private fun onCall(i: Intent) {
        val state = i.getStringExtra(TelephonyManager.EXTRA_STATE) ?: return
        if (state == lastCall) return
        val prev = lastCall
        lastCall = state
        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> ev("call_ring")
            TelephonyManager.EXTRA_STATE_OFFHOOK -> ev("call_start")
            TelephonyManager.EXTRA_STATE_IDLE -> if (prev != TelephonyManager.EXTRA_STATE_IDLE) ev("call_end")
        }
    }

    private fun onSms(i: Intent) {
        val msgs = try {
            Telephony.Sms.Intents.getMessagesFromIntent(i)
        } catch (_: Exception) {
            null
        } ?: return
        if (msgs.isEmpty()) return
        val from = msgs[0]?.originatingAddress.orEmpty()
        val body = msgs.joinToString("") { it?.messageBody.orEmpty() }
        ev("sms", mapOf("from" to from, "text" to body))
    }

    // ---- Pliage (Galaxy Z Fold) ----

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) {}
        override fun onDisplayRemoved(displayId: Int) {}
        override fun onDisplayChanged(displayId: Int) {
            if (displayId != Display.DEFAULT_DISPLAY) return
            val u = DeviceState.isUnfolded(this@FlareService)
            val last = lastUnfolded
            lastUnfolded = u
            if (last != null && last != u) ev(if (u) "unfold" else "fold")
        }
    }

    // ---- Casque filaire / USB ----

    private val audioCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) = checkHeadset()
        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) = checkHeadset()
    }

    private fun checkHeadset() {
        val now = DeviceState.headsetConnected(this)
        if (now != lastHeadset) {
            lastHeadset = now
            ev(if (now) "headset_on" else "headset_off")
        }
    }

    // ---- Wi-Fi ----

    private fun currentSsid(caps: NetworkCapabilities?): String? {
        val raw = if (Build.VERSION.SDK_INT >= 29) {
            (caps?.transportInfo as? WifiInfo)?.ssid
        } else {
            @Suppress("DEPRECATION")
            applicationContext.getSystemService(WifiManager::class.java).connectionInfo?.ssid
        }
        val s = raw?.trim('"')
        return if (s.isNullOrBlank() || s == "<unknown ssid>") null else s
    }

    private fun registerWifi() {
        val cm = getSystemService(ConnectivityManager::class.java)
        val req = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
        val cb = if (Build.VERSION.SDK_INT >= 31) {
            object : ConnectivityManager.NetworkCallback(ConnectivityManager.NetworkCallback.FLAG_INCLUDE_LOCATION_INFO) {
                override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) = onWifiCaps(network, caps)
                override fun onLost(network: Network) = onWifiLost(network)
            }
        } else {
            object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) = onWifiCaps(network, caps)
                override fun onLost(network: Network) = onWifiLost(network)
            }
        }
        try {
            cm.registerNetworkCallback(req, cb)
            netCallback = cb
        } catch (_: Exception) {
        }
    }

    private fun onWifiCaps(network: Network, caps: NetworkCapabilities) {
        val ssid = currentSsid(caps)
        main.post {
            if (ssid != null) DeviceState.ssid = ssid
            if (wifiNetwork == network) {
                // Le nom du réseau arrive parfois un peu après la connexion
                if (ssid != null && pendingWifi != null) {
                    main.removeCallbacks(pendingWifi!!)
                    pendingWifi = null
                    ev("wifi_on", mapOf("ssid" to ssid))
                }
                return@post
            }
            wifiNetwork = network
            DeviceState.wifiConnected = true
            if (ssid != null) {
                ev("wifi_on", mapOf("ssid" to ssid))
            } else {
                val r = Runnable {
                    pendingWifi = null
                    ev("wifi_on", mapOf("ssid" to DeviceState.ssid.orEmpty()))
                }
                pendingWifi = r
                main.postDelayed(r, 3000)
            }
        }
    }

    private fun onWifiLost(network: Network) {
        main.post {
            if (network != wifiNetwork) return@post
            pendingWifi?.let { main.removeCallbacks(it) }
            pendingWifi = null
            wifiNetwork = null
            DeviceState.wifiConnected = false
            val last = DeviceState.ssid.orEmpty()
            DeviceState.ssid = null
            ev("wifi_off", mapOf("ssid" to last))
        }
    }

    // ---- Cycle de vie ----

    override fun onCreate() {
        super.onCreate()
        Store.init(this)
        goForeground()
        running = true
        registerAll()
        scope.launch {
            Store.items.collect {
                Scheduler.rescheduleAll(this@FlareService)
                getSystemService(NotificationManager::class.java).notify(NOTIF_ID, buildNotification())
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        goForeground()
        if (intent?.getBooleanExtra("boot", false) == true) main.postDelayed({ ev("boot") }, 3000)
        return START_STICKY
    }

    override fun onDestroy() {
        running = false
        unregisterAll()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun goForeground() {
        val n = buildNotification()
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIF_ID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIF_ID, n)
        }
    }

    private fun buildNotification(): Notification {
        val count = Store.items.value.count { it.enabled && it.trigger != null }
        val open = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        return Notification.Builder(this, FlareChannels.SERVICE)
            .setSmallIcon(R.drawable.ic_stat_flare)
            .setContentTitle(getString(R.string.svc_title))
            .setContentText(getString(R.string.svc_text, count))
            .setContentIntent(open)
            .setOngoing(true)
            .setShowWhen(false)
            .build()
    }

    private fun registerAll() {
        val f = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
            addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        }
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(receiver, f, Context.RECEIVER_EXPORTED) else registerReceiver(receiver, f)
        receiverOn = true

        lastUnfolded = DeviceState.isUnfolded(this)
        getSystemService(DisplayManager::class.java).registerDisplayListener(displayListener, main)

        lastHeadset = DeviceState.headsetConnected(this)
        getSystemService(AudioManager::class.java).registerAudioDeviceCallback(audioCallback, main)

        registerWifi()

        val tc = object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                DeviceState.torchOn = enabled
            }
        }
        try {
            getSystemService(CameraManager::class.java).registerTorchCallback(tc, main)
            torchCallback = tc
        } catch (_: Exception) {
        }
    }

    private fun unregisterAll() {
        if (receiverOn) {
            try {
                unregisterReceiver(receiver)
            } catch (_: Exception) {
            }
            receiverOn = false
        }
        try {
            getSystemService(DisplayManager::class.java).unregisterDisplayListener(displayListener)
        } catch (_: Exception) {
        }
        try {
            getSystemService(AudioManager::class.java).unregisterAudioDeviceCallback(audioCallback)
        } catch (_: Exception) {
        }
        netCallback?.let {
            try {
                getSystemService(ConnectivityManager::class.java).unregisterNetworkCallback(it)
            } catch (_: Exception) {
            }
        }
        netCallback = null
        torchCallback?.let {
            try {
                getSystemService(CameraManager::class.java).unregisterTorchCallback(it)
            } catch (_: Exception) {
            }
        }
        torchCallback = null
        pendingWifi?.let { main.removeCallbacks(it) }
    }
}
