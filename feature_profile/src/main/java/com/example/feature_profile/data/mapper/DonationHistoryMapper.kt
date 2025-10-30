// D:\SmartBloodDonationAndroid\feature_profile\src\main\java\com\smartblood\profile\data\mapper\DonationHistoryMapper.kt
package com.smartblood.profile.data.mapper

import com.smartblood.core.data.local.entities.DonationHistoryEntity
import com.smartblood.profile.domain.model.DonationRecord

fun DonationHistoryEntity.toDonationRecord(): DonationRecord {
    return DonationRecord(
        id = id,
        hospitalName = hospitalName,
        date = date,
        unitsDonated = unitsDonated
    )
}

fun DonationRecord.toDonationHistoryEntity(userId: String): DonationHistoryEntity {
    return DonationHistoryEntity(
        id = id,
        userId = userId, // Cần userId để lưu vào bảng
        hospitalName = hospitalName,
        date = date,
        unitsDonated = unitsDonated
    )
}