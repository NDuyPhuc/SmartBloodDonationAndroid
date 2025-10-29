// feature_auth/src/main/java/com/smartblood/auth/data/repository/AuthRepositoryImpl.kt

package com.smartblood.auth.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.smartblood.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth // Hilt sẽ tự động cung cấp FirebaseAuth từ :core
) : AuthRepository {

    override fun isUserAuthenticated(): Boolean {
        // Kiểm tra xem currentUser có null hay không.
        // Đây là cách đơn giản và hiệu quả nhất để kiểm tra trạng thái đăng nhập.
        return auth.currentUser != null
    }
}