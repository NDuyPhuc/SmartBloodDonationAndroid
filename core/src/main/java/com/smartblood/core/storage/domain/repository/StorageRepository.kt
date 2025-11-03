package com.smartblood.core.storage.domain.repository

import android.net.Uri
import kotlin.Result

interface StorageRepository {
    suspend fun uploadImage(uri: Uri, path: String): Result<String>
}