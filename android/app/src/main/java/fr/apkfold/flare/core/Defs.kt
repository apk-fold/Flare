package fr.apkfold.flare.core

import fr.apkfold.flare.R

enum class Kind { TRIGGER, CONDITION, ACTION }

/** Accès dont une étape a besoin. */
enum class Need(val label: Int) {
    SHELL(R.string.need_shell),
    ACCESSIBILITY(R.string.r_accessibility),
    NOTIF_LISTENER(R.string.r_notif_listener),
    WRITE_SETTINGS(R.string.r_write_settings),
    DND(R.string.r_dnd),
    OVERLAY(R.string.r_overlay),
    BLUETOOTH(R.string.need_bt),
    LOCATION(R.string.need_location),
    BG_LOCATION(R.string.r_bg_location),
    PHONE(R.string.need_phone),
    CALL(R.string.need_phone),
    SMS(R.string.need_sms),
    SEND_SMS(R.string.need_sms),
    NOTIFICATIONS(R.string.need_notifications),
}

enum class PType { TEXT, MULTILINE, NUMBER, CHOICE, APP, BT, SSID, TIME, DAYS, ROUTINE, LATLON }

/** Valeur proposée ; label 0 = afficher la valeur telle quelle. */
class Choice(val value: String, val label: Int)

class P(
    val key: String,
    val label: Int,
    val type: PType,
    val choices: List<Choice> = emptyList(),
    val def: String = "",
    val hint: Int = 0,
)

enum class Cat(val label: Int) {
    CONNECTIONS(R.string.cat_connections),
    POWER(R.string.cat_power),
    SCREEN(R.string.cat_screen),
    TIME(R.string.cat_time),
    APPS(R.string.cat_apps),
    PHONE(R.string.cat_phone),
    PLACE(R.string.cat_place),
    SOUND(R.string.cat_sound),
    SYSTEM(R.string.cat_system),
    CONTROL(R.string.cat_control),
    FLOW(R.string.cat_flow),
}

/**
 * Définition d'un type d'étape.
 * [needs] : tous obligatoires. [anyOf] : au moins un des accès (l'appli choisit le meilleur chemin).
 * [exit] : événement inverse qui lance les actions « Quand ça s'arrête ».
 */
class Def(
    val type: String,
    val kind: Kind,
    val cat: Cat,
    val title: Int,
    val params: List<P> = emptyList(),
    val needs: Set<Need> = emptySet(),
    val anyOf: Set<Need> = emptySet(),
    val exit: String? = null,
)

object Defs {
    private fun ch(vararg pairs: Pair<String, Int>) = pairs.map { Choice(it.first, it.second) }

    val ON_OFF_TOGGLE = ch("on" to R.string.c_on, "off" to R.string.c_off, "toggle" to R.string.c_toggle)
    val ON_OFF = ch("on" to R.string.c_on, "off" to R.string.c_off)
    val YES_NO = ch("yes" to R.string.c_yes, "no" to R.string.c_no)
    val COMPARE = ch("below" to R.string.c_below, "above" to R.string.c_above)
    val CONNECTED = ch("yes" to R.string.c_connected, "no" to R.string.c_not_connected)
    val FOLD = ch("unfolded" to R.string.c_unfolded, "folded" to R.string.c_folded)
    val RINGER = ch("normal" to R.string.c_ringer_normal, "vibrate" to R.string.c_vibrate, "silent" to R.string.c_silent)
    val DND = ch(
        "off" to R.string.c_off, "priority" to R.string.c_dnd_priority,
        "alarms" to R.string.c_dnd_alarms, "none" to R.string.c_dnd_none,
    )
    val STREAMS = ch(
        "media" to R.string.c_media, "ring" to R.string.c_ring, "notification" to R.string.c_notification,
        "alarm" to R.string.c_alarm, "call" to R.string.c_call, "system" to R.string.c_system,
    )
    val MEDIA = ch(
        "play_pause" to R.string.c_play_pause, "play" to R.string.c_play, "pause" to R.string.c_pause,
        "next" to R.string.c_next, "previous" to R.string.c_previous, "stop" to R.string.c_stop,
    )
    val TRAVEL = ch("d" to R.string.c_drive, "w" to R.string.c_walk, "b" to R.string.c_bike, "l" to R.string.c_moto)
    val BRIGHTNESS_MODE = ch("manual" to R.string.c_manual, "auto" to R.string.c_auto)
    val PAGES = ch(
        "wifi" to R.string.a_wifi, "bluetooth" to R.string.a_bluetooth, "display" to R.string.c_page_display,
        "sound" to R.string.c_page_sound, "battery" to R.string.c_page_battery, "location" to R.string.a_location,
        "apps" to R.string.c_page_apps, "nfc" to R.string.a_nfc, "accessibility" to R.string.c_page_accessibility,
        "developer" to R.string.c_page_dev,
    )
    val INTENT_KIND = ch("activity" to R.string.c_activity, "broadcast" to R.string.c_broadcast)
    val NAMESPACES = ch("system" to 0, "secure" to 0, "global" to 0)
    val BUTTONS = ch(
        "back" to R.string.c_back, "home" to R.string.c_home, "recents" to R.string.c_recents,
        "notifications" to R.string.c_notifs, "quick" to R.string.c_quick, "power" to R.string.c_power,
        "split" to R.string.c_split,
    )

