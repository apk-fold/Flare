package fr.apkfold.flare.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.inputmethod.InputMethodManager
import fr.apkfold.flare.core.DeviceState
import fr.apkfold.flare.core.Engine

/**
 * Service d'accessibilité : détecte l'appli au premier plan (ouverte / fermée), touche des éléments
 * à l'écran, boutons système (retour, accueil, verrouillage, capture…), lancement d'applis en arrière-plan.
 */
class FlareAccessibility : AccessibilityService() {

    companion object {
        @Volatile
        var instance: FlareAccessibility? = null
            private set
    }

    private var ignored: Set<String> = emptySet()

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        val imm = getSystemService(InputMethodManager::class.java)
        ignored = setOf(packageName, "com.android.systemui", "android") +
            (imm?.enabledInputMethodList?.map { it.packageName } ?: emptyList())
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return
        if (pkg in ignored || pkg == DeviceState.foregroundApp) return
        val prev = DeviceState.foregroundApp
        DeviceState.foregroundApp = pkg
        if (prev != null) Engine.event(this, "app_close", mapOf("pkg" to prev))
        Engine.event(this, "app_open", mapOf("pkg" to pkg))
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    /** Touche l'élément qui contient ce texte, dans n'importe quelle fenêtre visible. */
    fun clickText(text: String): Boolean {
        if (text.isBlank()) return false
        val roots = buildList {
            rootInActiveWindow?.let { add(it) }
            try {
                windows.mapNotNull { it.root }.forEach { add(it) }
            } catch (_: Exception) {
            }
        }
        var fallback: AccessibilityNodeInfo? = null
        for (root in roots) {
            val nodes = root.findAccessibilityNodeInfosByText(text) ?: continue
            for (n in nodes) {
                if (fallback == null) fallback = n
                var cur: AccessibilityNodeInfo? = n
                var depth = 0
                while (cur != null && depth < 8) {
                    if (cur.isClickable && cur.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return true
                    cur = cur.parent
                    depth++
                }
            }
        }
        val target = fallback ?: return false
        val r = Rect()
        target.getBoundsInScreen(r)
        if (r.isEmpty) return false
        val path = Path().apply { moveTo(r.exactCenterX(), r.exactCenterY()) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 60))
            .build()
        return dispatchGesture(gesture, null, null)
    }
}
