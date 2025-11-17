package com.smartblood.profile.domain.usecase

import com.smartblood.core.domain.model.UserProfile
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CalculateNextDonationDateUseCase @Inject constructor() {

    // Giả sử thời gian chờ giữa 2 lần hiến máu là 84 ngày
    private val WAITING_DAYS = 84

    operator fun invoke(userProfile: UserProfile?): String {
        val lastDonationDate = userProfile?.lastDonationDate ?: return "Bạn có thể hiến máu ngay!"

        val calendar = Calendar.getInstance()
        calendar.time = lastDonationDate
        calendar.add(Calendar.DAY_OF_YEAR, WAITING_DAYS)
        val nextAvailableDate = calendar.time

        val today = Date()

        if (nextAvailableDate.before(today) || nextAvailableDate == today) {
            return "Bạn có thể hiến máu ngay!"
        }

        val diffInMillis = nextAvailableDate.time - today.time
        val daysRemaining = TimeUnit.MILLISECONDS.toDays(diffInMillis)

        return if (daysRemaining > 1) {
            "Bạn có thể hiến máu sau $daysRemaining ngày nữa"
        } else {
            "Bạn có thể hiến máu vào ngày mai"
        }
    }
}