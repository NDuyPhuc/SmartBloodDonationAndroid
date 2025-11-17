plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.feature_emergency"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    ksp(libs.hilt.compiler)
    implementation(project(":core"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.firebase.firestore)
    implementation(project(":feature_profile"))
    implementation(libs.firebase.auth)
    // Dòng hilt-android không cần thiết vì đã có trong core, nhưng để cũng không sao
    // implementation(libs.hilt.android)

    // THAY ĐỔI Ở ĐÂY
    // implementation(libs.androidx.material3) // <--- XÓA HOẶC CHÚ THÍCH DÒNG NÀY

    // THAY BẰNG CÁCH SỬ DỤNG BOM (Bill of Materials)
    implementation(platform(libs.androidx.compose.bom)) // Quan trọng: Khai báo BOM
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3) // Bây giờ dòng này sẽ hoạt động
    implementation(libs.androidx.compose.ui.tooling.preview)

    // ... các dependency khác
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.hilt.navigation.compose)

}