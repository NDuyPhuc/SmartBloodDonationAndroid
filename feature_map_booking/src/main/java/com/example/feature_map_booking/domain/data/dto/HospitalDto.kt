package com.example.feature_map_booking.domain.data.dto

import com.google.firebase.firestore.PropertyName

data class HospitalDto(
    val name: String = "",
    val address: String = "",
    val status: String = "",
    val licenseUrl: String = "",

    // Trong ảnh Firestore, location là một Map chứa lat/lng
    val location: Map<String, Double> = emptyMap(),

    // Inventory chứa số lượng máu (A+: 40, B+: 100...)
    val inventory: Map<String, Int> = emptyMap(),

    // Các trường này có thể chưa có trên Web, nên để default
    val phone: String = "",
    val workingHours: String = ""
)