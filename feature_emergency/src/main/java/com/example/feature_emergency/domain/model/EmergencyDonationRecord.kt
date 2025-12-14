package com.example.feature_emergency.domain.model

import com.smartblood.core.domain.model.LabResult
import java.util.Date

data class EmergencyDonationRecord(
    val id: String = "",
    val requestId: String = "",
    val hospitalName: String = "",
    val pledgedAt: Date = Date(),
    val status: String = "",
    val userBloodType: String = "",
    // Các field sau khi hoàn thành
    val certificateUrl: String? = null,
    val rating: Int = 0,
    val review: String? = null,
    val labResult: LabResult? = null,
    val rejectionReason: String? = null
)