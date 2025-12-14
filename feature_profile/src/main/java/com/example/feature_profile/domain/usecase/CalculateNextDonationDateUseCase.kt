package com.smartblood.profile.domain.usecase

import com.smartblood.core.domain.model.UserProfile
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class CalculateNextDonationDateUseCase @Inject constructor() {

    private val DAYS_WHOLE_BLOOD = 84 // 12 tuần
    private val DAYS_PLATELETS_PLASMA = 14 // 2 tuần

    operator fun invoke(userProfile: UserProfile?): String {
        val dateString = userProfile?.lastDonationDate
        if (dateString.isNullOrBlank()) {
            return "Bạn có thể hiến máu ngay!"
        }

        // 1. Chuyển đổi String "14/12/2025" sang Date
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val lastDonationDate: Date = try {
            dateFormat.parse(dateString) ?: return "Dữ liệu ngày không hợp lệ"
        } catch (e: Exception) {
            return "Bạn có thể hiến máu ngay!"
        }

        // 2. Kiểm tra loại hiến (Map từ Tiếng Việt trên Firestore sang logic)
        // Trên Firestore ghi: "Máu toàn phần"
        val lastType = userProfile.lastDonationType?.lowercase(Locale.getDefault()) ?: ""

        val waitingDays = when {
            lastType.contains("tiểu cầu") || lastType.contains("huyết tương") || lastType == "platelets" -> DAYS_PLATELETS_PLASMA
            else -> DAYS_WHOLE_BLOOD // Mặc định (Máu toàn phần)
        }

        // 3. Tính toán ngày
        val calendar = Calendar.getInstance()
        calendar.time = lastDonationDate
        calendar.add(Calendar.DAY_OF_YEAR, waitingDays)

        val nextAvailableDate = calendar.time
        val today = Date()

        // Reset giờ phút giây về 0 để so sánh ngày chính xác
        val todayCal = Calendar.getInstance().apply {
            time = today
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val nextCal = Calendar.getInstance().apply {
            time = nextAvailableDate
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }

        if (nextCal.before(todayCal) || nextCal == todayCal) {
            return "Bạn có thể hiến máu ngay!"
        }

        val diffInMillis = nextCal.timeInMillis - todayCal.timeInMillis
        val daysRemaining = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        val typeText = when {
            lastType.contains("tiểu cầu") -> "tiểu cầu"
            lastType.contains("huyết tương") -> "huyết tương"
            else -> "máu toàn phần"
        }

        return "Bạn có thể hiến $typeText sau $daysRemaining ngày nữa (${dateFormat.format(nextAvailableDate)})"
    }
}