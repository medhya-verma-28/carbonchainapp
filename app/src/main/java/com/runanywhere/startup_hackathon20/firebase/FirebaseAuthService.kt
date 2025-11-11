package com.runanywhere.startup_hackathon20.firebase

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Data class to hold user profile information
 */
data class UserProfile(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String? = null
)

/**
 * Service for managing Firebase Authentication
 */
class FirebaseAuthService(private val context: Context) {

    private val auth: FirebaseAuth = Firebase.auth

    // Google Sign-In client
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.runanywhere.startup_hackathon20.R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // One Tap Sign-In client
    private val oneTapClient: SignInClient by lazy {
        Identity.getSignInClient(context)
    }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Set up auth state listener
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            _authState.value = if (firebaseAuth.currentUser != null) {
                AuthState.Authenticated(firebaseAuth.currentUser!!)
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    /**
     * Get Google Sign-In intent
     */
    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    /**
     * Handle Google Sign-In result
     */
    suspend fun handleGoogleSignInResult(data: Intent?): Result<UserProfile> {
        return try {
            _authState.value = AuthState.Loading

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            if (account != null) {
                firebaseAuthWithGoogle(account)
            } else {
                _authState.value = AuthState.Error("Google Sign-In failed")
                Result.failure(Exception("Google Sign-In failed"))
            }
        } catch (e: ApiException) {
            _authState.value = AuthState.Error("Google Sign-In error: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            _authState.value = AuthState.Error("Unexpected error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Authenticate with Firebase using Google credentials
     */
    private suspend fun firebaseAuthWithGoogle(account: GoogleSignInAccount): Result<UserProfile> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user

            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                val profile = UserProfile(
                    uid = user.uid,
                    displayName = user.displayName ?: account.displayName,
                    email = user.email ?: account.email,
                    photoUrl = user.photoUrl?.toString() ?: account.photoUrl?.toString()
                )
                Result.success(profile)
            } else {
                _authState.value = AuthState.Error("Firebase authentication failed")
                Result.failure(Exception("Firebase authentication failed"))
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error("Firebase auth error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Sign out from Google and Firebase
     */
    suspend fun signOutFromGoogle() {
        try {
            googleSignInClient.signOut().await()
            auth.signOut()
            _authState.value = AuthState.Unauthenticated
        } catch (e: Exception) {
            // Continue with Firebase sign out even if Google sign out fails
            auth.signOut()
            _authState.value = AuthState.Unauthenticated
        }
    }

    /**
     * Login with username/email and password
     * For compatibility, username is treated as email
     */
    suspend fun login(usernameOrEmail: String, password: String): Result<UserProfile> {
        return try {
            _authState.value = AuthState.Loading

            // Treat username as email - if it doesn't contain @, append a domain
            val email = if (usernameOrEmail.contains("@")) {
                usernameOrEmail
            } else {
                "$usernameOrEmail@carbonchain.app"
            }

            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                val profile = UserProfile(
                    uid = user.uid,
                    displayName = user.displayName ?: usernameOrEmail,
                    email = user.email,
                    photoUrl = user.photoUrl?.toString()
                )
                Result.success(profile)
            } else {
                _authState.value = AuthState.Error("Authentication failed")
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    /**
     * Register a new user with username, email, and password
     */
    suspend fun register(username: String, email: String, password: String): Result<UserProfile> {
        return try {
            _authState.value = AuthState.Loading

            // If email is empty, create one from username
            val userEmail = if (email.contains("@")) {
                email
            } else if (email.isNotEmpty()) {
                "$email@carbonchain.app"
            } else {
                "$username@carbonchain.app"
            }

            val result = auth.createUserWithEmailAndPassword(userEmail, password).await()
            val user = result.user

            if (user != null) {
                // Update profile with display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                user.updateProfile(profileUpdates).await()

                _authState.value = AuthState.Authenticated(user)
                val profile = UserProfile(
                    uid = user.uid,
                    displayName = username,
                    email = user.email,
                    photoUrl = user.photoUrl?.toString()
                )
                Result.success(profile)
            } else {
                _authState.value = AuthState.Error("Registration failed")
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    /**
     * Logout the current user
     */
    suspend fun logout() {
        try {
            // Sign out from both Google and Firebase
            signOutFromGoogle()
        } catch (e: Exception) {
            // Fallback to Firebase sign out only
            auth.signOut()
            _authState.value = AuthState.Unauthenticated
        }
    }

    /**
     * Sign in with email and password
     */
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<FirebaseUser> {
        return try {
            _authState.value = AuthState.Loading
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                Result.success(user)
            } else {
                _authState.value = AuthState.Error("Authentication failed")
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    /**
     * Create a new user with email and password
     */
    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            _authState.value = AuthState.Loading
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                Result.success(user)
            } else {
                _authState.value = AuthState.Error("Registration failed")
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    /**
     * Sign out the current user
     */
    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    /**
     * Get current user ID
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Get current user email
     */
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    /**
     * Check if user is signed in
     */
    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update user email
     */
    suspend fun updateUserEmail(newEmail: String): Result<Unit> {
        return try {
            auth.currentUser?.updateEmail(newEmail)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update user password
     */
    suspend fun updateUserPassword(newPassword: String): Result<Unit> {
        return try {
            auth.currentUser?.updatePassword(newPassword)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete user account
     */
    suspend fun deleteUserAccount(): Result<Unit> {
        return try {
            auth.currentUser?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Sealed class representing authentication states
 */
sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}
