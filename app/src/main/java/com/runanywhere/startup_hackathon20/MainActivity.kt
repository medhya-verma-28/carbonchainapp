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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.runanywhere.startup_hackathon20.data.*
import com.runanywhere.startup_hackathon20.ui.theme.Startup_hackathon20Theme
import com.runanywhere.startup_hackathon20.viewmodel.CarbonViewModel
import com.runanywhere.startup_hackathon20.viewmodel.UiState
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Dark Theme Colors
val DarkBackground = Color(0xFF0A0E27)
val DarkSurface = Color(0xFF151932)
val PrimaryPurple = Color(0xFF6366F1)
val PrimaryBlue = Color(0xFF3B82F6)
val AccentGreen = Color(0xFF10B981)
val AccentOrange = Color(0xFFF59E0B)
val GlassWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB4B4C8)

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
    var showLoginScreen by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        when {
            !authState.isAuthenticated && !showLoginScreen -> {
                // Show Blue Carbon Monitor Homepage first
                BlueCarbonMonitorHomepage(
                    onNewProfileClick = { showLoginScreen = true }
                )
            }

            !authState.isAuthenticated && showLoginScreen -> {
                // Show Login Screen
                LoginScreen(
                    viewModel = viewModel,
                    onBack = { showLoginScreen = false }
                )
            }

            else -> {
                // Show Main App based on user role
                CarbonRegistryApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: CarbonViewModel,
    onBack: () -> Unit
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
                            Color(0xFF1E1B4B),
                            Color(0xFF312E81),
                            Color(0xFF4338CA),
                            Color(0xFF6366F1)
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
                    .background(PrimaryPurple.copy(alpha = 0.3f), CircleShape)
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
                    .background(AccentGreen.copy(alpha = 0.2f), CircleShape)
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
                                    accentColor = AccentGreen,
                                    onClick = { selectedLoginType = LoginType.USER }
                                )
                                
                                GlassLoginTypeCard(
                                    icon = Icons.Default.Settings,
                                    title = "Admin Login",
                                    description = "Manage projects, verify credits, and oversee operations",
                                    accentColor = AccentOrange,
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
                                            tint = if (loginType == LoginType.ADMIN) AccentOrange else AccentGreen,
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
                                            focusedBorderColor = PrimaryPurple,
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
                                                focusedBorderColor = PrimaryPurple,
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
                                            focusedBorderColor = PrimaryPurple,
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
                                            containerColor = if (loginType == LoginType.ADMIN) AccentOrange else AccentGreen
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
                                                color = AccentGreen
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
fun BlueCarbonMonitorHomepage(onNewProfileClick: () -> Unit) {
    val context = LocalContext.current
    var selectedSite by remember { mutableStateOf("Mangrove Restoration Site") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var latitude by remember { mutableStateOf<String?>(null) }
    var longitude by remember { mutableStateOf<String?>(null) }
    var isGettingLocation by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var showPhotoPreview by remember { mutableStateOf(false) }

    // Create photo file (using remember for stable file across recompositions)
    val photoFile = remember {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "carbon_photo_$ts.jpg"
        File(context.cacheDir, fileName)
    }

    // Create URI for camera
    val uri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }

    // Camera launcher
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

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    // Location permission launcher  
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

    // Camera capture function
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

    // Location update function
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

    // Upload simulation
    fun uploadToAnalysis() {
        if (photoUri == null) {
            Toast.makeText(context, "Please capture a photo first", Toast.LENGTH_SHORT).show()
            return
        }
        isUploading = true
        // Simulate upload
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            isUploading = false
            Toast.makeText(context, "Uploaded to analysis!", Toast.LENGTH_SHORT).show()
        }, 1500)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5B6FCC),
                        Color(0xFF7B6FCE),
                        Color(0xFF9B6FD0)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Blue Carbon Monitor",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        selectedSite,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = onNewProfileClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.Person, null, tint = TextPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("New Profile data", color = TextPrimary)
                }
            }

            // Main Content Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Sidebar
                Column(
                    modifier = Modifier
                        .width(250.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                        .height(100.dp)
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
                                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Remove", style = MaterialTheme.typography.bodySmall)
                                }
                            } else {
                                Text(
                                    "No photo captured yet.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
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
                                    tint = AccentGreen,
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
                                            if (latitude != null && longitude != null) AccentGreen else Color.Gray,
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
                                            if (photoUri != null) AccentGreen else Color.Gray,
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
                }

                // Main Photo Capture Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .glassEffect()
                        .clickable { capturePhoto() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (photoUri != null && showPhotoPreview) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = "Preview Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.7f)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Photo ready for upload",
                                style = MaterialTheme.typography.bodyLarge,
                                color = AccentGreen,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                Icons.Default.Face,
                                contentDescription = null,
                                tint = TextSecondary.copy(alpha = 0.5f),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Tap to capture image or photo",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Bottom Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { uploadToAnalysis() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen
                    ),
                    modifier = Modifier.width(280.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isUploading && photoUri != null
                ) {
                    if (isUploading) {
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
                        Icon(Icons.Default.Send, null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Upload to Analysis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
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

enum class LoginType {
    USER,
    ADMIN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarbonRegistryApp(viewModel: CarbonViewModel) {
    var selectedScreen by remember { mutableStateOf(Screen.Dashboard) }
    val uiState by viewModel.uiState.collectAsState()
    val authState by viewModel.authState.collectAsState()
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
                            tint = AccentGreen,
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
                            color = if (userType.name == "ADMIN") AccentOrange else AccentGreen,
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
                Screen.Profile -> ProfileScreen(viewModel)
            }

            if (uiState is UiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentGreen)
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
                    Text("Cancel", color = AccentGreen)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun BottomNavigationBar(selectedScreen: Screen, onScreenSelected: (Screen) -> Unit) {
    NavigationBar(
        containerColor = DarkSurface,
        tonalElevation = 8.dp
    ) {
        Screen.values().forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = selectedScreen == screen,
                onClick = { onScreenSelected(screen) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentGreen,
                    selectedTextColor = AccentGreen,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = AccentGreen.copy(alpha = 0.2f)
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
                    color = AccentGreen,
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
                    color = AccentOrange,
                    modifier = Modifier.weight(1f)
                )
                GlassStatCard(
                    title = "Your Credits",
                    value = formatNumber(wallet?.totalCreditsOwned ?: 0.0),
                    icon = Icons.Default.Edit,
                    color = PrimaryPurple,
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
                .background(AccentGreen.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = AccentGreen,
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
            color = if (authState.userType?.name == "ADMIN") AccentOrange else AccentGreen,
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
                            color = AccentGreen
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
                            color = PrimaryPurple
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
    Wallet("Wallet", Icons.Default.Settings),
    Profile("Profile", Icons.Default.Person)
}