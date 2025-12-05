package com.example.feature_profile.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.smartblood.profile.domain.model.DonationRecord
import com.smartblood.core.domain.model.UserProfile
import com.example.feature_profile.domain.repository.ProfileRepository
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import kotlin.Result

class ProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ProfileRepository {

    override suspend fun getUserProfile(): Result<UserProfile> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val document = firestore.collection("users").document(userId).get().await()
            val userProfile = document.toObject(UserProfile::class.java)
                ?: return Result.failure(Exception("User profile not found"))
            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            firestore.collection("users").document(userId).set(userProfile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDonationHistory(): Result<List<DonationRecord>> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            // Query vào collection chung "appointments"
            val querySnapshot = firestore.collection("appointments")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "COMPLETED") // Chỉ lấy lịch đã hoàn thành/đã cấp
                .orderBy("dateTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val history = querySnapshot.documents.map { doc ->
                // Map dữ liệu từ Firestore sang DonationRecord
                DonationRecord(
                    id = doc.id,
                    hospitalName = doc.getString("hospitalName") ?: "Bệnh viện",
                    hospitalAddress = doc.getString("hospitalAddress") ?: "",
                    date = doc.getDate("dateTime") ?: Date(),
                    status = doc.getString("status") ?: "COMPLETED",
                    certificateUrl = doc.getString("certificateUrl") // Lấy link chứng nhận
                )
            }

            Result.success(history)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}