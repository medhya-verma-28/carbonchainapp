package com.runanywhere.startup_hackathon20.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class CarbonCredit(
    val id: String = UUID.randomUUID().toString(),
    val projectName: String,
    val projectType: ProjectType,
    val amount: Double, // in tonnes CO2
    val price: Double, // per tonne
    val location: String,
    val verifier: String,
    val status: CreditStatus,
    val issueDate: Long,
    val expiryDate: Long,
    val blockchainHash: String,
    val vintage: Int, // year
    val methodology: String,
    val serialNumber: String,
    val description: String,
    val certificationBody: String,
    val imageUrl: String? = null
) : Parcelable

@Parcelize
enum class ProjectType : Parcelable {
    REFORESTATION,
    RENEWABLE_ENERGY,
    ENERGY_EFFICIENCY,
    METHANE_CAPTURE,
    OCEAN_CONSERVATION,
    CARBON_CAPTURE,
    SUSTAINABLE_AGRICULTURE
}

@Parcelize
enum class CreditStatus : Parcelable {
    ACTIVE,
    RETIRED,
    PENDING_VERIFICATION,
    EXPIRED,
    CANCELLED
}

@Parcelize
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val creditId: String,
    val fromAddress: String,
    val toAddress: String,
    val amount: Double,
    val timestamp: Long,
    val transactionHash: String,
    val blockNumber: Long,
    val gasUsed: String,
    val status: TransactionStatus,
    val type: TransactionType
) : Parcelable

@Parcelize
enum class TransactionStatus : Parcelable {
    PENDING,
    CONFIRMED,
    FAILED
}

@Parcelize
enum class TransactionType : Parcelable {
    ISSUANCE,
    TRANSFER,
    RETIREMENT,
    VERIFICATION
}

@Parcelize
data class CarbonProject(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: ProjectType,
    val location: String,
    val coordinates: Coordinates?,
    val description: String,
    val totalCredits: Double,
    val availableCredits: Double,
    val retiredCredits: Double,
    val startDate: Long,
    val projectDuration: Int, // in years
    val developer: String,
    val verificationStandard: String,
    val status: ProjectStatus,
    val impactMetrics: ImpactMetrics,
    val images: List<String>,
    val documents: List<Document>
) : Parcelable

@Parcelize
data class Coordinates(
    val latitude: Double,
    val longitude: Double
) : Parcelable

@Parcelize
enum class ProjectStatus : Parcelable {
    ACTIVE,
    COMPLETED,
    UNDER_VERIFICATION,
    SUSPENDED
}

@Parcelize
data class ImpactMetrics(
    val co2Reduced: Double,
    val treesPlanted: Int?,
    val energyGenerated: Double?,
    val landsRestored: Double?,
    val beneficiaries: Int?
) : Parcelable

@Parcelize
data class Document(
    val name: String,
    val type: String,
    val url: String,
    val uploadDate: Long
) : Parcelable

@Parcelize
data class UserWallet(
    val address: String,
    val privateKey: String,
    val balance: Double,
    val totalCreditsOwned: Double,
    val totalCreditsRetired: Double,
    val createdAt: Long
) : Parcelable

@Parcelize
data class VerificationRecord(
    val id: String = UUID.randomUUID().toString(),
    val creditId: String,
    val verifier: String,
    val verificationDate: Long,
    val status: VerificationStatus,
    val comments: String,
    val documents: List<Document>,
    val blockchainProof: String
) : Parcelable

@Parcelize
enum class VerificationStatus : Parcelable {
    VERIFIED,
    REJECTED,
    PENDING,
    REQUIRES_REVISION
}

@Parcelize
data class UserSubmission(
    val id: String = UUID.randomUUID().toString(),
    val submissionDate: Long = System.currentTimeMillis(),
    val location: String,
    val dataQuality: String, // "High", "Medium", "Low"
    val status: SubmissionStatus,
    val co2Value: Double, // in tons
    val hectaresValue: Double,
    val vegetationCoverage: Double, // percentage
    val aiConfidence: Double, // percentage
    val imageUrl: String? = null,
    val coordinates: Coordinates?,
    val gpsVerified: Boolean,
    val satelliteDataVerified: Boolean,
    val imageQualityVerified: Boolean,
    val coordinatesWithinRange: Boolean,
    val submitterName: String,
    val submitterEmail: String,
    val notes: String = "",
    val blockchainRegistryCompleted: Boolean = false,
    val completionDate: Long? = null,
    val aiLandscapeDetected: Boolean = false,
    val aiLandscapeCategory: String = "",
    val aiAnalysisDescription: String = "",
    val aiVerificationTimestamp: Long? = null
) : Parcelable

@Parcelize
enum class SubmissionStatus : Parcelable {
    PENDING,
    APPROVED,
    REJECTED
}

