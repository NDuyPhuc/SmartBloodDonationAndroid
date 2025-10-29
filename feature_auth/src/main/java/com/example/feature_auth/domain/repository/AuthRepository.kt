// feature_auth/src/main/java/com/smartblood/auth/domain/repository/AuthRepository.kt

package com.smartblood.auth.domain.repository

interface AuthRepository {
    /**
     * Kiểm tra xem có người dùng nào đang đăng nhập hay không.
     * @return true nếu đã đăng nhập, ngược lại false.
     */
    fun isUserAuthenticated(): Boolean
}