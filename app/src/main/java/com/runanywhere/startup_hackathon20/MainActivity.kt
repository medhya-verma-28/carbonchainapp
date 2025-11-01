package com.runanywhere.startup_hackathon20

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.runanywhere.startup_hackathon20.data.*
import com.runanywhere.startup_hackathon20.ui.theme.Startup_hackathon20Theme
import com.runanywhere.startup_hackathon20.viewmodel.CarbonViewModel
import com.runanywhere.startup_hackathon20.viewmodel.UiState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private val viewModel: CarbonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Startup_hackathon20Theme {
                CarbonRegistryApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarbonRegistryApp(viewModel: CarbonViewModel) {
    var selectedScreen by remember { mutableStateOf(Screen.Dashboard) }
    val uiState by viewModel.uiState.collectAsState()

    // Show snackbar for UI state changes
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar((uiState as UiState.Success).message)
                viewModel.clearUiState()
            }

            is UiState.Error -> {
                snackbarHostState.showSnackbar((uiState as UiState.Error).message)
                viewModel.clearUiState()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Carbon Registry",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(selectedScreen) { selectedScreen = it }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedScreen) {
                Screen.Dashboard -> DashboardScreen(viewModel)
                Screen.Projects -> ProjectsScreen(viewModel)
                Screen.Credits -> CreditsScreen(viewModel)
                Screen.Wallet -> WalletScreen(viewModel)
            }

            // Loading overlay
            if (uiState is UiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedScreen: Screen, onScreenSelected: (Screen) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Screen.values().forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = selectedScreen == screen,
                onClick = { onScreenSelected(screen) }
            )
        }
    }
}

@Composable
fun DashboardScreen(viewModel: CarbonViewModel) {
    val projects by viewModel.projects.collectAsState()
    val credits by viewModel.credits.collectAsState()
    val wallet by viewModel.userWallet.collectAsState()
    val totalCo2 by viewModel.totalCo2Offset.collectAsState()
    val activeProjects by viewModel.activeProjectsCount.collectAsState()
    val availableCredits by viewModel.availableCreditsCount.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            "Welcome to Carbon Registry",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Track, trade, and retire carbon credits on blockchain",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Statistics Cards
        item {
            Text(
                "Global Impact",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "CO₂ Offset",
                    value = formatNumber(totalCo2) + " t",
                    icon = Icons.Default.Check,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Active Projects",
                    value = activeProjects.toString(),
                    icon = Icons.Default.Star,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Available Credits",
                    value = availableCredits.toString(),
                    icon = Icons.Default.Info,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Your Credits",
                    value = formatNumber(wallet?.totalCreditsOwned ?: 0.0),
                    icon = Icons.Default.Edit,
                    color = Color(0xFF9C27B0),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Featured Projects
        item {
            Text(
                "Featured Projects",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(projects.take(3)) { project ->
                    FeaturedProjectCard(project, onClick = {
                        viewModel.selectProject(project)
                    })
                }
            }
        }

        // Recent Credits
        item {
            Text(
                "Recent Credits",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(credits.take(3)) { credit ->
            CreditCard(credit, viewModel)
        }
    }
}

@Composable
fun ProjectsScreen(viewModel: CarbonViewModel) {
    val projects by viewModel.projects.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<ProjectType?>(null) }

    val filteredProjects = remember(searchQuery, selectedType, projects) {
        projects.filter { project ->
            val matchesSearch = searchQuery.isBlank() ||
                    project.name.contains(searchQuery, ignoreCase = true) ||
                    project.location.contains(searchQuery, ignoreCase = true)
            val matchesType = selectedType == null || project.type == selectedType
            matchesSearch && matchesType
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search and Filter Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search projects...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedType == null,
                        onClick = { selectedType = null },
                        label = { Text("All") }
                    )
                }
                items(ProjectType.values()) { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = if (selectedType == type) null else type },
                        label = {
                            Text(
                                type.name.replace("_", " ").lowercase()
                                    .replaceFirstChar { it.uppercase() })
                        }
                    )
                }
            }
        }

        // Projects List
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredProjects) { project ->
                ProjectCard(project, onClick = {
                    viewModel.selectProject(project)
                })
            }

            if (filteredProjects.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.Search,
                        message = "No projects found"
                    )
                }
            }
        }
    }
}

