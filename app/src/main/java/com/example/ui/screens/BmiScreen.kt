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
import com.example.data.local.BmiRecordEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.BmiCategory
import com.example.util.FoodAdviceItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BmiScreen(
    viewModel: HealthViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val heightCm by viewModel.heightCm.collectAsState()
    val weightKg by viewModel.weightKg.collectAsState()
    val age by viewModel.age.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val bmiResult by viewModel.bmiResult.collectAsState()
    val bmiHistory by viewModel.bmiHistory.collectAsState()

    val isBn = language == AppLanguage.BANGLA
    var savedToast by remember { mutableStateOf(false) }

    val categoryColor = when (bmiResult.category) {
        BmiCategory.NORMAL -> HealthGreen
        BmiCategory.UNDERWEIGHT -> HealthCyan
        BmiCategory.OVERWEIGHT -> MasterGold
        BmiCategory.OBESE -> MasterRedPrimary
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MasterDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // BMI Hero Calculator Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF241318),
                                Color(0xFF141924),
                                Color(0xFF0E121B)
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.linearGradient(
                            colors = listOf(categoryColor, MasterRedPrimary.copy(alpha = 0.4f))
                        ),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(20.dp)
                    .testTag("bmi_calculator_card")
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
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(categoryColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = "BMI",
                                    tint = categoryColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = AppStrings.bmiTitle(language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimaryDark
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(categoryColor.copy(alpha = 0.2f))
                                .border(1.dp, categoryColor, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isBn) bmiResult.category.labelBn else bmiResult.category.labelEn,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = categoryColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // BMI Big Score & Ideal Weight Range
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isBn) "আপনার বর্তমান বিএমআই" else "Current BMI Index",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                            Text(
                                text = "${bmiResult.bmi}",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 42.sp
                                ),
                                color = categoryColor
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = AppStrings.idealWeight(language),
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                            Text(
                                text = "${bmiResult.idealWeightMinKg} - ${bmiResult.idealWeightMaxKg} kg",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            if (bmiResult.diffFromIdealKg > 0) {
                                Text(
                                    text = if (bmiResult.category == BmiCategory.UNDERWEIGHT)
                                        (if (isBn) "+${bmiResult.diffFromIdealKg} কেজি বাড়ানো লক্ষ্য" else "+${bmiResult.diffFromIdealKg} kg to gain")
                                    else
                                        (if (isBn) "-${bmiResult.diffFromIdealKg} কেজি কমানো লক্ষ্য" else "-${bmiResult.diffFromIdealKg} kg to lose"),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MasterRedLight
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Height Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isBn) "উচ্চতা" else "Height", fontSize = 12.sp, color = TextSecondaryDark)
                        val feet = (heightCm / 30.48f).toInt()
                        val inches = ((heightCm % 30.48f) / 2.54f).toInt()
                        Text(
                            text = "${heightCm.toInt()} cm (${feet}′ ${inches}″)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }
                    Slider(
                        value = heightCm,
                        onValueChange = { viewModel.updateBmiInputs(it, weightKg, age, gender) },
                        valueRange = 120f..220f,
                        colors = SliderDefaults.colors(
                            thumbColor = MasterRedPrimary,
                            activeTrackColor = MasterRedPrimary,
                            inactiveTrackColor = MasterCardBgDark
                        ),
                        modifier = Modifier.testTag("height_slider")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Weight Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (isBn) "ওজন" else "Weight", fontSize = 12.sp, color = TextSecondaryDark)
                        Text(
                            text = "${weightKg.toInt()} kg (${(weightKg * 2.20462f).toInt()} lbs)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }
                    Slider(
                        value = weightKg,
                        onValueChange = { viewModel.updateBmiInputs(heightCm, it, age, gender) },
                        valueRange = 30f..160f,
                        colors = SliderDefaults.colors(
                            thumbColor = MasterGold,
                            activeTrackColor = MasterGold,
                            inactiveTrackColor = MasterCardBgDark
                        ),
                        modifier = Modifier.testTag("weight_slider")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gender Selector & Save Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Male" to "পুরুষ", "Female" to "নারী").forEach { (genKey, genLabel) ->
                                val isSel = gender == genKey
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) MasterRedPrimary else MasterCardBgDark)
                                        .border(
                                            0.5.dp,
                                            if (isSel) MasterRedPrimary else MasterCardBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.updateBmiInputs(heightCm, weightKg, age, genKey) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (isBn) genLabel else genKey,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) TextPrimaryDark else TextSecondaryDark
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.saveCurrentBmiRecord()
                                savedToast = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (savedToast) HealthGreen else MasterRedPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("save_bmi_record_button")
                        ) {
                            Icon(
                                imageVector = if (savedToast) Icons.Default.Check else Icons.Default.BookmarkAdd,
                                contentDescription = "Save",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (savedToast) (if (isBn) "সংরক্ষিত ✓" else "Saved ✓") else (if (isBn) "রেকর্ড রাখুন" else "Save Log"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section 1: Foods to Eat (খাওয়া উচিত)
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(HealthGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.whatToEat(language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = HealthGreen
                )
            }
        }

        items(bmiResult.foodsToEat) { food ->
            FoodAdviceCard(food = food, isAvoid = false, isBn = isBn)
        }

        // Section 2: Foods to Avoid (পরিহার/এভয়েড করা উচিত)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MasterRedPrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.whatToAvoid(language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MasterRedLight
                )
            }
        }

        items(bmiResult.foodsToAvoid) { food ->
            FoodAdviceCard(food = food, isAvoid = true, isBn = isBn)
        }

        // Section 3: Recommended Daily Fitness Routines for this BMI
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MasterSurfaceDark)
                    .border(1.dp, MasterCardBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = if (isBn) "🏃 এই বিএমআই এর জন্য উপযোগী শরীরচর্চা ও অভ্যাস"
                        else "🏃 Tailored Fitness Habits for Your Category",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MasterGold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val tips = if (isBn) bmiResult.fitnessTipsBn else bmiResult.fitnessTips
                    tips.forEach { tip ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "•", color = MasterGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tip,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimaryDark.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // Section 4: BMI History Log
        if (bmiHistory.isNotEmpty()) {
            item {
                Text(
                    text = if (isBn) "পূর্বের বিএমআই ট্র্যাকিং হিস্টোরি" else "Past BMI Tracking History",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
            }

            items(bmiHistory.take(5)) { record ->
                BmiHistoryItem(record = record, isBn = isBn)
            }
        }
    }
}

@Composable
private fun FoodAdviceCard(
    food: FoodAdviceItem,
    isAvoid: Boolean,
    isBn: Boolean
) {
    val borderColor = if (isAvoid) MasterRedPrimary.copy(alpha = 0.35f) else HealthGreen.copy(alpha = 0.35f)
    val iconBg = if (isAvoid) MasterRedPrimary.copy(alpha = 0.15f) else HealthGreen.copy(alpha = 0.15f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MasterSurfaceDark)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = food.iconEmoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = if (isBn) food.titleBn else food.titleEn,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isAvoid) MasterRedLight else TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isBn) food.descriptionBn else food.descriptionEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun BmiHistoryItem(
    record: BmiRecordEntity,
    isBn: Boolean
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(record.timestamp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MasterSurfaceDark)
            .border(0.5.dp, MasterCardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${record.weightKg} kg  •  ${record.heightCm} cm",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )
                Text(text = dateStr, fontSize = 11.sp, color = TextSecondaryDark)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MasterCardBgDark)
                    .border(0.5.dp, MasterGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "BMI ${record.bmiValue}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MasterGold
                )
            }
        }
    }
}
