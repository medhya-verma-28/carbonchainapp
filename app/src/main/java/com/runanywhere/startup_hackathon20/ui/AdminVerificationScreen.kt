package com.runanywhere.startup_hackathon20.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.runanywhere.startup_hackathon20.data.*
import com.runanywhere.startup_hackathon20.viewmodel.CarbonViewModel
import com.runanywhere.startup_hackathon20.viewmodel.UiState
import java.text.SimpleDateFormat
import java.util.*

// Dark Theme Colors  - Modern Dark with Glass Effect
private val DarkBackground = Color(0xFF0A0E27)
private val DarkSurface = Color(0xFF1A1F3A)
private val PrimaryOrange = Color(0xFFFF6B35)
private val AccentCyan = Color(0xFF22D3EE)
private val PrimaryGreen = Color(0xFF10B981)
private val PrimaryRed = Color(0xFFEF4444)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFB4C6CC)
private val GlassWhite = Color(0xFFFFFFFF)

// Glass Effect Modifier for this screen
private fun Modifier.verificationGlassEffect() = this
    .background(
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp)
    )
    .border(
        width = 1.dp,
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(16.dp)
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVerificationScreen(viewModel: CarbonViewModel) {
    val userSubmissions by viewModel.userSubmissions.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var selectedSubmission by remember { mutableStateOf<UserSubmission?>(null) }
    var showApproveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E27),
                        Color(0xFF1A1F3A),
                        Color(0xFF0F1629)
                    )
                )
            )
    ) {
        // Animated background blobs
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-100).dp, y = (-150).dp)
                .background(AccentCyan.copy(alpha = 0.1f), CircleShape)
                .blur(120.dp)
        )
        Box(
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = 100.dp)
                .background(PrimaryOrange.copy(alpha = 0.08f), CircleShape)
                .blur(120.dp)
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 100.dp)
                .background(PrimaryGreen.copy(alpha = 0.1f), CircleShape)
                .blur(120.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verificationGlassEffect()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Verification Portal",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Expert validation and quality assurance",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(AccentCyan.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AccentCyan,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Statistics
            item {
                val pendingCount = userSubmissions.count { it.status == SubmissionStatus.PENDING }
                val approvedCount =
                    userSubmissions.count { it.status == SubmissionStatus.APPROVED }
                val rejectedCount =
                    userSubmissions.count { it.status == SubmissionStatus.REJECTED }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VerificationStatCard(
                        title = "Pending",
                        value = pendingCount.toString(),
                        color = PrimaryOrange,
                        modifier = Modifier.weight(1f)
                    )
                    VerificationStatCard(
                        title = "Approved",
                        value = approvedCount.toString(),
                        color = PrimaryGreen,
                        modifier = Modifier.weight(1f)
                    )
                    VerificationStatCard(
                        title = "Rejected",
                        value = rejectedCount.toString(),
                        color = PrimaryRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Submissions List
            items(userSubmissions) { submission ->
                SubmissionCard(
                    submission = submission,
                    onClick = {
                        selectedSubmission = submission
                    }
                )
            }

            if (userSubmissions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = TextSecondary.copy(alpha = 0.3f)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No submissions to verify",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Detail Bottom Sheet
        selectedSubmission?.let { submission ->
            SubmissionDetailSheet(
                submission = submission,
                onDismiss = { selectedSubmission = null },
                onApprove = {
                    showApproveDialog = true
                },
                onReject = {
                    showRejectDialog = true
                }
            )
        }

        // Approve Dialog
        if (showApproveDialog && selectedSubmission != null) {
            ApproveDialog(
                submission = selectedSubmission!!,
                onDismiss = { showApproveDialog = false },
                onConfirm = { notes ->
                    viewModel.approveSubmission(selectedSubmission!!.id, notes)
                    showApproveDialog = false
                    selectedSubmission = null
                }
            )
        }

        // Reject Dialog
        if (showRejectDialog && selectedSubmission != null) {
            RejectDialog(
                submission = selectedSubmission!!,
                onDismiss = { showRejectDialog = false },
                onConfirm = { notes ->
                    viewModel.rejectSubmission(selectedSubmission!!.id, notes)
                    showRejectDialog = false
                    selectedSubmission = null
                }
            )
        }

        // Loading Overlay
        if (uiState is UiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = AccentCyan,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Processing...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun VerificationStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.verificationGlassEffect()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(Modifier.height(4.dp))
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun SubmissionCard(
    submission: UserSubmission,
    onClick: () -> Unit
) {
    val statusColor = when (submission.status) {
        SubmissionStatus.PENDING -> PrimaryOrange
        SubmissionStatus.APPROVED -> PrimaryGreen
        SubmissionStatus.REJECTED -> PrimaryRed
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .verificationGlassEffect()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            ) {
                if (submission.imageUrl != null) {
                    AsyncImage(
                        model = submission.imageUrl,
                        contentDescription = "Submission Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        tint = TextSecondary.copy(alpha = 0.3f)
                    )
                }
            }

            // Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ID: ${submission.id}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Surface(
                            color = statusColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                submission.status.name,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = statusColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = TextSecondary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            submission.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DataBadge(
                            label = "CO₂",
                            value = "${submission.co2Value} tons",
                            color = PrimaryGreen
                        )
                        DataBadge(
                            label = "Quality",
                            value = submission.dataQuality,
                            color = AccentCyan
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DataBadge(
    label: String,
    value: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$label: ",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Text(
                value,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubmissionDetailSheet(
    submission: UserSubmission,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        "Submission Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "ID: ${submission.id}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            // Image
            item {
                if (submission.imageUrl != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        AsyncImage(
                            model = submission.imageUrl,
                            contentDescription = "Submission Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Basic Info
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verificationGlassEffect()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailRow("Date", formatSubmissionDate(submission.submissionDate))
                        DetailRow("Location", submission.location)
                        DetailRow("Data Quality", submission.dataQuality)
                        DetailRow("Status", submission.status.name)
                    }
                }
            }

            // Analysis Summary
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verificationGlassEffect()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Analysis Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AnalysisMetricCard(
                                label = "CO₂",
                                value = "${submission.co2Value}",
                                unit = "tons",
                                color = PrimaryGreen,
                                modifier = Modifier.weight(1f)
                            )
                            AnalysisMetricCard(
                                label = "Hectares",
                                value = "${submission.hectaresValue}",
                                unit = "ha",
                                color = AccentCyan,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AnalysisMetricCard(
                                label = "Vegetation",
                                value = "${submission.vegetationCoverage}",
                                unit = "%",
                                color = PrimaryGreen,
                                modifier = Modifier.weight(1f)
                            )
                            AnalysisMetricCard(
                                label = "AI Confidence",
                                value = "${submission.aiConfidence}",
                                unit = "%",
                                color = PrimaryOrange,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Verification Checklist
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verificationGlassEffect()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = if (submission.gpsVerified && submission.satelliteDataVerified &&
                                    submission.imageQualityVerified && submission.coordinatesWithinRange
                                ) PrimaryGreen else PrimaryOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "Verification Checklist",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        ChecklistItem("GPS coordinates verified", submission.gpsVerified)
                        ChecklistItem(
                            "Satellite data cross-referenced",
                            submission.satelliteDataVerified
                        )
                        ChecklistItem("Image quality standards", submission.imageQualityVerified)
                        ChecklistItem(
                            "Coordinates within valid range",
                            submission.coordinatesWithinRange
                        )
                    }
                }
            }

            // AI Verification Results
            if (submission.aiVerificationTimestamp != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verificationGlassEffect()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (submission.aiLandscapeDetected) AccentCyan else PrimaryRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "AI Landscape Verification",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            DetailRow(
                                "Landscape Detected",
                                if (submission.aiLandscapeDetected) "Yes" else "No"
                            )
                            if (submission.aiLandscapeCategory.isNotEmpty()) {
                                DetailRow("Category", submission.aiLandscapeCategory.replace("_", " "))
                            }
                            DetailRow(
                                "AI Confidence",
                                "${String.format("%.1f", submission.aiConfidence)}%"
                            )
                            // AI-based recommendation
                            Spacer(Modifier.height(12.dp))
                            val recommendApproval = submission.aiLandscapeDetected && 
                                                    submission.aiConfidence >= 75.0
                            Surface(
                                color = if (recommendApproval) PrimaryGreen.copy(alpha = 0.15f) 
                                       else PrimaryOrange.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (recommendApproval) Icons.Default.Check else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (recommendApproval) PrimaryGreen else PrimaryOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        if (recommendApproval) 
                                            "AI recommends approval - landscape verified with high confidence"
                                        else 
                                            "Manual review required - low landscape confidence",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Notes
            if (submission.notes.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verificationGlassEffect()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                "Verification Notes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                submission.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Action Buttons
            if (submission.status == SubmissionStatus.PENDING) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReject,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PrimaryRed
                            ),
                            border = BorderStroke(1.dp, PrimaryRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Close, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Reject", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onApprove,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Check, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Approve & Publish", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
private fun AnalysisMetricCard(
    label: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = color.copy(alpha = 0.8f)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ChecklistItem(
    text: String,
    checked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    if (checked) PrimaryGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                    RoundedCornerShape(4.dp)
                )
                .border(
                    1.dp,
                    if (checked) PrimaryGreen else Color.White.copy(alpha = 0.3f),
                    RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (checked) TextPrimary else TextSecondary
        )
    }
}

@Composable
private fun ApproveDialog(
    submission: UserSubmission,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Approve and Publish to Blockchain",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "Complete all verification checks before approving submission",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Add notes or comments (optional)", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(notes) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(Modifier.width(8.dp))
                Text("Approve & Publish on Blockchain", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
            ) {
                Text("Cancel")
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun RejectDialog(
    submission: UserSubmission,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Reject Submission",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "Please provide a reason for rejection",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Reason for rejection*", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(notes) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                shape = RoundedCornerShape(8.dp),
                enabled = notes.isNotBlank()
            ) {
                Icon(Icons.Default.Close, null)
                Spacer(Modifier.width(8.dp))
                Text("Reject Submission", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
            ) {
                Text("Cancel")
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

private fun formatSubmissionDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    return sdf.format(Date(timestamp))
}