@Composable
fun CreditsScreen(viewModel: CarbonViewModel) {
    val credits by viewModel.credits.collectAsState()
    var selectedStatus by remember { mutableStateOf<CreditStatus?>(null) }

    val filteredCredits = remember(selectedStatus, credits) {
        if (selectedStatus == null) credits
        else credits.filter { it.status == selectedStatus }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Filter Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                "Carbon Credits",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { selectedStatus = null },
                        label = { Text("All") }
                    )
                }
                items(CreditStatus.values()) { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = {
                            selectedStatus = if (selectedStatus == status) null else status
                        },
                        label = { Text(status.name.replace("_", " ")) }
                    )
                }
            }
        }

        // Credits List
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredCredits) { credit ->
                CreditCard(credit, viewModel)
            }

            if (filteredCredits.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.Settings,
                        message = "No credits found"
                    )
                }
            }
        }
    }
}

@Composable
fun WalletScreen(viewModel: CarbonViewModel) {
    val wallet by viewModel.userWallet.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Wallet Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A237E)
                ),
                shape = RoundedCornerShape(16.dp)
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
                        Text(
                            "My Wallet",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "Address",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        wallet?.address?.take(20) + "..." ?: "No wallet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Owned",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                formatNumber(wallet?.totalCreditsOwned ?: 0.0) + " tCO₂",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Retired",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                formatNumber(wallet?.totalCreditsRetired ?: 0.0) + " tCO₂",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }
        }

        if (wallet == null) {
            item {
                Button(
                    onClick = { viewModel.createWallet() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Create Wallet")
                }
            }
        }

        // Transaction History
        item {
            Text(
                "Transaction History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (transactions.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.Info,
                    message = "No transactions yet"
                )
            }
        } else {
            items(transactions) { transaction ->
                TransactionCard(transaction)
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun FeaturedProjectCard(project: CarbonProject, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                getProjectTypeColor(project.type),
                                getProjectTypeColor(project.type).copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getProjectTypeIcon(project.type),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    project.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        project.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            formatNumber(project.impactMetrics.co2Reduced) + " tCO₂",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            "Offset",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            formatNumber(project.availableCredits),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Available",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectCard(project: CarbonProject, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(120.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                getProjectTypeColor(project.type),
                                getProjectTypeColor(project.type).copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getProjectTypeIcon(project.type),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    project.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        project.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Badge(
                        text = "${formatNumber(project.impactMetrics.co2Reduced)} tCO₂",
                        color = Color(0xFF4CAF50)
                    )
                    Badge(
                        text = "${formatNumber(project.availableCredits)} Available",
                        color = Color(0xFF2196F3)
                    )
                }
            }
        }
    }
}

@Composable
fun CreditCard(credit: CarbonCredit, viewModel: CarbonViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        credit.projectName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        credit.serialNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
                StatusChip(credit.status)
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem("Amount", "${formatNumber(credit.amount)} tCO₂")
                InfoItem("Price", "$${formatNumber(credit.price)}/t")
                InfoItem("Vintage", credit.vintage.toString())
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (credit.status == CreditStatus.ACTIVE) {
                    OutlinedButton(
                        onClick = { viewModel.purchaseCredit(credit) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Buy")
                    }
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Retire")
                    }
                } else {
                    Button(
                        onClick = { viewModel.verifyCredit(credit.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Check, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Verify on Blockchain")
                    }
                }
            }
        }
    }

    if (showDialog) {
        RetireDialog(
            credit = credit,
            onDismiss = { showDialog = false },
            onConfirm = { amount, reason ->
                viewModel.retireCredit(credit.id, amount, reason)
                showDialog = false
            }
        )
    }
}

