//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\domain\model\DonationRecord.kt
package com.smartblood.profile.domain.model

import java.util.Date

data class DonationRecord(
    val id: String = "",
    val hospitalName: String = "",
    val hospitalAddress: String = "", // Thêm địa chỉ cho chi tiết
    val date: Date = Date(),
    val status: String = "",
    val certificateUrl: String? = null // <--- QUAN TRỌNG: Link chứng nhận từ Web
)