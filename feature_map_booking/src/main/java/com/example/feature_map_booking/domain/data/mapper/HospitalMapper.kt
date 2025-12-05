package com.example.feature_map_booking.domain.data.mapper

import com.example.feature_map_booking.domain.data.dto.HospitalDto
import com.google.firebase.firestore.GeoPoint
import com.smartblood.core.domain.model.Hospital

fun HospitalDto.toDomain(id: String): Hospital {
    val lat = this.location["lat"] ?: 0.0
    val lng = this.location["lng"] ?: 0.0
    val geoPoint = GeoPoint(lat, lng)

    val availableTypes = this.inventory.filter { it.value > 0 }.map { it.key }

    // Xử lý hiển thị nếu dữ liệu trên Firebase bị thiếu
    val displayPhone = if (this.phone.isNotBlank()) this.phone else "Đang cập nhật SĐT"
    val displayHours = if (this.workingHours.isNotBlank()) this.workingHours else "Giờ hành chính"

    return Hospital(
        id = id,
        name = this.name,
        address = this.address,
        location = geoPoint,
        phone = displayPhone,
        workingHours = displayHours,
        availableBloodTypes = availableTypes
    )
}