package com.runanywhere.startup_hackathon20.repository

import com.runanywhere.startup_hackathon20.blockchain.BlockchainService
import com.runanywhere.startup_hackathon20.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing carbon credits and projects
 */
class CarbonRepository(
    private val blockchainService: BlockchainService
) {

    private val _projects = MutableStateFlow<List<CarbonProject>>(emptyList())
    val projects: StateFlow<List<CarbonProject>> = _projects.asStateFlow()

    private val _credits = MutableStateFlow<List<CarbonCredit>>(emptyList())
    val credits: StateFlow<List<CarbonCredit>> = _credits.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _userWallet = MutableStateFlow<UserWallet?>(null)
    val userWallet: StateFlow<UserWallet?> = _userWallet.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val currentTime = System.currentTimeMillis()
        val yearInMillis = 365L * 24 * 60 * 60 * 1000

        // Mock Projects
        _projects.value = listOf(
            CarbonProject(
                id = "proj_001",
                name = "Amazon Rainforest Conservation",
                type = ProjectType.REFORESTATION,
                location = "Brazil, Amazon Basin",
                coordinates = Coordinates(-3.4653, -62.2159),
                description = "Large-scale reforestation project protecting 50,000 hectares of Amazon rainforest. Preventing deforestation and promoting biodiversity.",
                totalCredits = 150000.0,
                availableCredits = 87500.0,
                retiredCredits = 62500.0,
                startDate = currentTime - (2 * yearInMillis),
                projectDuration = 10,
                developer = "Amazon Conservation Trust",
                verificationStandard = "Verra VCS",
                status = ProjectStatus.ACTIVE,
                impactMetrics = ImpactMetrics(
                    co2Reduced = 150000.0,
                    treesPlanted = 2500000,
                    energyGenerated = null,
                    landsRestored = 50000.0,
                    beneficiaries = 15000
                ),
                images = listOf("forest1.jpg", "forest2.jpg"),
                documents = listOf(
                    Document("Project Design", "PDF", "https://example.com/doc1.pdf", currentTime)
                )
            ),
            CarbonProject(
                id = "proj_002",
                name = "Solar Farm India",
                type = ProjectType.RENEWABLE_ENERGY,
                location = "Rajasthan, India",
                coordinates = Coordinates(26.9124, 75.7873),
                description = "500MW solar power plant providing clean electricity to 400,000 homes, displacing coal-based power generation.",
                totalCredits = 250000.0,
                availableCredits = 125000.0,
                retiredCredits = 125000.0,
                startDate = currentTime - yearInMillis,
                projectDuration = 15,
                developer = "SolarTech Industries",
                verificationStandard = "Gold Standard",
                status = ProjectStatus.ACTIVE,
                impactMetrics = ImpactMetrics(
                    co2Reduced = 250000.0,
                    treesPlanted = null,
                    energyGenerated = 1200000.0,
                    landsRestored = null,
                    beneficiaries = 400000
                ),
                images = listOf("solar1.jpg", "solar2.jpg"),
                documents = listOf(
                    Document(
                        "Verification Report",
                        "PDF",
                        "https://example.com/doc2.pdf",
                        currentTime
                    )
                )
            ),
            CarbonProject(
                id = "proj_003",
                name = "Wind Energy Denmark",
                type = ProjectType.RENEWABLE_ENERGY,
                location = "North Sea, Denmark",
                coordinates = Coordinates(56.2639, 10.8783),
                description = "Offshore wind farm with 100 turbines generating 800MW of clean energy annually.",
                totalCredits = 320000.0,
                availableCredits = 200000.0,
                retiredCredits = 120000.0,
                startDate = currentTime - (3 * yearInMillis),
                projectDuration = 20,
                developer = "Nordic Wind Energy",
                verificationStandard = "Verra VCS",
                status = ProjectStatus.ACTIVE,
                impactMetrics = ImpactMetrics(
                    co2Reduced = 320000.0,
                    treesPlanted = null,
                    energyGenerated = 2400000.0,
                    landsRestored = null,
                    beneficiaries = 650000
                ),
                images = listOf("wind1.jpg", "wind2.jpg"),
                documents = listOf()
            ),
            CarbonProject(
                id = "proj_004",
                name = "Mangrove Restoration Vietnam",
                type = ProjectType.OCEAN_CONSERVATION,
                location = "Mekong Delta, Vietnam",
                coordinates = Coordinates(10.0452, 105.7469),
                description = "Restoring 10,000 hectares of mangrove forests to protect coastlines and enhance marine biodiversity.",
                totalCredits = 85000.0,
                availableCredits = 45000.0,
                retiredCredits = 40000.0,
                startDate = currentTime - (yearInMillis / 2),
                projectDuration = 8,
                developer = "Ocean Blue Foundation",
                verificationStandard = "Plan Vivo",
                status = ProjectStatus.ACTIVE,
                impactMetrics = ImpactMetrics(
                    co2Reduced = 85000.0,
                    treesPlanted = 1200000,
                    energyGenerated = null,
                    landsRestored = 10000.0,
                    beneficiaries = 8000
                ),
                images = listOf("mangrove1.jpg"),
                documents = listOf()
            ),
            CarbonProject(
                id = "proj_005",
                name = "Methane Capture Dairy Farm",
                type = ProjectType.METHANE_CAPTURE,
                location = "California, USA",
                coordinates = Coordinates(36.7783, -119.4179),
                description = "Capturing methane from dairy operations and converting it to renewable natural gas.",
                totalCredits = 45000.0,
                availableCredits = 30000.0,
                retiredCredits = 15000.0,
                startDate = currentTime - (yearInMillis / 3),
                projectDuration = 5,
                developer = "AgriGreen Solutions",
                verificationStandard = "Climate Action Reserve",
                status = ProjectStatus.ACTIVE,
                impactMetrics = ImpactMetrics(
                    co2Reduced = 45000.0,
                    treesPlanted = null,
                    energyGenerated = 85000.0,
                    landsRestored = null,
                    beneficiaries = 250
                ),
                images = listOf("farm1.jpg"),
                documents = listOf()
            )
        )

        // Mock Credits
        _credits.value = listOf(
            CarbonCredit(
                id = "credit_001",
                projectName = "Amazon Rainforest Conservation",
                projectType = ProjectType.REFORESTATION,
                amount = 100.0,
                price = 25.0,
                location = "Brazil",
                verifier = "Verra",
                status = CreditStatus.ACTIVE,
                issueDate = currentTime - (yearInMillis / 2),
                expiryDate = currentTime + (5 * yearInMillis),
                blockchainHash = "0x1a2b3c4d5e6f7890abcdef1234567890abcdef1234567890abcdef1234567890",
                vintage = 2024,
                methodology = "VM0042",
                serialNumber = "VCS-2024-001",
                description = "Carbon credits from verified rainforest conservation activities",
                certificationBody = "Verra",
                imageUrl = null
            ),
            CarbonCredit(
                id = "credit_002",
                projectName = "Solar Farm India",
                projectType = ProjectType.RENEWABLE_ENERGY,
                amount = 250.0,
                price = 22.0,
                location = "India",
                verifier = "Gold Standard",
                status = CreditStatus.ACTIVE,
                issueDate = currentTime - (yearInMillis / 3),
                expiryDate = currentTime + (4 * yearInMillis),
                blockchainHash = "0x2b3c4d5e6f7890abcdef1234567890abcdef1234567890abcdef1234567890ab",
                vintage = 2024,
                methodology = "GS-001",
                serialNumber = "GS-2024-002",
                description = "Renewable energy credits from solar power generation",
                certificationBody = "Gold Standard",
                imageUrl = null
            ),
            CarbonCredit(
                id = "credit_003",
                projectName = "Wind Energy Denmark",
                projectType = ProjectType.RENEWABLE_ENERGY,
                amount = 500.0,
                price = 28.0,
                location = "Denmark",
                verifier = "Verra",
                status = CreditStatus.ACTIVE,
                issueDate = currentTime - (yearInMillis / 4),
                expiryDate = currentTime + (6 * yearInMillis),
                blockchainHash = "0x3c4d5e6f7890abcdef1234567890abcdef1234567890abcdef1234567890abcd",
                vintage = 2024,
                methodology = "VM0089",
                serialNumber = "VCS-2024-003",
                description = "Clean energy credits from offshore wind farm",
                certificationBody = "Verra",
                imageUrl = null
            ),
            CarbonCredit(
                id = "credit_004",
                projectName = "Mangrove Restoration Vietnam",
                projectType = ProjectType.OCEAN_CONSERVATION,
                amount = 75.0,
                price = 30.0,
                location = "Vietnam",
                verifier = "Plan Vivo",
                status = CreditStatus.RETIRED,
                issueDate = currentTime - (yearInMillis / 6),
                expiryDate = currentTime + (3 * yearInMillis),
                blockchainHash = "0x4d5e6f7890abcdef1234567890abcdef1234567890abcdef1234567890abcdef",
                vintage = 2024,
                methodology = "PV-001",
                serialNumber = "PV-2024-004",
                description = "Blue carbon credits from mangrove restoration",
                certificationBody = "Plan Vivo",
                imageUrl = null
            )
        )
    }

    suspend fun createWallet(): UserWallet {
        delay(500)
        val wallet = blockchainService.createWallet()
        _userWallet.value = wallet
        return wallet
    }

    suspend fun purchaseCredit(credit: CarbonCredit): Result<Transaction> {
        return try {
            val transaction = blockchainService.issueCarbonCredit(credit)
            _transactions.value = _transactions.value + transaction

            // Update wallet
            _userWallet.value?.let { wallet ->
                _userWallet.value = wallet.copy(
                    totalCreditsOwned = wallet.totalCreditsOwned + credit.amount,
                    balance = wallet.balance - (credit.amount * credit.price)
                )
            }

            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun retireCredit(
        creditId: String,
        amount: Double,
        reason: String
    ): Result<Transaction> {
        return try {
            val transaction = blockchainService.retireCredits(creditId, amount, reason)
            _transactions.value = _transactions.value + transaction

            // Update wallet
            _userWallet.value?.let { wallet ->
                _userWallet.value = wallet.copy(
                    totalCreditsRetired = wallet.totalCreditsRetired + amount,
                    totalCreditsOwned = wallet.totalCreditsOwned - amount
                )
            }

            // Update credit status
            val updatedCredits = _credits.value.map { credit ->
                if (credit.id == creditId) {
                    credit.copy(status = CreditStatus.RETIRED)
                } else {
                    credit
                }
            }
            _credits.value = updatedCredits

            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyCredit(creditId: String): Result<VerificationRecord> {
        return try {
            val verification = blockchainService.verifyCarbonCredit(creditId)
            Result.success(verification)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getProjectById(projectId: String): CarbonProject? {
        return _projects.value.find { it.id == projectId }
    }

    fun getCreditById(creditId: String): CarbonCredit? {
        return _credits.value.find { it.id == creditId }
    }

    fun getTransactionsByCreditId(creditId: String): List<Transaction> {
        return _transactions.value.filter { it.creditId == creditId }
    }
}
