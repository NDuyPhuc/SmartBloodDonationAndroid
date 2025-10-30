package com.example.feature_map_booking.domain.data.repository
// feature_map_booking/src/main/java/com/smartblood/mapbooking/data/repository/MapBookingRepositoryImpl.kt


import com.example.feature_map_booking.domain.model.Appointment
import com.example.feature_map_booking.domain.model.Hospital
import com.example.feature_map_booking.domain.model.TimeSlot
import com.example.feature_map_booking.domain.repository.MapBookingRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.Result

class MapBookingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : MapBookingRepository {

    // Lưu ý: Việc tìm kiếm theo vị trí cần cấu hình Geo-query phức tạp hơn
    // hoặc sử dụng thư viện như GeoFirestore.
    // Để đơn giản, ở đây chúng ta sẽ lấy tất cả và lọc ở client-side (không hiệu quả cho quy mô lớn).
    override suspend fun getNearbyHospitals(lat: Double, lng: Double, radiusKm: Double): Result<List<Hospital>> {
        return try {
            val result = firestore.collection("hospitals").get().await()
            val hospitals = result.documents.mapNotNull { it.toObject<Hospital>()?.copy(id = it.id) }
            // TODO: Triển khai logic lọc theo khoảng cách nếu cần
            Result.success(hospitals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHospitalDetails(hospitalId: String): Result<Hospital> {
        return try {
            val document = firestore.collection("hospitals").document(hospitalId).get().await()
            val hospital = document.toObject<Hospital>()?.copy(id = document.id)
            if (hospital != null) {
                Result.success(hospital)
            } else {
                Result.failure(Exception("Hospital not found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Giả lập logic lấy khung giờ. Thực tế sẽ truy vấn collection appointments.
    override suspend fun getAvailableSlots(hospitalId: String, date: Date): Result<List<TimeSlot>> {
        return try {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            val startOfDay = calendar.time
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            val endOfDay = calendar.time

            // Lấy các lịch hẹn đã có trong ngày đó
            val appointmentsSnapshot = firestore.collection("appointments")
                .whereEqualTo("hospitalId", hospitalId)
                .whereGreaterThanOrEqualTo("dateTime", startOfDay)
                .whereLessThanOrEqualTo("dateTime", endOfDay)
                .get()
                .await()

            val bookedTimes = appointmentsSnapshot.documents.mapNotNull {
                val appointment = it.toObject<Appointment>()
                appointment?.dateTime
            }

            // Tạo danh sách các khung giờ mặc định (ví dụ từ 8h -> 17h)
            val allSlots = mutableListOf<TimeSlot>()
            val slotCalendar = Calendar.getInstance().apply { time = date }
            for (hour in 8..16) { // 8 AM to 4 PM
                slotCalendar.set(Calendar.HOUR_OF_DAY, hour)
                slotCalendar.set(Calendar.MINUTE, 0)
                val timeString = String.format("%02d:00", hour)

                val isBooked = bookedTimes.any { bookedDate ->
                    val bookedCalendar = Calendar.getInstance().apply { time = bookedDate }
                    bookedCalendar.get(Calendar.HOUR_OF_DAY) == hour
                }

                allSlots.add(TimeSlot(time = timeString, isAvailable = !isBooked))
            }

            Result.success(allSlots)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun bookAppointment(hospitalId: String, dateTime: Date): Result<Unit> {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            return Result.failure(Exception("User not authenticated."))
        }
        return try {
            // Lấy thông tin bệnh viện để lưu vào lịch hẹn
            val hospital = getHospitalDetails(hospitalId).getOrThrow()

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
            // TODO: Triển khai transaction để đảm bảo không có 2 người đặt cùng lúc
            firestore.collection("appointments").document(appointmentId).set(appointment).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}