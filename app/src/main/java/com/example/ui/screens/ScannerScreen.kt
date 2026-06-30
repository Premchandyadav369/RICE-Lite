package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.model.CropDiagnosis
import com.example.data.model.CropMarketPrediction
import com.example.data.model.DailyPricePrediction
import com.example.ui.theme.ForestGreen
import com.example.ui.viewmodel.ScannerUiState
import com.example.ui.viewmodel.MarketUiState
import com.example.ui.viewmodel.ScannerViewModel
import java.io.InputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val marketUiState by viewModel.marketUiState.collectAsState()
    val selectedImage by viewModel.selectedImage.collectAsState()

    var activeTab by remember { mutableStateOf(0) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showCameraView by remember { mutableStateOf(!hasCameraPermission || selectedImage == null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = context.loadUriAsBitmap(it)
            if (bitmap != null) {
                viewModel.selectImage(bitmap)
                showCameraView = false
                activeTab = 0 // Switch to scan tab to review
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (activeTab) {
                                    0 -> Icons.Default.Search
                                    1 -> Icons.Default.List
                                    else -> Icons.Default.Settings
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = when (activeTab) {
                                    0 -> "Crop Pathology Scanner"
                                    1 -> "Market Price Advisor"
                                    else -> "Gemma 4 Config"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Gemma 4 • Low-Latency 4-bit Engine",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (activeTab == 0 && !showCameraView && selectedImage != null) {
                        IconButton(
                            onClick = {
                                viewModel.clearImage()
                                showCameraView = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Scanner"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.Search, contentDescription = "AI Scan") },
                    label = { Text("AI Scan", fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Market Prediction") },
                    label = { Text("Market predict", fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Engine Control") },
                    label = { Text("Gemma Engine", fontWeight = FontWeight.Bold) }
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (activeTab) {
                0 -> {
                    // TAB 0: DISEASE PATHOLOGY SCANNER
                    when {
                        !hasCameraPermission -> {
                            PermissionRequestView(
                                onRequestPermission = {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            )
                        }
                        showCameraView -> {
                            CameraView(
                                onImageCaptured = { bitmap ->
                                    viewModel.selectImage(bitmap)
                                    showCameraView = false
                                },
                                onOpenGallery = {
                                    galleryLauncher.launch("image/*")
                                }
                            )
                        }
                        else -> {
                            ImageAnalysisView(
                                viewModel = viewModel,
                                image = selectedImage,
                                uiState = uiState,
                                onAnalyzeClick = { viewModel.analyzeImage() },
                                onResetClick = {
                                    viewModel.clearImage()
                                    showCameraView = true
                                }
                            )
                        }
                    }
                }
                1 -> {
                    // TAB 1: MARKET PRICE FORECAST HUB
                    MarketHubView(
                        viewModel = viewModel,
                        uiState = marketUiState
                    )
                }
                2 -> {
                    // TAB 2: GEMMA ENGINE HARDWARE MONITOR
                    GemmaEngineConfigView(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun PermissionRequestView(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "The leaf diagnostic scanner requires camera access to capture leaf details and identify pathogen spots directly in real time.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRequestPermission,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("request_permission_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Grant Camera Access", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CameraView(
    onImageCaptured: (Bitmap) -> Unit,
    onOpenGallery: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    var isCapturing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                // Fail-safe
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Custom Overlay with Animated Laser Scanning Lines
        ScannerTargetOverlay(modifier = Modifier.fillMaxSize())

        // Bottom Camera Action Controls
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 36.dp)
                .padding(horizontal = 24.dp)
        ) {
            // Gallery Selector (Left Side)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(56.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .clickable(onClick = onOpenGallery)
                    .testTag("gallery_picker_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Choose from Gallery",
                    tint = Color.White
                )
            }

            // Central Shutter Button
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(76.dp)
                    .background(Color.White.copy(alpha = 0.3f), CircleShape)
                    .padding(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White, CircleShape)
                        .clickable {
                            if (!isCapturing) {
                                isCapturing = true
                                capturePhoto(
                                    imageCapture = imageCapture,
                                    executor = cameraExecutor,
                                    onSuccess = { bitmap ->
                                        isCapturing = false
                                        onImageCaptured(bitmap)
                                    },
                                    onError = {
                                        isCapturing = false
                                    }
                                )
                            }
                        }
                        .testTag("shutter_button")
                )
            }

            // Info hint
            Text(
                text = "Tap to snap crop leaf",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-24).dp)
            )
        }
    }
}

@Composable
fun ScannerTargetOverlay(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanProgress"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val boxWidth = width * 0.75f
        val boxHeight = boxWidth * 1.25f
        val left = (width - boxWidth) / 2
        val top = (height - boxHeight) / 2 - 50

        // Draw translucent dark background mask with cutout
        drawRect(
            color = Color.Black.copy(alpha = 0.45f),
            size = size
        )

        // Clear the cutout area
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(boxWidth, boxHeight),
            cornerRadius = CornerRadius(24f, 24f),
            blendMode = BlendMode.Clear
        )

        // Draw green guiding brackets around target
        val bracketLength = 40f
        val thickness = 8f
        val bracketColor = Color(0xFF4CAF50)

        // Top Left
        drawLine(bracketColor, Offset(left, top), Offset(left + bracketLength, top), thickness)
        drawLine(bracketColor, Offset(left, top), Offset(left, top + bracketLength), thickness)

        // Top Right
        drawLine(bracketColor, Offset(left + boxWidth, top), Offset(left + boxWidth - bracketLength, top), thickness)
        drawLine(bracketColor, Offset(left + boxWidth, top), Offset(left + boxWidth, top + bracketLength), thickness)

        // Bottom Left
        drawLine(bracketColor, Offset(left, top + boxHeight), Offset(left + bracketLength, top + boxHeight), thickness)
        drawLine(bracketColor, Offset(left, top + boxHeight), Offset(left, top + boxHeight - bracketLength), thickness)

        // Bottom Right
        drawLine(bracketColor, Offset(left + boxWidth, top + boxHeight), Offset(left + boxWidth - bracketLength, top + boxHeight), thickness)
        drawLine(bracketColor, Offset(left + boxWidth, top + boxHeight), Offset(left + boxWidth, top + boxHeight - bracketLength), thickness)

        // Animated scan line moving back and forth
        val lineY = top + (boxHeight * scanProgress)
        drawLine(
            color = Color(0xFF00E676),
            start = Offset(left + 4f, lineY),
            end = Offset(left + boxWidth - 4f, lineY),
            strokeWidth = 6f
        )
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF00E676).copy(alpha = 0.35f),
                    Color(0xFF00E676).copy(alpha = 0.05f),
                    Color.Transparent
                ),
                startY = lineY - 2f,
                endY = lineY + 60f
            ),
            topLeft = Offset(left + 4f, lineY),
            size = Size(boxWidth - 8f, 60f)
        )
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Fit crop leaf inside brackets",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.offset(y = (-280).dp),
            textAlign = TextAlign.Center
        )
    }
}

private fun capturePhoto(
    imageCapture: ImageCapture,
    executor: Executor,
    onSuccess: (Bitmap) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = image.toBitmapSafe()
                onSuccess(bitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}

private fun ImageProxy.toBitmapSafe(): Bitmap {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val rotation = imageInfo.rotationDegrees
    return if (rotation != 0) {
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
}

private fun Context.loadUriAsBitmap(uri: Uri): Bitmap? {
    return try {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun ImageAnalysisView(
    viewModel: ScannerViewModel,
    image: Bitmap?,
    uiState: ScannerUiState,
    onAnalyzeClick: () -> Unit,
    onResetClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val gemmaThinking by viewModel.gemmaThinkingScanner.collectAsState()
    val latency by viewModel.telemetryLatency.collectAsState()
    val quantizationMode by viewModel.quantizationMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Leaf Image Preview Card with Active Scan animation if Loading
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (image != null) {
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = "Selected Crop Leaf",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            if (uiState is ScannerUiState.Loading) {
                val infiniteTransition = rememberInfiniteTransition(label = "analysis_scanner")
                val scanProgress by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scanProgress"
                )
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val lineY = size.height * scanProgress
                    drawLine(
                        color = Color(0xFF00E676),
                        start = Offset(0f, lineY),
                        end = Offset(size.width, lineY),
                        strokeWidth = 8f
                    )
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF00E676).copy(alpha = 0.45f),
                                Color(0xFF00E676).copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            startY = lineY,
                            endY = lineY + 70f
                        ),
                        topLeft = Offset(0f, lineY),
                        size = Size(size.width, 70f)
                    )
                }
            }

            // Small reticle guide
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Diagnostic Action or Status Container
        when (uiState) {
            is ScannerUiState.Idle -> {
                Button(
                    onClick = onAnalyzeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("analyze_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Diagnose Leaf Health", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onResetClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("retake_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retake Photo")
                }
            }

            is ScannerUiState.Loading -> {
                LoadingDiagnosticView(thinkingLog = gemmaThinking)
            }

            is ScannerUiState.Success -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Render the Collapsible Gemma-4 step-by-step thinking block
                    GemmaThinkingCard(
                        thinking = gemmaThinking,
                        latency = latency,
                        quantization = quantizationMode
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DiagnosisResultView(
                        diagnosis = uiState.diagnosis,
                        onResetClick = onResetClick
                    )
                }
            }

            is ScannerUiState.Error -> {
                ErrorDiagnosticView(
                    message = uiState.message,
                    onRetryClick = onAnalyzeClick,
                    onResetClick = onResetClick
                )
            }
        }
    }
}

