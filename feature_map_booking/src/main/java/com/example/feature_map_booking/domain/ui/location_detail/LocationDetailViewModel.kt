package com.example.feature_map_booking.domain.ui.location_detail

// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/location_detail/LocationDetailViewModel.kt

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_map_booking.domain.model.Hospital
import com.example.feature_map_booking.domain.usecase.GetHospitalDetailsUseCase
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

            // Giả lập độ trễ mạng
            delay(500)

            // --- BẮT ĐẦU SỬA LỖI ---
            // Tạo lại danh sách bệnh viện giả giống hệt như trong MapViewModel
            val fakeHospitals = listOf(
                Hospital(
                    id = "bv_cho_ray",
                    name = "Bệnh viện Chợ Rẫy",
                    address = "201B Nguyễn Chí Thanh, P. 12, Q. 5",
                    location = GeoPoint(10.7581, 106.6622),
                    phone = "028 3855 4137",
                    workingHours = "7:00 - 16:00 (Thứ 2 - Thứ 6)",
                    availableBloodTypes = listOf("A+", "O-", "B+")
                ),
                Hospital(
                    id = "bv_truyen_mau",
                    name = "BV. Truyền máu Huyết học",
                    address = "118 Hồng Bàng, P. 12, Q. 5",
                    location = GeoPoint(10.7597, 106.6608),
                    phone = "028 3957 1342",
                    workingHours = "8:00 - 17:00 (Cả tuần)",
                    availableBloodTypes = listOf("O+", "A-")
                ),
                Hospital(
                    id = "trung_tam_hien_mau",
                    name = "Trung tâm Hiến máu Nhân đạo",
                    address = "106 Thiên Phước, P. 9, Q. Tân Bình",
                    location = GeoPoint(10.7828, 106.6454),
                    phone = "028 3868 5506",
                    workingHours = "7:30 - 18:00 (Cả tuần)",
                    availableBloodTypes = listOf("A+", "B+", "O+", "AB+")
                )
            )

            // Tìm bệnh viện trong danh sách giả bằng ID đã nhận
            val hospital = fakeHospitals.find { it.id == hospitalId }

            if (hospital != null) {
                _state.update { it.copy(isLoading = false, hospital = hospital) }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Không tìm thấy bệnh viện với ID: $hospitalId"
                    )
                }
            }
            // --- KẾT THÚC SỬA LỖI ---
        }
    }
}