@Parcelize
data class CarbonRegistrySubmission(
    val id: String = UUID.randomUUID().toString(),
    val registrationStatus: RegistrationStatus,
    val blockNumber: String,
    val creditAmount: Double, // in tonnes CO2
    val projectArea: String, // in hectares
    val vintageYear: Int,
    val verificationDate: String,
    val transactionHash: String,
    val contractAddress: String,
    val network: String,
    val tokenStandard: String,
    val auditTrail: List<AuditItem>,
    val registryNotes: String,
    val imageUrl: String? = null,
    val location: String,
    val coordinates: Coordinates?,
    val submissionDate: Long = System.currentTimeMillis(),
    val submitterName: String,
    val submitterEmail: String,
    val status: SubmissionStatus
) : Parcelable

@Parcelize
data class AuditItem(
    val description: String,
    val completed: Boolean
) : Parcelable

@Parcelize
enum class RegistrationStatus : Parcelable {
    REGISTERED,
    PENDING,
    REJECTED
}

// Blockchain Registry Form Data
@Parcelize
data class BlockchainRegistryForm(
    val creditAmount: String = "",
    val projectArea: String = "",
    val vintageYear: String = "",
    val verificationDate: String = "",
    val registryNotes: String = ""
) : Parcelable

// Smart Contract Data
@Parcelize
data class SmartContractData(
    val transactionStatus: String = "Pending",
    val carbonCreditTokens: Double = 0.0,
    val tokenStandard: String = "ERC-20",
    val network: String = "BNB",
    val tokenGeneration: String = "ERC-20",
    val contractAddress: String = "",
    val deploymentDate: String = "",
    val gasUsed: String = "",
    val accessFunctions: List<String> = listOf(
        "transferToken(address,amount)",
        "approveDelegate(address)",
        "retireCredit()",
        "burnToken(address)"
    ),
    val contractVerification: List<ContractVerificationItem> = listOf(),
    val contractNotes: String = ""
) : Parcelable

@Parcelize
data class ContractVerificationItem(
    val description: String,
    val verified: Boolean
) : Parcelable

// Carbon Marketplace Data
@Parcelize
data class CarbonMarketplace(
    val totalVolume: Double = 0.0,
    val activeListings: Int = 0,
    val tokenAllocation: TokenAllocation = TokenAllocation(),
    val marketPrices: MarketPrices = MarketPrices(),
    val outstandingBids: List<Bid> = listOf(),
    val transactionHistory: List<MarketTransaction> = listOf(),
    val marketNotes: String = ""
) : Parcelable

@Parcelize
data class TokenAllocation(
    val allocatedToken: Double = 100.0,
    val stakedToken: Double = 20.0,
    val burnedToken: Double = 30.0,
    val remainingToken: Double = 50.0
) : Parcelable

@Parcelize
data class MarketPrices(
    val carbonOffset: Double = 45.17,
    val renewable: Double = 42.22,
    val forest: Double = 43.89
) : Parcelable

@Parcelize
data class Bid(
    val amount: Double,
    val bidder: String,
    val expiresIn: String
) : Parcelable

@Parcelize
data class MarketTransaction(
    val type: String, // "Buy" or "Sell"
    val amount: Double,
    val price: Double,
    val date: String
) : Parcelable

@Parcelize
data class ImpactDashboardData(
    val carbonReduced: Double = 2.27, // in tonnes
    val marketValue: Double = 117.0, // in dollars
    val monitoringPeriod: String = "M1",
    val monitoringDate: String = java.text.SimpleDateFormat(
        "MM/dd/yyyy",
        java.util.Locale.US
    ).format(java.util.Date(System.currentTimeMillis())),
    val carbonSequestered: Double = 150.0, // in kg
    val co2eReduction: Double = 2391.0, // in kg
    val peopleImpacted: Int = 516020,
    val treesPlanted: Int = 8016,
    val progressIndicators: List<ProgressIndicator> = listOf(
        ProgressIndicator("Monitoring Progress", 78),
        ProgressIndicator("Stakeholder Engagement", 65),
        ProgressIndicator("Compliance Tracking", 82),
        ProgressIndicator("Biodiversity", 11)
    ),
    val environmentalHealth: List<EnvironmentalMetric> = listOf(
        EnvironmentalMetric("Water Quality", "Sustainable"),
        EnvironmentalMetric("Habitat Health", "Good"),
        EnvironmentalMetric("Service Life", "Recovering")
    ),
    val communityBenefits: CommunityBenefits = CommunityBenefits(
        familiesToSupported = 156,
        jobsCreated = 12
    ),
    val monitoringDetails: MonitoringDetails = MonitoringDetails(
        durationOfProject = "Active",
        nextMonitoring = "12 Months",
        lastReported = "Monthly",
        currentProjectMembership = "Active"
    ),
    val sustainabilityMessage: String = "Your latest carbon project is then helping livelihoods and is making a tangible impact on climate change. Continue monitoring via satellite imagery and/or evidence for ongoing impact tracking."
) : Parcelable

@Parcelize
data class ProgressIndicator(
    val name: String,
    val percentage: Int
) : Parcelable

@Parcelize
data class EnvironmentalMetric(
    val name: String,
    val status: String
) : Parcelable

@Parcelize
data class CommunityBenefits(
    val familiesToSupported: Int,
    val jobsCreated: Int
) : Parcelable

@Parcelize
data class MonitoringDetails(
    val durationOfProject: String,
    val nextMonitoring: String,
    val lastReported: String,
    val currentProjectMembership: String
) : Parcelable