    private const val ALL_DAYS = "1,2,3,4,5,6,7"

    private fun state(choices: List<Choice> = ON_OFF_TOGGLE, def: String = "on") =
        P("state", R.string.p_state, PType.CHOICE, choices, def)

    private val devices = P("devices", R.string.p_devices, PType.BT, hint = R.string.bt_hint)
    private val ssid = P("ssid", R.string.p_ssid, PType.SSID, hint = R.string.ssid_hint)
    private val app = P("app", R.string.p_app, PType.APP)
    private val level = P("level", R.string.p_level, PType.NUMBER, def = "50")
    private val compare = P("compare", R.string.p_compare, PType.CHOICE, COMPARE, "below")
    private val place = P("latlon", R.string.p_latlon, PType.LATLON)
    private val radius = P("radius", R.string.p_radius, PType.NUMBER, def = "150")

    val triggers: List<Def> = listOf(
        Def("bt_on", Kind.TRIGGER, Cat.CONNECTIONS, R.string.t_bt_on, listOf(devices), setOf(Need.BLUETOOTH), exit = "bt_off"),
        Def("bt_off", Kind.TRIGGER, Cat.CONNECTIONS, R.string.t_bt_off, listOf(devices), setOf(Need.BLUETOOTH), exit = "bt_on"),
        Def("wifi_on", Kind.TRIGGER, Cat.CONNECTIONS, R.string.t_wifi_on, listOf(ssid), setOf(Need.LOCATION), exit = "wifi_off"),
        Def("wifi_off", Kind.TRIGGER, Cat.CONNECTIONS, R.string.t_wifi_off, listOf(ssid), setOf(Need.LOCATION), exit = "wifi_on"),
        Def("airplane", Kind.TRIGGER, Cat.CONNECTIONS, R.string.t_airplane, listOf(state(ON_OFF))),
        Def("power_on", Kind.TRIGGER, Cat.POWER, R.string.t_power_on, exit = "power_off"),
        Def("power_off", Kind.TRIGGER, Cat.POWER, R.string.t_power_off, exit = "power_on"),
        Def("battery", Kind.TRIGGER, Cat.POWER, R.string.t_battery, listOf(compare, P("level", R.string.p_level, PType.NUMBER, def = "20"))),
        Def("boot", Kind.TRIGGER, Cat.POWER, R.string.t_boot),
        Def("screen_on", Kind.TRIGGER, Cat.SCREEN, R.string.t_screen_on, exit = "screen_off"),
        Def("screen_off", Kind.TRIGGER, Cat.SCREEN, R.string.t_screen_off, exit = "screen_on"),
        Def("unlock", Kind.TRIGGER, Cat.SCREEN, R.string.t_unlock),
        Def("unfold", Kind.TRIGGER, Cat.SCREEN, R.string.t_unfold, exit = "fold"),
        Def("fold", Kind.TRIGGER, Cat.SCREEN, R.string.t_fold, exit = "unfold"),
        Def("headset_on", Kind.TRIGGER, Cat.SCREEN, R.string.t_headset_on, exit = "headset_off"),
        Def("headset_off", Kind.TRIGGER, Cat.SCREEN, R.string.t_headset_off, exit = "headset_on"),
        Def(
            "time", Kind.TRIGGER, Cat.TIME, R.string.t_time,
            listOf(P("time", R.string.p_time, PType.TIME, def = "08:00"), P("days", R.string.p_days, PType.DAYS, def = ALL_DAYS)),
        ),
        Def("interval", Kind.TRIGGER, Cat.TIME, R.string.t_interval, listOf(P("minutes", R.string.p_minutes, PType.NUMBER, def = "30"))),
        Def("app_open", Kind.TRIGGER, Cat.APPS, R.string.t_app_open, listOf(app), setOf(Need.ACCESSIBILITY), exit = "app_close"),
        Def("app_close", Kind.TRIGGER, Cat.APPS, R.string.t_app_close, listOf(app), setOf(Need.ACCESSIBILITY), exit = "app_open"),
        Def(
            "notification", Kind.TRIGGER, Cat.APPS, R.string.t_notification,
            listOf(P("app", R.string.p_app, PType.APP), P("text", R.string.p_contains, PType.TEXT)),
            setOf(Need.NOTIF_LISTENER),
        ),
        Def("call_ring", Kind.TRIGGER, Cat.PHONE, R.string.t_call_ring, needs = setOf(Need.PHONE)),
        Def("call_start", Kind.TRIGGER, Cat.PHONE, R.string.t_call_start, needs = setOf(Need.PHONE), exit = "call_end"),
        Def("call_end", Kind.TRIGGER, Cat.PHONE, R.string.t_call_end, needs = setOf(Need.PHONE)),
        Def(
            "sms", Kind.TRIGGER, Cat.PHONE, R.string.t_sms,
            listOf(P("sender", R.string.p_sender, PType.TEXT), P("text", R.string.p_contains, PType.TEXT)),
            setOf(Need.SMS),
        ),
        Def("place_in", Kind.TRIGGER, Cat.PLACE, R.string.t_place_in, listOf(place, radius), setOf(Need.LOCATION, Need.BG_LOCATION), exit = "place_out"),
        Def("place_out", Kind.TRIGGER, Cat.PLACE, R.string.t_place_out, listOf(place, radius), setOf(Need.LOCATION, Need.BG_LOCATION), exit = "place_in"),
    )

