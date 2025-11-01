//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\domain\model\UserProfile.kt
package com.smartblood.profile.domain.model

import java.util.Date
data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String? = null,
    val bloodType: String? = null, // Ví dụ: "A+", "O-", ...
    val avatarUrl: String? = null, // <<-- ĐÂY LÀ CHỖ CẦN THÊM HOẶC KIỂM TRA
    val dateOfBirth: Date? = null,
    val gender: String? = null, // "Male", "Female", "Other"
    val lastDonationDate: Date? = null
)