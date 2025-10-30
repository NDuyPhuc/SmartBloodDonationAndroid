// app/src/main/java/com/smartblood/donation/MainApplication.kt
package com.smartblood.donation


import android.app.Application
import com.trackasia.android.TrackAsia
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Chỉ khởi tạo TrackAsia ở đây là đủ
        TrackAsia.getInstance(applicationContext)
    }
}