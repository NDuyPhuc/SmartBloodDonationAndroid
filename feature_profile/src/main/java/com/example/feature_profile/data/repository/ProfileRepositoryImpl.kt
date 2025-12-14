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
                // .whereEqualTo("status", "COMPLETED") // Tạm bỏ để test xem có hiện lịch sử không
                .orderBy("dateTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val history = querySnapshot.documents.map { doc ->
                // --- XỬ LÝ AN TOÀN CHO LAB RESULT ---
                // Lấy field labResult dạng Map để tránh lỗi Deserialization
                val labResultMap = doc.get("labResult") as? Map<String, Any>
                val labResult = if (labResultMap != null) {
                    com.smartblood.core.domain.model.LabResult(
                        documentUrl = labResultMap["documentUrl"] as? String,
                        conclusion = labResultMap["conclusion"] as? String,
                        // Xử lý an toàn: Nếu là Timestamp thì convert, nếu null thì thôi
                        recordedAt = (labResultMap["recordedAt"] as? com.google.firebase.Timestamp)?.toDate()
                    )
                } else {
                    null
                }

                // Map dữ liệu thủ công
                DonationRecord(
                    id = doc.id,
                    hospitalName = doc.getString("hospitalName") ?: "Bệnh viện",
                    hospitalAddress = doc.getString("hospitalAddress") ?: "",
                    // Xử lý ngày tháng an toàn
                    date = doc.getDate("dateTime") ?: Date(),
                    status = doc.getString("status") ?: "COMPLETED",
                    certificateUrl = doc.getString("certificateUrl"),
                    actualVolume = doc.getString("actualVolume"), // Lấy dung tích thực tế
                    labResult = labResult // Gán kết quả xét nghiệm
                )
            }
            Result.success(history)
        } catch (e: Exception) {
            e.printStackTrace()
            // Log lỗi ra để debug nếu cần
            Result.failure(e)
        }
    }
    override fun signOut() {
        auth.signOut()
    }
}