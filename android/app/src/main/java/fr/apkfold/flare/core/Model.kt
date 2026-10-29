package fr.apkfold.flare.core

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/** Une étape : déclencheur, condition ou action, identifiée par son type et ses paramètres texte. */
data class Step(val type: String, val params: Map<String, String> = emptyMap()) {
    fun p(key: String): String = params[key] ?: ""

    fun toJson(): JSONObject = JSONObject().apply {
        put("type", type)
        put("params", JSONObject().apply { params.forEach { (k, v) -> put(k, v) } })
    }

    companion object {
        fun fromJson(o: JSONObject): Step {
            val p = o.optJSONObject("params")
            val m = linkedMapOf<String, String>()
            if (p != null) {
                val it = p.keys()
                while (it.hasNext()) {
                    val k = it.next()
                    m[k] = p.optString(k)
                }
            }
            return Step(o.getString("type"), m)
        }
    }
}

/**
 * Une automatisation (avec déclencheur) ou un raccourci (sans déclencheur, lancé à la main,
 * depuis l'écran d'accueil ou une tuile des réglages rapides).
 */
data class Automation(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val enabled: Boolean = true,
    val trigger: Step? = null,
    val conditions: List<Step> = emptyList(),
    val actions: List<Step> = emptyList(),
    val exitActions: List<Step> = emptyList(),
) {
    val isShortcut: Boolean get() = trigger == null

    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("enabled", enabled)
        if (trigger != null) put("trigger", trigger.toJson())
        put("conditions", steps(conditions))
        put("actions", steps(actions))
        put("exitActions", steps(exitActions))
    }

    companion object {
        private fun steps(list: List<Step>) = JSONArray().apply { list.forEach { put(it.toJson()) } }

        private fun readSteps(a: JSONArray?): List<Step> {
            if (a == null) return emptyList()
            val out = ArrayList<Step>()
            for (i in 0 until a.length()) {
                val o = a.optJSONObject(i) ?: continue
                try {
                    out += Step.fromJson(o)
                } catch (_: Exception) {
                }
            }
            return out
        }

        fun fromJson(o: JSONObject): Automation = Automation(
            id = o.optString("id").ifBlank { UUID.randomUUID().toString() },
            name = o.optString("name"),
            enabled = o.optBoolean("enabled", true),
            trigger = o.optJSONObject("trigger")?.let { Step.fromJson(it) },
            conditions = readSteps(o.optJSONArray("conditions")),
            actions = readSteps(o.optJSONArray("actions")),
            exitActions = readSteps(o.optJSONArray("exitActions")),
        )

        fun listToJson(list: List<Automation>): String =
            JSONArray().apply { list.forEach { put(it.toJson()) } }.toString(2)

        fun listFromJson(text: String): List<Automation> {
            val a = JSONArray(text)
            val out = ArrayList<Automation>()
            for (i in 0 until a.length()) {
                val o = a.optJSONObject(i) ?: continue
                out += fromJson(o)
            }
            return out
        }
    }
}

data class LogEntry(val time: Long, val title: String, val text: String, val ok: Boolean) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("t", time); put("title", title); put("text", text); put("ok", ok)
    }

    companion object {
        fun fromJson(o: JSONObject) = LogEntry(o.optLong("t"), o.optString("title"), o.optString("text"), o.optBoolean("ok", true))
    }
}
