// D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\data\repository\ProfileRepositoryImpl.kt
package com.smartblood.profile.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.smartblood.core.data.local.dao.DonationHistoryDao
import com.smartblood.core.data.local.dao.UserDao
import com.smartblood.profile.data.mapper.*
import com.smartblood.profile.domain.model.DonationRecord
import com.smartblood.profile.domain.model.UserProfile
import com.smartblood.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.Result

class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userDao: UserDao,
    private val donationHistoryDao: DonationHistoryDao // Inject DAO mới
) : ProfileRepository {

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    // --- USER PROFILE (Đã hoàn thành) ---
    override fun getUserProfile(): Flow<UserProfile?> {
        val userId = currentUserId ?: return kotlinx.coroutines.flow.flowOf(null)
        return userDao.getUser(userId).map { it?.toUserProfile() }
    }

    override suspend fun refreshUserProfile() {
        try {
            val userId = currentUserId ?: throw Exception("User not logged in")
            val document = firestore.collection("users").document(userId).get().await()
            val userProfile = document.toObject(UserProfile::class.java)
            userProfile?.let { userDao.insertUser(it.toUserEntity()) }
        } catch (e: Exception) {
            e.printStackTrace()
            // Có thể re-throw nếu muốn ViewModel xử lý lỗi
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            val userId = currentUserId ?: return Result.failure(Exception("User not logged in"))
            firestore.collection("users").document(userId).set(userProfile).await()
            userDao.insertUser(userProfile.toUserEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- DONATION HISTORY (Bổ sung) ---
    override fun getDonationHistory(): Flow<List<DonationRecord>> {
        val userId = currentUserId ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return donationHistoryDao.getDonationHistory(userId).map { entityList ->
            entityList.map { it.toDonationRecord() }
        }
    }

    override suspend fun refreshDonationHistory() {
        try {
            val userId = currentUserId ?: throw Exception("User not logged in")
            val querySnapshot = firestore.collection("users").document(userId)
                .collection("donation_history")
                .orderBy("date", Query.Direction.DESCENDING)
                .get().await()

            val historyRecords = querySnapshot.toObjects(DonationRecord::class.java)
            val historyEntities = historyRecords.map { it.toDonationHistoryEntity(userId) }

            donationHistoryDao.insertAll(historyEntities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}