@Composable
fun LoadingDiagnosticView(thinkingLog: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Gemma-4 is Thinking...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Gemma-4 (31B-it) is running on-demand 4-bit AWQ hardware acceleration, identifying lesions, vein necrosis, leaf chlorosis patterns, and calculating treatments.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (!thinkingLog.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Gemma 4 Logs",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Active Inference Telemetry:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = thinkingLog,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun ErrorDiagnosticView(
    message: String,
    onRetryClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Inference Execution Failed",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRetryClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Retry Gemma 4 Inference")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onResetClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Select Another Photo")
            }
        }
    }
}

@Composable
fun DiagnosisResultView(
    diagnosis: CropDiagnosis,
    onResetClick: () -> Unit
) {
    val isHealthy = diagnosis.health_status.equals("Healthy", ignoreCase = true)
    val statusColor = if (isHealthy) Color(0xFF2E7D32) else Color(0xFFD84315)
    val statusContainerColor = if (isHealthy) Color(0xFFE8F5E9) else Color(0xFFFBE9E7)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Status Badge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = statusContainerColor),
            shape = RoundedCornerShape(20.dp)
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
                        .background(statusColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isHealthy) Icons.Default.Check else Icons.Default.Warning,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = if (isHealthy) "Crop is Healthy" else "Disease Detected",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                    Text(
                        text = "Confidence Level: ${(diagnosis.confidence * 100).toInt()}%",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Identification Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Pathology Identification",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                DetailItemRow(label = "Crop Species", value = diagnosis.crop_name)
                DetailItemRow(label = "Primary Condition", value = diagnosis.disease_name)

                if (!isHealthy) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Visible Symptoms",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    diagnosis.symptoms.forEach { symptom ->
                        BulletItem(text = symptom)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Potential Primary Causes",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    diagnosis.causes.forEach { cause ->
                        BulletItem(text = cause)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Treatment Options (Tabs)
        Text(
            text = "Gemma-4 Pathogen Treatment Plan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        var selectedTab by remember { mutableStateOf(0) }
        val tabTitles = listOf("Immediate", "Organic", "Chemical", "Preventive")

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val listToDisplay = when (selectedTab) {
                    0 -> diagnosis.treatments.immediate_actions
                    1 -> diagnosis.treatments.organic_control
                    2 -> diagnosis.treatments.chemical_control
                    else -> diagnosis.treatments.preventive_measures
                }

                if (listToDisplay.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No treatments listed in this category.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    listToDisplay.forEachIndexed { idx, action ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(18.dp)
                                    .background(
                                        if (selectedTab == 3) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else statusColor.copy(alpha = 0.15f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (idx + 1).toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == 3) MaterialTheme.colorScheme.primary else statusColor
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = action,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onResetClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("scan_another_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text("Scan Another Leaf", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun DetailItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun BulletItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun GemmaThinkingCard(
    thinking: String?,
    latency: Long = 0,
    quantization: String = "4-bit INT4 (AWQ)"
) {
    if (thinking.isNullOrBlank()) return

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E24) // Custom tech space-slate dark background
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF81C784).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Gemma-4 Reasoning Chain",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "google/gemma-4-31B-it • $quantization",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (latency > 0) {
                        Text(
                            text = "${latency}ms",
                            color = Color(0xFF00E676),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle Thoughts",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color.White.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = thinking,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFFC8E6C9), // Clean green monospace output
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Low Latency 4-Bit Acceleration Enabled",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

// --- TAB 1: MARKET COMMODITY FORECASTER ---
@Composable
fun MarketHubView(
    viewModel: ScannerViewModel,
    uiState: MarketUiState
) {
    var cropInput by remember { mutableStateOf("") }
    var regionInput by remember { mutableStateOf("Punjab Agro Trade Hub") }
    val gemmaThinking by viewModel.gemmaThinkingMarket.collectAsState()
    val latency by viewModel.telemetryLatency.collectAsState()
    val quantizationMode by viewModel.quantizationMode.collectAsState()
    val scrollState = rememberScrollState()

    val quickCrops = listOf("Tomato", "Rice", "Wheat", "Potato", "Corn", "Apple", "Cotton")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Gemma-4 Price Predictor",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Forecast local crop rates and 7-day future trends using the quantized 31B Gemma-4 model.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Crop Input Field
                OutlinedTextField(
                    value = cropInput,
                    onValueChange = { cropInput = it },
                    label = { Text("Enter Crop Name") },
                    placeholder = { Text("e.g. Tomato, Rice, Cotton") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick selector chips
                Text(
                    text = "Select Popular Crops:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickCrops.forEach { crop ->
                        FilterChip(
                            selected = cropInput.equals(crop, ignoreCase = true),
                            onClick = { cropInput = crop },
                            label = { Text(crop) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Region Input Field
                OutlinedTextField(
                    value = regionInput,
                    onValueChange = { regionInput = it },
                    label = { Text("Market Region / Center") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (cropInput.isNotBlank()) {
                            viewModel.predictMarketPrice(cropInput, regionInput)
                        }
                    },
                    enabled = cropInput.isNotBlank() && uiState !is MarketUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.List, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Predict Market Price", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Handle states
        when (uiState) {
            is MarketUiState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Commodity Selected",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            is MarketUiState.Loading -> {
                LoadingDiagnosticView(thinkingLog = gemmaThinking)
            }
            is MarketUiState.Success -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Display Gemma 4 thought chain
                    GemmaThinkingCard(
                        thinking = gemmaThinking,
                        latency = latency,
                        quantization = quantizationMode
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Projection Dashboard Card
                    MarketResultDashboard(prediction = uiState.prediction)
                }
            }
            is MarketUiState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Market Analysis Failed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarketResultDashboard(prediction: CropMarketPrediction) {
    val isUp = prediction.trend_direction.equals("Up", ignoreCase = true)
    val isDown = prediction.trend_direction.equals("Down", ignoreCase = true)
    
    val trendColor = if (isUp) Color(0xFF2E7D32) else if (isDown) Color(0xFFC62828) else Color(0xFFEF6C00)
    val trendBg = if (isUp) Color(0xFFE8F5E9) else if (isDown) Color(0xFFFFEBEE) else Color(0xFFFFF3E0)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Price Summary Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = trendBg),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CURRENT PRICE ESTIMATE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                        Text(
                            text = prediction.current_price,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Flashing Neon Trend Indicator Badge
                    Card(
                        colors = CardDefaults.cardColors(containerColor = trendColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isUp) Icons.Default.Check else Icons.Default.Warning, // Standard icons
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${prediction.predicted_price_change} (${prediction.trend_direction})",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = trendColor.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Forecast Confidence",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(prediction.confidence * 100).toInt()}% • High Acc.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = trendColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Beautiful Canvas-Drawn Chart Curves
        CropPriceChart(
            predictions = prediction.predictions_7_days,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Advisor Recommendations
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "AI Commodity Recommendation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Demand Index
                DetailItemRow(label = "Demand-Supply Index", value = prediction.demand_supply_index)
                
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                // Recommendation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = prediction.recommendation,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Deep Market Sentiment
                Text(
                    text = "Gemma-4 Deep Sentiment Analysis",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = prediction.market_sentiment,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CropPriceChart(
    predictions: List<DailyPricePrediction>,
    modifier: Modifier = Modifier
) {
    if (predictions.isEmpty()) return

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "7-Day Projected Price Curve",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Quantized commodity projection model output",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            val prices = predictions.map { it.price }
            val maxPrice = prices.maxOrNull() ?: 100.0
            val minPrice = prices.minOrNull() ?: 0.0
            val priceRange = (maxPrice - minPrice).coerceAtLeast(1.0)

            val chartMax = maxPrice + (priceRange * 0.15)
            val chartMin = (minPrice - (priceRange * 0.15)).coerceAtLeast(0.0)
            val chartRange = chartMax - chartMin

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                val width = size.width
                val height = size.height
                val pointCount = predictions.size
                val stepX = width / (pointCount - 1).coerceAtLeast(1)

                // Draw Grid Lines (Y-Axis)
                val gridLinesCount = 3
                for (i in 0..gridLinesCount) {
                    val ratio = i.toFloat() / gridLinesCount
                    val y = height * ratio
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.25f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                // Prepare Points
                val points = predictions.mapIndexed { idx, pred ->
                    val x = idx * stepX
                    val yRatio = ((pred.price - chartMin) / chartRange).toFloat()
                    val y = height - (height * yRatio)
                    Offset(x, y)
                }

                // Draw Area Gradient under the line
                val fillPath = Path().apply {
                    moveTo(0f, height)
                    points.forEach { point ->
                        lineTo(point.x, point.y)
                    }
                    lineTo(width, height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF81C784).copy(alpha = 0.45f),
                            Color(0xFF81C784).copy(alpha = 0.01f)
                        ),
                        startY = 0f,
                        endY = height
                    )
                )

                // Draw Smooth Curve
                val linePath = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            val pPrev = points[i - 1]
                            val pCurr = points[i]
                            val controlX = (pPrev.x + pCurr.x) / 2
                            cubicTo(
                                controlX, pPrev.y,
                                controlX, pCurr.y,
                                pCurr.x, pCurr.y
                            )
                        }
                    }
                }

                drawPath(
                    path = linePath,
                    color = Color(0xFF2E7D32),
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )

                // Draw node dots
                points.forEach { point ->
                    drawCircle(
                        color = Color(0xFF2E7D32),
                        radius = 6f,
                        center = point
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3f,
                        center = point
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // X-Axis Text Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                predictions.forEach { pred ->
                    Text(
                        text = pred.day,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// --- TAB 2: GEMMA ENGINE HARDWARE MONITOR ---
@Composable
fun GemmaEngineConfigView(viewModel: ScannerViewModel) {
    val lowLatencyMode by viewModel.lowLatencyMode.collectAsState()
    val quantizationMode by viewModel.quantizationMode.collectAsState()
    val lastLatency by viewModel.telemetryLatency.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cockpit Display Panel (Styled like a retro terminal grid)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF00E676).copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0F12)), // Tech deep black
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GEMMA ENGINE DIAGNOSTICS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E676)
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF00E676), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Grid Terminal Info
                TerminalInfoRow(label = "ACTIVE_MODEL", value = "google/gemma-4-31B-it")
                TerminalInfoRow(label = "QUANTIZATION", value = quantizationMode)
                TerminalInfoRow(label = "LATENCY_TUNING", value = if (lowLatencyMode) "LOW_LATENCY_ACTIVE" else "STANDARD_PASS")
                TerminalInfoRow(label = "LAST_LATENCY", value = if (lastLatency > 0) "${lastLatency} ms" else "IDLE")
                TerminalInfoRow(label = "GPU_VRAM_POOL", value = "24GB HBM3 Alloc")
                TerminalInfoRow(label = "COMPUTE_BACKEND", value = "vLLM / SGLang Turbo")
                TerminalInfoRow(label = "ACTIVE_EXPERTS", value = "MoE 8 active / 128 total")
                TerminalInfoRow(label = "CONTEXT_WINDOW", value = "256K tokens (max)")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Parameter Settings Panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Gemma-4 Model Execution Config",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Adjust parameters dynamically for low latency and 4-bit execution profiles.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Low Latency Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Low-Latency Turbo Execution",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Increases compilation speed, skips heavy visual rendering passes.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = lowLatencyMode,
                        onCheckedChange = { viewModel.setLowLatencyMode(it) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                // Quantization options
                Text(
                    text = "Engine Precision & Quantization Mode",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "4-bit AWQ provides the absolute lowest latency on-device. Higher precision requires larger servers.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                val quantizationModes = listOf("4-bit INT4 (AWQ)", "8-bit INT8 (GPTQ)", "16-bit BF16 (Raw)")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quantizationModes.forEach { mode ->
                        val isSelected = quantizationMode == mode
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setQuantizationMode(mode) }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mode.split(" ")[0],
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TerminalInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ">> $label:",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = Color.LightGray.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00E676)
        )
    }
}
