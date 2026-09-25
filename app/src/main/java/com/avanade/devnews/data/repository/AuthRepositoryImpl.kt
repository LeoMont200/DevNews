package com.avanade.devnews.data.repository

<<<<<<< HEAD
import android.content.SharedPreferences
>>>>>>> origin/integracao-noticias
import com.avanade.devnews.domain.model.User
import com.avanade.devnews.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences
) : AuthRepository {
    companion object {
        private const val KEY_REMEMBER_ME = "remember_me"
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            val user = User(
                id = firebaseUser?.uid ?: "",
                name = firebaseUser?.displayName ?: "",
                email = firebaseUser?.email ?: ""
            )

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null

        return User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: ""
        )
    }

<<<<<<< HEAD
    override fun setRememberMe(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_REMEMBER_ME, enabled).apply()
    }

    override fun isRememberMeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
    }

    override fun hasActiveSession(): Boolean {
        val hasFirebaseUser = firebaseAuth.currentUser != null
        val rememberMeEnabled = isRememberMeEnabled()

        if (hasFirebaseUser && !rememberMeEnabled) {
            firebaseAuth.signOut()
            return false
        }

        return hasFirebaseUser && rememberMeEnabled
    }

    override fun logout() {
        setRememberMe(false)
        firebaseAuth.signOut()
    }

    override suspend fun register(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: ""
                )
                Result.success(user)
            } else {
                Result.failure(Exception("User registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun recoverPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth
                .sendPasswordResetEmail(email)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
