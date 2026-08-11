package com.musically.studio.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import timber.log.Timber

object SecurityUtils {
    /**
     * Validates that a string URL uses a safe web scheme (http or https).
     * Prevents malicious scheme injection (e.g. file://, javascript:, intent://).
     */
    fun isValidWebUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val lower = url.trim().lowercase()
        return lower.startsWith("https://") || lower.startsWith("http://")
    }

    /**
     * Safely launches an external web browser intent only if the URL is valid.
     */
    fun safeLaunchUrl(context: Context, url: String?) {
        if (!isValidWebUrl(url)) {
            Timber.w("Refusing to launch invalid or unsafe URL scheme: $url")
            return
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url!!.trim()))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to launch safe browser intent for URL: $url")
        }
    }
}
