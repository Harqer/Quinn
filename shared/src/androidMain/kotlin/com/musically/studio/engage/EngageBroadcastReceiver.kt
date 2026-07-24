package com.musically.studio.engage

import android.content.BroadcastReceiver
import android.content.Context
import androidx.core.content.ContextCompat
import android.content.Intent
import android.content.IntentFilter
import com.google.android.engage.service.BroadcastReceiverPermissions
import com.google.android.engage.service.Intents

class EngageBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intents.ACTION_PUBLISH_RECOMMENDATION,
            Intents.ACTION_PUBLISH_CONTINUATION,
            Intents.ACTION_PUBLISH_FEATURED -> {
                EngagePublisher.publish(context)
            }
        }
    }

    companion object {
        fun register(context: Context) {
            val appContext = context.applicationContext

            val receiver = EngageBroadcastReceiver()
            
            ContextCompat.registerReceiver(
                appContext,
                receiver,
                IntentFilter(Intents.ACTION_PUBLISH_RECOMMENDATION),
                BroadcastReceiverPermissions.BROADCAST_REQUEST_DATA_PUBLISH_PERMISSION,
                null,
                ContextCompat.RECEIVER_EXPORTED
            )

            ContextCompat.registerReceiver(
                appContext,
                receiver,
                IntentFilter(Intents.ACTION_PUBLISH_CONTINUATION),
                BroadcastReceiverPermissions.BROADCAST_REQUEST_DATA_PUBLISH_PERMISSION,
                null,
                ContextCompat.RECEIVER_EXPORTED
            )

            ContextCompat.registerReceiver(
                appContext,
                receiver,
                IntentFilter(Intents.ACTION_PUBLISH_FEATURED),
                BroadcastReceiverPermissions.BROADCAST_REQUEST_DATA_PUBLISH_PERMISSION,
                null,
                ContextCompat.RECEIVER_EXPORTED
            )
        }
    }
}
