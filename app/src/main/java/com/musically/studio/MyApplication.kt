package com.musically.studio

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import com.meta.wearable.dat.core.Wearables
import com.musically.studio.appfunctions.MaveFunctions
import com.musically.studio.logging.CrashlyticsTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), AppFunctionConfiguration.Provider {

    @Inject lateinit var maveFunctions: MaveFunctions

    override val appFunctionConfiguration: AppFunctionConfiguration =
        AppFunctionConfiguration.Builder()
            .addEnclosingClassFactory(MaveFunctions::class.java) { maveFunctions }
            .build()

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
