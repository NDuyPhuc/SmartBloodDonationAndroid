package com.smartblood.donation

import android.app.Application
import android.util.Log
import android.net.Uri // <-- Thêm Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cloudinary.android.MediaManager
import com.trackasia.android.TrackAsia // <-- Thêm TrackAsia
import dagger.hilt.android.AndroidEntryPoint
import java.io.InputStream
import java.util.Properties
import java.util.Date // <-- Thêm Date
import java.util.Locale // <-- Thêm Locale
import java.text.SimpleDateFormat // <-- Thêm SimpleDateFormat
import androidx.compose.foundation.lazy.grid.GridCells // <-- Thêm GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid // <-- Thêm LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items // <-- Thêm items
import androidx.compose.foundation.shape.CircleShape // <-- Thêm CircleShape
import androidx.compose.material.icons.Icons // <-- Thêm Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // <-- Thêm ArrowBack
import androidx.compose.material.icons.filled.Phone // <-- Thêm Phone
import androidx.compose.ui.graphics.vector.ImageVector // <-- Thêm ImageVector
import androidx.compose.ui.text.font.FontWeight // <-- Thêm FontWeight
import androidx.compose.ui.unit.sp // <-- Thêm sp
import androidx.compose.ui.viewinterop.AndroidView // <-- Thêm AndroidView
import androidx.lifecycle.Lifecycle // <-- Thêm Lifecycle
import androidx.lifecycle.LifecycleEventObserver // <-- Thêm LifecycleEventObserver
import com.example.feature_map_booking.domain.model.Hospital // <-- Thêm Hospital
import com.google.gson.JsonParser // <-- Thêm JsonParser
import com.trackasia.android.camera.CameraUpdateFactory // <-- Thêm CameraUpdateFactory
import com.trackasia.android.geometry.LatLng // <-- Thêm LatLng
import com.trackasia.android.location.LocationComponent // <-- Thêm LocationComponent
import com.trackasia.android.location.LocationComponentActivationOptions // <-- Thêm LocationComponentActivationOptions
import com.trackasia.android.location.modes.CameraMode // <-- Thêm CameraMode
import com.trackasia.android.location.modes.RenderMode // <-- Thêm RenderMode
import com.trackasia.android.maps.MapView // <-- Thêm MapView
import com.trackasia.android.maps.Style // <-- Thêm Style
import com.trackasia.android.plugins.annotation.SymbolManager // <-- Thêm SymbolManager
import com.trackasia.android.plugins.annotation.SymbolOptions // <-- Thêm SymbolOptions
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