//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\domain\model\DonationRecord.kt
package com.smartblood.profile.domain.model

import com.smartblood.core.domain.model.LabResult
import java.util.Date

data class DonationRecord(
    val id: String = "",
    val hospitalName: String = "",
    val hospitalAddress: String = "",
    val date: Date = Date(),
    val status: String = "",
    val certificateUrl: String? = null, // Link chứng nhận

    // --- CÁC TRƯỜNG MỚI ---
    val actualVolume: String? = null, // Hiển thị lượng máu đã hiến
    val labResult: LabResult? = null  // Chứa link PDF kết quả và lời dặn
)