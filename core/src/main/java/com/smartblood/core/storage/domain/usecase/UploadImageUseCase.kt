package com.smartblood.core.storage.domain.usecase

import android.net.Uri
import com.smartblood.core.storage.domain.repository.StorageRepository
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val repository: StorageRepository
) {
    suspend operator fun invoke(uri: Uri, path: String) = repository.uploadImage(uri, path)
}