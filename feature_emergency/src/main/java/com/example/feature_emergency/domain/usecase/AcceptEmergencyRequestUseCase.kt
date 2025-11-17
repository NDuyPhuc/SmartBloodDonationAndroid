package com.example.feature_emergency.domain.usecase

import com.smartblood.core.domain.model.Donor
import com.example.feature_emergency.domain.repository.EmergencyRepository
import com.smartblood.core.domain.model.UserProfile // Import từ feature_profile

import javax.inject.Inject

class AcceptEmergencyRequestUseCase @Inject constructor(
    private val emergencyRepository: EmergencyRepository
) {
    suspend operator fun invoke(requestId: String, userProfile: UserProfile): Result<Unit> {
        // Kiểm tra thông tin cần thiết từ profile
        if (userProfile.uid.isBlank() || userProfile.fullName.isBlank()) {
            return Result.failure(Exception("Thông tin người dùng không đầy đủ."))
        }

        // Tạo đối tượng Donor từ UserProfile
        val donorInfo = Donor(
            userId = userProfile.uid,
            userName = userProfile.fullName,
            userPhone = userProfile.phoneNumber ?: "",
            userBloodType = userProfile.bloodType ?: "N/A"
            // pledgedAt và status sẽ dùng giá trị mặc định
        )

        // Gọi phương thức từ repository
        return emergencyRepository.acceptEmergencyRequest(requestId, donorInfo)
    }
}