package com.runanywhere.startup_hackathon20.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.startup_hackathon20.ai.LandscapeClassifier
import com.runanywhere.startup_hackathon20.blockchain.BlockchainService
import com.runanywhere.startup_hackathon20.data.*
import com.runanywhere.startup_hackathon20.repository.CarbonRepository
import com.runanywhere.startup_hackathon20.firebase.FirebaseAuthService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for managing carbon registry UI state and business logic
 */
class CarbonViewModel(context: Context) : ViewModel() {

    private val blockchainService = BlockchainService()
    private val landscapeClassifier = LandscapeClassifier(context)
    private val repository = CarbonRepository(blockchainService, landscapeClassifier)
    private val firebaseAuth = FirebaseAuthService(context)

    // State flows from repository
    val projects = repository.projects
    val credits = repository.credits
    val transactions = repository.transactions
    val userWallet = repository.userWallet
    val userSubmissions = repository.userSubmissions
    val carbonRegistrySubmissions = repository.carbonRegistrySubmissions

    // UI State
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedProject = MutableStateFlow<CarbonProject?>(null)
    val selectedProject: StateFlow<CarbonProject?> = _selectedProject.asStateFlow()

    private val _selectedCredit = MutableStateFlow<CarbonCredit?>(null)
    val selectedCredit: StateFlow<CarbonCredit?> = _selectedCredit.asStateFlow()

    // Authentication state
    data class AuthState(
        val isAuthenticated: Boolean = false,
        val userType: UserType? = null,
        val username: String? = null,
        val email: String? = null
    )

    enum class UserType {
        USER,
        ADMIN
    }

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Statistics
    val totalCo2Offset = projects.map { projectList ->
        projectList.sumOf { it.impactMetrics.co2Reduced }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val activeProjectsCount = projects.map { projectList ->
        projectList.count { it.status == ProjectStatus.ACTIVE }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val availableCreditsCount = credits.map { creditList ->
        creditList.count { it.status == CreditStatus.ACTIVE }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    init {
        // Initialize wallet if not exists
        viewModelScope.launch {
            if (userWallet.value == null) {
                createWallet()
            }
        }
    }

    fun createWallet() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.createWallet()
                _uiState.value = UiState.Success("Wallet created successfully")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to create wallet")
            }
        }
    }

    fun selectProject(project: CarbonProject) {
        _selectedProject.value = project
    }

    fun selectCredit(credit: CarbonCredit) {
        _selectedCredit.value = credit
    }

