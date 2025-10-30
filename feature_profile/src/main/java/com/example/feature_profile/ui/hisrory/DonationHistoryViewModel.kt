//D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\example\feature_profile\ui\hisrory\DonationHistoryViewModel.kt
package com.smartblood.profile.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartblood.profile.domain.model.DonationRecord
import com.smartblood.profile.domain.usecase.GetDonationHistoryUseCase
import com.smartblood.profile.domain.usecase.RefreshDonationHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonationHistoryState(
    val isLoading: Boolean = false,
    val history: List<DonationRecord> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DonationHistoryViewModel @Inject constructor(
    private val getDonationHistoryUseCase: GetDonationHistoryUseCase,
    private val refreshDonationHistoryUseCase: RefreshDonationHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DonationHistoryState())
    val state: StateFlow<DonationHistoryState> = _state.asStateFlow()

    init {
        observeLocalHistory()
        refreshHistoryData()
    }

    private fun observeLocalHistory() {
        getDonationHistoryUseCase()
            .onEach { historyList ->
                _state.update { it.copy(history = historyList) }
            }
            .launchIn(viewModelScope)
    }

    fun refreshHistoryData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                refreshDonationHistoryUseCase()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}