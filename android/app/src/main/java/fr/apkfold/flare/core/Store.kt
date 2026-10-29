package fr.apkfold.flare.core

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import java.io.File
import java.util.concurrent.Executors

/** Automatisations, raccourcis, journal et préférences, stockés sur l'appareil uniquement. */
object Store {
    private lateinit var app: Context
    private val io = Executors.newSingleThreadExecutor()

    private val _items = MutableStateFlow<List<Automation>>(emptyList())
    val items: StateFlow<List<Automation>> = _items

    private val _log = MutableStateFlow<List<LogEntry>>(emptyList())
    val log: StateFlow<List<LogEntry>> = _log

    private const val LOG_MAX = 300

    val prefs: SharedPreferences get() = app.getSharedPreferences("flare", Context.MODE_PRIVATE)

    @Synchronized
    fun init(ctx: Context) {
        if (::app.isInitialized) return
        app = ctx.applicationContext
        _items.value = try {
            val f = File(app.filesDir, "automations.json")
            if (f.exists()) Automation.listFromJson(f.readText()) else emptyList()
        } catch (_: Exception) {
            emptyList()
        }
        _log.value = try {
            val f = File(app.filesDir, "log.json")
            if (f.exists()) {
                val a = JSONArray(f.readText())
                (0 until a.length()).mapNotNull { a.optJSONObject(it)?.let(LogEntry::fromJson) }
            } else emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun get(id: String): Automation? = _items.value.firstOrNull { it.id == id }

    @Synchronized
    fun save(a: Automation) {
        val list = _items.value.toMutableList()
        val i = list.indexOfFirst { it.id == a.id }
        if (i >= 0) list[i] = a else list += a
        _items.value = list
        persist()
    }

    @Synchronized
    fun delete(id: String) {
        _items.value = _items.value.filterNot { it.id == id }
        for (n in 1..3) if (tileTarget(n) == id) setTile(n, null)
        persist()
    }

    fun setEnabled(id: String, enabled: Boolean) {
        val a = get(id) ?: return
        if (a.enabled != enabled) save(a.copy(enabled = enabled))
    }

    /** Ajoute ou remplace (même identifiant) les éléments importés. */
    @Synchronized
    fun importAll(list: List<Automation>) {
        val cur = _items.value.toMutableList()
        for (a in list) {
            val i = cur.indexOfFirst { it.id == a.id }
            if (i >= 0) cur[i] = a else cur += a
        }
        _items.value = cur
        persist()
    }

    private fun persist() {
        val text = Automation.listToJson(_items.value)
        io.execute { atomicWrite(File(app.filesDir, "automations.json"), text) }
    }

    private fun atomicWrite(f: File, text: String) {
        try {
            val tmp = File(f.parentFile, f.name + ".tmp")
            tmp.writeText(text)
            if (!tmp.renameTo(f)) {
                f.writeText(text)
                tmp.delete()
            }
        } catch (_: Exception) {
        }
    }

    /** Efface automatisations, raccourcis, journal et état (tests sur émulateur). */
    @Synchronized
    fun clearAll() {
        _items.value = emptyList()
        persist()
        clearLog()
        prefs.edit().remove("active").apply()
    }

    // ---- Journal ----

    @Synchronized
    fun addLog(title: String, text: String, ok: Boolean) {
        val list = listOf(LogEntry(System.currentTimeMillis(), title, text, ok)) + _log.value
        _log.value = if (list.size > LOG_MAX) list.subList(0, LOG_MAX) else list
        val snapshot = _log.value
        io.execute {
            atomicWrite(File(app.filesDir, "log.json"), JSONArray().apply { snapshot.forEach { put(it.toJson()) } }.toString())
        }
    }

    @Synchronized
    fun clearLog() {
        _log.value = emptyList()
        io.execute { File(app.filesDir, "log.json").delete() }
    }

    // ---- Préférences ----

    fun tileTarget(n: Int): String? = prefs.getString("tile$n", null)
    fun setTile(n: Int, id: String?) = prefs.edit().apply { if (id == null) remove("tile$n") else putString("tile$n", id) }.apply()

    var useRoot: Boolean
        get() = prefs.getBoolean("use_root", false)
        set(v) = prefs.edit().putBoolean("use_root", v).apply()

    var serviceEnabled: Boolean
        get() = prefs.getBoolean("service", true)
        set(v) = prefs.edit().putBoolean("service", v).apply()

    var exampleAdded: Boolean
        get() = prefs.getBoolean("example_added", false)
        set(v) = prefs.edit().putBoolean("example_added", v).apply()

    /** Automatisations « en cours » (entrée déclenchée, sortie pas encore passée). */
    fun activeIds(): Set<String> = prefs.getStringSet("active", emptySet()) ?: emptySet()

    @Synchronized
    fun setActive(id: String, active: Boolean) {
        val s = activeIds().toMutableSet()
        if (active) s += id else s -= id
        prefs.edit().putStringSet("active", s).apply()
    }
}
