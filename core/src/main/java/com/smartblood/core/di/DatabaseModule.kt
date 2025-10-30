// D:\SmartBloodDonationAndroid\core\src\main\java\com\smartblood\core\di\DatabaseModule.kt

package com.smartblood.core.di

import android.content.Context
import androidx.room.Room
import com.smartblood.core.data.local.AppDatabase
import com.smartblood.core.data.local.dao.DonationHistoryDao
import com.smartblood.core.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smartblood_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideDonationHistoryDao(appDatabase: AppDatabase): DonationHistoryDao {
        return appDatabase.donationHistoryDao()
    }
}