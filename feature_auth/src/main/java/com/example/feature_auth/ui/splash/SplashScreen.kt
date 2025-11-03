// D:\...\feature_auth\src\main\java\com\smartblood\auth\ui\splash\SplashScreen.kt

package com.example.feature_auth.ui.splash

// --- CÁC IMPORT MỚI CẦN THÊM ---
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
// --------------------------------

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset // --- IMPORT MỚI ---
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale // --- IMPORT MỚI ---
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp // --- IMPORT MỚI ---
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartblood.core.ui.theme.PrimaryRed
import com.smartblood.core.ui.theme.PrimaryRedDark
import com.smartblood.core.ui.theme.SmartBloodTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    navigateToDashboard: () -> Unit
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    var startAnimation by remember { mutableStateOf(false) } // <--- Đổi tên biến alphaAnim thành startAnimation để rõ nghĩa hơn

    // Kích hoạt animation và xử lý điều hướng
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000L) // Tăng thời gian chờ lên 3 giây để xem animation rõ hơn

        // Sau khi chờ, nếu chưa có kết quả xác thực thì mặc định đi đến login
        if (isAuthenticated == null) {
            navigateToLogin()
        }
    }

    // Thêm một LaunchedEffect khác để xử lý điều hướng ngay khi có kết quả
    LaunchedEffect(key1 = isAuthenticated) {
        if (isAuthenticated != null) {
            if (isAuthenticated == true) {
                navigateToDashboard()
            } else {
                navigateToLogin()
            }
        }
    }

    // Gọi giao diện Splash và truyền vào công tắc animation
    SplashContent(startAnimation = startAnimation)
}

// Tách riêng phần giao diện để dễ dàng preview và tái sử dụng
@Composable
fun SplashContent(startAnimation: Boolean) { // <--- THAM SỐ ĐÃ THAY ĐỔI

    // --- NÂNG CẤP 1: ANIMATION CHO HIỆU ỨNG "BƠM TIM" ---
    val scale = remember { Animatable(1f) }
    LaunchedEffect(startAnimation) {
        if (startAnimation) {
            scale.animateTo(
                targetValue = 1.1f, // Phóng to 10%
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 700), // Thời gian 1 nhịp
                    repeatMode = RepeatMode.Reverse // Lặp ngược lại (phóng to -> thu nhỏ)
                )
            )
        }
    }

    // --- NÂNG CẤP 2: ANIMATION CHO HIỆU ỨNG "TRƯỢT LÊN" ---
    val offsetY: Dp by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 100.dp,
        animationSpec = tween(durationMillis = 1500),
        label = "offset_animation"
    )

    // --- GIỮ NGUYÊN: ANIMATION CHO HIỆU ỨNG "MỜ DẦN" ---
    val alpha: Float by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "alpha_animation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryRed,
                        PrimaryRedDark
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alpha) // Vẫn áp dụng hiệu ứng mờ dần
        ) {
            // NÂNG CẤP: Áp dụng hiệu ứng "Bơm Tim" vào Icon
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value), // <-- Dùng giá trị scale từ animation
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // NÂNG CẤP: Áp dụng hiệu ứng "Trượt Lên" vào Text
            Text(
                modifier = Modifier.offset(y = offsetY), // <-- Dùng giá trị offset từ animation
                text = "Smart Blood Donation",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Hàm Preview để xem trước giao diện ngay trong Android Studio
@Preview(showBackground = true, name = "Splash Screen Preview")
@Composable
fun SplashScreenPreview() {
    SmartBloodTheme {
        // Thay đổi tham số để xem trước trạng thái animation đã bắt đầu
        SplashContent(startAnimation = true)
    }
}