// D:\SmartBloodDonationAndroid\core\src\main\java\com\smartblood\core\data\local\entities\DonationHistoryEntity.kt

package com.smartblood.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "donation_history")
data class DonationHistoryEntity(
    @PrimaryKey val id: String,
    val userId: String, // Thêm khóa ngoại để biết lịch sử này của ai
    val hospitalName: String,
    val date: Date,
    val unitsDonated: Int
)