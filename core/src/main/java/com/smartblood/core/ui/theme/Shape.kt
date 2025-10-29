// core/src/main/java/com/smartblood/core/ui/theme/Shape.kt

package com.smartblood.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    small = RoundedCornerShape(4.dp),    // Dùng cho các component nhỏ như chip, tag
    medium = RoundedCornerShape(8.dp),   // Dùng cho Card, Button, Input Field
    large = RoundedCornerShape(16.dp)    // Dùng cho Dialog, Bottom Sheet
)