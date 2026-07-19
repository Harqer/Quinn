package com.musically.studio

import android.app.Application
import com.meta.wearable.dat.core.Wearables
import com.musically.studio.logging.CrashlyticsTree
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val isDebug = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }

        val result = Wearables.initialize(this)
        result.onFailure { error ->
            Timber.e("Failed to initialize DAT: $error")
        }
    }
}
