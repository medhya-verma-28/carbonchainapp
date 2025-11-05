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
    val notes: String = ""
) : Parcelable

@Parcelize
enum class SubmissionStatus : Parcelable {
    PENDING,
    APPROVED,
    REJECTED
}
