package fr.apkfold.flare.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import fr.apkfold.flare.core.Engine

/** Accès aux notifications : déclencheur « notification reçue », contrôle des lecteurs, effacement. */
class FlareNotificationListener : NotificationListenerService() {

    companion object {
        @Volatile
        var instance: FlareNotificationListener? = null
            private set
    }

    /** Évite de relancer une automatisation quand une notification est seulement mise à jour. */
    private val seen = object : LinkedHashMap<String, Int>(64, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Int>?): Boolean = size > 200
    }

    override fun onListenerConnected() {
        instance = this
    }

    override fun onListenerDisconnected() {
        instance = null
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val n = sbn ?: return
        if (n.packageName == packageName || n.isOngoing) return
        if (n.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) return
        val ex = n.notification.extras
        val title = ex.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = ex.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val hash = (title + "\u0000" + text).hashCode()
        synchronized(seen) {
            if (seen[n.key] == hash) return
            seen[n.key] = hash
        }
        Engine.event(this, "notification", mapOf("pkg" to n.packageName, "title" to title, "text" to text))
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        val key = sbn?.key ?: return
        synchronized(seen) { seen.remove(key) }
    }
}
