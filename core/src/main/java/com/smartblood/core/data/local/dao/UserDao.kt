// D:\SmartBloodDonationAndroid\core\src\main\java\com\smartblood\core\data\local\dao\UserDao.kt
// (Tạo package 'dao')
package com.smartblood.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smartblood.core.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user_profile WHERE uid = :userId")
    fun getUser(userId: String): Flow<UserEntity?> // Trả về Flow để UI tự cập nhật

    @Query("DELETE FROM user_profile")
    suspend fun clearUser()
}