package com.example.feature_map_booking.domain.ui.map

// feature_map_booking/src/main/java/com/smartblood/mapbooking/ui/map/MapViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import com.smartblood.core.domain.model.Hospital
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
                        address = "201B Nguyễn Chí Thanh, Phường 12, Quận 5, TP.HCM",
                        location = GeoPoint(10.7581, 106.6622),
                        phone = "028 3855 4137",
                        workingHours = "Tiếp nhận máu 24/7",
                        availableBloodTypes = listOf("Tất cả các nhóm")
                    ),
                    Hospital(
                        id = "bv_truyen_mau_huyet_hoc",
                        name = "Bệnh viện Truyền máu Huyết học",
                        address = "118 Hồng Bàng, Phường 12, Quận 5, TP.HCM",
                        location = GeoPoint(10.7597, 106.6608),
                        phone = "028 3957 1342",
                        workingHours = "7:00 - 16:30 (Thứ 2 - CN)",
                        availableBloodTypes = listOf("Tất cả các nhóm")
                    ),
                    Hospital(
                        id = "trung_tam_hien_mau_nhan_dao",
                        name = "Trung tâm Hiến máu Nhân đạo TP.HCM",
                        address = "106 Thiên Phước, Phường 9, Quận Tân Bình, TP.HCM",
                        location = GeoPoint(10.7828, 106.6454),
                        phone = "028 3868 5506",
                        workingHours = "7:00 - 16:00 (Thứ 2 - Thứ 6)",
                        availableBloodTypes = listOf("Tất cả các nhóm")
                    ),
                    Hospital(
                        id = "bv_nhan_dan_gia_dinh",
                        name = "Bệnh viện Nhân dân Gia Định",
                        address = "1 Nơ Trang Long, Phường 7, Quận Bình Thạnh, TP.HCM",
                        location = GeoPoint(10.8003, 106.6973),
                        phone = "028 3841 2692",
                        workingHours = "Tiếp nhận máu giờ hành chính",
                        availableBloodTypes = listOf("A", "B", "O")
                    ),
                    Hospital(
                        id = "bv_hung_vuong",
                        name = "Bệnh viện Hùng Vương",
                        address = "128 Hồng Bàng, Phường 12, Quận 5, TP.HCM",
                        location = GeoPoint(10.7588, 106.6573),
                        phone = "028 3855 8532",
                        workingHours = "Tiếp nhận máu giờ hành chính",
                        availableBloodTypes = listOf("A", "O")
                    ),
                    Hospital(
                        id = "bv_nhi_dong_1",
                        name = "Bệnh viện Nhi đồng 1",
                        address = "341 Sư Vạn Hạnh, Phường 10, Quận 10, TP.HCM",
                        location = GeoPoint(10.7711, 106.6699),
                        phone = "028 3927 1119",
                        workingHours = "Tiếp nhận máu 24/7 (ưu tiên khẩn cấp)",
                        availableBloodTypes = listOf("Tất cả các nhóm")
                    ),
                    Hospital(
                        id = "bv_thong_nhat",
                        name = "Bệnh viện Thống Nhất",
                        address = "1 Lý Thường Kiệt, Phường 7, Quận Tân Bình, TP.HCM",
                        location = GeoPoint(10.7853, 106.6502),
                        phone = "028 3864 2142",
                        workingHours = "Tiếp nhận máu giờ hành chính",
                        availableBloodTypes = listOf("B", "O")
                    ),
                    Hospital(
                        id = "diem_hien_mau_quan_5",
                        name = "Điểm hiến máu Chữ Thập Đỏ Quận 5",
                        address = "201 Nguyễn Trãi, Phường 3, Quận 5, TP.HCM",
                        location = GeoPoint(10.7610, 106.6804),
                        phone = "(028) 38 382 646",
                        workingHours = "7:30 - 16:00 (Thứ 2 - Thứ 7)",
                        availableBloodTypes = listOf("Tất cả các nhóm")
                    ),
                    Hospital(
                        id = "bv_tu_du",
                        name = "Bệnh viện Từ Dũ",
                        address = "284 Cống Quỳnh, Phường Phạm Ngũ Lão, Quận 1, TP.HCM",
                        location = GeoPoint(10.7686, 106.6888),
                        phone = "028 5404 2829",
                        workingHours = "Tiếp nhận máu 24/7",
                        availableBloodTypes = listOf("O", "A")
                    ),
                    Hospital(
                        id = "bv_ung_buou",
                        name = "Bệnh viện Ung Bướu (Cơ sở 2)",
                        address = "Đường 400, Long Thạnh Mỹ, Thành phố Thủ Đức, TP.HCM",
                        location = GeoPoint(10.8497, 106.8248),
                        phone = "028 3844 5217",
                        workingHours = "Tiếp nhận máu giờ hành chính",
                        availableBloodTypes = listOf("Tất cả các nhóm")
                    )
                )
                _state.update { it.copy(isLoading = false, hospitals = fakeHospitals) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}