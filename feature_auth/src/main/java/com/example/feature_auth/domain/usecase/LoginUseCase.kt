// feature_auth/src/main/java/com/smartblood/auth/domain/usecase/LoginUseCase.kt

package com.example.feature_auth.domain.usecase

import com.example.feature_auth.domain.model.User
import com.example.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Có thể thêm logic kiểm tra dữ liệu đầu vào ở đây
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password cannot be empty."))
        }
        return repository.loginWithEmail(email, password)
    }
}