package com.example.feature_emergency.domain.repository

import com.example.feature_emergency.domain.model.EmergencyDonationRecord
import com.smartblood.core.domain.model.BloodRequest
import com.smartblood.core.domain.model.Donor
import kotlinx.coroutines.flow.Flow
import kotlin.Result

interface EmergencyRepository {
    // Giữ lại các hàm cũ nếu cần
    suspend fun createEmergencyRequest(request: BloodRequest): Result<Unit>
    suspend fun getMyRequests(): Result<List<BloodRequest>>

    // **HÀM MỚI**: Lấy tất cả các yêu cầu khẩn cấp đang hoạt động
    fun getActiveEmergencyRequests(): Flow<Result<List<BloodRequest>>>
    suspend fun acceptEmergencyRequest(requestId: String, donorInfo: Donor): Result<Unit>
    fun getMyPledgedRequests(): Flow<Result<List<BloodRequest>>>

    suspend fun getEmergencyDonationHistory(): Result<List<EmergencyDonationRecord>>
}