// feature_auth/src/main/java/com/smartblood/auth/domain/usecase/RegisterUseCase.kt

package com.example.feature_auth.domain.usecase

import com.example.feature_auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(fullName: String, email: String, password: String): Result<Unit> {
        if (fullName.isBlank() || email.isBlank() || password.length < 6) {
            return Result.failure(IllegalArgumentException("Vui lòng điền đầy đủ thông tin. Mật khẩu phải có ít nhất 6 ký tự."))
        }
        return repository.registerUser(fullName, email, password)
    }
}