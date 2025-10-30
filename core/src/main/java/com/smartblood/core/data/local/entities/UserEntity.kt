// D:\SmartBloodDonationAndroid\core\src\main\java\com\smartblood\core\data\local\entities\UserEntity.kt
package com.smartblood.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String?,
    val bloodType: String?,
    val avatarUrl: String?,
    val dateOfBirth: Date?,
    val gender: String?,
    val lastDonationDate: Date?
)