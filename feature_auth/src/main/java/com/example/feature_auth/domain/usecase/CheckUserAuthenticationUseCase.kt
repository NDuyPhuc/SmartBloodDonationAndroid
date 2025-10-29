// feature_auth/src/main/java/com/smartblood/auth/domain/usecase/CheckUserAuthenticationUseCase.kt

package com.smartblood.auth.domain.usecase

import com.smartblood.auth.domain.repository.AuthRepository
import javax.inject.Inject

class CheckUserAuthenticationUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return repository.isUserAuthenticated()
    }
}
