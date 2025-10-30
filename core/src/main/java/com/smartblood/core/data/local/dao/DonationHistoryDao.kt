// D:\SmartBloodDonationAndroid\core\src\main\java\com\smartblood\core\data\local\dao\DonationHistoryDao.kt
package com.smartblood.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smartblood.core.data.local.entities.DonationHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationHistoryDao {
    @Query("SELECT * FROM donation_history WHERE userId = :userId ORDER BY date DESC")
    fun getDonationHistory(userId: String): Flow<List<DonationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(history: List<DonationHistoryEntity>)

    @Query("DELETE FROM donation_history WHERE userId = :userId")
    suspend fun clearHistory(userId: String)
}