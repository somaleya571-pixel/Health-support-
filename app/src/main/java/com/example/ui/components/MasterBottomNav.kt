package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppStrings

enum class AppNavTab(val icon: ImageVector) {
    HOME(Icons.Default.Home),
    PRAYER(Icons.Default.Mosque),
    SCANNER(Icons.Default.CameraAlt),
    BMI(Icons.Default.FitnessCenter),
    CHATBOT(Icons.Default.SmartToy)
}

@Composable
fun MasterBottomNav(
    currentTab: AppNavTab,
    onSelectTab: (AppNavTab) -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MasterSurfaceDark)
            .border(1.dp, MasterCardBorder, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("master_bottom_navigation")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppNavTab.values().forEach { tab ->
                val isSelected = tab == currentTab
                val label = when (tab) {
                    AppNavTab.HOME -> AppStrings.navHome(language)
                    AppNavTab.PRAYER -> AppStrings.navPrayer(language)
                    AppNavTab.SCANNER -> AppStrings.navScanner(language)
                    AppNavTab.BMI -> AppStrings.navBmi(language)
                    AppNavTab.CHATBOT -> AppStrings.navChatbot(language)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectTab(tab) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("nav_tab_${tab.name.lowercase()}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 38.dp else 32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MasterRedPrimary.copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                color = if (isSelected) MasterRedPrimary.copy(alpha = 0.6f) else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = label,
                            tint = if (isSelected) MasterRedPrimary else TextSecondaryDark,
                            modifier = Modifier.size(if (isSelected) 22.dp else 20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MasterRedLight else TextSecondaryDark
                    )
                }
            }
        }
    }
}
