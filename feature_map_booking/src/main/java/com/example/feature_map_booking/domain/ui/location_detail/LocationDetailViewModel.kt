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

    // Lấy ID được truyền từ màn hình Map sang
    private val hospitalId: String = checkNotNull(savedStateHandle["hospitalId"])

    init {
        fetchHospitalDetails()
    }

    private fun fetchHospitalDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // --- SỬA ĐỔI QUAN TRỌNG: Gọi UseCase lấy dữ liệu thật ---
            getHospitalDetailsUseCase(hospitalId)
                .onSuccess { hospital ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            hospital = hospital,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Lỗi: Không tìm thấy bệnh viện với ID: $hospitalId"
                        )
                    }
                }
        }
    }
}