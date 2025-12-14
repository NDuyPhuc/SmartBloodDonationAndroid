package com.smartblood.core.domain.model

import java.util.Date

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String? = null,
    val bloodType: String? = null,
    val avatarUrl: String? = null,
    val dateOfBirth: String? = null, // Lưu ý: Nếu Web lưu string thì để String
    val gender: String? = null,

    // --- SỬA Ở ĐÂY ---
    // Đổi từ Date? sang String? để khớp với dữ liệu "14/12/2025" trên Firestore
    val lastDonationDate: String? = null,

    val donationCount: Int = 0,
    val lastDonationType: String? = null,
    val isPriority: Boolean = false,
    val status: String = "Active"
)