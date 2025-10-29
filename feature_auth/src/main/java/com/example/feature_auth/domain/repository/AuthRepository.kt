// feature_auth/src/main/java/com/smartblood/auth/domain/repository/AuthRepository.kt

package com.smartblood.auth.domain.repository

// Sử dụng Result của Kotlin để đóng gói thành công hoặc lỗi một cách an toàn
import com.smartblood.auth.domain.model.User
import kotlin.Result

interface AuthRepository {
    fun isUserAuthenticated(): Boolean

    /**
     * Thực hiện đăng nhập bằng email và mật khẩu.
     * @return Result.success(Unit) nếu thành công, Result.failure(Exception) nếu thất bại.
     */
    suspend fun loginWithEmail(email: String, password: String): Result<User>
    suspend fun registerUser(fullName: String, email: String, password: String): Result<Unit>
}