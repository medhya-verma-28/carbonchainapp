package com.runanywhere.startup_hackathon20

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.runanywhere.startup_hackathon20.data.*
import com.runanywhere.startup_hackathon20.ui.theme.Startup_hackathon20Theme
import com.runanywhere.startup_hackathon20.ui.AdminVerificationScreen
import com.runanywhere.startup_hackathon20.viewmodel.CarbonViewModel
import com.runanywhere.startup_hackathon20.viewmodel.UiState
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Dark Theme Colors - Green-Blue Gradient Theme (Updated for better text visibility)
val DarkBackground = Color(0xFF07151A)
val DarkSurface = Color(0xFF0A2326)
val PrimaryGreen = Color(0xFF0EA676)
val PrimaryTeal = Color(0xFF109893)
val PrimaryBlue = Color(0xFF0894B4)
val SecondaryGreen = Color(0xFF036D56)
val AccentCyan = Color(0xFF14C9E6)
val AccentEmerald = Color(0xFF1EC197)
val GlassWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA6B9C4)

class MainActivity : ComponentActivity() {
    private val viewModel: CarbonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Startup_hackathon20Theme(darkTheme = true) {
                MainApp(viewModel)
            }
        }
    }
}

// Glass Effect Modifier
fun Modifier.glassEffect() = this
    .background(
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp)
    )
    .border(
        width = 1.dp,
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(20.dp)
    )

