package com.example.feature_profile.domain.usecase

import com.example.feature_profile.domain.repository.ProfileRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    operator fun invoke() {
        repository.signOut()
    }
}