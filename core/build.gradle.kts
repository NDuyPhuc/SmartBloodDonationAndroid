// D:\SmartBloodDonationAndroid\core\build.gradle.kts

plugins {
    // Sử dụng plugin cho thư viện Android
    alias(libs.plugins.android.library)
    // Plugin cho Kotlin
    alias(libs.plugins.kotlin.android)
    // Plugin cho KSP (để Hilt và Room hoạt động)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose.compiler)

}

android {
    namespace = "com.smartblood.core" // Đổi thành namespace của dự án
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8 // Sử dụng 1.8 là đủ và phổ biến
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Bật tính năng Jetpack Compose
    buildFeatures {
        compose = true
    }

}

dependencies {
    // Sử dụng bí danh từ libs.versions.toml để nhất quán

    // Core Android KTX
    implementation(libs.androidx.core.ktx)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom)) // BoM quản lý phiên bản
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Dependency Injection - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Local Database - Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    // Remote - Firebase
    implementation(platform(libs.firebase.bom)) // BoM quản lý phiên bản
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.play.services.auth) // Google Sign-In

    // Asynchronous - Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Networking (Để dành cho tương lai)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
}