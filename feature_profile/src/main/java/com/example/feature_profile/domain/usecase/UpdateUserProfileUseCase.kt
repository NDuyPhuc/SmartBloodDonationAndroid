// Vị trí: feature_profile/src/main/java/com/smartblood/profile/domain/usecase/UpdateUserProfileUseCase.kt
package com.smartblood.profile.domain.usecase

import com.smartblood.profile.domain.model.UserProfile
import com.smartblood.profile.domain.repository.ProfileRepository
import javax.inject.Inject
import kotlin.Result

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(userProfile: UserProfile): Result<Unit> {
        // Có thể thêm logic validation ở đây nếu cần
        return repository.updateUserProfile(userProfile)
    }
}