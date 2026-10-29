package fr.apkfold.flare.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import fr.apkfold.flare.core.Engine
import fr.apkfold.flare.core.Scheduler
import fr.apkfold.flare.core.Store

/** Démarrage du téléphone ou mise à jour de Flare : relance le service et les alarmes. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        Store.init(ctx)
        val boot = intent.action == Intent.ACTION_BOOT_COMPLETED
        Scheduler.rescheduleAll(ctx)
        if (Store.serviceEnabled) FlareService.start(ctx, boot)
        else if (boot) Engine.event(ctx, "boot")
    }
}

/** Heure fixe ou intervalle atteint. */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        Store.init(ctx)
        val id = intent.data?.lastPathSegment ?: return
        val a = Store.get(id) ?: return
        val type = a.trigger?.type ?: return
        if (type == "time" || type == "interval") {
            val pending = goAsync()
            Engine.event(ctx, type, mapOf("id" to id))
            Scheduler.schedule(ctx, a)
            // laisse au moteur le temps de lancer les actions si le service n'est pas actif
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ pending.finish() }, 8000)
        }
    }
}

/** Arrivée dans une zone ou départ. */
class PlaceReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        Store.init(ctx)
        val id = intent.data?.lastPathSegment ?: return
        val entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)
        Engine.event(ctx, if (entering) "place_in" else "place_out", mapOf("id" to id))
    }
}
