// feature_map_booking/src/main/java/com/smartblood/mapbooking/domain/model/Hospital.kt
package com.smartblood.core.domain.model

import com.google.firebase.firestore.GeoPoint

data class Hospital(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val location: GeoPoint? = null,
    val phone: String = "",
    val workingHours: String = "",
    val availableBloodTypes: List<String> = emptyList(),
    val inventory: Map<String, Int> = emptyMap() // Map<Nhóm máu, Số lượng>

)