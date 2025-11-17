package com.example.feature_emergency.di

import com.example.feature_emergency.data.repository.EmergencyRepositoryImpl
import com.example.feature_emergency.domain.repository.EmergencyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EmergencyModule {

    @Binds
    @Singleton
    abstract fun bindEmergencyRepository(
        emergencyRepositoryImpl: EmergencyRepositoryImpl
    ): EmergencyRepository

}