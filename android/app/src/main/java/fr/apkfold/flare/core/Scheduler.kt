package fr.apkfold.flare.core

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import fr.apkfold.flare.service.AlarmReceiver
import fr.apkfold.flare.service.PlaceReceiver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/** Alarmes (heure fixe, intervalle) et zones (arrivée/départ d'un lieu). */
object Scheduler {

    private fun alarmIntent(ctx: Context, id: String, flags: Int): PendingIntent? =
        PendingIntent.getBroadcast(
            ctx, 0,
            Intent(ctx, AlarmReceiver::class.java).setData(Uri.parse("flare://alarm/$id")),
            flags or PendingIntent.FLAG_IMMUTABLE,
        )

    private fun placeIntent(ctx: Context, id: String, flags: Int): PendingIntent? {
        val mutable = if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_MUTABLE else 0
        return PendingIntent.getBroadcast(
            ctx, 0,
            Intent(ctx, PlaceReceiver::class.java).setData(Uri.parse("flare://place/$id")),
            flags or mutable,
        )
    }

    /** Recalcule toutes les alarmes et zones selon les automatisations actives. */
    fun rescheduleAll(ctx: Context) {
        val prefs = Store.prefs
        val previous = prefs.getStringSet("scheduled", emptySet()) ?: emptySet()
        val now = mutableSetOf<String>()
        for (a in Store.items.value) {
            val t = a.trigger ?: continue
            if (!a.enabled) continue
            when (t.type) {
                "time", "interval" -> {
                    schedule(ctx, a)
                    now += "a:" + a.id
                }
                "place_in", "place_out" -> {
                    if (addPlace(ctx, a)) now += "p:" + a.id
                }
            }
        }
        for (key in previous - now) {
            val id = key.substring(2)
            if (key.startsWith("a:")) cancelAlarm(ctx, id) else removePlace(ctx, id)
        }
        prefs.edit().putStringSet("scheduled", now).apply()
    }

    fun schedule(ctx: Context, a: Automation) {
        val t = a.trigger ?: return
        val at = when (t.type) {
            "time" -> nextTime(t) ?: return
            "interval" -> System.currentTimeMillis() + (t.p("minutes").toLongOrNull() ?: 30L).coerceAtLeast(1L) * 60_000L
            else -> return
        }
        val pi = alarmIntent(ctx, a.id, PendingIntent.FLAG_UPDATE_CURRENT) ?: return
        val am = ctx.getSystemService(AlarmManager::class.java)
        try {
            if (Perms.exactAlarms(ctx)) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
            else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        } catch (_: SecurityException) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        }
    }

    private fun cancelAlarm(ctx: Context, id: String) {
        val pi = alarmIntent(ctx, id, PendingIntent.FLAG_NO_CREATE) ?: return
        ctx.getSystemService(AlarmManager::class.java).cancel(pi)
        pi.cancel()
    }

    /** Prochaine occurrence de l'heure choisie un des jours choisis. */
    fun nextTime(t: Step): Long? {
        val time = Engine.parseTime(t.p("time")) ?: return null
        val days = Engine.parseDays(t.p("days")).ifEmpty { (1..7).toSet() }
        val now = LocalDateTime.now()
        for (i in 0..7) {
            val d = LocalDate.now().plusDays(i.toLong())
            val dt = d.atTime(time)
            if (dt.isAfter(now) && d.dayOfWeek.value in days) {
                return dt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
        }
        return null
    }

    @SuppressLint("MissingPermission")
    private fun addPlace(ctx: Context, a: Automation): Boolean {
        val t = a.trigger ?: return false
        if (!Perms.has(ctx, Need.LOCATION)) return false
        val parts = t.p("latlon").split(',').mapNotNull { it.trim().toDoubleOrNull() }
        if (parts.size != 2) return false
        val radius = (t.p("radius").toFloatOrNull() ?: 150f).coerceIn(50f, 50_000f)
        val pi = placeIntent(ctx, a.id, PendingIntent.FLAG_UPDATE_CURRENT) ?: return false
        return try {
            ctx.getSystemService(LocationManager::class.java).addProximityAlert(parts[0], parts[1], radius, -1, pi)
            true
        } catch (_: Exception) {
            false
        }
    }

    @SuppressLint("MissingPermission")
    private fun removePlace(ctx: Context, id: String) {
        val pi = placeIntent(ctx, id, PendingIntent.FLAG_NO_CREATE) ?: return
        try {
            ctx.getSystemService(LocationManager::class.java).removeProximityAlert(pi)
        } catch (_: Exception) {
        }
        pi.cancel()
    }
}
