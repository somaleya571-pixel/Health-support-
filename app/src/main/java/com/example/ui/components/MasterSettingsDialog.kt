package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun MasterSettingsDialog(
    viewModel: HealthViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.language.collectAsState()
    val currentApiKey by viewModel.customApiKey.collectAsState()

    var apiKeyInput by remember { mutableStateOf(currentApiKey) }
    var isSaved by remember { mutableStateOf(false) }

    val isBn = language == AppLanguage.BANGLA

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MasterSurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MasterGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Key, contentDescription = "Master Key", tint = MasterGold, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = AppStrings.masterApiKey(language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = if (isBn)
                        "আপনার কাস্টম জেমিনি এপিআই কি (Gemini API Key) যুক্ত করুন যা আপনার স্ক্যানার এবং চ্যাটবটের সরাসরি সংযোগ নিশ্চিত করবে।"
                    else
                        "Configure your custom Gemini API key to power seamless food scanning and personalized AI health consultations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark,
                    lineHeight = 18.sp
                )

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = {
                        apiKeyInput = it
                        isSaved = false
                    },
                    label = { Text("GEMINI API KEY", color = TextSecondaryDark, fontSize = 11.sp) },
                    placeholder = { Text("AIzaSy...", color = TextMutedDark, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedBorderColor = MasterRedPrimary,
                        unfocusedBorderColor = MasterCardBorder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("master_api_key_input")
                )

                // Language Selector inside dialog
                Text(
                    text = if (isBn) "ভাষা নির্বাচন (Language):" else "Select Language:",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = language == lang
                        Button(
                            onClick = {
                                if (language != lang) viewModel.toggleLanguage()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MasterRedPrimary else MasterCardBgDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${lang.flag} ${lang.displayName}",
                                color = if (isSelected) TextPrimaryDark else TextSecondaryDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Security Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MasterDarkBg)
                        .padding(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = "Security", tint = HealthGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "এনক্রিপ্টকৃত অন-ডিভাইস স্টোরেজ" else "Encrypted on-device local storage",
                        fontSize = 10.sp,
                        color = HealthGreen
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.setCustomApiKey(apiKeyInput)
                    isSaved = true
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MasterRedPrimary)
            ) {
                Text(
                    text = if (isBn) "সংরক্ষণ করুন" else "Save Key",
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isBn) "বন্ধ করুন" else "Close",
                    color = TextSecondaryDark
                )
            }
        }
    )
}
