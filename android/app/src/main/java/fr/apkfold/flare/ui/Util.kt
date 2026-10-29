package fr.apkfold.flare.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.graphics.drawable.toBitmap
import fr.apkfold.flare.R
import fr.apkfold.flare.core.Automation
import fr.apkfold.flare.core.Cat
import fr.apkfold.flare.core.Defs
import fr.apkfold.flare.core.Engine
import fr.apkfold.flare.core.Need
import fr.apkfold.flare.core.PType
import fr.apkfold.flare.core.Perms
import fr.apkfold.flare.core.Shell
import fr.apkfold.flare.core.Step
import fr.apkfold.flare.core.Store
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/** Compteur incrémenté au retour dans l'appli : force le recalcul des accès accordés. */
val LocalTick = compositionLocalOf { 0 }

/** Demande des permissions Android (fenêtre système). */
val LocalRequestPerms = compositionLocalOf<(Array<String>) -> Unit> { {} }

data class AppInfo(val pkg: String, val label: String)

object Apps {
    private val labels = ConcurrentHashMap<String, String>()
    private val icons = ConcurrentHashMap<String, ImageBitmap>()

    fun list(ctx: Context): List<AppInfo> {
        val pm = ctx.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return pm.queryIntentActivities(intent, 0)
            .map { it.activityInfo.packageName to it.loadLabel(pm).toString() }
            .distinctBy { it.first }
            .map { (pkg, label) ->
                labels[pkg] = label
                AppInfo(pkg, label)
            }
            .sortedBy { it.label.lowercase() }
    }

    fun label(ctx: Context, pkg: String): String = labels.getOrPut(pkg) {
        try {
            val pm = ctx.packageManager
            pm.getApplicationInfo(pkg, 0).loadLabel(pm).toString()
        } catch (_: PackageManager.NameNotFoundException) {
            pkg
        }
    }

    fun icon(ctx: Context, pkg: String): ImageBitmap? = icons[pkg] ?: try {
        ctx.packageManager.getApplicationIcon(pkg).toBitmap(96, 96).asImageBitmap().also { icons[pkg] = it }
    } catch (_: Exception) {
        null
    }
}

fun catIcon(c: Cat): ImageVector = when (c) {
    Cat.CONNECTIONS -> Icons.Default.Wifi
    Cat.POWER -> Icons.Default.BatteryChargingFull
    Cat.SCREEN -> Icons.Default.Smartphone
    Cat.TIME -> Icons.Default.Schedule
    Cat.APPS -> Icons.Default.Apps
    Cat.PHONE -> Icons.Default.Phone
    Cat.PLACE -> Icons.Default.LocationOn
    Cat.SOUND -> Icons.Default.MusicNote
    Cat.SYSTEM -> Icons.Default.Settings
    Cat.CONTROL -> Icons.Default.TouchApp
    Cat.FLOW -> Icons.Default.Timer
}

fun daysSummary(ctx: Context, value: String): String {
    val days = Engine.parseDays(value)
    if (days.size == 7) return ctx.getString(R.string.every_day)
    return days.sorted().joinToString(" ") { DayOfWeek.of(it).getDisplayName(TextStyle.SHORT, Locale.getDefault()) }
}

/** Résumé lisible des paramètres d'une étape. */
fun summary(ctx: Context, step: Step): String {
    val def = Defs.get(step.type) ?: return step.type
    return def.params.mapNotNull { p ->
        val v = step.p(p.key)
        if (v.isBlank()) null else when (p.type) {
            PType.CHOICE -> p.choices.firstOrNull { it.value == v }?.let { if (it.label == 0) it.value else ctx.getString(it.label) } ?: v
            PType.APP -> Apps.label(ctx, v)
            PType.DAYS -> daysSummary(ctx, v)
            PType.ROUTINE -> Store.get(v)?.name ?: v
            PType.TEXT, PType.MULTILINE -> v.lineSequence().first().let { if (it.length > 40) it.take(40) + "…" else it }
            PType.NUMBER -> if (p.key == "level") "$v %" else v
            else -> v
        }
    }.joinToString(" · ")
}

fun stepTitle(ctx: Context, step: Step): String = Defs.get(step.type)?.let { ctx.getString(it.title) } ?: step.type

/** Accès manquants pour toute l'automatisation. */
fun missingFor(ctx: Context, a: Automation): Set<Need> {
    val steps = listOfNotNull(a.trigger) + a.conditions + a.actions + a.exitActions
    return steps.mapNotNull { Defs.get(it.type) }.flatMap { Perms.missing(ctx, it) }.toSet()
}

object Templates {
    const val OPUS = "fr.naviviolet.app"
    const val MAPS = "com.google.android.apps.maps"

    /** Exemple : connexion à la voiture → Ne pas déranger, volume, musique Opus, Maps. */
    fun carMode(ctx: Context): Automation {
        val music = if (Shell.isInstalled(ctx, OPUS)) Step("uri", mapOf("uri" to "opus://flow"))
        else Step("media", mapOf("command" to "play"))
        return Automation(
            name = ctx.getString(R.string.example_name),
            trigger = Step("bt_on", mapOf("devices" to "Smart Box, Media Nav")),
            actions = listOf(
                Step("dnd", mapOf("mode" to "priority")),
                Step("volume", mapOf("stream" to "media", "level" to "70")),
                music,
                Step("wait", mapOf("seconds" to "3")),
                Step("launch", mapOf("app" to MAPS)),
            ),
            exitActions = listOf(
                Step("media", mapOf("command" to "pause")),
                Step("dnd", mapOf("mode" to "off")),
            ),
        )
    }
}