    fun purchaseCredit(credit: CarbonCredit) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.purchaseCredit(credit)
            result.fold(
                onSuccess = { transaction ->
                    _uiState.value = UiState.Success(
                        "Credit purchased successfully!\nTx: ${
                            transaction.transactionHash.take(10)
                        }..."
                    )
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Purchase failed")
                }
            )
        }
    }

    fun retireCredit(creditId: String, amount: Double, reason: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.retireCredit(creditId, amount, reason)
            result.fold(
                onSuccess = { transaction ->
                    _uiState.value = UiState.Success(
                        "Credit retired successfully!\nTx: ${
                            transaction.transactionHash.take(10)
                        }..."
                    )
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Retirement failed")
                }
            )
        }
    }

    fun verifyCredit(creditId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.verifyCredit(creditId)
            result.fold(
                onSuccess = { verification ->
                    _uiState.value = UiState.Success(
                        "Credit verified on blockchain!\nProof: ${
                            verification.blockchainProof.take(10)
                        }..."
                    )
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Verification failed")
                }
            )
        }
    }

    fun clearUiState() {
        _uiState.value = UiState.Idle
    }

    fun getProjectsByType(type: ProjectType): List<CarbonProject> {
        return projects.value.filter { it.type == type }
    }

    fun searchProjects(query: String): List<CarbonProject> {
        return if (query.isBlank()) {
            projects.value
        } else {
            projects.value.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.location.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
    }

    // Authentication Functions
    fun login(username: String, password: String, isAdmin: Boolean) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = firebaseAuth.login(username, password)
            result.fold(
                onSuccess = { user ->
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        userType = if (isAdmin) UserType.ADMIN else UserType.USER,
                        username = user.displayName,
                        email = user.email
                    )
                    _uiState.value =
                        UiState.Success("Login successful! Welcome ${user.displayName}")
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Login failed")
                }
            )
        }
    }

    /**
     * Handle Google Sign-In
     */
    fun getGoogleSignInIntent(): android.content.Intent {
        return firebaseAuth.getGoogleSignInIntent()
    }

    /**
     * Launch Google Sign-In flow
     */
    fun launchGoogleSignIn(
        context: Context,
        isAdmin: Boolean,
        signInLauncher: androidx.activity.result.ActivityResultLauncher<android.content.Intent>
    ) {
        try {
            val signInIntent = firebaseAuth.getGoogleSignInIntent()
            signInLauncher.launch(signInIntent)
        } catch (e: Exception) {
            viewModelScope.launch {
                _uiState.value = UiState.Error("Failed to launch Google Sign-In: ${e.message}")
            }
        }
    }

    /**
     * Process Google Sign-In result
     */
    fun handleGoogleSignInResult(data: android.content.Intent?, isAdmin: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = firebaseAuth.handleGoogleSignInResult(data)
            result.fold(
                onSuccess = { user ->
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        userType = if (isAdmin) UserType.ADMIN else UserType.USER,
                        username = user.displayName,
                        email = user.email
                    )
                    _uiState.value =
                        UiState.Success("Login successful! Welcome ${user.displayName}")
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Google Sign-In failed")
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            firebaseAuth.logout()
            _authState.value = AuthState()
            _uiState.value = UiState.Success("Logged out successfully")
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = firebaseAuth.register(username, email, password)
            result.fold(
                onSuccess = { user ->
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        userType = UserType.USER,
                        username = user.displayName,
                        email = user.email
                    )
                    _uiState.value =
                        UiState.Success("Registration successful! Welcome ${user.displayName}")
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Registration failed")
                }
            )
        }
    }

    fun approveSubmission(submissionId: String, notes: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.approveSubmission(submissionId, notes)
            result.fold(
                onSuccess = {
                    _uiState.value =
                        UiState.Success("Submission approved and uploaded to blockchain!")
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Approval failed")
                }
            )
        }
    }

    fun rejectSubmission(submissionId: String, notes: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.rejectSubmission(submissionId, notes)
            result.fold(
                onSuccess = {
                    _uiState.value = UiState.Success("Submission rejected")
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Rejection failed")
                }
            )
        }
    }

    fun submitCarbonRegistry(
        photoUri: String?,
        latitude: String?,
        longitude: String?,
        selectedSite: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val username = authState.value.username ?: "Unknown"
            val email = authState.value.email ?: "no-email@example.com"

            val result = repository.submitCarbonRegistry(
                photoUri, latitude, longitude, selectedSite, username, email
            )

            result.fold(
                onSuccess = { submission ->
                    _uiState.value = UiState.Success(
                        "✓ Submission successful! Your data is being verified by admin before adding to blockchain.\n\nSubmission ID: ${submission.id}"
                    )
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Submission failed")
                }
            )
        }
    }

    fun getUserCarbonRegistries(): List<CarbonRegistrySubmission> {
        val username = authState.value.username ?: return emptyList()
        return repository.getCarbonRegistrySubmissions(username)
    }

    fun getUserPendingSubmissions(): List<UserSubmission> {
        val username = authState.value.username ?: return emptyList()
        return repository.getUserSubmissions(username)
    }

    fun buyCarbonCredits(amount: Double) {
        viewModelScope.launch {
            val result = repository.buyCarbonCredits(amount)
            result.fold(
                onSuccess = {
                    // Success handled by payment flow
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Purchase failed")
                }
            )
        }
    }

    fun sellCarbonCredits(amount: Double) {
        viewModelScope.launch {
            val result = repository.sellCarbonCredits(amount)
            result.fold(
                onSuccess = {
                    // Success handled by payment flow
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Listing failed")
                }
            )
        }
    }

    fun markSubmissionAsCompleted(submissionId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.markSubmissionAsCompleted(submissionId)
            result.fold(
                onSuccess = {
                    _uiState.value = UiState.Success(
                        "🎉 Blockchain Registry Completed!\n\nYour carbon credit has been successfully registered on the blockchain with all verification details."
                    )
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Failed to mark as completed")
                }
            )
        }
    }
}

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

/**
 * Factory for creating CarbonViewModel with Context
 */
class CarbonViewModelFactory(private val context: Context) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarbonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarbonViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
