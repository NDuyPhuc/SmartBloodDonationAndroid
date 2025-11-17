package com.example.feature_emergency.data.repository


import com.example.feature_emergency.data.dto.BloodRequestDto
import com.example.feature_emergency.data.dto.toDomain
import com.example.feature_emergency.domain.model.BloodRequest
import com.example.feature_emergency.domain.model.Donor
import com.example.feature_emergency.domain.repository.EmergencyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.Result

/**
 * Lớp này triển khai các phương thức từ EmergencyRepository.
 * Nó chịu trách nhiệm lấy và gửi dữ liệu liên quan đến yêu cầu khẩn cấp từ Firebase Firestore.
 * @param firestore Instance của FirebaseFirestore để tương tác với database.
 * @param auth Instance của FirebaseAuth để lấy thông tin người dùng hiện tại.
 */
class EmergencyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : EmergencyRepository {

    override suspend fun acceptEmergencyRequest(requestId: String, donorInfo: Donor): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Người dùng chưa đăng nhập."))

            // Tạo một document mới trong sub-collection với ID là userId
            firestore.collection("blood_requests")
                .document(requestId)
                .collection("donors")
                .document(userId)
                .set(donorInfo)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    /**
     * Triển khai phương thức tạo một yêu cầu khẩn cấp mới (logic cũ của bạn).
     */
    override suspend fun createEmergencyRequest(request: BloodRequest): Result<Unit> {
        // Đây là nơi bạn sẽ triển khai logic để tạo request mới, nếu cần.
        // Ví dụ:
        return try {
            firestore.collection("blood_requests").add(request).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Triển khai phương thức lấy các yêu cầu do người dùng hiện tại tạo (logic cũ của bạn).
     */
    override suspend fun getMyRequests(): Result<List<BloodRequest>> {
        // Đây là nơi bạn sẽ triển khai logic để lấy các request của người dùng, nếu cần.
        // Ví dụ:
        return try {
            val currentUser = auth.currentUser ?: return Result.failure(Exception("Người dùng chưa đăng nhập"))
            val snapshot = firestore.collection("blood_requests")
                .whereEqualTo("creatorId", currentUser.uid) // Giả sử có trường creatorId
                .get()
                .await()
            val requests = snapshot.toObjects(BloodRequest::class.java)
            Result.success(requests)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- TRIỂN KHAI PHƯƠNG THỨC MỚI ---

    /**
     * Lấy tất cả các yêu cầu máu đang hoạt động từ Firestore.
     * Các yêu cầu được sắp xếp theo ngày tạo mới nhất.
     */
    override suspend fun getActiveEmergencyRequests(): Result<List<BloodRequest>> {
        return try {
            val snapshot = firestore.collection("blood_requests")
                .whereEqualTo("status", "ĐANG HOẠT ĐỘNG")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            // Sử dụng DTO để lấy dữ liệu, sau đó chuyển sang Domain model
            val requests = snapshot.documents.mapNotNull { doc ->
                doc.toObject(BloodRequestDto::class.java)?.toDomain(id = doc.id)
            }
            Result.success(requests)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}