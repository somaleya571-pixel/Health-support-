package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun MasterCardHeader(
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onOpenMasterSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF280B10),
                        Color(0xFF181D28),
                        Color(0xFF0F131C)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        MasterRedPrimary.copy(alpha = 0.8f),
                        MasterGold.copy(alpha = 0.5f),
                        MasterRedDark.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
            .testTag("master_card_header")
    ) {
        Column {
            // Top Row: Logo, Brand & Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // App Logo Icon
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "Health Conscious Logo",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MasterRedPrimary.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = AppStrings.appTitle(language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            ),
                            color = TextPrimaryDark
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(HealthGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "MASTER EDITION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MasterGold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Header Action Buttons
                Row {
                    // Language Switcher (Bangla / English)
                    IconButton(
                        onClick = onToggleLanguage,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MasterSurfaceDark)
                            .border(0.5.dp, MasterCardBorder, RoundedCornerShape(8.dp))
                            .testTag("language_toggle_button")
                    ) {
                        Text(
                            text = language.flag,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Master Key / Settings
                    IconButton(
                        onClick = onOpenMasterSettings,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MasterSurfaceDark)
                            .border(0.5.dp, MasterGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .testTag("master_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Master API Settings",
                            tint = MasterGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Middle: Mastercard Chip & Contactless wave icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gold Security Chip Simulation
                Box(
                    modifier = Modifier
                        .size(width = 38.dp, height = 28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFFDF7A),
                                    Color(0xFFD4AF37),
                                    Color(0xFF996515)
                                )
                            )
                        )
                        .border(0.5.dp, Color.Black.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(0.5.dp, Color(0xFF6B4406), RoundedCornerShape(2.dp))
                    )
                }

                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = "Wireless Sync",
                    tint = TextSecondaryDark,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card Number / Wellness Identity
            Text(
                text = "8800  •  7712  •  HEALTH  •  2026",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = TextPrimaryDark.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Row: Holder & Tier
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = if (language == AppLanguage.BANGLA) "সদস্য স্ট্যাটাস" else "MEMBERSHIP TIER",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondaryDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (language == AppLanguage.BANGLA) "ফিটনেস মাস্টার" else "FITNESS MASTER ELITE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark
                    )
                }

                // MasterCard dual-circle inspired badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MasterRedPrimary.copy(alpha = 0.85f))
                    )
                    Box(
                        modifier = Modifier
                            .offset(x = (-8).dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MasterGold.copy(alpha = 0.85f))
                    )
                }
            }
        }
    }
}
