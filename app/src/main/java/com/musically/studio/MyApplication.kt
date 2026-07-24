package com.musically.studio

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import com.meta.wearable.dat.core.Wearables
import com.musically.studio.appfunctions.MaveFunctions
import com.musically.studio.engage.EngageBroadcastReceiver
import com.musically.studio.logging.CrashlyticsTree
import dagger.hilt.android.HiltAndroidApp
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
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
        
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
            firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
        } else {
            firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
            val prefs = getSharedPreferences("mave_prefs", android.content.Context.MODE_PRIVATE)
            val hasAcceptedPrivacy = prefs.getBoolean("has_accepted_privacy_policy", false)
            if (hasAcceptedPrivacy) {
                Timber.plant(CrashlyticsTree())
            }
        }

        val result = Wearables.initialize(this)
        result.onFailure { error ->
            Timber.e("Failed to initialize DAT: $error")
        }

        EngageBroadcastReceiver.register(this)
    }
}
