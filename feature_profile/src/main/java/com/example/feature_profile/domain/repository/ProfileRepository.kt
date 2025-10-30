// D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\smartblood\profile\domain\repository\ProfileRepository.kt

package com.smartblood.profile.domain.repository

import com.smartblood.profile.domain.model.DonationRecord
import com.smartblood.profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlin.Result

interface ProfileRepository {

    suspend fun refreshUserProfile()
    fun getUserProfile(): Flow<UserProfile?> // <-- Đổi thành Flow

    suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit>

    fun getDonationHistory(): Flow<List<DonationRecord>> // <-- Đổi thành Flow

    suspend fun refreshDonationHistory()

}