// feature_auth/src/main/java/com/smartblood/auth/domain/model/User.kt

package com.example.feature_auth.domain.model

data class User(
    val uid: String = "",
    val email: String = "",
    val fullName: String = ""
    // Thêm các trường khác sau này, ví dụ:
    // val bloodType: String? = null,
    // val phoneNumber: String? = null
)