    val conditions: List<Def> = listOf(
        Def(
            "k_time", Kind.CONDITION, Cat.TIME, R.string.k_time,
            listOf(P("start", R.string.p_start, PType.TIME, def = "22:00"), P("end", R.string.p_end, PType.TIME, def = "07:00")),
        ),
        Def("k_days", Kind.CONDITION, Cat.TIME, R.string.k_days, listOf(P("days", R.string.p_days, PType.DAYS, def = "1,2,3,4,5"))),
        Def("k_battery", Kind.CONDITION, Cat.POWER, R.string.k_battery, listOf(compare, P("level", R.string.p_level, PType.NUMBER, def = "30"))),
        Def("k_charging", Kind.CONDITION, Cat.POWER, R.string.k_charging, listOf(state(YES_NO, "yes"))),
        Def("k_bt", Kind.CONDITION, Cat.CONNECTIONS, R.string.k_bt, listOf(devices, state(CONNECTED, "yes")), setOf(Need.BLUETOOTH)),
        Def("k_wifi", Kind.CONDITION, Cat.CONNECTIONS, R.string.k_wifi, listOf(ssid, state(CONNECTED, "yes")), setOf(Need.LOCATION)),
        Def("k_fold", Kind.CONDITION, Cat.SCREEN, R.string.k_fold, listOf(state(FOLD, "unfolded"))),
        Def("k_headset", Kind.CONDITION, Cat.SCREEN, R.string.k_headset, listOf(state(YES_NO, "yes"))),
        Def("k_screen", Kind.CONDITION, Cat.SCREEN, R.string.k_screen, listOf(state(YES_NO, "yes"))),
        Def("k_ringer", Kind.CONDITION, Cat.SOUND, R.string.k_ringer, listOf(P("mode", R.string.p_mode, PType.CHOICE, RINGER, "normal"))),
        Def("k_app", Kind.CONDITION, Cat.APPS, R.string.k_app, listOf(app, state(YES_NO, "yes")), setOf(Need.ACCESSIBILITY)),
    )

    private val launch = setOf(Need.OVERLAY, Need.ACCESSIBILITY)

