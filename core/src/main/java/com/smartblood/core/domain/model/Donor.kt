package com.smartblood.core.domain.model

import java.util.Date

// Model này chứa thông tin người hiến máu sẽ được lưu vào sub-collection
data class Donor(
    val userId: String = "",
    val userName: String = "",
    val userAge: Int = 0,
    val userGender: String = "Không xác định",
    val userPhone: String = "",
    val userBloodType: String = "",
    val requestedBloodType: String = "",
    val pledgedAt: Date = Date(),
    val status: String = "Pending",
    val pledgedVolume: String = "",
    val rejectionReason: String? = null

)