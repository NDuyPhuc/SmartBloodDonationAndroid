// D:\SmartBloodDonationAndroid\core\src\main\java\com\smartblood\core\data\local\Converters.kt

package com.smartblood.core.data.local

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}