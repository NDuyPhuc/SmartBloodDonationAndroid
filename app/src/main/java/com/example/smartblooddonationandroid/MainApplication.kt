// app/src/main/java/com/smartblood/donation/MainApplication.kt
package com.smartblood.donation


import android.app.Application
import com.trackasia.android.TrackAsia
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val trackAsiaApiKey = "52fedb6b306931761836057e5580a05be7"

        // Chỉ khởi tạo TrackAsia ở đây là đủ
        TrackAsia.getInstance(applicationContext)
    }
}