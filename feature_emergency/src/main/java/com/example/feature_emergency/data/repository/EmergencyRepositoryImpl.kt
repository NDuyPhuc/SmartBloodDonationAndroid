package com.example.feature_emergency.data.repository


import com.example.feature_emergency.data.dto.BloodRequestDto
import com.example.feature_emergency.data.dto.toDomain
import com.example.feature_emergency.domain.model.EmergencyDonationRecord
import com.smartblood.core.domain.model.BloodRequest
import com.smartblood.core.domain.model.Donor
import com.example.feature_emergency.domain.repository.EmergencyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
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

    override fun getMyPledgedRequests(): Flow<Result<List<BloodRequest>>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(Result.failure(Exception("Người dùng chưa đăng nhập.")))
            close()
            return@callbackFlow
        }

        // Query vào sub-collection "donors"
        val query = firestore.collectionGroup("donors").whereEqualTo("userId", userId)

        val listenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }
            if (snapshot == null || snapshot.isEmpty) {
                trySend(Result.success(emptyList()))
                return@addSnapshotListener
            }

            // 1. Tạo Map lưu trữ: ParentID -> PledgedTime (Thời gian chấp nhận)
            // Lấy pledgedAt từ document donor
            val pledgedMap = snapshot.documents.associate { doc ->
                val parentId = doc.reference.parent.parent?.id ?: ""
                val pledgedAt = doc.getDate("pledgedAt") ?: Date()
                parentId to pledgedAt
            }

            val parentRequestRefs = snapshot.documents.mapNotNull { it.reference.parent.parent }

            if (parentRequestRefs.isEmpty()) {
                trySend(Result.success(emptyList()))
                return@addSnapshotListener
            }

            launch {
                try {
                    val requestSnapshots = parentRequestRefs.map { it.get().await() }

                    val bloodRequests = requestSnapshots.mapNotNull { doc ->
                        val request = doc.toObject<BloodRequestDto>()?.toDomain(id = doc.id)

                        // 2. Gán thời gian chấp nhận vào Model
                        // Lấy thời gian từ Map đã tạo ở bước 1
                        val pledgedTime = pledgedMap[doc.id]

                        request?.copy(userPledgedDate = pledgedTime)
                    }

                    // 3. SẮP XẾP: Mới nhất lên đầu (Dựa vào userPledgedDate)
                    val sortedRequests = bloodRequests.sortedByDescending {
                        it.userPledgedDate ?: it.createdAt
                    }

                    trySend(Result.success(sortedRequests))
                } catch (e: Exception) {
                    trySend(Result.failure(e))
                }
            }
        }
        awaitClose { listenerRegistration.remove() }
    }
    override suspend fun getEmergencyDonationHistory(): Result<List<EmergencyDonationRecord>> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Người dùng chưa đăng nhập."))

            // 1. Dùng Collection Group để lấy tất cả docs trong các sub-collection 'donors'
            // mà có userId trùng với user hiện tại.
            val querySnapshot = firestore.collectionGroup("donors")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            // 2. Xử lý bất đồng bộ để fetch thông tin Hospital từ Parent Document
            val historyList = coroutineScope {
                querySnapshot.documents.map { donorDoc ->
                    async {
                        // Lấy reference đến document cha
                        val parentRef = donorDoc.reference.parent.parent
                        var hospitalName = "Không xác định"

                        if (parentRef != null) {
                            val parentSnap = parentRef.get().await()
                            hospitalName = parentSnap.getString("hospitalName") ?: "Không xác định"
                        }

                        // --- XỬ LÝ MAP LAB RESULT TỪ FIRESTORE ---
                        // Firestore lưu object dưới dạng Map<String, Any>
                        val labResultMap = donorDoc.get("labResult") as? Map<String, Any>
                        val labResult = if (labResultMap != null) {
                            com.smartblood.core.domain.model.LabResult(
                                documentUrl = labResultMap["documentUrl"] as? String,
                                conclusion = labResultMap["conclusion"] as? String,
                                // Kiểm tra kỹ kiểu dữ liệu của recordedAt
                                recordedAt = when (val rawDate = labResultMap["recordedAt"]) {
                                    is com.google.firebase.Timestamp -> rawDate.toDate() // Chuẩn Firestore
                                    is Date -> rawDate // Trường hợp hiếm
                                    else -> null
                                }
                            )
                        } else {
                            null
                        }


                        // Map dữ liệu vào Model
                        EmergencyDonationRecord(
                            id = donorDoc.id,
                            requestId = parentRef?.id ?: "",
                            hospitalName = hospitalName,
                            pledgedAt = donorDoc.getDate("pledgedAt") ?: Date(),
                            status = donorDoc.getString("status") ?: "Pending",
                            userBloodType = donorDoc.getString("userBloodType") ?: "",
                            certificateUrl = donorDoc.getString("certificateUrl"),
                            rating = donorDoc.getLong("rating")?.toInt() ?: 0,
                            review = donorDoc.getString("review"),

                            // Gán kết quả xét nghiệm vừa map được
                            labResult = labResult,
                            rejectionReason = donorDoc.getString("rejectionReason")
                        )
                    }
                }.awaitAll()
            }

            val sortedList = historyList.sortedByDescending { it.pledgedAt }

            // Sắp xếp theo ngày mới nhất
            Result.success(sortedList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
    override fun getActiveEmergencyRequests(): Flow<Result<List<BloodRequest>>> = callbackFlow {
        val query = firestore.collection("blood_requests")
            .whereEqualTo("status", "ĐANG HOẠT ĐỘNG")
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val requests = snapshot.documents.mapNotNull { doc ->
                    doc.toObject<BloodRequestDto>()?.toDomain(id = doc.id)
                }
                trySend(Result.success(requests))
            }
        }
        // Hủy listener khi Flow bị hủy
        awaitClose { listener.remove() }
    }



}