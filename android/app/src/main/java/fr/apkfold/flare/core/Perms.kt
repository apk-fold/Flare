package fr.apkfold.flare.core

import android.Manifest
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import fr.apkfold.flare.service.FlareAccessibility
import fr.apkfold.flare.service.FlareNotificationListener

object Perms {

    fun granted(ctx: Context, perm: String): Boolean =
        ctx.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED

    fun has(ctx: Context, need: Need): Boolean = when (need) {
        Need.SHELL -> Shell.available(ctx)
        Need.ACCESSIBILITY -> accessibilityEnabled(ctx)
        Need.NOTIF_LISTENER -> notificationListenerEnabled(ctx)
        Need.WRITE_SETTINGS -> Settings.System.canWrite(ctx)
        Need.DND -> ctx.getSystemService(NotificationManager::class.java).isNotificationPolicyAccessGranted
        Need.OVERLAY -> Settings.canDrawOverlays(ctx)
        Need.BLUETOOTH -> Build.VERSION.SDK_INT < 31 || granted(ctx, Manifest.permission.BLUETOOTH_CONNECT)
        Need.LOCATION -> granted(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
        Need.BG_LOCATION -> Build.VERSION.SDK_INT < 29 || granted(ctx, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        Need.PHONE -> granted(ctx, Manifest.permission.READ_PHONE_STATE)
        Need.CALL -> granted(ctx, Manifest.permission.CALL_PHONE)
        Need.SMS -> granted(ctx, Manifest.permission.RECEIVE_SMS)
        Need.SEND_SMS -> granted(ctx, Manifest.permission.SEND_SMS)
        Need.NOTIFICATIONS -> Build.VERSION.SDK_INT < 33 || granted(ctx, Manifest.permission.POST_NOTIFICATIONS)
    }

    /** Accès manquants pour une définition (le premier de [Def.anyOf] si aucun n'est accordé). */
    fun missing(ctx: Context, def: Def): List<Need> {
        val m = def.needs.filterNot { has(ctx, it) }.toMutableList()
        if (def.anyOf.isNotEmpty() && def.anyOf.none { has(ctx, it) }) m += def.anyOf.first()
        return m
    }

    /** Permissions Android demandées par la fenêtre système classique (null : accès spécial à part). */
    fun runtimeFor(need: Need): Array<String>? = when (need) {
        Need.BLUETOOTH -> if (Build.VERSION.SDK_INT >= 31) arrayOf(Manifest.permission.BLUETOOTH_CONNECT) else null
        Need.LOCATION -> arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        Need.BG_LOCATION -> if (Build.VERSION.SDK_INT >= 29) arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION) else null
        Need.PHONE -> arrayOf(Manifest.permission.READ_PHONE_STATE)
        Need.CALL -> arrayOf(Manifest.permission.CALL_PHONE)
        Need.SMS -> arrayOf(Manifest.permission.RECEIVE_SMS)
        Need.SEND_SMS -> arrayOf(Manifest.permission.SEND_SMS)
        Need.NOTIFICATIONS -> if (Build.VERSION.SDK_INT >= 33) arrayOf(Manifest.permission.POST_NOTIFICATIONS) else null
        else -> null
    }

    fun secureGranted(ctx: Context): Boolean = granted(ctx, Manifest.permission.WRITE_SECURE_SETTINGS)

    fun batteryUnrestricted(ctx: Context): Boolean =
        ctx.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(ctx.packageName)

    fun exactAlarms(ctx: Context): Boolean = if (Build.VERSION.SDK_INT >= 31) {
        ctx.getSystemService(android.app.AlarmManager::class.java).canScheduleExactAlarms()
    } else true

    fun accessibilityComponent(ctx: Context) = ComponentName(ctx, FlareAccessibility::class.java)
    fun listenerComponent(ctx: Context) = ComponentName(ctx, FlareNotificationListener::class.java)

    fun accessibilityEnabled(ctx: Context): Boolean {
        if (FlareAccessibility.instance != null) return true
        val list = Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
        val me = accessibilityComponent(ctx)
        return list.split(':').any { ComponentName.unflattenFromString(it) == me }
    }

    fun notificationListenerEnabled(ctx: Context): Boolean {
        val list = Settings.Secure.getString(ctx.contentResolver, "enabled_notification_listeners") ?: return false
        val me = listenerComponent(ctx)
        return list.split(':').any { ComponentName.unflattenFromString(it) == me }
    }

    /** Active le service d'accessibilité en écrivant le réglage protégé (droits système ou Shizuku). */
    fun enableAccessibility(ctx: Context): Boolean {
        val me = accessibilityComponent(ctx).flattenToString()
        val cur = Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).orEmpty()
        val parts = cur.split(':').filter { it.isNotBlank() && ComponentName.unflattenFromString(it) != accessibilityComponent(ctx) }
        val value = (parts + me).joinToString(":")
        if (secureGranted(ctx)) {
            try {
                Settings.Secure.putString(ctx.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, value)
                Settings.Secure.putString(ctx.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED, "1")
                return true
            } catch (_: Exception) {
            }
        }
        val r = Shell.run(
            ctx,
            "settings put secure enabled_accessibility_services ${Shell.q(value)} && settings put secure accessibility_enabled 1",
        )
        return r?.ok == true
    }

