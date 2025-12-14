package com.example.feature_map_booking.domain.data.repository
// feature_map_booking/src/main/java/com/smartblood/mapbooking/data/repository/MapBookingRepositoryImpl.kt

import com.example.feature_map_booking.domain.data.dto.HospitalDto
import com.example.feature_map_booking.domain.data.mapper.toDomain
import com.example.feature_map_booking.domain.repository.MapBookingRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.smartblood.core.domain.model.Appointment
import com.smartblood.core.domain.model.Hospital
import com.smartblood.core.domain.model.TimeSlot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import kotlin.Result

class MapBookingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : MapBookingRepository {

    // --- CẬP NHẬT: Lấy danh sách bệnh viện thật từ Firestore ---
    override suspend fun getNearbyHospitals(lat: Double, lng: Double, radiusKm: Double): Result<List<Hospital>> {
        return try {
            // Lấy các bệnh viện có status là "Đã duyệt" (theo như trong ảnh console)
            val snapshot = firestore.collection("hospitals")
                .whereEqualTo("status", "Đã duyệt")
                .get()
                .await()

            val hospitals = snapshot.documents.mapNotNull { doc ->
                // Convert document sang DTO rồi sang Domain Model
                doc.toObject(HospitalDto::class.java)?.toDomain(doc.id)
            }

            // TODO: (Nâng cao) Có thể lọc theo khoảng cách radiusKm ở đây nếu muốn
            // Hiện tại trả về toàn bộ danh sách đã duyệt
            Result.success(hospitals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- CẬP NHẬT: Lấy chi tiết bệnh viện thật ---
    override suspend fun getHospitalDetails(hospitalId: String): Result<Hospital> {
        return try {
            val document = firestore.collection("hospitals")
                .document(hospitalId)
                .get()
                .await()

            val hospitalDto = document.toObject(HospitalDto::class.java)

            if (hospitalDto != null) {
                Result.success(hospitalDto.toDomain(document.id))
            } else {
                Result.failure(Exception("Hospital not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ... Giữ nguyên các hàm getAvailableSlots, bookAppointment, getMyAppointments ...
    // (Các hàm này đã viết đúng logic Firestore ở file cũ của bạn, không cần sửa)

    override suspend fun getAvailableSlots(hospitalId: String, date: Date): Result<List<TimeSlot>> {
        // Logic cũ của bạn ok
        return try {
            val calendar = Calendar.getInstance().apply { time = date }
            calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(Calendar.MINUTE, 0); calendar.set(Calendar.SECOND, 0)
            val startOfDay = calendar.time
            calendar.set(Calendar.HOUR_OF_DAY, 23); calendar.set(Calendar.MINUTE, 59); calendar.set(Calendar.SECOND, 59)
            val endOfDay = calendar.time

            val appointmentsSnapshot = firestore.collection("appointments")
                .whereEqualTo("hospitalId", hospitalId)
                .whereGreaterThanOrEqualTo("dateTime", startOfDay)
                .whereLessThanOrEqualTo("dateTime", endOfDay)
                .get().await()

            val bookedHours = appointmentsSnapshot.documents.mapNotNull {
                it.toObject(Appointment::class.java)?.dateTime?.let { bookedDate ->
                    Calendar.getInstance().apply { time = bookedDate }.get(Calendar.HOUR_OF_DAY)
                }
            }

            val allSlots = mutableListOf<TimeSlot>()
            val START_HOUR = 7 // Cập nhật theo giờ làm việc thực tế
            val END_HOUR = 16

            for (hour in START_HOUR until END_HOUR) {
                val timeString = String.format("%02d:00", hour)
                val isBooked = bookedHours.contains(hour)
                allSlots.add(TimeSlot(time = timeString, isAvailable = !isBooked))
            }
            Result.success(allSlots)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun bookAppointment(hospitalId: String, dateTime: Date, volume: String): Result<Unit> {
        val currentUser = auth.currentUser ?: return Result.failure(Exception("User not authenticated."))

        // ... (Giữ nguyên các đoạn code lấy thông tin bệnh viện ở trên) ...
        val hospitalResult = getHospitalDetails(hospitalId)
        if (hospitalResult.isFailure) {
            return Result.failure(hospitalResult.exceptionOrNull() ?: Exception("Unknown error finding hospital."))
        }
        val hospital = hospitalResult.getOrNull() ?: return Result.failure(Exception("Hospital data is null."))

        return try {
            val appointmentId = firestore.collection("appointments").document().id

            val appointment = Appointment(
                id = appointmentId,
                userId = currentUser.uid,
                hospitalId = hospitalId,
                hospitalName = hospital.name,
                hospitalAddress = hospital.address,
                dateTime = dateTime,

                // --- SỬA Ở ĐÂY: Đổi từ "CONFIRMED" thành "PENDING" ---
                status = "PENDING",
                // "PENDING" nghĩa là Chờ duyệt. Admin trên web sẽ bấm duyệt để chuyển thành "CONFIRMED"
                registeredVolume = volume,

            )

            firestore.collection("appointments").document(appointmentId).set(appointment).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMyAppointments(): Flow<Result<List<Appointment>>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(Result.failure(Exception("Người dùng chưa đăng nhập.")))
            close()
            return@callbackFlow
        }

        val query = firestore.collection("appointments")
            .whereEqualTo("userId", userId)
            .orderBy("dateTime", com.google.firebase.firestore.Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // --- SỬA LỖI CRASH: Map dữ liệu thủ công thay vì dùng .toObjects() ---
                val appointments = snapshot.documents.map { doc ->

                    // 1. Xử lý an toàn cho LabResult
                    val labResultMap = doc.get("labResult") as? Map<String, Any>
                    val labResult = if (labResultMap != null) {
                        com.smartblood.core.domain.model.LabResult(
                            documentUrl = labResultMap["documentUrl"] as? String,
                            conclusion = labResultMap["conclusion"] as? String,
                            // Xử lý recordedAt: Chấp nhận Timestamp hoặc Date, bỏ qua nếu là HashMap lỗi
                            recordedAt = when (val rawDate = labResultMap["recordedAt"]) {
                                is com.google.firebase.Timestamp -> rawDate.toDate()
                                is java.util.Date -> rawDate
                                else -> null // Bỏ qua nếu dữ liệu sai định dạng (HashMap)
                            }
                        )
                    } else {
                        null
                    }

                    // 2. Map thủ công các trường của Appointment
                    Appointment(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        hospitalId = doc.getString("hospitalId") ?: "",
                        hospitalName = doc.getString("hospitalName") ?: "",
                        hospitalAddress = doc.getString("hospitalAddress") ?: "",
                        dateTime = doc.getDate("dateTime") ?: java.util.Date(),
                        status = doc.getString("status") ?: "PENDING",

                        // Map thêm các trường mới
                        actualVolume = doc.getString("actualVolume"),
                        registeredVolume = doc.getString("registeredVolume") ?: "350ml",
                        labResult = labResult
                    )
                }
                trySend(Result.success(appointments))
            }
        }
        awaitClose { listener.remove() }
    }
}