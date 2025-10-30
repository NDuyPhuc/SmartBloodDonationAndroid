// D:\SmartBloodDonationAndroid\core\src\main\java\com\smartblood\core\data\local\AppDatabase.kt

package com.smartblood.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smartblood.core.data.local.dao.DonationHistoryDao
import com.smartblood.core.data.local.dao.UserDao
import com.smartblood.core.data.local.entities.DonationHistoryEntity
import com.smartblood.core.data.local.entities.UserEntity

@Database(
    entities = [UserEntity::class, DonationHistoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun donationHistoryDao(): DonationHistoryDao
}