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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
        
        val prefs = getSharedPreferences("mave_prefs", android.content.Context.MODE_PRIVATE)
        val hasAcceptedPrivacy = prefs.getBoolean("has_accepted_privacy_policy", false)
        
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
            firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
        } else {
            firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
            if (hasAcceptedPrivacy) {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
                FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
                Timber.plant(CrashlyticsTree())
            } else {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
                FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(false)
            }
        }

        val isEmulator = android.os.Build.HARDWARE.contains("ranchu") || android.os.Build.HARDWARE.contains("goldfish") || android.os.Build.HARDWARE.contains("qemu") || android.os.Build.FINGERPRINT.contains("generic") || android.os.Build.MODEL.contains("Emulator") || android.os.Build.PRODUCT.contains("sdk") || android.os.Build.BRAND.contains("google")
        if (isEmulator) {
            Timber.i("Running on emulator, using MockDeviceKit")
            val mockDeviceKit = com.meta.wearable.dat.mockdevice.MockDeviceKit.getInstance(this)
            mockDeviceKit.enable()
        } else {
            val result = Wearables.initialize(this)
            result.onFailure { error ->
                Timber.e("Failed to initialize DAT: $error")
            }
        }


        EngageBroadcastReceiver.register(this)
    }
}
