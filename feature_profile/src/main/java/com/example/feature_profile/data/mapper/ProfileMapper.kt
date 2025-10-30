// D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\smartblood\profile\data\mapper\ProfileMapper.kt
package com.smartblood.profile.data.mapper

import com.smartblood.core.data.local.entities.UserEntity
import com.smartblood.profile.domain.model.UserProfile

fun UserEntity.toUserProfile(): UserProfile {
    return UserProfile(
        uid = uid,
        email = email,
        fullName = fullName,
        phoneNumber = phoneNumber,
        bloodType = bloodType,
        avatarUrl = avatarUrl,
        dateOfBirth = dateOfBirth,
        gender = gender,
        lastDonationDate = lastDonationDate
    )
}

fun UserProfile.toUserEntity(): UserEntity {
    return UserEntity(
        uid = uid,
        email = email,
        fullName = fullName,
        phoneNumber = phoneNumber,
        bloodType = bloodType,
        avatarUrl = avatarUrl,
        dateOfBirth = dateOfBirth,
        gender = gender,
        lastDonationDate = lastDonationDate
    )
}

// TODO: Thêm mapper cho DonationRecord và DonationHistoryEntity tương tự