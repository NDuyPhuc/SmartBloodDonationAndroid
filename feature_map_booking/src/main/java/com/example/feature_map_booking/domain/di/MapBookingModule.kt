package com.example.feature_map_booking.domain.di
// feature_map_booking/src/main/java/com/smartblood/mapbooking/di/MapBookingModule.kt

import com.example.feature_map_booking.domain.data.repository.MapBookingRepositoryImpl
import com.example.feature_map_booking.domain.repository.MapBookingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapBookingModule {

    @Binds
    @Singleton
    abstract fun bindMapBookingRepository(
        mapBookingRepositoryImpl: MapBookingRepositoryImpl
    ): MapBookingRepository
}