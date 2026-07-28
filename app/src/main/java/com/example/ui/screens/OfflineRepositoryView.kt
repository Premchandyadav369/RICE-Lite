package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OfflineManualEntity
import com.example.ui.KrishiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineRepositoryView(
    viewModel: KrishiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val manuals by viewModel.offlineManuals.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val isTelugu = selectedLanguage.contains("Telugu") || selectedLanguage.contains("తెలుగు")

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedType by remember { mutableStateOf("ALL") }
    var activeViewerManual by remember { mutableStateOf<OfflineManualEntity?>(null) }

    val categories = listOf("All", "Cotton", "Paddy", "Chilli", "Turmeric", "Soil & Water")
    val types = listOf("ALL", "MANUAL", "VIDEO", "INFOGRAPHIC")

    // Filter manuals
    val filteredManuals = manuals.filter { manual ->
        val matchesCategory = selectedCategory == "All" || manual.cropCategory.equals(selectedCategory, ignoreCase = true)
        val matchesType = selectedType == "ALL" || manual.type.equals(selectedType, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                manual.titleEn.contains(searchQuery, ignoreCase = true) ||
                manual.titleTe.contains(searchQuery, ignoreCase = true) ||
                manual.descriptionEn.contains(searchQuery, ignoreCase = true) ||
                manual.descriptionTe.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesType && matchesSearch
    }

    val totalCachedMb = manuals.filter { it.isCachedOffline }.sumOf { it.fileSizeMb }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- Header Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Offline Cache",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isTelugu) "ఆఫ్‌లైన్ వ్యవసాయ మార్గదర్శకాల భాండాగారం" else "Offline Agricultural Knowledge Repository",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isTelugu) "నెట్‌వర్క్ లేని పొలాల్లో ఉచితంగా ఉపయోగపడే సలహాలు & వీడియోలు" else "Zero-internet manuals, video guides & infographics for farmers",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                // Offline Cache Storage Status Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SdStorage,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isTelugu) "ఆఫ్‌లైన్ నిల్వ: %.1f MB".format(totalCachedMb) else "Cached Storage: %.1f MB".format(totalCachedMb),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF16A34A).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isTelugu) "✓ లోకల్ డాటాబేస్ సిద్ధం" else "✓ Local DB Ready",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // --- Search TextField ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (isTelugu) "పంట, పురుగు లేదా మందు పేరును వెతకండి..." else "Search manuals, pests, or crops...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        // --- Category Filters LazyRow ---
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // --- Type Filter Chips Row ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            types.forEach { type ->
                val label = when (type) {
                    "MANUAL" -> if (isTelugu) "📖 పుస్తకాలు" else "📖 Manuals"
                    "VIDEO" -> if (isTelugu) "🎬 వీడియోలు" else "🎬 Videos"
                    "INFOGRAPHIC" -> if (isTelugu) "🖼️ ఇన్ఫోగ్రాఫిక్స్" else "🖼️ Charts"
                    else -> if (isTelugu) "అన్నీ" else "All Types"
                }
                FilterChip(
                    selected = selectedType == type,
                    onClick = { selectedType = type },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- Manual Items List ---
        if (filteredManuals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Text(
                        text = if (isTelugu) "ఫలితాలు ఏవీ దొరకలేదు" else "No matching guides found",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredManuals, key = { it.id }) { manual ->
                    ManualCardItem(
                        manual = manual,
                        isTelugu = isTelugu,
                        onToggleCache = {
                            viewModel.toggleManualCacheStatus(manual.id, manual.isCachedOffline)
                            val msg = if (!manual.isCachedOffline) "💾 Downloaded for offline access" else "Removed from offline cache"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        onOpenViewer = {
                            activeViewerManual = manual
                        }
                    )
                }
            }
        }
    }

    // --- Offline Media Viewer Sheet Dialog ---
    activeViewerManual?.let { manual ->
        OfflineMediaViewerDialog(
            manual = manual,
            isTelugu = isTelugu,
            onDismiss = { activeViewerManual = null }
        )
    }
}

@Composable
private fun ManualCardItem(
    manual: OfflineManualEntity,
    isTelugu: Boolean,
    onToggleCache: () -> Unit,
    onOpenViewer: () -> Unit
) {
    val typeIcon = when (manual.type) {
        "VIDEO" -> Icons.Default.PlayCircle
        "INFOGRAPHIC" -> Icons.Default.Image
        else -> Icons.Default.MenuBook
    }

    val typeBadgeColor = when (manual.type) {
        "VIDEO" -> Color(0xFFDC2626)
        "INFOGRAPHIC" -> Color(0xFFD97706)
        else -> Color(0xFF2563EB)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenViewer() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(typeBadgeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = typeIcon,
                    contentDescription = manual.type,
                    tint = typeBadgeColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = typeBadgeColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = manual.cropCategory,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = typeBadgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (manual.type == "VIDEO" && manual.videoDurationMinutes > 0) {
                        Text(
                            text = "• ${manual.videoDurationMinutes} min",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = "• %.1f MB".format(manual.fileSizeMb),
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (isTelugu) manual.titleTe else manual.titleEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = if (isTelugu) manual.descriptionTe else manual.descriptionEn,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onToggleCache) {
                Icon(
                    imageVector = if (manual.isCachedOffline) Icons.Default.CheckCircle else Icons.Default.DownloadForOffline,
                    contentDescription = "Toggle Cache",
                    tint = if (manual.isCachedOffline) Color(0xFF16A34A) else Color.Gray,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun OfflineMediaViewerDialog(
    manual: OfflineManualEntity,
    isTelugu: Boolean,
    onDismiss: () -> Unit
) {
    var isPlayingVideo by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(if (isTelugu) "మూసివేయి (Close)" else "Close Guide")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (manual.type == "VIDEO") Icons.Default.PlayCircle else Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isTelugu) manual.titleTe else manual.titleEn,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                // Video Player Simulation Box if VIDEO type
                if (manual.type == "VIDEO") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { isPlayingVideo = !isPlayingVideo },
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(Color.White.copy(alpha = 0.8f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isPlayingVideo) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.Black,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Text(
                                text = if (isPlayingVideo) "▶️ Offline Video Playing (${manual.videoDurationMinutes}:00)" else "Tap to Play Offline Video",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Infographic Graphic Banner Simulation
                if (manual.type == "INFOGRAPHIC") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🖼️ LOCALIZED STEP-BY-STEP INFOGRAPHIC CHART", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = if (isTelugu) manual.contentMarkdownTe else manual.contentMarkdownEn, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF78350F))
                        }
                    }
                }

                // Content Markdown / Advisory Text
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isTelugu) manual.contentMarkdownTe else manual.contentMarkdownEn,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "💾 Status: Cached Local", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                    Text(text = "Last Sync: ${manual.lastUpdated}", fontSize = 10.sp, color = Color.Gray)
                }
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
