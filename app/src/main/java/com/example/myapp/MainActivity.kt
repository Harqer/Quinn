package com.example.myapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.types.RegistrationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Meta Wearables Companion App Controller
 * Implements the Wearables Device Access Toolkit (WDAT) protocols on Android.
 * Connects to the real Meta Wearables SDK and launches the foreground streaming service.
 */
class MainActivity : Activity() {

    private val TAG = "MetaWearablesWDAT"
    private val mainScope = CoroutineScope(Dispatchers.Main)

    // Telemetry and hardware state
    var currentBatteryLevel: Int = 100
    var isWearDetected: Boolean = true
    var isUsbCharging: Boolean = false
    var lastLoggedGesture: String = ""
    var isAppSwitcherOpen: Boolean = false

    // Registered credentials
    var metaAppId: String = ""
    var clientToken: String = ""

    private var webView: WebView? = null

    // WebAppInterface for bridging Kotlin DAT events to WebView
    class WebAppInterface(private val activity: MainActivity) {
        @JavascriptInterface
        fun onGestureEvent(gesture: String) {
            activity.runOnUiThread {
                activity.logI("WebAppInterface", "Gesture event from glasses: $gesture")
                activity.sendGestureToWebview(gesture)
            }
        }

        @JavascriptInterface
        fun triggerCameraFrame(base64Frame: String) {
            activity.runOnUiThread {
                activity.logI("WebAppInterface", "Glasses POV camera frame injected")
                activity.sendCameraFrameToWebview(base64Frame)
            }
        }

        @JavascriptInterface
        fun triggerTelemetry(batteryLevel: Int, isWearDetected: Boolean) {
            activity.runOnUiThread {
                activity.logI("WebAppInterface", "Telemetry status updated")
                activity.sendTelemetryToWebview(batteryLevel, isWearDetected)
            }
        }
    }

    fun sendGestureToWebview(gesture: String) {
        runOnUiThread {
            webView?.evaluateJavascript("javascript:if(window.onAndroidGesture) { window.onAndroidGesture('$gesture'); }", null)
        }
    }

    fun sendCameraFrameToWebview(base64Frame: String) {
        runOnUiThread {
            webView?.evaluateJavascript("javascript:if(window.onAndroidCameraFrame) { window.onAndroidCameraFrame('$base64Frame'); }", null)
        }
    }

    fun sendTelemetryToWebview(batteryLevel: Int, isWearDetected: Boolean) {
        runOnUiThread {
            webView?.evaluateJavascript("javascript:if(window.onAndroidTelemetry) { window.onAndroidTelemetry($batteryLevel, $isWearDetected); }", null)
        }
    }

    // Safe logging utility to prevent JUnit runtime exceptions
    fun logI(tag: String, msg: String) {
        try {
            android.util.Log.i(tag, msg)
        } catch (e: Exception) {
            println("[$tag] [INFO] $msg")
        }
    }

    fun logD(tag: String, msg: String) {
        try {
            android.util.Log.d(tag, msg)
        } catch (e: Exception) {
            println("[$tag] [DEBUG] $msg")
        }
    }

    fun logW(tag: String, msg: String) {
        try {
            android.util.Log.w(tag, msg)
        } catch (e: Exception) {
            println("[$tag] [WARNING] $msg")
        }
    }

