// feature_auth/src/main/java/com/smartblood/auth/data/repository/AuthRepositoryImpl.kt

package com.smartblood.auth.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.smartblood.auth.domain.model.User
import com.smartblood.auth.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.Result

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            // Bước 1: Xác thực người dùng với Firebase Authentication
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // Bước 2: Lấy thông tin người dùng từ Cloud Firestore
                // Dùng UID từ kết quả xác thực để truy vấn đúng document.
                val userDocument = firestore.collection("users").document(firebaseUser.uid).get().await()

                // Chuyển đổi DocumentSnapshot từ Firestore thành đối tượng User của chúng ta.
                val user = userDocument.toObject(User::class.java)

                if (user != null) {
                    Result.success(user) // Trả về đối tượng User nếu thành công
                } else {
                    // Trường hợp hiếm gặp: Xác thực thành công nhưng không tìm thấy bản ghi user trong Firestore
                    // (có thể do lỗi khi đăng ký hoặc dữ liệu bị xóa thủ công).
                    Result.failure(Exception("Không tìm thấy dữ liệu người dùng trong cơ sở dữ liệu."))
                }
            } else {
                Result.failure(Exception("Không xác thực được người dùng."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerUser(fullName: String, email: String, password: String): Result<Unit> {
        return try {
            // Bước 1: Tạo user trong Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // Bước 2: Tạo đối tượng User để lưu vào Firestore
                val user = User(
                    uid = firebaseUser.uid,
                    email = email,
                    fullName = fullName
                )

                // Bước 3: Lưu đối tượng User vào collection "users" trong Firestore
                // với document ID chính là UID của người dùng.
                firestore.collection("users").document(firebaseUser.uid).set(user).await()

                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create user."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}