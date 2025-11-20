package com.example.feature_profile.domain.repository

import com.smartblood.profile.domain.model.DonationRecord
import com.smartblood.core.domain.model.UserProfile
import kotlin.Result

interface ProfileRepository {
    suspend fun getUserProfile(): Result<UserProfile>
    suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit>
    suspend fun getDonationHistory(): Result<List<DonationRecord>>

    // Hàm đăng xuất
    fun signOut()
}