package com.example.feature_profile.domain.model

import java.util.Date

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String? = null,
    val bloodType: String? = null,
    val avatarUrl: String? = null,
    val dateOfBirth: Date? = null,
    val gender: String? = null,
    val lastDonationDate: Date? = null
)