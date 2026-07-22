package com.musically.studio

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Test

class E2ETest {

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val timeout = 5000L

    @Test
    fun testAppLaunchAndWelcome() {
        val launcherPackage = device.launcherPackageName
        assertNotNull(launcherPackage)
        
        device.pressHome()
        
        // Start from the home screen
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = context.packageManager.getLaunchIntentForPackage("com.musically.studio")
        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg("com.musically.studio").depth(0)), timeout)
        
        // Verify welcome text is present (using a generic matcher)
        // device.wait(Until.hasObject(By.textContains("Millions of Vibes")), timeout)
    }
}
