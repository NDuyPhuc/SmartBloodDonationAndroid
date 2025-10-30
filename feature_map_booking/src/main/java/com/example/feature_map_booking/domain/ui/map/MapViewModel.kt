package com.example.feature_map_booking.domain.ui.map

// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/map/MapViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import com.example.feature_map_booking.domain.model.Hospital
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    // private val getNearbyHospitalsUseCase: GetNearbyHospitalsUseCase // Sẽ dùng sau
) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()

    fun onEvent(event: MapEvent) {
        when (event) {
            MapEvent.OnMapLoaded -> {
                fetchHospitals()
            }

            is MapEvent.OnMapReady -> TODO()
            is MapEvent.OnMarkerClick -> TODO()
        }
    }

    private fun fetchHospitals() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Giả lập độ trễ mạng
                delay(2000)

                // Tạo danh sách bệnh viện giả
                val fakeHospitals = listOf(
                    Hospital(
                        id = "bv_cho_ray",
                        name = "Bệnh viện Chợ Rẫy",
                        address = "201B Nguyễn Chí Thanh, P. 12, Q. 5",
                        location = GeoPoint(10.7581, 106.6622)
                    ),
                    Hospital(
                        id = "bv_truyen_mau",
                        name = "BV. Truyền máu Huyết học",
                        address = "118 Hồng Bàng, P. 12, Q. 5",
                        location = GeoPoint(10.7597, 106.6608)
                    ),
                    Hospital(
                        id = "trung_tam_hien_mau",
                        name = "Trung tâm Hiến máu Nhân đạo",
                        address = "106 Thiên Phước, P. 9, Q. Tân Bình",
                        location = GeoPoint(10.7828, 106.6454)
                    )
                )
                _state.update { it.copy(isLoading = false, hospitals = fakeHospitals) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}