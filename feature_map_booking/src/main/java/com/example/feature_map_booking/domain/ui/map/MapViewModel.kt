package com.example.feature_map_booking.domain.ui.map

// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/map/MapViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackasia.android.geometry.LatLng
import com.example.feature_map_booking.domain.usecase.GetNearbyHospitalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getNearbyHospitalsUseCase: GetNearbyHospitalsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()

    fun onEvent(event: MapEvent) {
        when (event) {
            is MapEvent.OnMapReady -> {
                _state.update { it.copy(lastKnownLocation = event.location) }
                // Gọi hàm fetch với kiểu LatLng mới
                fetchHospitals(event.location)
            }
            is MapEvent.OnMarkerClick -> {
                // Để trống, xử lý trong Composable
            }

            MapEvent.OnMapLoaded -> TODO()
        }
    }

    // SỬA THAM SỐ CỦA HÀM NÀY
    private fun fetchHospitals(location: LatLng) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // Truyền trực tiếp latitude và longitude
            getNearbyHospitalsUseCase(location.latitude, location.longitude)
                .onSuccess { hospitals ->
                    _state.update {
                        it.copy(isLoading = false, hospitals = hospitals)
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isLoading = false, error = error.message)
                    }
                }
        }
    }
}