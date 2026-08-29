package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LiveClockWidget
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.CityLocation
import com.example.util.PrayerTimeCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerScreen(
    viewModel: HealthViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val schedule by viewModel.prayerSchedule.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val context = LocalContext.current

    var showCityDropdown by remember { mutableStateOf(false) }

    val isBn = language == AppLanguage.BANGLA

    // Location Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            try {
                val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                loc?.let { viewModel.updateGpsLocation(it) }
            } catch (_: SecurityException) {}
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
        // Location Selector Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MasterSurfaceDark)
                    .border(1.dp, MasterCardBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp)
                    .testTag("prayer_location_bar")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showCityDropdown = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HealthGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = HealthGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = if (isBn) "নির্বাচিত অবস্থান" else "Active Location",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isBn) selectedCity.nameBn else selectedCity.nameEn,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select City",
                                    tint = MasterGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // GPS Auto-detect Button
                    IconButton(
                        onClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MasterCardBgDark)
                            .border(0.5.dp, MasterRedPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .testTag("gps_detect_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Auto GPS",
                            tint = MasterRedPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Live Clock
        item {
            LiveClockWidget(language = language)
        }

        // Active Prayer & Next Countdown Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF192A20),
                                Color(0xFF131822),
                                Color(0xFF1F1216)
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                HealthGreen.copy(alpha = 0.7f),
                                MasterGold.copy(alpha = 0.5f),
                                MasterRedPrimary.copy(alpha = 0.4f)
                            )
                        ),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(20.dp)
                    .testTag("active_prayer_banner")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(HealthGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn) "বর্তমান ওয়াক্ত" else "CURRENT WAQT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HealthGreen,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MasterDarkBg.copy(alpha = 0.6f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = schedule.dateDisplay,
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isBn) schedule.activePrayerNameBn else schedule.activePrayerNameEn,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp
                        ),
                        color = TextPrimaryDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Divider(color = MasterCardBorder)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = AppStrings.nextPrayer(language),
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                            Text(
                                text = if (isBn) schedule.nextPrayerNameBn else schedule.nextPrayerNameEn,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MasterGold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MasterRedPrimary.copy(alpha = 0.2f))
                                .border(1.dp, MasterRedPrimary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = schedule.timeRemainingNext,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MasterRedLight
                            )
                        }
                    }
                }
            }
        }

        // Jummah Special Card (Friday Prayer)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF2B220B),
                                Color(0xFF181D26)
                            )
                        )
                    )
                    .border(1.dp, MasterGold.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(16.dp)
                    .testTag("jummah_special_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MasterGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mosque,
                                contentDescription = "Jummah",
                                tint = MasterGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = AppStrings.jummahSpecial(language),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MasterGold
                            )
                            Text(
                                text = if (isBn) "জুম্মার খুতবা ও জামাত: ০১:১৫ PM" else "Jummah Khutbah & Jamat: 01:15 PM",
                                fontSize = 12.sp,
                                color = TextPrimaryDark
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MasterGold)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isBn) "শুক্রবার" else "FRIDAY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = MasterDarkBg
                        )
                    }
                }
            }
        }

        // Daily 5 Waqt Prayer Times List
        item {
            Text(
                text = if (isBn) "প্রতিদিনের ৫ ওয়াক্তের সময়সূচি" else "Daily 5 Waqt Prayer Timetable",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
            )
        }

        item {
            PrayerTimeRow(
                nameEn = "Fajr (Dawn)",
                nameBn = "ফজর (ভোর)",
                time = schedule.fajr,
                isActive = schedule.activePrayerNameEn.contains("Fajr"),
                icon = Icons.Default.WbTwilight
            )
        }

        item {
            PrayerTimeRow(
                nameEn = "Sunrise / Ishraq",
                nameBn = "সূর্যোদয় / ইশরাক",
                time = schedule.sunrise,
                isActive = false,
                icon = Icons.Default.WbSunny
            )
        }

        item {
            PrayerTimeRow(
                nameEn = "Dhuhr (Noon)",
                nameBn = "জোহর (দুপুর)",
                time = schedule.dhuhr,
                isActive = schedule.activePrayerNameEn.contains("Dhuhr"),
                icon = Icons.Default.LightMode
            )
        }

        item {
            PrayerTimeRow(
                nameEn = "Asr (Afternoon)",
                nameBn = "আসর (বিকাল)",
                time = schedule.asr,
                isActive = schedule.activePrayerNameEn.contains("Asr"),
                icon = Icons.Default.WbCloudy
            )
        }

        item {
            PrayerTimeRow(
                nameEn = "Maghrib (Sunset)",
                nameBn = "মাগরিব (সন্ধ্যা)",
                time = schedule.maghrib,
                isActive = schedule.activePrayerNameEn.contains("Maghrib"),
                icon = Icons.Default.NightlightRound
            )
        }

        item {
            PrayerTimeRow(
                nameEn = "Isha (Night)",
                nameBn = "এশা (রাত)",
                time = schedule.isha,
                isActive = schedule.activePrayerNameEn.contains("Isha"),
                icon = Icons.Default.Bedtime
            )
        }

        // Qibla Angle Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MasterSurfaceDark)
                    .border(1.dp, MasterCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .testTag("qibla_compass_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HealthCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = "Qibla",
                                tint = HealthCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = AppStrings.qiblaDirection(language),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark
                            )
                            Text(
                                text = if (isBn) "মক্কার ক্বিবলা কোণ: ${schedule.qiblaAngle.toInt()}° (উত্তর-পশ্চিম)"
                                else "Makkah Qibla Angle: ${schedule.qiblaAngle.toInt()}° North-West",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Text(
                        text = "${schedule.qiblaAngle.toInt()}°",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = HealthCyan
                    )
                }
            }
        }
    }

    // City Selection Dialog
    if (showCityDropdown) {
        AlertDialog(
            onDismissRequest = { showCityDropdown = false },
            containerColor = MasterSurfaceDark,
            title = {
                Text(
                    text = if (isBn) "শহর নির্বাচন করুন" else "Select City Location",
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(PrayerTimeCalculator.PRESET_CITIES.size) { idx ->
                        val city = PrayerTimeCalculator.PRESET_CITIES[idx]
                        val isSelected = city == selectedCity
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MasterRedPrimary.copy(alpha = 0.2f) else MasterCardBgDark)
                                .border(
                                    0.5.dp,
                                    if (isSelected) MasterRedPrimary else MasterCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    viewModel.selectCity(city)
                                    showCityDropdown = false
                                }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = if (isBn) city.nameBn else city.nameEn,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MasterRedLight else TextPrimaryDark
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDropdown = false }) {
                    Text(if (isBn) "ঠিক আছে" else "Close", color = MasterGold)
                }
            }
        )
    }
}

@Composable
private fun PrayerTimeRow(
    nameEn: String,
    nameBn: String,
    time: String,
    isActive: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isActive) MasterSurfaceDark else MasterSurfaceDark.copy(alpha = 0.6f))
            .border(
                1.dp,
                if (isActive) HealthGreen.copy(alpha = 0.6f) else MasterCardBorder,
                RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActive) HealthGreen.copy(alpha = 0.2f) else MasterCardBgDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = nameEn,
                        tint = if (isActive) HealthGreen else TextSecondaryDark,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = nameBn,
                        fontSize = 13.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (isActive) TextPrimaryDark else TextSecondaryDark
                    )
                    Text(
                        text = nameEn,
                        fontSize = 10.sp,
                        color = TextMutedDark
                    )
                }
            }

            Text(
                text = time,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                ),
                color = if (isActive) HealthGreen else TextPrimaryDark
            )
        }
    }
}