    /** Écran des réglages où l'utilisateur accorde l'accès. */
    fun settingsIntent(ctx: Context, need: Need): Intent? {
        val pkgUri = Uri.parse("package:" + ctx.packageName)
        return when (need) {
            Need.ACCESSIBILITY -> Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            Need.NOTIF_LISTENER -> Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            Need.WRITE_SETTINGS -> Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, pkgUri)
            Need.DND -> Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            Need.OVERLAY -> Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, pkgUri)
            else -> appDetails(ctx)
        }
    }

    fun appDetails(ctx: Context) =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + ctx.packageName))

    fun batteryIntent(ctx: Context): Intent =
        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + ctx.packageName))

    fun alarmsIntent(ctx: Context): Intent? = if (Build.VERSION.SDK_INT >= 31) {
        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + ctx.packageName))
    } else null

    /**
     * Accorde tout d'un coup avec Shizuku/root : droits système permanents, permissions, accès spéciaux,
     * pas de restriction de batterie, accessibilité. Renvoie (réussies, total).
     */
    fun grantAll(ctx: Context): Pair<Int, Int> {
        val pkg = ctx.packageName
        val cmds = mutableListOf(
            "pm grant $pkg android.permission.WRITE_SECURE_SETTINGS",
            "appops set $pkg ACCESS_RESTRICTED_SETTINGS allow",
            "appops set $pkg SYSTEM_ALERT_WINDOW allow",
            "appops set $pkg WRITE_SETTINGS allow",
            "cmd notification allow_listener ${listenerComponent(ctx).flattenToString()}",
            "cmd notification allow_dnd $pkg",
            "dumpsys deviceidle whitelist +$pkg",
            "appops set $pkg RUN_IN_BACKGROUND allow",
            "appops set $pkg RUN_ANY_IN_BACKGROUND allow",
        )
        if (Build.VERSION.SDK_INT >= 31) cmds += "appops set $pkg SCHEDULE_EXACT_ALARM allow"
        val runtime = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
        )
        if (Build.VERSION.SDK_INT >= 29) runtime += Manifest.permission.ACCESS_BACKGROUND_LOCATION
        if (Build.VERSION.SDK_INT >= 31) runtime += Manifest.permission.BLUETOOTH_CONNECT
        if (Build.VERSION.SDK_INT >= 33) runtime += Manifest.permission.POST_NOTIFICATIONS
        runtime.forEach { cmds += "pm grant $pkg $it" }

        var ok = 0
        for (c in cmds) if (Shell.run(ctx, c)?.ok == true) ok++
        val total = cmds.size + 1
        if (enableAccessibility(ctx)) ok++
        return ok to total
    }
}
