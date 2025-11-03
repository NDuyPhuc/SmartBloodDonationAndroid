package com.smartblood.core.storage.data.repository

import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback // <<-- SỬ DỤNG UploadCallback
import com.smartblood.core.storage.domain.repository.StorageRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.Result
import kotlin.coroutines.resume

class StorageRepositoryImpl @Inject constructor() : StorageRepository {

    override suspend fun uploadImage(uri: Uri, path: String): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            // Sử dụng path làm public_id để có thể ghi đè ảnh đại diện cũ
            // Cloudinary không khuyến khích dùng '/' trong public_id
            val publicId = path

            MediaManager.get().upload(uri)
                .option("public_id", publicId)
                .option("overwrite", true) // Ghi đè file cũ nếu có cùng public_id
                .callback(object : UploadCallback { // <<-- SỬ DỤNG UploadCallback
                    override fun onStart(requestId: String) {
                        Log.d("CloudinaryUpload", "Upload started: $requestId")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        // Optional: Handle progress
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                        val secureUrl = resultData?.get("secure_url") as? String
                        if (secureUrl != null) {
                            Log.d("CloudinaryUpload", "Upload success: $secureUrl")
                            continuation.resume(Result.success(secureUrl))
                        } else {
                            Log.e("CloudinaryUpload", "Upload error: URL is null")
                            continuation.resume(Result.failure(Exception("Upload successful but URL not found")))
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo?) {
                        val errorMessage = error?.description ?: "Unknown upload error"
                        Log.e("CloudinaryUpload", "Upload error: $errorMessage")
                        continuation.resume(Result.failure(Exception(errorMessage)))
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo?) {
                        // Not typically used
                    }
                }).dispatch() // <<-- SỬ DỤNG dispatch()
        }
    }
}