    val actions: List<Def> = listOf(
        // Connexions
        Def("wifi", Kind.ACTION, Cat.CONNECTIONS, R.string.a_wifi, listOf(state()), setOf(Need.SHELL)),
        Def("data", Kind.ACTION, Cat.CONNECTIONS, R.string.a_data, listOf(state()), setOf(Need.SHELL)),
        Def("bluetooth", Kind.ACTION, Cat.CONNECTIONS, R.string.a_bluetooth, listOf(state()), setOf(Need.SHELL)),
        Def("airplane_set", Kind.ACTION, Cat.CONNECTIONS, R.string.a_airplane, listOf(state()), setOf(Need.SHELL)),
        Def("nfc", Kind.ACTION, Cat.CONNECTIONS, R.string.a_nfc, listOf(state()), setOf(Need.SHELL)),
        Def("location", Kind.ACTION, Cat.CONNECTIONS, R.string.a_location, listOf(state()), setOf(Need.SHELL)),
        // Écran
        Def(
            "brightness", Kind.ACTION, Cat.SCREEN, R.string.a_brightness,
            listOf(P("mode", R.string.p_mode, PType.CHOICE, BRIGHTNESS_MODE, "manual"), level),
            setOf(Need.WRITE_SETTINGS),
        ),
        Def("timeout", Kind.ACTION, Cat.SCREEN, R.string.a_timeout, listOf(P("seconds", R.string.p_seconds, PType.NUMBER, def = "60")), setOf(Need.WRITE_SETTINGS)),
        Def("rotation", Kind.ACTION, Cat.SCREEN, R.string.a_rotation, listOf(state()), setOf(Need.WRITE_SETTINGS)),
        Def("dark", Kind.ACTION, Cat.SCREEN, R.string.a_dark, listOf(state()), setOf(Need.SHELL)),
        Def("stay_awake", Kind.ACTION, Cat.SCREEN, R.string.a_stay_awake, listOf(state(ON_OFF)), setOf(Need.SHELL)),
        Def("battery_saver", Kind.ACTION, Cat.SCREEN, R.string.a_battery_saver, listOf(state(ON_OFF)), setOf(Need.SHELL)),
        Def("flashlight", Kind.ACTION, Cat.SCREEN, R.string.a_flashlight, listOf(state())),
        Def("lock", Kind.ACTION, Cat.SCREEN, R.string.a_lock, anyOf = setOf(Need.ACCESSIBILITY, Need.SHELL)),
        Def("wake", Kind.ACTION, Cat.SCREEN, R.string.a_wake, needs = setOf(Need.SHELL)),
        Def("screenshot", Kind.ACTION, Cat.SCREEN, R.string.a_screenshot, anyOf = setOf(Need.ACCESSIBILITY, Need.SHELL)),
        // Son et médias
        Def("ringer", Kind.ACTION, Cat.SOUND, R.string.a_ringer, listOf(P("mode", R.string.p_mode, PType.CHOICE, RINGER, "vibrate")), setOf(Need.DND)),
        Def("dnd", Kind.ACTION, Cat.SOUND, R.string.a_dnd, listOf(P("mode", R.string.p_mode, PType.CHOICE, DND, "priority")), anyOf = setOf(Need.DND, Need.SHELL)),
        Def(
            "volume", Kind.ACTION, Cat.SOUND, R.string.a_volume,
            listOf(P("stream", R.string.p_stream, PType.CHOICE, STREAMS, "media"), level),
        ),
        Def("media", Kind.ACTION, Cat.SOUND, R.string.a_media, listOf(P("command", R.string.p_command_media, PType.CHOICE, MEDIA, "play_pause"))),
        Def(
            "play_app", Kind.ACTION, Cat.SOUND, R.string.a_play_app,
            listOf(app, P("open_first", R.string.p_open_first, PType.CHOICE, YES_NO, "yes")),
            anyOf = launch,
        ),
        Def("speak", Kind.ACTION, Cat.SOUND, R.string.a_speak, listOf(P("text", R.string.p_text, PType.MULTILINE))),
        Def("vibrate", Kind.ACTION, Cat.SOUND, R.string.a_vibrate, listOf(P("ms", R.string.p_duration_ms, PType.NUMBER, def = "400"))),
        // Applis
        Def("launch", Kind.ACTION, Cat.APPS, R.string.a_launch, listOf(app), anyOf = launch),
        Def("uri", Kind.ACTION, Cat.APPS, R.string.a_uri, listOf(P("uri", R.string.p_uri, PType.TEXT, hint = R.string.uri_hint)), anyOf = launch),
        Def(
            "navigate", Kind.ACTION, Cat.APPS, R.string.a_navigate,
            listOf(P("destination", R.string.p_destination, PType.TEXT), P("travel", R.string.p_travel, PType.CHOICE, TRAVEL, "d")),
            anyOf = launch,
        ),
        Def("force_stop", Kind.ACTION, Cat.APPS, R.string.a_force_stop, listOf(app), setOf(Need.SHELL)),
        Def("settings_page", Kind.ACTION, Cat.APPS, R.string.a_settings_page, listOf(P("page", R.string.p_page, PType.CHOICE, PAGES, "wifi")), anyOf = launch),
        Def(
            "intent", Kind.ACTION, Cat.APPS, R.string.a_intent,
            listOf(
                P("kind", R.string.p_kind, PType.CHOICE, INTENT_KIND, "activity"),
                P("action", R.string.p_action, PType.TEXT),
                P("data", R.string.p_data, PType.TEXT),
                P("package", R.string.p_package, PType.TEXT),
                P("extras", R.string.p_extras, PType.TEXT, hint = R.string.extras_hint),
            ),
        ),
        // Pilotage de l'écran
        Def("global", Kind.ACTION, Cat.CONTROL, R.string.a_global, listOf(P("button", R.string.p_button, PType.CHOICE, BUTTONS, "back")), setOf(Need.ACCESSIBILITY)),
        Def("click", Kind.ACTION, Cat.CONTROL, R.string.a_click, listOf(P("text", R.string.p_text, PType.TEXT)), setOf(Need.ACCESSIBILITY)),
        Def("clear_notifs", Kind.ACTION, Cat.CONTROL, R.string.a_clear_notifs, needs = setOf(Need.NOTIF_LISTENER)),
        // Appels et SMS
        Def(
            "send_sms", Kind.ACTION, Cat.PHONE, R.string.a_sms,
            listOf(P("number", R.string.p_number, PType.TEXT), P("message", R.string.p_message, PType.MULTILINE)),
            setOf(Need.SEND_SMS),
        ),
        Def("call", Kind.ACTION, Cat.PHONE, R.string.a_call, listOf(P("number", R.string.p_number, PType.TEXT)), setOf(Need.CALL)),
        // Système
        Def(
            "notify", Kind.ACTION, Cat.SYSTEM, R.string.a_notify,
            listOf(P("title", R.string.p_title, PType.TEXT), P("text", R.string.p_text, PType.MULTILINE)),
            setOf(Need.NOTIFICATIONS),
        ),
        Def("shell", Kind.ACTION, Cat.SYSTEM, R.string.a_shell, listOf(P("command", R.string.p_command, PType.MULTILINE)), setOf(Need.SHELL)),
        Def(
            "setting", Kind.ACTION, Cat.SYSTEM, R.string.a_setting,
            listOf(
                P("namespace", R.string.p_namespace, PType.CHOICE, NAMESPACES, "system"),
                P("key", R.string.p_key, PType.TEXT),
                P("value", R.string.p_value, PType.TEXT),
            ),
            anyOf = setOf(Need.SHELL, Need.WRITE_SETTINGS),
        ),
        // Déroulement
        Def("wait", Kind.ACTION, Cat.FLOW, R.string.a_wait, listOf(P("seconds", R.string.p_seconds, PType.NUMBER, def = "5"))),
        Def("run", Kind.ACTION, Cat.FLOW, R.string.a_run, listOf(P("routine", R.string.p_routine, PType.ROUTINE))),
        Def(
            "toggle_auto", Kind.ACTION, Cat.FLOW, R.string.a_toggle_auto,
            listOf(P("routine", R.string.p_routine, PType.ROUTINE), state()),
        ),
    )

    private val all: Map<String, Def> by lazy { (triggers + conditions + actions).associateBy { it.type } }

    fun get(type: String): Def? = all[type]

    fun list(kind: Kind): List<Def> = when (kind) {
        Kind.TRIGGER -> triggers
        Kind.CONDITION -> conditions
        Kind.ACTION -> actions
    }

    /** Étape neuve avec les valeurs par défaut. */
    fun newStep(def: Def): Step = Step(def.type, def.params.associate { it.key to it.def })
}
