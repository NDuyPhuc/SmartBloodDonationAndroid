// feature_auth/src/main/java/com/smartblood/auth/di/AuthModule.kt

package com.smartblood.auth.di

import com.smartblood.auth.data.repository.AuthRepositoryImpl
import com.smartblood.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}