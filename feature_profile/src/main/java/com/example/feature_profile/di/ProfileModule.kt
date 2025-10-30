//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\di\ProfileModule.kt
package com.smartblood.profile.di

import com.smartblood.profile.data.repository.ProfileRepositoryImpl
import com.smartblood.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository
}