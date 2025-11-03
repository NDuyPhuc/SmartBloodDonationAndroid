package com.smartblood.core.storage.di

// storage/di/StorageModule.kt

import com.smartblood.core.storage.data.repository.StorageRepositoryImpl
import com.smartblood.core.storage.domain.repository.StorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
    @Binds
    @Singleton
    abstract fun bindStorageRepository(impl: StorageRepositoryImpl): StorageRepository
}