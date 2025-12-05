package com.example.feature_emergency.domain.model

import java.util.Date

data class EmergencyDonationRecord(
    val id: String = "",
    val requestId: String = "", // ID của yêu cầu gốc
    val hospitalName: String = "", // Lấy từ Parent Document
    val pledgedAt: Date = Date(),
    val status: String = "", // 'Pending', 'Completed', 'Cancelled'
    val userBloodType: String = "",

    // Các field sau khi hoàn thành
    val certificateUrl: String? = null,
    val rating: Int = 0, // 1-5 sao
    val review: String? = null
)