    fun logE(tag: String, msg: String) {
        try {
            android.util.Log.e(tag, msg)
        } catch (e: Exception) {
            System.err.println("[$tag] [ERROR] $msg")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logI(TAG, "Initializing Meta Wearables Companion Service...")
        
        // Initialize WebView programmatically for the companion app bridge
        try {
            webView = WebView(this).apply {
                settings.javaScriptEnabled = true
                addJavascriptInterface(WebAppInterface(this@MainActivity), "AndroidBridge")
            }
            logI(TAG, "Android WebApp Javascript Interface successfully registered on 'AndroidBridge'")
        } catch (e: Exception) {
            logW(TAG, "WebView runtime not available in headful container: ${e.message}")
        }
        
        // Load configuration from manifest placeholders
        loadManifestCredentials()

        // 1. Initialize SDK
        Wearables.initialize(this).onFailure { error, _ ->
            logE(TAG, "Failed to initialize DAT: ${error.description}")
        }

        // 2. Start Registration Flow with Meta AI App
        Wearables.startRegistration(this)

        // 3. Observe Registration State
        mainScope.launch {
            Wearables.registrationState.collect { state ->
                logI(TAG, "WDAT Registration State changed: $state")
                if (state == RegistrationState.REGISTERED) {
                    logI(TAG, "App successfully registered with Meta Directory.")
                    startForegroundStreamingService()
                }
            }
        }

        // 4. Observe Discovered Devices (For logging/UI purposes)
        mainScope.launch {
            Wearables.devices.collect { devices ->
                logI(TAG, "Discovered bonded wearables: $devices")
            }
        }
    }

    private fun startForegroundStreamingService() {
        val intent = Intent(this, WearableStreamingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    /**
     * Reads cryptographic WDAT authorization parameters from Android Manifest Metadata.
     */
    fun loadManifestCredentials() {
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val bundle = appInfo.metaData
            if (bundle != null) {
                metaAppId = bundle.getString("mwdat_application_id") ?: ""
                clientToken = bundle.getString("mwdat_client_token") ?: ""
                logD(TAG, "Loaded WDAT AppID: $metaAppId")
            }
        } catch (e: Exception) {
            logE(TAG, "Failed to load manifest WDAT credentials: ${e.message}")
        }
    }

    /**
     * Processes telemetry payloads coming from the smart glasses.
     */
    fun handleTelemetryUpdate(battery: Int, onHead: Boolean, usbConnected: Boolean) {
        currentBatteryLevel = battery
        isWearDetected = onHead
        isUsbCharging = usbConnected
        
        logI(TAG, "TELEMETRY SYNCHRONIZED -> Battery: ${battery}%, Wear Detected: $onHead, Charging: $usbConnected")
        sendTelemetryToWebview(battery, onHead)
        
        if (!onHead) {
            logW(TAG, "Wearer proximity lost! Suspending POV live camera stream.")
        }
    }

    /**
     * Processes physical and hand air gestures detected by the wearable peripherals.
     */
    fun registerGesture(gestureType: String) {
        lastLoggedGesture = gestureType
        logD(TAG, "GESTURE EVENT -> Captured: $gestureType")
        sendGestureToWebview(gestureType)

        when (gestureType) {
            "tap" -> logI(TAG, "Action: Single Tap -> Play / Pause Audio")
            "double_tap" -> logI(TAG, "Action: Double Tap -> Skip Track Forward")
            "triple_tap" -> logI(TAG, "Action: Triple Tap -> Go Back a Track")
            "swipe_forward" -> logI(TAG, "Action: Swipe Forward -> Volume Up")
            "swipe_backward" -> logI(TAG, "Action: Swipe Backward -> Volume Down")
            "index_finger_turn_right" -> logI(TAG, "Action: Index Turn Right -> Volume Up")
            "index_finger_turn_left" -> logI(TAG, "Action: Index Turn Left -> Volume Down")
            "thumb_to_index_double_tap" -> logI(TAG, "Action: Thumb-Index Double Tap -> Toggle Meta Voice AI")
            "middle_finger_to_thumb_hold" -> {
                isAppSwitcherOpen = !isAppSwitcherOpen
                logI(TAG, "Action: Middle-Thumb Hold -> ${if (isAppSwitcherOpen) "Open" else "Close"} Horizon OS App Switcher")
            }
            "wrist_turn_clockwise" -> logI(TAG, "Action: Wrist Clockwise -> Volume Up")
            "wrist_turn_counter_clockwise" -> logI(TAG, "Action: Wrist Counter-Clockwise -> Volume Down")
            else -> logW(TAG, "Unknown gesture type received: $gestureType")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}
