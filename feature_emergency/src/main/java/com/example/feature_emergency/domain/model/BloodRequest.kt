package com.example.feature_emergency.domain.model

import java.util.Date

data class BloodRequest(
    val id: String = "",
    val bloodType: String = "",
    val hospitalId: String = "",
    val hospitalName: String = "",
    val priority: String = "Trung bình",
    val quantity: Int = 0,
    val status: String = "ĐANG HOẠT ĐỘNG",
    val createdAt: Date = Date()
)