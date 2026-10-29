package fr.apkfold.flare.service

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import fr.apkfold.flare.R
import fr.apkfold.flare.core.Engine
import fr.apkfold.flare.core.Store

/** Tuile des réglages rapides qui lance le raccourci qu'on lui a attribué. */
abstract class BaseTile(private val n: Int) : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        refresh()
    }

    override fun onClick() {
        super.onClick()
        Store.init(this)
        val a = Store.tileTarget(n)?.let { Store.get(it) } ?: return
        Engine.runManual(this, a)
        qsTile?.let {
            it.state = Tile.STATE_ACTIVE
            it.updateTile()
        }
        Handler(Looper.getMainLooper()).postDelayed({ refresh() }, 900)
    }

    private fun refresh() {
        Store.init(this)
        val t = qsTile ?: return
        val a = Store.tileTarget(n)?.let { Store.get(it) }
        t.label = a?.name ?: getString(R.string.tile_n, n)
        t.state = if (a == null) Tile.STATE_UNAVAILABLE else Tile.STATE_INACTIVE
        t.updateTile()
    }
}

class FlareTile1 : BaseTile(1)
class FlareTile2 : BaseTile(2)
class FlareTile3 : BaseTile(3)

/** Raccourci épinglé sur l'écran d'accueil : lance les actions puis se ferme aussitôt. */
class RunShortcutActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Store.init(this)
        intent?.getStringExtra("id")?.let { Store.get(it) }?.let { Engine.runManual(this, it) }
        finish()
    }
}
