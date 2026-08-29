package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.RoutineItemEntity
import com.example.ui.components.CircularActivityRing
import com.example.ui.components.LiveClockWidget
import com.example.ui.components.MasterCardHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun HomeScreen(
    viewModel: HealthViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val routines by viewModel.routines.collectAsState()
    val summary by viewModel.healthSummary.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }

    // Progress calculations
    val stepsProgress = if (summary.stepsGoal > 0) summary.steps.toFloat() / summary.stepsGoal else 0f
    val waterProgress = if (summary.waterGoalMl > 0) summary.waterMl.toFloat() / summary.waterGoalMl else 0f
    val calProgress = if (summary.caloriesBurnedGoal > 0) summary.caloriesBurned.toFloat() / summary.caloriesBurnedGoal else 0f
    val prayerProgress = if (summary.prayersGoal > 0) summary.prayersCompleted.toFloat() / summary.prayersGoal else 0f

    val completedRoutines = routines.count { it.isCompleted }
    val totalRoutines = routines.size
    val routinePercentage = if (totalRoutines > 0) (completedRoutines * 100) / totalRoutines else 0

    val overallScore = ((stepsProgress.coerceIn(0f, 1f) +
            waterProgress.coerceIn(0f, 1f) +
            calProgress.coerceIn(0f, 1f) +
            prayerProgress.coerceIn(0f, 1f)) / 4f * 100).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MasterDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Card Identity Header
        item {
            MasterCardHeader(
                language = language,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onOpenMasterSettings = { viewModel.setSettingsSheetVisible(true) }
            )
        }

        // Live Clock Widget
        item {
            LiveClockWidget(language = language)
        }

        // Circular Multi-Ring Activity Graph
        item {
            CircularActivityRing(
                stepsProgress = stepsProgress,
                waterProgress = waterProgress,
                calorieProgress = calProgress,
                prayerProgress = prayerProgress,
                overallPercentage = overallScore,
                language = language
            )
        }

        // Quick Action Tracker Chips
        item {
            QuickActionsBar(
                language = language,
                waterMl = summary.waterMl,
                steps = summary.steps,
                calories = summary.caloriesBurned,
                onAddWater = { viewModel.addWater(250) },
                onAddSteps = { viewModel.addSteps(500) }
            )
        }

        // Daily Golden Health Tip Card
        item {
            DailyHealthTipCard(language = language)
        }

        // Routine Tracker Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = AppStrings.dailyRoutines(language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "$completedRoutines/$totalRoutines ${if (language == AppLanguage.BANGLA) "কাজ সম্পন্ন" else "tasks completed"} ($routinePercentage%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                    )
                }

                Button(
                    onClick = { showAddTaskDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MasterRedPrimary,
                        contentColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_custom_routine_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA) "নতুন কাজ" else "Add Task",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Routine Items List
        items(routines, key = { it.id }) { routine ->
            RoutineItemCard(
                item = routine,
                language = language,
                onToggle = { viewModel.toggleRoutineCompleted(routine) },
                onDelete = { viewModel.deleteRoutine(routine.id) }
            )
        }
    }

    if (showAddTaskDialog) {
        AddCustomTaskDialog(
            language = language,
            onDismiss = { showAddTaskDialog = false },
            onAddTask = { title, cat, time ->
                viewModel.addCustomRoutine(title, cat, time)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
private fun QuickActionsBar(
    language: AppLanguage,
    waterMl: Int,
    steps: Int,
    calories: Int,
    onAddWater: () -> Unit,
    onAddSteps: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MasterSurfaceDark)
            .border(1.dp, MasterCardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Text(
            text = AppStrings.quickLog(language),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Water Quick Log
            QuickActionItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.WaterDrop,
                iconColor = HealthCyan,
                title = "+250ml",
                subtitle = "$waterMl / 3000 ml",
                onClick = onAddWater,
                testTag = "quick_add_water_button"
            )

            // Steps Quick Log
            QuickActionItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.DirectionsWalk,
                iconColor = MasterRedPrimary,
                title = "+500 Steps",
                subtitle = "$steps / 8000",
                onClick = onAddSteps,
                testTag = "quick_add_steps_button"
            )

            // Calories Burned
            QuickActionItem(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalFireDepartment,
                iconColor = MasterGold,
                title = "$calories kcal",
                subtitle = if (language == AppLanguage.BANGLA) "বার্ন হয়েছে" else "Burned",
                onClick = { /* Display info */ },
                testTag = "quick_view_calories_card"
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MasterCardBgDark)
            .border(0.5.dp, MasterCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(10.dp)
            .testTag(testTag)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            Text(text = subtitle, fontSize = 9.sp, color = TextSecondaryDark, maxLines = 1)
        }
    }
}

@Composable
private fun DailyHealthTipCard(language: AppLanguage) {
    val tipTitle = if (language == AppLanguage.BANGLA) "💧 আর্দ্রতা ও মেটাবলিজম" else "💧 Hydration & Metabolism"
    val tipDesc = if (language == AppLanguage.BANGLA)
        "সকালে ঘুম থেকে উঠে খালি পেটে ২ গ্লাস কুসুম গরম পানি পান করলে পরিপাকতন্ত্র দ্রুত সচল হয় এবং মেটাবলিক রেট প্রায় ৩০% পর্যন্ত বৃদ্ধি পায়।"
    else
        "Drinking 2 glasses of lukewarm water right after waking up jumpstarts your digestion and elevates resting metabolic rate by nearly 30%."

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E281F),
                        Color(0xFF131822)
                    )
                )
            )
            .border(1.dp, HealthGreen.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("health_tip_card")
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(HealthGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TipsAndUpdates,
                    contentDescription = "Health Tip",
                    tint = HealthGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = tipTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = HealthGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tipDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryDark.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

private val modifier = Modifier

@Composable
private fun RoutineItemCard(
    item: RoutineItemEntity,
    language: AppLanguage,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val isBn = language == AppLanguage.BANGLA
    val title = if (isBn) item.titleBn else item.titleEn

    val icon = when (item.category) {
        "WATER" -> Icons.Default.WaterDrop
        "PRAYER" -> Icons.Default.Mosque
        "EXERCISE" -> Icons.Default.FitnessCenter
        "MEAL" -> Icons.Default.Restaurant
        "SLEEP" -> Icons.Default.Bedtime
        else -> Icons.Default.CheckCircleOutline
    }

    val iconColor = when (item.category) {
        "WATER" -> HealthCyan
        "PRAYER" -> HealthGreen
        "EXERCISE" -> MasterRedPrimary
        "MEAL" -> MasterGold
        "SLEEP" -> HealthPurple
        else -> TextSecondaryDark
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (item.isCompleted) MasterSurfaceDark.copy(alpha = 0.6f) else MasterSurfaceDark)
            .border(
                1.dp,
                if (item.isCompleted) HealthGreen.copy(alpha = 0.3f) else MasterCardBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable { onToggle() }
            .padding(14.dp)
            .testTag("routine_item_${item.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Checkbox status
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (item.isCompleted) HealthGreen else Color.Transparent
                        )
                        .border(
                            1.5.dp,
                            if (item.isCompleted) HealthGreen else TextSecondaryDark,
                            RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = MasterDarkBg,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = item.category, tint = iconColor, modifier = Modifier.size(18.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (item.isCompleted) FontWeight.Normal else FontWeight.SemiBold
                        ),
                        color = if (item.isCompleted) TextSecondaryDark else TextPrimaryDark
                    )
                    Text(
                        text = item.timeSlot,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Routine",
                    tint = TextMutedDark,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AddCustomTaskDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onAddTask: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("EXERCISE") }
    var timeSlot by remember { mutableStateOf("05:00 PM") }

    val isBn = language == AppLanguage.BANGLA

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MasterSurfaceDark,
        title = {
            Text(
                text = if (isBn) "নতুন দৈনিক কাজ যোগ করুন" else "Add New Daily Routine Task",
                color = TextPrimaryDark,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isBn) "কাজের বিবরণ" else "Task Title", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedBorderColor = MasterRedPrimary,
                        unfocusedBorderColor = MasterCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_task_input")
                )

                OutlinedTextField(
                    value = timeSlot,
                    onValueChange = { timeSlot = it },
                    label = { Text(if (isBn) "সময় (যেমন: 07:00 AM)" else "Time Slot", color = TextSecondaryDark) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedBorderColor = MasterRedPrimary,
                        unfocusedBorderColor = MasterCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = if (isBn) "ক্যাটাগরি নির্বাচন করুন:" else "Select Category:",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("EXERCISE", "WATER", "MEAL", "SLEEP", "PRAYER").forEach { cat ->
                        val isSel = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MasterRedPrimary else MasterCardBgDark)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) TextPrimaryDark else TextSecondaryDark
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAddTask(title, selectedCategory, timeSlot) },
                colors = ButtonDefaults.buttonColors(containerColor = MasterRedPrimary)
            ) {
                Text(if (isBn) "যোগ করুন" else "Add Task", color = TextPrimaryDark)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBn) "বাতিল" else "Cancel", color = TextSecondaryDark)
            }
        }
    )
}
