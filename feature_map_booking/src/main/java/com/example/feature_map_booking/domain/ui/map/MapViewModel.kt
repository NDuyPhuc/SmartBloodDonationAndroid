package com.example.feature_map_booking.domain.ui.map

// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/map/MapViewModel.kt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_map_booking.domain.usecase.GetNearbyHospitalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    // Inject UseCase thay vì Repository trực tiếp (Clean Architecture)
    private val getNearbyHospitalsUseCase: GetNearbyHospitalsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state = _state.asStateFlow()

    fun onEvent(event: MapEvent) {
        when (event) {
            MapEvent.OnMapLoaded -> {
                fetchHospitals()
            }
            // Các event khác giữ nguyên hoặc TODO
            is MapEvent.OnMapReady -> {}
            is MapEvent.OnMarkerClick -> {}
        }
    }

    private fun fetchHospitals() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Gọi UseCase để lấy dữ liệu thật
            // Lat/Lng 0.0, 0.0 là demo, thực tế sẽ lấy từ location của user
            val result = getNearbyHospitalsUseCase(10.7, 106.6, 10.0)

            result.onSuccess { hospitals ->
                Log.d("MapViewModel", "Lấy thành công: ${hospitals.size} bệnh viện")
                _state.update {
                    it.copy(
                        isLoading = false,
                        hospitals = hospitals,
                        error = null
                    )
                }
            }.onFailure { error ->
                Log.e("MapViewModel", "Lỗi lấy bệnh viện: ${error.message}")
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            }
        }
    }
}