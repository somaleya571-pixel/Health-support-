package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.FoodScanResult
import com.example.data.local.ScannedFoodEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ScannerScreen(
    viewModel: HealthViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val scannedBitmap by viewModel.scannedBitmap.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val scanResult by viewModel.foodScanResult.collectAsState()
    val foodLogs by viewModel.foodLogs.collectAsState()

    val isBn = language == AppLanguage.BANGLA

    // Camera Capture Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let { viewModel.setScannedBitmap(it) }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        }
    }

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                viewModel.setScannedBitmap(bitmap)
            } catch (_: Exception) {}
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MasterDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scanner Hero Title Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF2B0E14),
                                Color(0xFF161B24),
                                Color(0xFF0F131C)
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                MasterRedPrimary,
                                MasterGold.copy(alpha = 0.5f)
                            )
                        ),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MasterRedPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera Scanner",
                                tint = MasterRedPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = AppStrings.scanTitle(language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark
                            )
                            Text(
                                text = if (isBn) "গুগল স্ক্যান ও জেমিনি ভিশন নিউট্রিশন এনালাইজার"
                                else "Google Scan & Gemini Vision Nutrition Engine",
                                fontSize = 11.sp,
                                color = MasterGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = AppStrings.scanSubtitle(language),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons (Camera & Gallery)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("camera_scan_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MasterRedPrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Camera", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = AppStrings.capturePhoto(language), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("gallery_pick_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryDark),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(MasterGold, MasterRedPrimary))
                            ),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = "Gallery", tint = MasterGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = AppStrings.selectGallery(language), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Image Preview & Scanning Indicator
        if (scannedBitmap != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MasterSurfaceDark)
                        .border(1.dp, MasterCardBorder, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = scannedBitmap!!.asImageBitmap(),
                        contentDescription = "Scanned Food",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    if (isScanning) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MasterRedPrimary)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (isBn) "খাবারের উপাদান স্ক্যান হচ্ছে..." else "Analyzing Food & Calories...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            }
                        }
                    }
                }
            }
        }

        // Nutrition Scan Result Card
        if (scanResult != null) {
            item {
                FoodResultCard(
                    scan = scanResult!!,
                    language = language,
                    onGoogleSearch = { foodName ->
                        try {
                            val encoded = URLEncoder.encode("nutritional facts ingredients calories of $foodName", StandardCharsets.UTF_8.toString())
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$encoded"))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    onAddCalories = { cal ->
                        viewModel.addCaloriesConsumed(cal)
                    }
                )
            }
        }

        // Scanned Food History List Header
        item {
            Text(
                text = if (isBn) "পূর্বের স্ক্যান করা খাবারসমূহ" else "Recently Scanned Food History",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )
        }

        if (foodLogs.isEmpty() && scanResult == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MasterSurfaceDark)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBn) "কোনো খাবার স্ক্যান করা হয়নি। উপরে ছবি তুলুন বা গ্যালারি থেকে নির্বাচন করুন।"
                        else "No food scanned yet. Take a picture or select an image from gallery above.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        items(foodLogs, key = { it.id }) { log ->
            ScannedFoodHistoryItem(log = log, language = language, onDelete = { viewModel.deleteScannedFood(log.id) })
        }
    }
}

@Composable
private fun FoodResultCard(
    scan: FoodScanResult,
    language: AppLanguage,
    onGoogleSearch: (String) -> Unit,
    onAddCalories: (Int) -> Unit
) {
    val isBn = language == AppLanguage.BANGLA
    var addedToLog by remember { mutableStateOf(false) }

    val verdictColor = when (scan.healthRating) {
        "EXCELLENT" -> HealthGreen
        "MODERATE" -> MasterGold
        else -> MasterRedPrimary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MasterSurfaceDark)
            .border(1.5.dp, verdictColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag("food_scan_result_card")
    ) {
        Column {
            // Food Name & Health Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBn) scan.foodNameBn else scan.foodNameEn,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = TextPrimaryDark
                    )
                    Text(
                        text = if (isBn) scan.foodNameEn else scan.foodNameBn,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(verdictColor.copy(alpha = 0.2f))
                        .border(1.dp, verdictColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = scan.healthRating,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = verdictColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calories Highlight Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MasterCardBgDark)
                    .border(0.5.dp, MasterCardBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = "Calories", tint = MasterGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "মোট ক্যালোরি" else "Total Energy",
                            fontSize = 13.sp,
                            color = TextSecondaryDark
                        )
                    }

                    Text(
                        text = "${scan.calories} kcal",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = MasterGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Macros Breakdown Grid
            Text(
                text = AppStrings.nutritionFacts(language),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MacroCard(
                    modifier = Modifier.weight(1f),
                    label = if (isBn) "প্রোটিন" else "Protein",
                    value = "${scan.proteinGrams}g",
                    color = MasterRedPrimary
                )
                MacroCard(
                    modifier = Modifier.weight(1f),
                    label = if (isBn) "কার্বস" else "Carbs",
                    value = "${scan.carbsGrams}g",
                    color = MasterGold
                )
                MacroCard(
                    modifier = Modifier.weight(1f),
                    label = if (isBn) "ফ্যাট" else "Fats",
                    value = "${scan.fatGrams}g",
                    color = HealthOrange
                )
                MacroCard(
                    modifier = Modifier.weight(1f),
                    label = if (isBn) "ফাইবার" else "Fiber",
                    value = "${scan.fiberGrams}g",
                    color = HealthGreen
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Ingredients
            if (scan.ingredients.isNotBlank()) {
                Text(
                    text = AppStrings.ingredients(language),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = scan.ingredients,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Health Advice Verdict
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MasterDarkBg)
                    .padding(12.dp)
            ) {
                Text(
                    text = if (isBn) scan.healthAdviceBn else scan.healthAdviceEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryDark.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dual Action Buttons: Google Scan & Add to Daily Log
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Google Chrome Search button
                OutlinedButton(
                    onClick = { onGoogleSearch(scan.foodNameEn) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("google_scan_chrome_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryDark),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(HealthCyan, MasterGold))
                    )
                ) {
                    Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = "Google Search", tint = HealthCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "গুগল স্ক্যান" else "Google Scan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Add to Daily Calorie Log Button
                Button(
                    onClick = {
                        onAddCalories(scan.calories)
                        addedToLog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_calorie_log_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (addedToLog) HealthGreen else MasterRedPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (addedToLog) Icons.Default.Check else Icons.Default.AddChart,
                        contentDescription = "Log Calorie",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (addedToLog) (if (isBn) "লগ হয়েছে ✓" else "Logged ✓") else AppStrings.addToDailyLog(language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MasterCardBgDark)
            .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 10.sp, color = TextSecondaryDark)
        }
    }
}

@Composable
private fun ScannedFoodHistoryItem(
    log: ScannedFoodEntity,
    language: AppLanguage,
    onDelete: () -> Unit
) {
    val isBn = language == AppLanguage.BANGLA

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MasterSurfaceDark)
            .border(1.dp, MasterCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isBn) log.foodNameBn else log.foodNameEn,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
                Text(
                    text = "${log.calories} kcal  •  P: ${log.proteinGrams}g  •  C: ${log.carbsGrams}g  •  F: ${log.fatGrams}g",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMutedDark, modifier = Modifier.size(16.dp))
            }
        }
    }
}
