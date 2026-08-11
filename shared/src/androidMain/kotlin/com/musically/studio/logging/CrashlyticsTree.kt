package com.musically.studio.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        val crashlytics = FirebaseCrashlytics.getInstance()
        val priorityStr = when (priority) {
            Log.INFO -> "INFO"
            Log.WARN -> "WARN"
            Log.ERROR -> "ERROR"
            Log.ASSERT -> "ASSERT"
            else -> "UNKNOWN"
        }

        crashlytics.setCustomKey("log_level", priorityStr)
        tag?.let { crashlytics.setCustomKey("tag", it) }
        crashlytics.setCustomKey("thread_name", Thread.currentThread().name)
        crashlytics.log("[$priorityStr] ${tag?.let { "[$it] " } ?: ""}$message")

        if (priority == Log.ERROR || priority == Log.ASSERT) {
            if (t != null) {
                crashlytics.recordException(t)
            } else {
                crashlytics.recordException(Exception(message))
            }
        }
    }

    companion object {
        fun setUserId(userId: String) {
            FirebaseCrashlytics.getInstance().setUserId(userId)
        }

        fun setCustomKey(key: String, value: String) {
            FirebaseCrashlytics.getInstance().setCustomKey(key, value)
        }
    }
}

