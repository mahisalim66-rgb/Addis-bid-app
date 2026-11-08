package com.example.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    data class Success(val user: FirebaseUser?, val email: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class FirebaseAuthService {
    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (_: Exception) {
            null
        }
    }

    fun getCurrentFirebaseUser(): FirebaseUser? = auth?.currentUser

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        val firebaseAuth = auth ?: return AuthResult.Error("Firebase Auth is not initialized. Check internet connection.")
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            AuthResult.Success(result.user, result.user?.email ?: email.trim())
        } catch (e: Exception) {
            val msg = when {
                e.message?.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) == true ||
                e.message?.contains("disabled", ignoreCase = true) == true ||
                e.message?.contains("operation is not allowed", ignoreCase = true) == true ->
                    "የፋየርቤዝ ኢሜይል አገልግሎት በ Firebase Console አልበራም (Email/Password provider not enabled in Console)."
                e.message?.contains("no user record", ignoreCase = true) == true ||
                e.message?.contains("user-not-found", ignoreCase = true) == true ->
                    "ይህ አካውንት አልተገኘም። እባክዎ 'አካውንት ይክፈቱ (Sign Up)' የሚለውን ነክተው ይመዝገቡ።"
                e.message?.contains("wrong-password", ignoreCase = true) == true ||
                e.message?.contains("invalid-credential", ignoreCase = true) == true ->
                    "የተሳሳተ ኢሜይል ወይም የይለፍ ቃል። እባክዎ ደግመው ይሞክሩ።"
                e.message?.contains("email-already-in-use", ignoreCase = true) == true ->
                    "ይህ ኢሜይል አስቀድሞ ተመዝግቧል። እባክዎ 'ይግቡ (Sign In)' የሚለውን ነክተው ይግቡ።"
                e.message?.contains("network", ignoreCase = true) == true ->
                    "የኔትወርክ ግንኙነት ችግር። እባክዎ ኢንተርኔትዎን ያረጋግጡ።"
                else -> e.localizedMessage ?: "Authentication failed"
            }
            AuthResult.Error(msg)
        }
    }

    suspend fun registerWithEmailAndPassword(email: String, password: String): AuthResult {
        val firebaseAuth = auth ?: return AuthResult.Error("Firebase Auth is not initialized. Check internet connection.")
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
            AuthResult.Success(result.user, result.user?.email ?: email.trim())
        } catch (e: Exception) {
            val msg = when {
                e.message?.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) == true ||
                e.message?.contains("disabled", ignoreCase = true) == true ||
                e.message?.contains("operation is not allowed", ignoreCase = true) == true ->
                    "የፋየርቤዝ ኢሜይል አገልግሎት በ Firebase Console አልበራም (Email/Password provider not enabled in Console)."
                e.message?.contains("email-already-in-use", ignoreCase = true) == true ->
                    "ይህ ኢሜይል አስቀድሞ ተመዝግቧል። እባክዎ 'ይግቡ (Sign In)' የሚለውን ነክተው ይግቡ።"
                e.message?.contains("weak-password", ignoreCase = true) == true ->
                    "የይለፍ ቃሉ ደካማ ነው። እባክዎ ጠንከር ያለ የይለፍ ቃል ያስገቡ።"
                e.message?.contains("network", ignoreCase = true) == true ->
                    "የኔትወርክ ግንኙነት ችግር። እባክዎ ኢንተርኔትዎን ያረጋግጡ።"
                else -> e.localizedMessage ?: "Registration failed"
            }
            AuthResult.Error(msg)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): AuthResult {
        val firebaseAuth = auth ?: return AuthResult.Error("Firebase Auth is not initialized.")
        return try {
            firebaseAuth.sendPasswordResetEmail(email.trim()).await()
            AuthResult.Success(null, email.trim())
        } catch (e: Exception) {
            val msg = when {
                e.message?.contains("user-not-found", ignoreCase = true) == true ->
                    "በዚህ ኢሜይል የተመዘገበ አካውንት የለም።"
                else -> e.localizedMessage ?: "Failed to send reset email"
            }
            AuthResult.Error(msg)
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (_: Exception) {
        }
    }
}
