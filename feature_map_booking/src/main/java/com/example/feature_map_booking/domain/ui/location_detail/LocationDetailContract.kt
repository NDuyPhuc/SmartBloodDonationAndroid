package com.example.feature_map_booking.domain.ui.location_detail

// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/location_detail/LocationDetailContract.kt

import com.smartblood.core.domain.model.Hospital

data class LocationDetailState(
    val isLoading: Boolean = true,
    val hospital: Hospital? = null,
    val error: String? = null
)