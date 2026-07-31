package com.example.myapp

import android.app.Application
import com.meta.wearable.dat.core.Wearables

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Wearables.initialize(this)
            .onFailure { error, _ -> 
                android.util.Log.e("MyApplication", "Failed to initialize DAT: ${error.description}")
            }
    }
}
