package com.musically.studio

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import android.os.Build

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class MainActivityTest {

    @Test
    fun `launch MainActivity to check for crash`() {
        try {
            val activity = Robolectric.buildActivity(MainActivity::class.java)
                .create()
                .start()
                .resume()
                .get()
            println("MainActivity launched successfully.")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
