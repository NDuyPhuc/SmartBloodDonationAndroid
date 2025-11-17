package com.example.feature_emergency.domain.repository

import com.example.feature_emergency.domain.model.BloodRequest
import com.example.feature_emergency.domain.model.Donor
import kotlin.Result

interface EmergencyRepository {
    // Giữ lại các hàm cũ nếu cần
    suspend fun createEmergencyRequest(request: BloodRequest): Result<Unit>
    suspend fun getMyRequests(): Result<List<BloodRequest>>

    // **HÀM MỚI**: Lấy tất cả các yêu cầu khẩn cấp đang hoạt động
    suspend fun getActiveEmergencyRequests(): Result<List<BloodRequest>>
    suspend fun acceptEmergencyRequest(requestId: String, donorInfo: Donor): Result<Unit>
}