@Composable
fun MainApp(viewModel: CarbonViewModel) {
    val authState by viewModel.authState.collectAsState()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        when {
            !authState.isAuthenticated -> {
                // Show Login Screen first
                LoginScreen(viewModel = viewModel)
            }

            authState.userType == CarbonViewModel.UserType.USER -> {
                // User sees Blue Carbon Monitor homepage
                BlueCarbonMonitorHomepage(
                    viewModel = viewModel
                )
            }

            authState.userType == CarbonViewModel.UserType.ADMIN -> {
                // Admin sees full Carbon Registry App with verification portal
                CarbonRegistryApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: CarbonViewModel,
    onBack: (() -> Unit)? = null
) {
    var selectedLoginType by remember { mutableStateOf<LoginType?>(null) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Error -> {
                snackbarHostState.showSnackbar((uiState as UiState.Error).message)
                viewModel.clearUiState()
            }
            else -> {}
        }
    }
    
    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF07151A),
                            Color(0xFF0A2326),
                            Color(0xFF0C3339),
                            Color(0xFF0A2832)
                        )
                    )
                )
                .padding(padding)
        ) {
            // Animated gradient blobs for glass effect background
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .offset(x = (-100).dp, y = (-150).dp)
                    .background(PrimaryTeal.copy(alpha = 0.3f), CircleShape)
                    .blur(100.dp)
            )
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 100.dp, y = 50.dp)
                    .background(PrimaryBlue.copy(alpha = 0.3f), CircleShape)
                    .blur(100.dp)
            )
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-50).dp, y = 100.dp)
                    .background(PrimaryGreen.copy(alpha = 0.2f), CircleShape)
                    .blur(100.dp)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Top Bar Back Button
                if (onBack != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(onClick = { onBack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                "Back",
                                tint = TextPrimary
                            )
                        }
                    }
                }
                // Logo and Title
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(Modifier.height(16.dp))
                
                Text(
                    "Carbon Registry",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Text(
                    "Blockchain Carbon Credit Platform",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                
                Spacer(Modifier.height(48.dp))
                
                // Login Type Selection or Login Form
                AnimatedContent(
                    targetState = selectedLoginType,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) + 
                        slideInVertically { it / 2 } togetherWith 
                        fadeOut(animationSpec = tween(500))
                    },
                    label = "login_type"
                ) { loginType ->
                    when (loginType) {
                        null -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    "Select Login Type",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                
                                Spacer(Modifier.height(8.dp))
                                
                                GlassLoginTypeCard(
                                    icon = Icons.Default.Person,
                                    title = "User Login",
                                    description = "Access carbon credits and manage your portfolio",
                                    accentColor = PrimaryGreen,
                                    onClick = { selectedLoginType = LoginType.USER }
                                )
                                
                                GlassLoginTypeCard(
                                    icon = Icons.Default.Settings,
                                    title = "Admin Login",
                                    description = "Manage projects, verify credits, and oversee operations",
                                    accentColor = AccentCyan,
                                    onClick = { selectedLoginType = LoginType.ADMIN }
                                )
                            }
                        }
                        else -> {
                            // Login Form with Glass Effect
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassEffect()
                                    .animateContentSize()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(onClick = { 
                                            selectedLoginType = null
                                            isRegistering = false
                                            username = ""
                                            password = ""
                                            email = ""
                                        }) {
                                            Icon(
                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                "Back",
                                                tint = TextPrimary
                                            )
                                        }
                                        
                                        Icon(
                                            if (loginType == LoginType.ADMIN) Icons.Default.Settings else Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (loginType == LoginType.ADMIN) AccentCyan else PrimaryGreen,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    
                                    Spacer(Modifier.height(8.dp))
                                    
                                    Text(
                                        if (isRegistering) "Create Account" else "${loginType.name.lowercase().replaceFirstChar { it.uppercase() }} Login",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    
                                    Spacer(Modifier.height(24.dp))
                                    
                                    // Username Field
                                    OutlinedTextField(
                                        value = username,
                                        onValueChange = { username = it },
                                        label = { Text("Username", color = TextSecondary) },
                                        leadingIcon = { Icon(Icons.Default.Person, null, tint = TextSecondary) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = PrimaryTeal,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                                        )
                                    )
                                    
                                    Spacer(Modifier.height(16.dp))
                                    
                                    if (isRegistering && loginType == LoginType.USER) {
                                        OutlinedTextField(
                                            value = email,
                                            onValueChange = { email = it },
                                            label = { Text("Email", color = TextSecondary) },
                                            leadingIcon = { Icon(Icons.Default.Email, null, tint = TextSecondary) },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                focusedBorderColor = PrimaryTeal,
                                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                                            )
                                        )
                                        
                                        Spacer(Modifier.height(16.dp))
                                    }
                                    
                                    // Password Field
                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = { Text("Password", color = TextSecondary) },
                                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = TextSecondary) },
                                        trailingIcon = {
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                    if (passwordVisible) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    "Toggle",
                                                    tint = TextSecondary
                                                )
                                            }
                                        },
                                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = PrimaryTeal,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                                        )
                                    )
                                    
                                    Spacer(Modifier.height(24.dp))
                                    
                                    // Login/Register Button
                                    Button(
                                        onClick = {
                                            if (isRegistering && loginType == LoginType.USER) {
                                                viewModel.register(username, email, password)
                                            } else {
                                                viewModel.login(username, password, loginType == LoginType.ADMIN)
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (loginType == LoginType.ADMIN) AccentCyan else PrimaryGreen
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        enabled = username.isNotBlank() && password.isNotBlank() && 
                                                  (!isRegistering || !email.isBlank() || loginType == LoginType.ADMIN)
                                    ) {
                                        if (uiState is UiState.Loading) {
                                            CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        } else {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    if (isRegistering) "Register" else "Login",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                                            }
                                        }
                                    }
                                    
                                    if (loginType == LoginType.USER) {
                                        Spacer(Modifier.height(16.dp))
                                        
                                        TextButton(
                                            onClick = { 
                                                isRegistering = !isRegistering
                                                password = ""
                                                email = ""
                                            }
                                        ) {
                                            Text(
                                                if (isRegistering) "Already have an account? Login" else "Don't have an account? Register",
                                                color = PrimaryGreen
                                            )
                                        }
                                    }
                                    
                                    if (loginType == LoginType.ADMIN) {
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Default: admin / admin123",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(32.dp))
                
                Text(
                    "Built with ❤️ for a sustainable future",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun GlassLoginTypeCard(
    icon: ImageVector,
    title: String,
    description: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassEffect()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(accentColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = accentColor
            )
        }
    }
}

@Composable
fun BlueCarbonMonitorHomepage(
    viewModel: CarbonViewModel
) {
    val context = LocalContext.current
    var selectedSite by remember { mutableStateOf("Mangrove Restoration Site") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var latitude by remember { mutableStateOf<String?>(null) }
    var longitude by remember { mutableStateOf<String?>(null) }
    var isGettingLocation by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var showPhotoPreview by remember { mutableStateOf(false) }
    var showHistoryDrawer by remember { mutableStateOf(false) }
    var selectedPendingSubmission by remember { mutableStateOf<UserSubmission?>(null) }
    var selectedApprovedRegistry by remember { mutableStateOf<CarbonRegistrySubmission?>(null) }

    // Filter state for history drawer
    var historyFilter by remember { mutableStateOf<SubmissionStatus?>(null) }

    // Multi-step portal flow
    var showBlockchainRegistryForm by remember { mutableStateOf(false) }
    var showSmartContractsPortal by remember { mutableStateOf(false) }
    var showCarbonMarketplace by remember { mutableStateOf(false) }
    var showImpactDashboard by remember { mutableStateOf(false) }
    var registryFormData by remember { mutableStateOf(BlockchainRegistryForm()) }
    var smartContractData by remember { mutableStateOf<SmartContractData?>(null) }
    var marketplaceData by remember { mutableStateOf<CarbonMarketplace?>(null) }
    var impactDashboardData by remember { mutableStateOf<ImpactDashboardData?>(null) }

    // Payment processing state
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentProgress by remember { mutableStateOf(0f) }
    var paymentStatus by remember { mutableStateOf("") }

    // For gallery picker
    var galleryPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // For carbon credits profile section
    val wallet by viewModel.userWallet.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Get user's carbon registries for history
    val userSubmissions by viewModel.userSubmissions.collectAsState()
    val pendingSubmissions = remember(authState.username, userSubmissions) {
        derivedStateOf {
            userSubmissions.filter { it.submitterName == authState.username }
        }
    }
    val carbonRegistries = remember(authState.username) {
        derivedStateOf { viewModel.getUserCarbonRegistries() }
    }

    // Show success/error message and reset form if needed
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                Toast.makeText(context, (uiState as UiState.Success).message, Toast.LENGTH_LONG)
                    .show()
                viewModel.clearUiState()
                // Reset after successful submission
                photoUri = null
                showPhotoPreview = false
                latitude = null
                longitude = null
            }

            is UiState.Error -> {
                Toast.makeText(context, (uiState as UiState.Error).message, Toast.LENGTH_LONG)
                    .show()
                viewModel.clearUiState()
            }

            else -> {}
        }
    }

    // Camera and gallery/location logic as previously
    val photoFile = remember {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "carbon_photo_$ts.jpg"
        File(context.cacheDir, fileName)
    }
    val uri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri = uri
            showPhotoPreview = true
            Toast.makeText(context, "Photo captured!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to capture photo", Toast.LENGTH_SHORT).show()
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { selectedUri: Uri? ->
        if (selectedUri != null) {
            photoUri = selectedUri
            showPhotoPreview = true
            Toast.makeText(context, "Photo selected from gallery!", Toast.LENGTH_SHORT).show()
        }
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            getLocation(context) { lat, lon ->
                latitude = lat
                longitude = lon
                isGettingLocation = false
                Toast.makeText(context, "Location updated!", Toast.LENGTH_SHORT).show()
            }
        } else {
            isGettingLocation = false
            Toast.makeText(context, "Location permission required", Toast.LENGTH_SHORT).show()
        }
    }
    fun capturePhoto() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                cameraLauncher.launch(uri)
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    fun pickFromGallery() {
        galleryLauncher.launch("image/*")
    }
    fun updateLocation() {
        isGettingLocation = true
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLocation(context) { lat, lon ->
                    latitude = lat
                    longitude = lon
                    isGettingLocation = false
                    Toast.makeText(context, "Location updated!", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
    fun uploadToAnalysis() {
        if (photoUri == null) {
            Toast.makeText(context, "Please capture or select a photo first", Toast.LENGTH_SHORT)
                .show()
            return
        }
        viewModel.submitCarbonRegistry(
            photoUri = photoUri.toString(),
            latitude = latitude,
            longitude = longitude,
            selectedSite = selectedSite
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A2326),
                        Color(0xFF07151A),
                        Color(0xFF0A2326)
                    )
                )
            )
    ) {
        // Impact Dashboard Portal (screen-level)
        if (showImpactDashboard && impactDashboardData != null) {
            Dialog(
                onDismissRequest = {
                    // Mark as completed and close
                    selectedApprovedRegistry?.id?.let { submissionId ->
                        viewModel.markSubmissionAsCompleted(submissionId)
                    }
                    showImpactDashboard = false
                    impactDashboardData = null
                    selectedApprovedRegistry = null
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF07151A),
                                    Color(0xFF0A2326),
                                    Color(0xFF0C3339),
                                    Color(0xFF0A2832)
                                )
                            )
                        )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Animated gradient blobs
                        Box(
                            modifier = Modifier
                                .size(400.dp)
                                .offset(x = (-100).dp, y = (-150).dp)
                                .background(PrimaryTeal.copy(alpha = 0.3f), CircleShape)
                                .blur(100.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(350.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 100.dp, y = 50.dp)
                                .background(PrimaryBlue.copy(alpha = 0.3f), CircleShape)
                                .blur(100.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(300.dp)
                                .align(Alignment.BottomStart)
                                .offset(x = (-50).dp, y = 100.dp)
                                .background(PrimaryGreen.copy(alpha = 0.2f), CircleShape)
                                .blur(100.dp)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp)
                        ) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Impact Dashboard",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        "Global Blue Carbon Environmental Monitoring",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                                IconButton(onClick = {
                                    selectedApprovedRegistry?.id?.let { submissionId ->
                                        viewModel.markSubmissionAsCompleted(submissionId)
                                    }
                                    showImpactDashboard = false
                                    impactDashboardData = null
                                    selectedApprovedRegistry = null
                                }) {
                                    Icon(Icons.Default.Close, "Close", tint = TextPrimary)
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            // Project Overview Cards
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .glassEffect()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "${impactDashboardData!!.carbonReduced}",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen
                                        )
                                        Text(
                                            "Carbon Reduced (tCO₂)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .glassEffect()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "${formatNumber(impactDashboardData!!.marketValue)}",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentCyan
                                        )
                                        Text(
                                            "Market Value",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Monitoring Period
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassEffect()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            "Period: ${impactDashboardData!!.monitoringPeriod}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextPrimary
                                        )
                                        Text(
                                            "Monitoring Period",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            impactDashboardData!!.monitoringDate,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextPrimary
                                        )
                                        Text(
                                            "Active",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PrimaryGreen
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Monitoring Method Tabs (Static for now)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    color = PrimaryGreen,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "M1",
                                        modifier = Modifier.padding(8.dp),
                                        textAlign = TextAlign.Center,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    color = Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "M2",
                                        modifier = Modifier.padding(8.dp),
                                        textAlign = TextAlign.Center,
                                        color = TextSecondary
                                    )
                                }
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    color = Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "M3",
                                        modifier = Modifier.padding(8.dp),
                                        textAlign = TextAlign.Center,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Impact Metrics
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .glassEffect()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = AccentCyan,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "${formatNumber(impactDashboardData!!.carbonSequestered)}kg",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            "Carbon Sequestered",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .glassEffect()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = PrimaryGreen,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "${formatNumber(impactDashboardData!!.co2eReduction)}kg",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            "CO2e Reduction",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .glassEffect()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = AccentEmerald,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "${formatNumber(impactDashboardData!!.peopleImpacted.toDouble())}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            "People Impacted",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .glassEffect()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.Face,
                                            contentDescription = null,
                                            tint = PrimaryTeal,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "${impactDashboardData!!.treesPlanted}/100",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            "Trees Planted",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Progress Indicators
                            Text(
                                "Progress Indicators",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassEffect()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    impactDashboardData!!.progressIndicators.forEach { indicator ->
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    indicator.name,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = TextPrimary
                                                )
                                                Text(
                                                    "${indicator.percentage}%",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = PrimaryGreen
                                                )
                                            }
                                            Spacer(Modifier.height(4.dp))
                                            LinearProgressIndicator(
                                                progress = indicator.percentage / 100f,
                                                modifier = Modifier.fillMaxWidth(),
                                                color = PrimaryGreen,
                                                trackColor = Color.White.copy(alpha = 0.1f)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Environmental Health
                            Text(
                                "Environmental Health",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassEffect()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    impactDashboardData!!.environmentalHealth.forEach { metric ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = when (metric.status) {
                                                        "Sustainable" -> PrimaryGreen
                                                        "Good" -> AccentCyan
                                                        "Recovering" -> Color(0xFFFF9800)
                                                        else -> TextSecondary
                                                    },
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    metric.name,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = TextPrimary
                                                )
                                            }
                                            Surface(
                                                color = when (metric.status) {
                                                    "Sustainable" -> PrimaryGreen.copy(alpha = 0.2f)
                                                    "Good" -> AccentCyan.copy(alpha = 0.2f)
                                                    "Recovering" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                                    else -> Color.Gray.copy(alpha = 0.2f)
                                                },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    metric.status,
                                                    modifier = Modifier.padding(
                                                        horizontal = 8.dp,
                                                        vertical = 4.dp
                                                    ),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = when (metric.status) {
                                                        "Sustainable" -> PrimaryGreen
                                                        "Good" -> AccentCyan
                                                        "Recovering" -> Color(0xFFFF9800)
                                                        else -> TextSecondary
                                                    },
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Community Benefits
                            Text(
                                "Community Benefits",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .glassEffect()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "${impactDashboardData!!.communityBenefits.familiesToSupported}",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentEmerald
                                        )
                                        Text(
                                            "Families Supported",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .glassEffect()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "${impactDashboardData!!.communityBenefits.jobsCreated}",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryTeal
                                        )
                                        Text(
                                            "Jobs Created",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Monitoring Details
                            Text(
                                "Monitoring Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassEffect()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Duration of Project",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary
                                        )
                                        Text(
                                            impactDashboardData!!.monitoringDetails.durationOfProject,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Next Monitoring",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary
                                        )
                                        Text(
                                            impactDashboardData!!.monitoringDetails.nextMonitoring,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Last Reported",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary
                                        )
                                        Text(
                                            impactDashboardData!!.monitoringDetails.lastReported,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Current Project Membership",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary
                                        )
                                        Text(
                                            impactDashboardData!!.monitoringDetails.currentProjectMembership,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Sustainability Message
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        AccentEmerald.copy(alpha = 0.15f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = AccentEmerald,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Column {
                                        Text(
                                            "Project Sustainability Developed!",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentEmerald
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            impactDashboardData!!.sustainabilityMessage,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            "View Detailed Analysis feature coming soon!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Info, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "View Detailed Analysis",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Button(
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            "Start New Monitoring Project feature coming soon!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Start New Monitoring Project",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Close Button
                            Button(
                                onClick = {
                                    // Mark as completed and close
                                    selectedApprovedRegistry?.id?.let { submissionId ->
                                        viewModel.markSubmissionAsCompleted(submissionId)
                                    }
                                    showImpactDashboard = false
                                    impactDashboardData = null
                                    selectedApprovedRegistry = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.1f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Complete & Close",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Main Content Area and top bar - BELOW History Drawer
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar - Centered
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showHistoryDrawer = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            "History",
                            tint = AccentCyan
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Blue Carbon Monitor",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            selectedSite,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            "Logout",
                            tint = TextPrimary
                        )
                    }
                }
            }

            // Main Content Area - Centered
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // Centered Column with fixed width for all cards
                Column(
                    modifier = Modifier
                        .width(320.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Photo Documentation Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassEffect()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Face,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Photo Documentation",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            if (photoUri != null && showPhotoPreview) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = "Captured Photo",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        showPhotoPreview = false
                                        photoUri = null
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF44336)
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text("Remove", style = MaterialTheme.typography.bodySmall)
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { capturePhoto() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PrimaryGreen
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(8.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.CameraAlt,
                                                null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                "Capture\nphoto",
                                                style = MaterialTheme.typography.labelSmall,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                    Button(
                                        onClick = { pickFromGallery() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PrimaryBlue
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(8.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Photo,
                                                null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                "Upload\nfrom\nGallery",
                                                style = MaterialTheme.typography.labelSmall,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Upload to Analysis Button
                    Button(
                        onClick = { uploadToAnalysis() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentEmerald
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isUploading && photoUri != null
                    ) {
                        if (uiState is UiState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Uploading...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Send, null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Upload to Analysis",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Location Data Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassEffect()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Location Data",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Text(
                                "Coordinates:",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )

                            if (latitude != null && longitude != null) {
                                Text(
                                    "$latitude, $longitude",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                            } else {
                                Text(
                                    "Not available",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }

                            Button(
                                onClick = { updateLocation() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryBlue
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                enabled = !isGettingLocation
                            ) {
                                if (isGettingLocation) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text("Locating...", style = MaterialTheme.typography.bodySmall)
                                } else {
                                    Icon(
                                        Icons.Default.Refresh,
                                        null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "Update Location",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    // Collection Status Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassEffect()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Call,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Collection Status",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Text(
                                "Points collected:",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            if (latitude != null && longitude != null) PrimaryGreen else Color.Gray,
                                            CircleShape
                                        )
                                )
                                Text(
                                    if (latitude != null && longitude != null) "GPS coordinates" else "GPS pending",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            if (photoUri != null) PrimaryGreen else Color.Gray,
                                            CircleShape
                                        )
                                )
                                Text(
                                    if (photoUri != null) "Photo captured" else "Awaiting photo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    // User Profile Section: Carbon Credits
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassEffect()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    "Profile",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            // Username
                            Text(
                                authState.username.orEmpty(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            // Email
                            if (!authState.email.isNullOrEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.Email,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        authState.email.orEmpty(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            // Credits Info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "Owned",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                    Text(
                                        formatNumber(wallet?.totalCreditsOwned ?: 0.0) + " tCO₂",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "Retired",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                    Text(
                                        formatNumber(wallet?.totalCreditsRetired ?: 0.0) + " tCO₂",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryTeal
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            // Wallet Address
                            Text(
                                "Wallet: " + (wallet?.address?.take(20) ?: "Not created") + "...",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // History Drawer - ABOVE Main Content with completely opaque background
        AnimatedVisibility(
            visible = showHistoryDrawer,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Scrim overlay to block and dim background content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.9f))
                        .clickable(
                            onClick = { showHistoryDrawer = false },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )

                // Drawer surface - completely opaque with dark green background
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(340.dp)
                        .align(Alignment.CenterStart)
                        .background(Color.Black) // First layer: solid black
                        .background(DarkBackground) // Second layer: solid dark background
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = DarkBackground, // Completely opaque dark background
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkBackground) // Third layer inside column
                            .padding(20.dp)
                    ) {
                        // Drawer Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = AccentCyan,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    "History",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            IconButton(onClick = { showHistoryDrawer = false }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close history",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Filter Buttons Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = historyFilter == null,
                                onClick = { historyFilter = null },
                                label = {
                                    Text(
                                        "All",
                                        color = if (historyFilter == null) PrimaryGreen else TextSecondary
                                    )
                                }
                            )
                            FilterChip(
                                selected = historyFilter == SubmissionStatus.APPROVED,
                                onClick = {
                                    historyFilter =
                                        if (historyFilter == SubmissionStatus.APPROVED) null else SubmissionStatus.APPROVED
                                },
                                label = {
                                    Text(
                                        "Approved",
                                        color = if (historyFilter == SubmissionStatus.APPROVED) PrimaryGreen else TextSecondary
                                    )
                                }
                            )
                            FilterChip(
                                selected = historyFilter == SubmissionStatus.PENDING,
                                onClick = {
                                    historyFilter =
                                        if (historyFilter == SubmissionStatus.PENDING) null else SubmissionStatus.PENDING
                                },
                                label = {
                                    Text(
                                        "Pending",
                                        color = if (historyFilter == SubmissionStatus.PENDING) AccentCyan else TextSecondary
                                    )
                                }
                            )
                            FilterChip(
                                selected = historyFilter == SubmissionStatus.REJECTED,
                                onClick = {
                                    historyFilter =
                                        if (historyFilter == SubmissionStatus.REJECTED) null else SubmissionStatus.REJECTED
                                },
                                label = {
                                    Text(
                                        "Rejected",
                                        color = if (historyFilter == SubmissionStatus.REJECTED) Color(
                                            0xFFEF4444
                                        ) else TextSecondary
                                    )
                                }
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // List of Registry Submissions with filters applied
                        val filteredPending =
                            if (historyFilter == null) pendingSubmissions.value
                            else pendingSubmissions.value.filter { it.status == historyFilter }
                        val filteredRegistries =
                            if (historyFilter == null) carbonRegistries.value
                            else carbonRegistries.value.filter { registry ->
                                // Only show "Approved" registry if filter is set to APPROVED, otherwise empty
                                historyFilter == SubmissionStatus.APPROVED
                            }

                        val totalFiltered =
                            filteredPending.size + filteredRegistries.size

                        if (totalFiltered == 0) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        if (historyFilter == null) "No submissions yet"
                                        else "No ${
                                            historyFilter?.name?.lowercase()
                                                ?.replaceFirstChar { it.uppercase() }
                                        } submissions found",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Upload your first carbon registry data",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Show filtered pending submissions
                                items(filteredPending) { submission ->
                                    HistoryItemCard(
                                        id = submission.id,
                                        location = submission.location,
                                        date = formatDate(submission.submissionDate),
                                        status = submission.status.name,
                                        statusColor = when (submission.status) {
                                            SubmissionStatus.PENDING -> AccentCyan
                                            SubmissionStatus.APPROVED -> PrimaryGreen
                                            SubmissionStatus.REJECTED -> Color(0xFFEF4444)
                                        },
                                        onClick = {
                                            selectedPendingSubmission = submission
                                            showHistoryDrawer = false
                                        }
                                    )
                                }
                                // Show filtered approved/registered submissions only if filter allows
                                items(filteredRegistries) { registry ->
                                    HistoryItemCard(
                                        id = registry.id,
                                        location = registry.location,
                                        date = formatDate(registry.submissionDate),
                                        status = "REGISTERED",
                                        statusColor = PrimaryGreen,
                                        onClick = {
                                            selectedApprovedRegistry = registry
                                            registryFormData = BlockchainRegistryForm()
                                            showHistoryDrawer = false
                                            showBlockchainRegistryForm = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
        
    // Dialogs Section - Above everything else at screen level
        // Pending Submission Details Dialog
        if (selectedPendingSubmission != null) {
            // Get fresh submission data from the updated list
            val currentSubmission = pendingSubmissions.value.find { 
                it.id == selectedPendingSubmission?.id 
            } ?: selectedPendingSubmission

            Dialog(
                onDismissRequest = { selectedPendingSubmission = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight(),
                    color = DarkSurface,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Submission Details",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { selectedPendingSubmission = null }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = TextPrimary
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        if (currentSubmission?.status == SubmissionStatus.PENDING) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        AccentCyan.copy(alpha = 0.15f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = AccentCyan,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        "⏳ Awaiting Verification",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Your carbon registry request is pending admin approval. Please check back later.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else if (currentSubmission?.status == SubmissionStatus.APPROVED) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        PrimaryGreen.copy(alpha = 0.15f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        "✅ Submission Approved!",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Congratulations! Your carbon registry submission has been approved by the admin. You can now proceed with blockchain registration.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else if (currentSubmission?.status == SubmissionStatus.REJECTED) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Color(0xFFEF4444).copy(alpha = 0.15f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        "❌ Submission Rejected",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Your submission did not meet verification requirements.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                    if (!currentSubmission?.notes.isNullOrEmpty()) {
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Reason: ${currentSubmission?.notes}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Submission Info
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color.White.copy(alpha = 0.05f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoRow("ID", currentSubmission?.id?.take(16) + "...")
                            InfoRow("Location", currentSubmission?.location ?: "-")
                            InfoRow(
                                "Submitted",
                                currentSubmission?.submissionDate?.let { formatDate(it) }
                                    ?: "-"
                            )
                            InfoRow("Status", currentSubmission?.status?.name ?: "-")
                        }

                        if (currentSubmission?.imageUrl != null) {
                            Spacer(Modifier.height(8.dp))
                            AsyncImage(
                                model = currentSubmission?.imageUrl,
                                contentDescription = "Photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        // Show "Proceed to Blockchain Registry" button if APPROVED
                        if (currentSubmission?.status == SubmissionStatus.APPROVED) {
                            // Check if blockchain registry is completed
                            if (currentSubmission?.blockchainRegistryCompleted == true) {
                                // Show completion message
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            PrimaryGreen.copy(alpha = 0.15f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(16.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = PrimaryGreen,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(Modifier.height(12.dp))
                                        Text(
                                            "✅ Blockchain Registry Completed!",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Your carbon credit has been successfully registered on the blockchain with all verification details.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                        if (currentSubmission?.completionDate != null) {
                                            Spacer(Modifier.height(8.dp))
                                            Text(
                                                "Completed: ${formatDate(currentSubmission?.completionDate!!)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                            } else {
                                // Show "Proceed to Blockchain Registry" button
                                Button(
                                    onClick = {
                                        // Convert the approved submission to a CarbonRegistrySubmission
                                        val registrySubmission = CarbonRegistrySubmission(
                                            id = currentSubmission?.id ?: "",
                                            registrationStatus = RegistrationStatus.REGISTERED,
                                            blockNumber = "12,346,678",
                                            creditAmount = currentSubmission?.co2Value
                                                ?: 2.3,
                                            projectArea = "${currentSubmission?.hectaresValue ?: 1.2} hectares",
                                            vintageYear = 2023,
                                            verificationDate = "10/10/2023",
                                            transactionHash = "0x7a3f2b2c4a5d4f4b2c3a2b1c5d4f2a1b6c7d8e9f1a2b3c4d5e6f7a8b9c0d1e2f",
                                            contractAddress = "0xabc123def456",
                                            network = "BNB",
                                            tokenStandard = "ERC-20",
                                            auditTrail = listOf(
                                                AuditItem("Initial assessment completed", true),
                                                AuditItem("Scientific analysis verified", true),
                                                AuditItem("Expert verification approved", true),
                                                AuditItem("Third-party compliance confirmed", true),
                                                AuditItem("Blockchain registration complete", true)
                                            ),
                                            registryNotes = "",
                                            imageUrl = currentSubmission?.imageUrl,
                                            location = currentSubmission?.location ?: "",
                                            coordinates = currentSubmission?.coordinates,
                                            submissionDate = currentSubmission?.submissionDate
                                                ?: System.currentTimeMillis(),
                                            submitterName = currentSubmission?.submitterName
                                                ?: "",
                                            submitterEmail = currentSubmission?.submitterEmail
                                                ?: "",
                                            status = SubmissionStatus.APPROVED
                                        )
                                        selectedApprovedRegistry = registrySubmission
                                        registryFormData = BlockchainRegistryForm()
                                        selectedPendingSubmission = null
                                        showBlockchainRegistryForm = true
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.ArrowForward, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Proceed to Blockchain Registry",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }

                        Button(
                            onClick = { selectedPendingSubmission = null },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentSubmission?.status == SubmissionStatus.APPROVED)
                                Color.White.copy(alpha = 0.1f) else PrimaryGreen
                            )
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }

        // Blockchain Registry Form Portal (screen-level)
        if (selectedApprovedRegistry != null && showBlockchainRegistryForm) {
            BlockchainRegistryFormPortal(
                registry = selectedApprovedRegistry!!,
                formData = registryFormData,
                onFormDataChange = { registryFormData = it },
                onDismiss = {
                    showBlockchainRegistryForm = false
                    selectedApprovedRegistry = null
                    registryFormData = BlockchainRegistryForm()
                },
                onSubmit = {
                    // Generate smart contract data
                    smartContractData = SmartContractData(
                        transactionStatus = "Completed",
                        carbonCreditTokens = registryFormData.creditAmount.toDoubleOrNull() ?: 2.3,
                        tokenStandard = "ERC-20",
                        network = "BNB",
                        contractAddress = "0xabc123...def789",
                        deploymentDate = "10/10/2023",
                        gasUsed = "340,459",
                        contractVerification = listOf(
                            ContractVerificationItem("Source code verified", true),
                            ContractVerificationItem("Security audit passed", true),
                            ContractVerificationItem("Compliance checks completed", true)
                        )
                    )
                    showBlockchainRegistryForm = false
                    showSmartContractsPortal = true
                }
            )
        }

        // Smart Contracts Portal (screen-level)
        if (showSmartContractsPortal && smartContractData != null) {
            Dialog(
                onDismissRequest = {
                    showSmartContractsPortal = false
                    smartContractData = null
                    selectedApprovedRegistry = null
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF07151A),
                                    Color(0xFF0A2326),
                                    Color(0xFF0C3339),
                                    Color(0xFF0A2832)
                                )
                            )
                        )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Animated gradient blobs
                        Box(
                            modifier = Modifier
                                .size(400.dp)
                                .offset(x = (-100).dp, y = (-150).dp)
                                .background(PrimaryTeal.copy(alpha = 0.3f), CircleShape)
                                .blur(100.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(350.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 100.dp, y = 50.dp)
                                .background(PrimaryBlue.copy(alpha = 0.3f), CircleShape)
                                .blur(100.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(300.dp)
                                .align(Alignment.BottomStart)
                                .offset(x = (-50).dp, y = 100.dp)
                                .background(PrimaryGreen.copy(alpha = 0.2f), CircleShape)
                                .blur(100.dp)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp)
                        ) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Smart Contracts",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        "Token Generation & Deployment",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                                IconButton(onClick = {
                                    showSmartContractsPortal = false
                                    smartContractData = null
                                    selectedApprovedRegistry = null
                                }) {
                                    Icon(Icons.Default.Close, "Close", tint = TextPrimary)
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            // Transaction Status
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassEffect()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        smartContractData!!.transactionStatus,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        "Carbon Credit Tokens",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                    Text(
                                        "${smartContractData!!.carbonCreditTokens} tCO₂",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            // Contract Details
                            Text(
                                "Contract Details",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassEffect()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    InfoRow("Token Standard", smartContractData!!.tokenStandard)
                                    InfoRow("Network", smartContractData!!.network)
                                    InfoRow("Contract Address", smartContractData!!.contractAddress)
                                    InfoRow("Deployment Date", smartContractData!!.deploymentDate)
                                    InfoRow("Gas Used", smartContractData!!.gasUsed)
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Access Functions
                            Text(
                                "Access Functions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassEffect()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    smartContractData!!.accessFunctions.forEach { func ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Settings,
                                                contentDescription = null,
                                                tint = AccentCyan,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                func,
                                                color = TextPrimary,
                                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Contract Verification
                            Text(
                                "Contract Verification",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassEffect()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    smartContractData!!.contractVerification.forEach { item ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Icon(
                                                if (item.verified) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                tint = if (item.verified) PrimaryGreen else Color(
                                                    0xFFEF4444
                                                ),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(item.description, color = TextPrimary)
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            // Proceed Button
                            Button(
                                onClick = {
                                    // Generate marketplace data
                                    marketplaceData = CarbonMarketplace(
                                        totalVolume = 4517.0,
                                        activeListings = 23,
                                        tokenAllocation = TokenAllocation(
                                            100.0,
                                            20.0,
                                            30.0,
                                            50.0
                                        ),
                                        marketPrices = MarketPrices(
                                            45.17,
                                            42.22,
                                            43.89
                                        ),
                                        outstandingBids = listOf(
                                            Bid(
                                                12.87,
                                                "Wallet: 0xabc...def",
                                                "2.5 hours @ \$12.81/ton"
                                            )
                                        ),
                                        transactionHistory = listOf(
                                            MarketTransaction(
                                                "Buy",
                                                12.0,
                                                12.62,
                                                "14/12/2023"
                                            ),
                                            MarketTransaction(
                                                "Sell",
                                                10.0,
                                                46.16,
                                                "16/12/2023"
                                            )
                                        )
                                    )
                                    showSmartContractsPortal = false
                                    showCarbonMarketplace = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.ArrowForward, null)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Proceed to Marketplace",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Carbon Marketplace Portal (screen-level)
        if (showCarbonMarketplace && marketplaceData != null) {
            Dialog(
                onDismissRequest = {
                    showCarbonMarketplace = false
                    marketplaceData = null
                    selectedApprovedRegistry = null
                },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF07151A),
                                    Color(0xFF0A2326),
                                    Color(0xFF0C3339),
                                    Color(0xFF0A2832)
                                )
                            )
                        )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Animated gradient blobs for glass effect background
                        Box(
                            modifier = Modifier
                                .size(400.dp)
                                .offset(x = (-100).dp, y = (-150).dp)
                                .background(PrimaryTeal.copy(alpha = 0.3f), CircleShape)
                                .blur(100.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(350.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 100.dp, y = 50.dp)
                                .background(PrimaryBlue.copy(alpha = 0.3f), CircleShape)
                                .blur(100.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(300.dp)
                                .align(Alignment.BottomStart)
                                .offset(x = (-50).dp, y = 100.dp)
                                .background(PrimaryGreen.copy(alpha = 0.2f), CircleShape)
                                .blur(100.dp)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp)
                        ) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Carbon Marketplace",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        "Trade Carbon Credits",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                                IconButton(onClick = {
                                    showCarbonMarketplace = false
                                    marketplaceData = null
                                    selectedApprovedRegistry = null
                                }) {
                                    Icon(Icons.Default.Close, "Close", tint = TextPrimary)
                                }
                            }

                            Spacer(Modifier.height(24.dp))

                            // Market Overview
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassEffect()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "Total Volume",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                        Text(
                                            "${formatNumber(marketplaceData!!.totalVolume)} tCO₂",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "Active Listings",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                        Text(
                                            marketplaceData!!.activeListings.toString(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentCyan
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Token Allocation
                            Text(
                                "Token Allocation",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassEffect()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Allocated Token", color = TextSecondary)
                                        Text(
                                            "${formatNumber(marketplaceData!!.tokenAllocation.allocatedToken)} tCO₂",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Staked Token", color = TextSecondary)
                                        Text(
                                            "${formatNumber(marketplaceData!!.tokenAllocation.stakedToken)} tCO₂",
                                            color = PrimaryTeal,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Burned Token", color = TextSecondary)
                                        Text(
                                            "${formatNumber(marketplaceData!!.tokenAllocation.burnedToken)} tCO₂",
                                            color = Color(0xFFEF4444),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Remaining Token", color = TextSecondary)
                                        Text(
                                            "${formatNumber(marketplaceData!!.tokenAllocation.remainingToken)} tCO₂",
                                            color = PrimaryGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Market Prices
                            Text(
                                "Market Prices (per tCO₂)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .glassEffect()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "Carbon Offset",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                        Text(
                                            "${formatNumber(marketplaceData!!.marketPrices.carbonOffset)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .glassEffect()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "Renewable",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                        Text(
                                            "${formatNumber(marketplaceData!!.marketPrices.renewable)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentCyan
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .glassEffect()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "Forest",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                        Text(
                                            "${formatNumber(marketplaceData!!.marketPrices.forest)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentEmerald
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Transaction History
                            if (marketplaceData!!.transactionHistory.isNotEmpty()) {
                                Text(
                                    "Transaction History",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .glassEffect()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        marketplaceData!!.transactionHistory.forEach { tx ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 6.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        tx.type,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (tx.type == "Buy") PrimaryGreen else Color(
                                                            0xFFFF9800
                                                        )
                                                    )
                                                    Text(
                                                        tx.date,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = TextSecondary
                                                    )
                                                }
                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        "${formatNumber(tx.amount)} tCO₂",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = TextPrimary
                                                    )
                                                    Text(
                                                        "${formatNumber(tx.price)}/t",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = TextSecondary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(Modifier.height(16.dp))
                            }

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val amount = 10.0
                                        isProcessingPayment = true
                                        paymentProgress = 0f
                                        paymentStatus = "Initializing payment..."

                                        // Simulate Razorpay payment flow with progress updates
                                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                            // Step 1: Initialize
                                            kotlinx.coroutines.delay(500)
                                            paymentProgress = 0.2f
                                            paymentStatus = "Connecting to payment gateway..."

                                            // Step 2: Gateway connection
                                            kotlinx.coroutines.delay(800)
                                            paymentProgress = 0.4f
                                            paymentStatus = "Processing payment..."

                                            // Step 3: Payment verification
                                            kotlinx.coroutines.delay(1000)
                                            paymentProgress = 0.7f
                                            paymentStatus = "Verifying transaction..."

                                            // Step 4: Blockchain recording
                                            kotlinx.coroutines.delay(800)
                                            paymentProgress = 0.9f
                                            paymentStatus = "Recording on blockchain..."

                                            // Step 5: Complete
                                            kotlinx.coroutines.delay(500)
                                            paymentProgress = 1.0f
                                            paymentStatus = "Payment successful!"

                                            kotlinx.coroutines.delay(500)
                                            // Update wallet with purchased credits
                                            viewModel.buyCarbonCredits(amount)

                                            kotlinx.coroutines.delay(500)
                                            isProcessingPayment = false
                                            paymentProgress = 0f
                                            paymentStatus = ""

                                            Toast.makeText(
                                                context,
                                                "✓ Successfully purchased $amount tCO₂ credits",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            // Transition to Impact Dashboard
                                            impactDashboardData = ImpactDashboardData()
                                            showImpactDashboard = true
                                            showCarbonMarketplace = false
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isProcessingPayment
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.ShoppingCart, null)
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Buy Credits",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        val amount = 5.0
                                        isProcessingPayment = true
                                        paymentProgress = 0f
                                        paymentStatus = "Initializing sell order..."

                                        // Simulate sell payment flow with progress updates
                                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
                                            .launch {
                                                // Step 1: Initialize
                                                kotlinx.coroutines.delay(500)
                                                paymentProgress = 0.2f
                                                paymentStatus = "Creating marketplace listing..."

                                                // Step 2: Verification
                                                kotlinx.coroutines.delay(800)
                                                paymentProgress = 0.5f
                                                paymentStatus = "Verifying credit ownership..."

                                                // Step 3: Blockchain update
                                                kotlinx.coroutines.delay(1000)
                                                paymentProgress = 0.8f
                                                paymentStatus = "Updating blockchain..."

                                                // Step 4: Complete
                                                kotlinx.coroutines.delay(700)
                                                paymentProgress = 1.0f
                                                paymentStatus = "Listing successful!"

                                                kotlinx.coroutines.delay(500)
                                                // Update wallet with sold credits
                                                viewModel.sellCarbonCredits(amount)

                                                kotlinx.coroutines.delay(500)
                                                isProcessingPayment = false
                                                paymentProgress = 0f
                                                paymentStatus = ""

                                                Toast.makeText(
                                                    context,
                                                    "✓ Successfully listed $amount tCO₂ credits for sale",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isProcessingPayment
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Star, null)
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Sell Credits",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Payment Progress / Impact Dashboard Button
                            if (isProcessingPayment) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .glassEffect()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "Payment Processing",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = paymentProgress,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = PrimaryGreen
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        paymentStatus,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        // Mark submission as completed before closing
                                        selectedApprovedRegistry?.id?.let { submissionId ->
                                            viewModel.markSubmissionAsCompleted(submissionId)
                                        }
                                        // Transition to Impact Dashboard
                                        impactDashboardData = ImpactDashboardData()
                                        showImpactDashboard = true
                                        showCarbonMarketplace = false
                                        marketplaceData = null
                                        selectedApprovedRegistry = null
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AccentEmerald
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Star, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Go to Impact Dashboard",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Main Content Area and top bar
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar - Centered
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showHistoryDrawer = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            "History",
                            tint = AccentCyan
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Blue Carbon Monitor",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            selectedSite,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            "Logout",
                            tint = TextPrimary
                        )
                    }
                }
            }

            // Main Content Area - Centered
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // Centered Column with fixed width for all cards
                Column(
                    modifier = Modifier
                        .width(320.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Photo Documentation Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassEffect()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Face,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Photo Documentation",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            if (photoUri != null && showPhotoPreview) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = "Captured Photo",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        showPhotoPreview = false
                                        photoUri = null
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF44336)
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text("Remove", style = MaterialTheme.typography.bodySmall)
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { capturePhoto() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PrimaryGreen
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(8.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.CameraAlt,
                                                null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                "Capture\nphoto",
                                                style = MaterialTheme.typography.labelSmall,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                    Button(
                                        onClick = { pickFromGallery() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PrimaryBlue
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(8.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Photo,
                                                null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                "Upload\nfrom\nGallery",
                                                style = MaterialTheme.typography.labelSmall,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Upload to Analysis Button
                    Button(
                        onClick = { uploadToAnalysis() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentEmerald
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isUploading && photoUri != null
                    ) {
                        if (uiState is UiState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Uploading...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Send, null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Upload to Analysis",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Location Data Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassEffect()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Location Data",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Text(
                                "Coordinates:",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )

                            if (latitude != null && longitude != null) {
                                Text(
                                    "$latitude, $longitude",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                            } else {
                                Text(
                                    "Not available",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }

                            Button(
                                onClick = { updateLocation() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryBlue
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                enabled = !isGettingLocation
                            ) {
                                if (isGettingLocation) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text("Locating...", style = MaterialTheme.typography.bodySmall)
                                } else {
                                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Update Location", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    // Collection Status Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassEffect()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Call,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Collection Status",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Text(
                                "Points collected:",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            if (latitude != null && longitude != null) PrimaryGreen else Color.Gray,
                                            CircleShape
                                        )
                                )
                                Text(
                                    if (latitude != null && longitude != null) "GPS coordinates" else "GPS pending",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            if (photoUri != null) PrimaryGreen else Color.Gray,
                                            CircleShape
                                        )
                                )
                                Text(
                                    if (photoUri != null) "Photo captured" else "Awaiting photo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    // User Profile Section: Carbon Credits
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassEffect()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    "Profile",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            // Username
                            Text(
                                authState.username.orEmpty(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            // Email
                            if (!authState.email.isNullOrEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        authState.email.orEmpty(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            // Credits Info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "Owned",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                    Text(
                                        formatNumber(wallet?.totalCreditsOwned ?: 0.0) + " tCO₂",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "Retired",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                    Text(
                                        formatNumber(wallet?.totalCreditsRetired ?: 0.0) + " tCO₂",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryTeal
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            // Wallet Address
                            Text(
                                "Wallet: " + (wallet?.address?.take(20) ?: "Not created") + "...",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to get location
private fun getLocation(
    context: android.content.Context,
    onResult: (String, String) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location: Location? ->
            location?.let {
                val lat = "%.5f°".format(it.latitude)
                val lon = "%.5f°".format(it.longitude)
                onResult(lat, lon)
            }
        }
    }
}

// Card for submission history item (user or registry)
@Composable
fun HistoryItemCard(
    id: String,
    location: String?,
    date: String,
    status: String,
    statusColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F3338) // Slightly lighter than drawer background for contrast
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(statusColor.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (status) {
                        "PENDING" -> Icons.Default.Info
                        "APPROVED", "REGISTERED" -> Icons.Default.Check
                        "REJECTED" -> Icons.Default.Close
                        else -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    location ?: "-",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary
                )
                Text(
                    date,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1
                )
                Text(
                    id.take(12) + "...",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(alpha = 0.7f),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
            Surface(
                color = statusColor,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Helper for info row in dialogs
@Composable
fun InfoRow(label: String, value: String?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            label + ":",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            modifier = Modifier.width(90.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            value.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Blockchain Registry Form Portal
@Composable
fun BlockchainRegistryFormPortal(
    registry: CarbonRegistrySubmission,
    formData: BlockchainRegistryForm,
    onFormDataChange: (BlockchainRegistryForm) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF07151A),
                            Color(0xFF0A2326),
                            Color(0xFF0C3339),
                            Color(0xFF0A2832)
                        )
                    )
                )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Animated gradient blobs for glass effect background
                Box(
                    modifier = Modifier
                        .size(400.dp)
                        .offset(x = (-100).dp, y = (-150).dp)
                        .background(PrimaryTeal.copy(alpha = 0.3f), CircleShape)
                        .blur(100.dp)
                )
                Box(
                    modifier = Modifier
                        .size(350.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 100.dp, y = 50.dp)
                        .background(PrimaryBlue.copy(alpha = 0.3f), CircleShape)
                        .blur(100.dp)
                )
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-50).dp, y = 100.dp)
                        .background(PrimaryGreen.copy(alpha = 0.2f), CircleShape)
                        .blur(100.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Blockchain Registry",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                "Immutable Carbon Credit Ledger",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, "Close", tint = TextPrimary)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Registration Status
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassEffect()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Registered",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Block Number",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                registry.blockNumber,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Transaction",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                "2023-10-15 14:30:22 UTC",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Credit Details Form
                    Text(
                        "Credit Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = formData.creditAmount,
                        onValueChange = { onFormDataChange(formData.copy(creditAmount = it)) },
                        label = { Text("Credit Amount*", color = TextSecondary) },
                        placeholder = {
                            Text(
                                "2.3 tCO₂",
                                color = TextSecondary.copy(alpha = 0.5f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = formData.projectArea,
                        onValueChange = { onFormDataChange(formData.copy(projectArea = it)) },
                        label = { Text("Project Area*", color = TextSecondary) },
                        placeholder = {
                            Text(
                                "1.2 hectares",
                                color = TextSecondary.copy(alpha = 0.5f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = formData.vintageYear,
                            onValueChange = { onFormDataChange(formData.copy(vintageYear = it)) },
                            label = { Text("Vintage Year*", color = TextSecondary) },
                            placeholder = {
                                Text(
                                    "2023",
                                    color = TextSecondary.copy(alpha = 0.5f)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PrimaryTeal,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = formData.verificationDate,
                            onValueChange = { onFormDataChange(formData.copy(verificationDate = it)) },
                            label = {
                                Text(
                                    "Verification Date*",
                                    color = TextSecondary
                                )
                            },
                            placeholder = {
                                Text(
                                    "10/10/2023",
                                    color = TextSecondary.copy(alpha = 0.5f)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = PrimaryTeal,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Transaction Hash
                    Text(
                        "Transaction Hash",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassEffect()
                    ) {
                        Text(
                            registry.transactionHash,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Audit Trail
                    Text(
                        "Audit Trail",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glassEffect()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            registry.auditTrail.forEach { audit ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        if (audit.completed) Icons.Default.Check else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (audit.completed) PrimaryGreen else Color(
                                            0xFFEF4444
                                        ),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(audit.description, color = TextPrimary)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Registry Notes
                    Text(
                        "Registry Notes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formData.registryNotes,
                        onValueChange = { onFormDataChange(formData.copy(registryNotes = it)) },
                        placeholder = {
                            Text(
                                "Add any additional notes or observations...",
                                color = TextSecondary.copy(alpha = 0.5f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = PrimaryTeal,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                        enabled = formData.creditAmount.isNotBlank() &&
                                formData.projectArea.isNotBlank() &&
                                formData.vintageYear.isNotBlank() &&
                                formData.verificationDate.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ArrowForward, null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Proceed to Tokenization",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

enum class LoginType {
    USER,
    ADMIN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarbonRegistryApp(viewModel: CarbonViewModel) {
    val authState by viewModel.authState.collectAsState()
    var selectedScreen by remember {
        mutableStateOf(
            if (authState.userType == CarbonViewModel.UserType.ADMIN) Screen.Profile else Screen.Dashboard
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

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
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                "Carbon Registry",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            authState.username?.let {
                                Text(
                                    "Welcome, $it",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                },
                actions = {
                    authState.userType?.let { userType ->
                        Surface(
                            color = if (userType.name == "ADMIN") AccentCyan else PrimaryGreen,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                userType.name,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = selectedScreen,
                onScreenSelected = { selectedScreen = it },
                isAdmin = authState.userType == CarbonViewModel.UserType.ADMIN
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            val isAdmin = authState.userType == CarbonViewModel.UserType.ADMIN
            when (selectedScreen) {
                Screen.Dashboard -> DashboardScreen(viewModel)
                Screen.Projects -> ProjectsScreen(viewModel)
                Screen.Credits -> CreditsScreen(viewModel)
                Screen.Wallet -> WalletScreen(viewModel)
                Screen.Profile -> ProfileScreen(viewModel)
                Screen.AdminVerification -> if (isAdmin) AdminVerificationScreen(viewModel)
            }

            if (uiState is UiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", color = TextPrimary) },
            text = { Text("Are you sure you want to logout?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = PrimaryGreen)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun BottomNavigationBar(
    selectedScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    isAdmin: Boolean
) {
    val screens = remember(isAdmin) {
        if (isAdmin) {
            listOf(
                Screen.Profile,
                Screen.AdminVerification
            )
        } else {
            listOf(
                Screen.Dashboard,
                Screen.Projects,
                Screen.Credits,
                Screen.Wallet,
                Screen.Profile
            )
        }
    }

    NavigationBar(
        containerColor = DarkSurface,
        tonalElevation = 8.dp
    ) {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = selectedScreen == screen,
                onClick = { onScreenSelected(screen) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryGreen,
                    selectedTextColor = PrimaryGreen,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = PrimaryGreen.copy(alpha = 0.2f)
                )
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
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header with glass effect
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassEffect()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        "Welcome to Carbon Registry",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Track, trade, and retire carbon credits on blockchain",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            Text(
                "Global Impact",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassStatCard(
                    title = "CO₂ Offset",
                    value = formatNumber(totalCo2) + " t",
                    icon = Icons.Default.Check,
                    color = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )
                GlassStatCard(
                    title = "Active Projects",
                    value = activeProjects.toString(),
                    icon = Icons.Default.Star,
                    color = PrimaryGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassStatCard(
                    title = "Available Credits",
                    value = availableCredits.toString(),
                    icon = Icons.Default.Info,
                    color = AccentCyan,
                    modifier = Modifier.weight(1f)
                )
                GlassStatCard(
                    title = "Your Credits",
                    value = formatNumber(wallet?.totalCreditsOwned ?: 0.0),
                    icon = Icons.Default.Edit,
                    color = PrimaryTeal,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                "Featured Projects",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
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

        item {
            Text(
                "Recent Credits",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
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
fun GlassStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.glassEffect()
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
                color = TextSecondary
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

@Composable
fun ProfileScreen(viewModel: CarbonViewModel) {
    val authState by viewModel.authState.collectAsState()
    val wallet by viewModel.userWallet.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(32.dp))
        // Avatar with glass effect
        Box(
            modifier = Modifier
                .size(90.dp)
                .glassEffect()
                .background(PrimaryGreen.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        // Username
        Text(
            authState.username.orEmpty(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Surface(
            color = if (authState.userType?.name == "ADMIN") AccentCyan else PrimaryGreen,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                authState.userType?.name.orEmpty(),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(16.dp))
        // Email
        if (!authState.email.isNullOrEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary)
                Spacer(Modifier.width(6.dp))
                Text(
                    authState.email.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        // Wallet with glass effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassEffect()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    "Wallet Address",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    wallet?.address?.take(40)?.plus("...") ?: "No wallet created",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            "Owned",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Text(
                            formatNumber(wallet?.totalCreditsOwned ?: 0.0) + " tCO₂",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Retired",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Text(
                            formatNumber(wallet?.totalCreditsRetired ?: 0.0) + " tCO₂",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryTeal
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(36.dp))
        // Logout Button
        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
            Spacer(Modifier.width(8.dp))
            Text("Logout")
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "Profile and account settings coming soon!",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

// Helper Functions
fun getProjectTypeColor(type: ProjectType): Color = when (type) {
    ProjectType.REFORESTATION -> PrimaryGreen
    ProjectType.RENEWABLE_ENERGY -> AccentEmerald
    ProjectType.ENERGY_EFFICIENCY -> PrimaryTeal
    ProjectType.METHANE_CAPTURE -> AccentCyan
    ProjectType.OCEAN_CONSERVATION -> PrimaryBlue
    ProjectType.CARBON_CAPTURE -> SecondaryGreen
    ProjectType.SUSTAINABLE_AGRICULTURE -> PrimaryGreen
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
    Wallet("Wallet", Icons.Default.Settings),
    Profile("Profile", Icons.Default.Person),
    AdminVerification("Verification", Icons.Default.Check)
}