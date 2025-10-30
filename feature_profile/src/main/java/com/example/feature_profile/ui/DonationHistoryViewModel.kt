package com.example.feature_profile.ui

// feature_profile/src/main/java/com/smartblood/profile/ui/history/DonationHistoryViewModel.kt


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartblood.profile.domain.model.DonationRecord
import com.smartblood.profile.domain.usecase.GetDonationHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonationHistoryState(
    val isLoading: Boolean = true,
    val history: List<DonationRecord> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DonationHistoryViewModel @Inject constructor(
    private val getDonationHistoryUseCase: GetDonationHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DonationHistoryState())
    val state = _state.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getDonationHistoryUseCase()
                .onSuccess { historyList ->
                    _state.update { it.copy(isLoading = false, history = historyList) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}