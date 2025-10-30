package com.example.feature_map_booking.domain.ui.map

// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/map/MapViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_map_booking.domain.usecase.GetNearbyHospitalsUseCase
import com.google.android.gms.maps.model.LatLng
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
                fetchHospitals(event.location)
            }
            is MapEvent.OnMarkerClick -> {
                // Navigation will be handled in the Composable
            }
        }
    }

    private fun fetchHospitals(location: LatLng) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
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