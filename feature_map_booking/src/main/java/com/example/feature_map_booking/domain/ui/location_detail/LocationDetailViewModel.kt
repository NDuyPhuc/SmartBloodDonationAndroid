package com.example.feature_map_booking.domain.ui.location_detail

// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/location_detail/LocationDetailViewModel.kt

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_map_booking.domain.usecase.GetHospitalDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationDetailViewModel @Inject constructor(
    private val getHospitalDetailsUseCase: GetHospitalDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(LocationDetailState())
    val state = _state.asStateFlow()

    private val hospitalId: String = checkNotNull(savedStateHandle["hospitalId"])

    init {
        fetchHospitalDetails()
    }

    private fun fetchHospitalDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getHospitalDetailsUseCase(hospitalId)
                .onSuccess { hospital ->
                    _state.update { it.copy(isLoading = false, hospital = hospital) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}