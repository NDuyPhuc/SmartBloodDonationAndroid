package com.example.feature_emergency.ui

import com.smartblood.core.domain.model.BloodRequest

data class EmergencyListState(
    val isLoading: Boolean = true,
    val requests: List<BloodRequest> = emptyList(),
    val error: String? = null,
    val acceptSuccess: Boolean = false
)

sealed class EmergencyListEvent {
    data class OnAcceptClick(val request: BloodRequest) : EmergencyListEvent()
    object OnDialogDismiss : EmergencyListEvent()
}