@Composable
fun TransactionCard(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        getTransactionTypeColor(transaction.type).copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    getTransactionTypeIcon(transaction.type),
                    contentDescription = null,
                    tint = getTransactionTypeColor(transaction.type)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    transaction.type.name.replace("_", " "),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    formatDate(transaction.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    "Tx: ${transaction.transactionHash.take(16)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${formatNumber(transaction.amount)} tCO₂",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                StatusChip(transaction.status)
            }
        }
    }
}

@Composable
fun RetireDialog(
    credit: CarbonCredit,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var amount by remember { mutableStateOf(credit.amount.toString()) }
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Retire Carbon Credits") },
        text = {
            Column {
                Text("Retiring credits permanently removes them from circulation.")
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (tCO₂)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    amount.toDoubleOrNull()?.let { amt ->
                        if (amt > 0 && amt <= credit.amount) {
                            onConfirm(amt, reason)
                        }
                    }
                }
            ) {
                Text("Retire")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun StatusChip(status: CreditStatus) {
    val color = when (status) {
        CreditStatus.ACTIVE -> Color(0xFF4CAF50)
        CreditStatus.RETIRED -> Color(0xFF9E9E9E)
        CreditStatus.PENDING_VERIFICATION -> Color(0xFFFF9800)
        CreditStatus.EXPIRED -> Color(0xFFF44336)
        CreditStatus.CANCELLED -> Color(0xFF757575)
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StatusChip(status: TransactionStatus) {
    val color = when (status) {
        TransactionStatus.CONFIRMED -> Color(0xFF4CAF50)
        TransactionStatus.PENDING -> Color(0xFFFF9800)
        TransactionStatus.FAILED -> Color(0xFFF44336)
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmptyState(icon: ImageVector, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}

// Helper Functions
fun getProjectTypeColor(type: ProjectType): Color = when (type) {
    ProjectType.REFORESTATION -> Color(0xFF4CAF50)
    ProjectType.RENEWABLE_ENERGY -> Color(0xFFFFEB3B)
    ProjectType.ENERGY_EFFICIENCY -> Color(0xFF03A9F4)
    ProjectType.METHANE_CAPTURE -> Color(0xFFFF5722)
    ProjectType.OCEAN_CONSERVATION -> Color(0xFF2196F3)
    ProjectType.CARBON_CAPTURE -> Color(0xFF9C27B0)
    ProjectType.SUSTAINABLE_AGRICULTURE -> Color(0xFF8BC34A)
}

fun getProjectTypeIcon(type: ProjectType): ImageVector = when (type) {
    ProjectType.REFORESTATION -> Icons.Default.Star
    ProjectType.RENEWABLE_ENERGY -> Icons.Default.Favorite
    ProjectType.ENERGY_EFFICIENCY -> Icons.Default.Settings
    ProjectType.METHANE_CAPTURE -> Icons.Default.Info
    ProjectType.OCEAN_CONSERVATION -> Icons.Default.Star
    ProjectType.CARBON_CAPTURE -> Icons.Default.Settings
    ProjectType.SUSTAINABLE_AGRICULTURE -> Icons.Default.Star
}

fun getTransactionTypeColor(type: TransactionType): Color = when (type) {
    TransactionType.ISSUANCE -> Color(0xFF4CAF50)
    TransactionType.TRANSFER -> Color(0xFF2196F3)
    TransactionType.RETIREMENT -> Color(0xFF9E9E9E)
    TransactionType.VERIFICATION -> Color(0xFFFF9800)
}

fun getTransactionTypeIcon(type: TransactionType): ImageVector = when (type) {
    TransactionType.ISSUANCE -> Icons.Default.Add
    TransactionType.TRANSFER -> Icons.AutoMirrored.Filled.ArrowForward
    TransactionType.RETIREMENT -> Icons.Default.Delete
    TransactionType.VERIFICATION -> Icons.Default.Check
}

fun formatNumber(number: Double): String {
    return NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }.format(number)
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US)
    return sdf.format(Date(timestamp))
}

enum class Screen(val title: String, val icon: ImageVector) {
    Dashboard("Dashboard", Icons.Default.Home),
    Projects("Projects", Icons.Default.Star),
    Credits("Credits", Icons.Default.Info),
    Wallet("Wallet", Icons.Default.Settings)
}