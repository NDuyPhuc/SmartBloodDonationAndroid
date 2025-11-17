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
        // --- BẮT ĐẦU CÁCH TIẾP CẬN MỚI ---
        // Tạo một đối tượng Properties để chứa các khóa
        val properties = Properties()
        try {
            // Mở file local.properties từ thư mục assets
            val inputStream: InputStream = assets.open("local.properties")
            // Tải dữ liệu từ file vào đối tượng properties
            properties.load(inputStream)
        } catch (e: Exception) {
            // Ghi log lỗi nếu không tìm thấy file
            Log.e("MainApplication", "Could not read local.properties file", e)
        }
        // Lấy các giá trị từ properties
        val cloudName = properties.getProperty("CLOUDINARY_CLOUD_NAME", "")
        val apiKey = properties.getProperty("CLOUDINARY_API_KEY", "")
        val apiSecret = properties.getProperty("CLOUDINARY_API_SECRET", "") // <-- Đảm bảo API Secret cũng được lấy
        // Kiểm tra xem các khóa có rỗng không (để debug)
        if (cloudName.isEmpty() || apiKey.isEmpty() /*|| apiSecret.isEmpty()*/) { // <-- Bỏ kiểm tra apiSecret nếu bạn chỉ dùng apiKey cho init MediaManager
            Log.e("MainApplication", "Cloudinary credentials are not set in local.properties")
        }
        // Tạo map config và khởi tạo MediaManager
        val config = mapOf(
            "cloud_name" to cloudName,
            "api_key" to apiKey,
            "api_secret" to apiSecret // <-- Thêm apiSecret vào config nếu Cloudinary SDK yêu cầu
        )
        MediaManager.init(this, config) // <-- Khởi tạo Cloudinary

        // --- KẾT THÚC CÁCH TIẾP CẬN MỚI ---

        TrackAsia.getInstance(applicationContext)
    }
}