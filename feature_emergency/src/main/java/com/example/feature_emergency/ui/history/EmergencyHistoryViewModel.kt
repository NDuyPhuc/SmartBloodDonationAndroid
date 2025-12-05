package com.example.feature_emergency.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature_emergency.domain.model.EmergencyDonationRecord
import com.example.feature_emergency.domain.usecase.GetEmergencyHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmergencyHistoryState(
    val isLoading: Boolean = true,
    val history: List<EmergencyDonationRecord> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class EmergencyHistoryViewModel @Inject constructor(
    private val getEmergencyHistoryUseCase: GetEmergencyHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EmergencyHistoryState())
    val state = _state.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getEmergencyHistoryUseCase()
                .onSuccess { list ->
                    _state.update { it.copy(isLoading = false, history = list) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}