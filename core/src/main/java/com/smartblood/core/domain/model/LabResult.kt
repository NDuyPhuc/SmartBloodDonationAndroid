package com.smartblood.core.domain.model

import java.util.Date

/**
 * Model chứa kết quả xét nghiệm (Phiên bản Public cho User App).
 * Lưu ý: Các trường nội bộ như 'screeningStatus' hay 'confirmedBloodType'
 * KHÔNG được đưa vào đây để đảm bảo người dùng chỉ thấy thông tin được phép.
 */
data class LabResult(
    val documentUrl: String? = null, // Link file PDF/Ảnh kết quả xét nghiệm
    val conclusion: String? = null,  // Lời dặn của bác sĩ hoặc kết luận công khai
    val recordedAt: Date? = null     // Thời gian ghi nhận kết quả
)