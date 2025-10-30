//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\domain\repository\ProfileRepository.kt
package com.smartblood.profile.domain.repository

import com.smartblood.profile.domain.model.DonationRecord
import com.smartblood.profile.domain.model.UserProfile
import kotlin.Result

interface ProfileRepository {
    /**
     * Lấy thông tin hồ sơ của người dùng hiện tại.
     */
    suspend fun getUserProfile(): Result<UserProfile>

    /**
     * Cập nhật thông tin hồ sơ của người dùng.
     */
    suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit>

    /**
     * Lấy lịch sử hiến máu của người dùng hiện tại.
     */
    suspend fun getDonationHistory(): Result<List<DonationRecord>>
}