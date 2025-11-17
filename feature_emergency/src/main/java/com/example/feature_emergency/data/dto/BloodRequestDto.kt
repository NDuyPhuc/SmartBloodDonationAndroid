package com.example.feature_emergency.data.dto


import com.smartblood.core.domain.model.BloodRequest
import com.google.firebase.Timestamp

// Lớp DTO này khớp với cấu trúc trên Firestore
data class BloodRequestDto(
    val bloodType: String = "",
    val hospitalId: String = "",
    val hospitalName: String = "",
    val priority: String = "Trung bình",
    val quantity: Int = 0,
    val status: String = "ĐANG HOẠT ĐỘNG",
    val createdAt: Timestamp = Timestamp.now()
)

// Hàm chuyển đổi từ DTO sang Domain Model
fun BloodRequestDto.toDomain(id: String): BloodRequest {
    return BloodRequest(
        id = id,
        bloodType = this.bloodType,
        hospitalId = this.hospitalId,
        hospitalName = this.hospitalName,
        priority = this.priority,
        quantity = this.quantity,
        status = this.status,
        createdAt = this.createdAt.toDate() // Việc chuyển đổi diễn ra an toàn ở đây
    )
}