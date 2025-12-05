package com.smartblood.donation

import android.app.Application
import android.util.Log
import com.cloudinary.android.MediaManager
import com.trackasia.android.TrackAsia // <-- Thêm TrackAsia
import java.io.InputStream
import java.util.Properties
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val properties = Properties()
        try {
            val inputStream: InputStream = assets.open("local.properties")
            properties.load(inputStream)
        } catch (e: Exception) {
            Log.e("MainApplication", "Could not read local.properties file", e)
        }
        val cloudName = properties.getProperty("CLOUDINARY_CLOUD_NAME", "")
        val apiKey = properties.getProperty("CLOUDINARY_API_KEY", "")
        val apiSecret = properties.getProperty("CLOUDINARY_API_SECRET", "")
        if (cloudName.isEmpty() || apiKey.isEmpty() ) {
            Log.e("MainApplication", "Cloudinary credentials are not set in local.properties")
        }
        val config = mapOf(
            "cloud_name" to cloudName,
            "api_key" to apiKey,
            "api_secret" to apiSecret
        )
        MediaManager.init(this, config)


        TrackAsia.getInstance(applicationContext)
    }
}