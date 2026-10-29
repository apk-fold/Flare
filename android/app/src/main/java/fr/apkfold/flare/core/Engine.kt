package fr.apkfold.flare.core

import android.content.Context
import android.media.AudioManager
import fr.apkfold.flare.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime

/** Reçoit les événements, vérifie les conditions et lance les actions. */
object Engine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /** Événement système : [type] correspond au type d'un déclencheur, [data] à ses détails. */
    fun event(ctx: Context, type: String, data: Map<String, String> = emptyMap()) {
        val app = ctx.applicationContext
        Store.init(app)
        for (a in Store.items.value) {
            val t = a.trigger ?: continue
            if (!a.enabled) continue
            val def = Defs.get(t.type) ?: continue
            if (t.type == type && matches(app, a, t, data)) {
                if (!conditionsOk(app, a.conditions)) {
                    Store.addLog(a.name, app.getString(R.string.log_skipped), true)
                    continue
                }
                if (def.exit != null) Store.setActive(a.id, true)
                run(app, a, a.actions, app.getString(R.string.log_triggered, app.getString(def.title)))
            } else if (def.exit == type && a.id in Store.activeIds() && matches(app, a, t.copy(type = type), data)) {
                Store.setActive(a.id, false)
                if (a.exitActions.isNotEmpty()) {
                    val exitDef = Defs.get(type)
                    val label = if (exitDef != null) app.getString(exitDef.title) else type
                    run(app, a, a.exitActions, app.getString(R.string.log_exit, label))
                }
            }
        }
    }

    /** Lance les actions d'une automatisation ou d'un raccourci à la main. */
    fun runManual(ctx: Context, a: Automation) {
        val app = ctx.applicationContext
        run(app, a, a.actions, app.getString(R.string.log_manual))
    }

    private fun run(ctx: Context, a: Automation, steps: List<Step>, reason: String) {
        scope.launch { execute(ctx, a, steps, reason, 0) }
    }

    suspend fun execute(ctx: Context, a: Automation, steps: List<Step>, reason: String, depth: Int) {
        val lines = mutableListOf(reason)
        var allOk = true
        for (s in steps) {
            val def = Defs.get(s.type) ?: continue
            val r = try {
                Actions.exec(ctx, s, depth)
            } catch (e: SecurityException) {
                Outcome(false, ctx.getString(R.string.err_failed, e.message ?: "SecurityException"))
            } catch (e: Throwable) {
                Outcome(false, ctx.getString(R.string.err_failed, e.message ?: e.javaClass.simpleName))
            }
            if (!r.ok) allOk = false
            val name = ctx.getString(def.title)
            lines += (if (r.ok) "✓ " else "✗ ") + name + (if (!r.msg.isNullOrBlank()) " — " + r.msg else "")
        }
        Store.addLog(a.name.ifBlank { ctx.getString(R.string.app_name) }, lines.joinToString("\n"), allOk)
    }

    // ---- Correspondance des déclencheurs ----

    private fun matches(ctx: Context, a: Automation, t: Step, data: Map<String, String>): Boolean = when (t.type) {
        "bt_on", "bt_off" -> DeviceState.deviceMatches(t.p("devices"), data["name"], data["address"])
        "wifi_on", "wifi_off" -> DeviceState.ssidMatches(t.p("ssid"), data["ssid"])
        "battery" -> {
            val lvl = data["level"]?.toIntOrNull() ?: -1
            val prev = data["prev"]?.toIntOrNull() ?: -1
            val target = t.p("level").toIntOrNull() ?: 20
            if (lvl < 0 || prev < 0) false
            else if (t.p("compare") == "above") prev <= target && lvl > target
            else prev >= target && lvl < target
        }
        "airplane" -> t.p("state").ifBlank { "on" } == data["state"]
        "time", "interval", "place_in", "place_out" -> data["id"] == a.id
        "app_open", "app_close" -> t.p("app").isBlank() || t.p("app") == data["pkg"]
        "notification" -> {
            val okApp = t.p("app").isBlank() || t.p("app") == data["pkg"]
            val text = (data["title"].orEmpty() + "\n" + data["text"].orEmpty())
            okApp && (t.p("text").isBlank() || text.contains(t.p("text"), ignoreCase = true))
        }
        "sms" -> {
            val okFrom = t.p("sender").isBlank() || data["from"].orEmpty().contains(t.p("sender"), ignoreCase = true)
            okFrom && (t.p("text").isBlank() || data["text"].orEmpty().contains(t.p("text"), ignoreCase = true))
        }
        else -> true
    }

    // ---- Conditions ----

    fun conditionsOk(ctx: Context, list: List<Step>): Boolean = list.all { cond ->
        try {
            check(ctx, cond)
        } catch (_: Throwable) {
            false
        }
    }

    private fun yes(s: Step, def: String = "yes") = s.p("state").ifBlank { def } == "yes"

    private fun check(ctx: Context, c: Step): Boolean = when (c.type) {
        "k_time" -> {
            val now = LocalTime.now()
            val a = parseTime(c.p("start")) ?: LocalTime.MIDNIGHT
            val b = parseTime(c.p("end")) ?: LocalTime.MIDNIGHT
            if (a <= b) !now.isBefore(a) && now.isBefore(b) else !now.isBefore(a) || now.isBefore(b)
        }
        "k_days" -> LocalDateTime.now().dayOfWeek.value in parseDays(c.p("days"))
        "k_battery" -> {
            val lvl = DeviceState.batteryLevel
            val target = c.p("level").toIntOrNull() ?: 30
            lvl >= 0 && if (c.p("compare") == "above") lvl > target else lvl < target
        }
        "k_charging" -> DeviceState.charging == yes(c)
        "k_bt" -> {
            val connected = DeviceState.btConnected.entries.any { DeviceState.deviceMatches(c.p("devices"), it.value, it.key) }
            connected == yes(c)
        }
        "k_wifi" -> {
            val connected = DeviceState.wifiConnected && DeviceState.ssidMatches(c.p("ssid"), DeviceState.ssid)
            connected == yes(c)
        }
        "k_fold" -> DeviceState.isUnfolded(ctx) == (c.p("state").ifBlank { "unfolded" } == "unfolded")
        "k_headset" -> DeviceState.headsetConnected(ctx) == yes(c)
        "k_screen" -> DeviceState.screenOn(ctx) == yes(c)
        "k_ringer" -> {
            val m = ctx.getSystemService(AudioManager::class.java).ringerMode
            val want = when (c.p("mode")) {
                "vibrate" -> AudioManager.RINGER_MODE_VIBRATE
                "silent" -> AudioManager.RINGER_MODE_SILENT
                else -> AudioManager.RINGER_MODE_NORMAL
            }
            m == want
        }
        "k_app" -> (DeviceState.foregroundApp == c.p("app")) == yes(c)
        else -> true
    }

    fun parseTime(s: String): LocalTime? = try {
        val (h, m) = s.trim().split(':').map { it.trim().toInt() }
        LocalTime.of(h, m)
    } catch (_: Exception) {
        null
    }

    /** « 1,2,3 » → lundi, mardi, mercredi (numérotation ISO). */
    fun parseDays(s: String): Set<Int> = s.split(',').mapNotNull { it.trim().toIntOrNull() }.filter { it in 1..7 }.toSet()
}

data class Outcome(val ok: Boolean, val msg: String? = null)
