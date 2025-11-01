package com.example.feature_map_booking.domain.data.repository
// feature_map_booking/src/main/java/com/smartblood/mapbooking/data/repository/MapBookingRepositoryImpl.kt

import com.example.feature_map_booking.domain.model.Appointment
import com.example.feature_map_booking.domain.model.Hospital
import com.example.feature_map_booking.domain.model.TimeSlot
import com.example.feature_map_booking.domain.repository.MapBookingRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import kotlin.Result

class MapBookingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : MapBookingRepository {

    override suspend fun getNearbyHospitals(lat: Double, lng: Double, radiusKm: Double): Result<List<Hospital>> {
        // Hiện tại đang dùng dữ liệu giả từ FakeHospitalDataSource
        return Result.success(FakeHospitalDataSource.hospitals)
    }

    override suspend fun getHospitalDetails(hospitalId: String): Result<Hospital> {
        // Hiện tại đang dùng dữ liệu giả từ FakeHospitalDataSource
        val hospital = FakeHospitalDataSource.hospitals.find { it.id == hospitalId }
        return if (hospital != null) {
            Result.success(hospital)
        } else {
            Result.failure(Exception("Hospital not found."))
        }
    }

    override suspend fun getAvailableSlots(hospitalId: String, date: Date): Result<List<TimeSlot>> {
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
            val START_HOUR = 8
            val END_HOUR = 17

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

    // --- PHIÊN BẢN ĐÃ SỬA LỖI HOÀN CHỈNH ---
    override suspend fun bookAppointment(hospitalId: String, dateTime: Date): Result<Unit> {
        val currentUser = auth.currentUser ?: return Result.failure(Exception("User not authenticated."))

        // Bước 1: Gọi hàm lấy chi tiết bệnh viện
        val hospitalResult = getHospitalDetails(hospitalId)

        // Bước 2: Kiểm tra kết quả của hospitalResult
        if (hospitalResult.isFailure) {
            // Nếu không tìm thấy bệnh viện, trả về lỗi ngay lập tức
            return Result.failure(hospitalResult.exceptionOrNull() ?: Exception("Unknown error finding hospital."))
        }

        // Nếu tới được đây, nghĩa là hospitalResult.isSuccess là true
        val hospital = hospitalResult.getOrNull() ?: return Result.failure(Exception("Hospital data is null."))

        // Bước 3: Tiếp tục logic tạo và lưu lịch hẹn
        return try {
            val appointmentId = firestore.collection("appointments").document().id
            val appointment = Appointment(
                id = appointmentId,
                userId = currentUser.uid,
                hospitalId = hospitalId,
                hospitalName = hospital.name,
                hospitalAddress = hospital.address,
                dateTime = dateTime,
                status = "CONFIRMED"
            )
            firestore.collection("appointments").document(appointmentId).set(appointment).await()
            Result.success(Unit) // Trả về thành công
        } catch (e: Exception) {
            Result.failure(e) // Trả về thất bại nếu có lỗi khi lưu vào Firestore
        }
    }
}