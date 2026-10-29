package fr.apkfold.flare

import android.app.Application
import fr.apkfold.flare.core.FlareChannels
import fr.apkfold.flare.core.Store

class FlareApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Store.init(this)
        FlareChannels.create(this)
    }
}
