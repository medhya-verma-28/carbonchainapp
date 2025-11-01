package com.runanywhere.startup_hackathon20.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.startup_hackathon20.blockchain.BlockchainService
import com.runanywhere.startup_hackathon20.data.*
import com.runanywhere.startup_hackathon20.repository.CarbonRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for managing carbon registry UI state and business logic
 */
class CarbonViewModel : ViewModel() {

    private val blockchainService = BlockchainService()
    private val repository = CarbonRepository(blockchainService)

    // State flows from repository
    val projects = repository.projects
    val credits = repository.credits
    val transactions = repository.transactions
    val userWallet = repository.userWallet

    // UI State
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _selectedProject = MutableStateFlow<CarbonProject?>(null)
    val selectedProject: StateFlow<CarbonProject?> = _selectedProject.asStateFlow()

    private val _selectedCredit = MutableStateFlow<CarbonCredit?>(null)
    val selectedCredit: StateFlow<CarbonCredit?> = _selectedCredit.asStateFlow()

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
}

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}
