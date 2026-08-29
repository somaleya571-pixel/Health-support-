package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun CircularActivityRing(
    stepsProgress: Float,
    waterProgress: Float,
    calorieProgress: Float,
    prayerProgress: Float,
    overallPercentage: Int,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val animSteps by animateFloatAsState(targetValue = stepsProgress.coerceIn(0f, 1f), animationSpec = tween(1200), label = "steps")
    val animWater by animateFloatAsState(targetValue = waterProgress.coerceIn(0f, 1f), animationSpec = tween(1200), label = "water")
    val animCalorie by animateFloatAsState(targetValue = calorieProgress.coerceIn(0f, 1f), animationSpec = tween(1200), label = "cal")
    val animPrayer by animateFloatAsState(targetValue = prayerProgress.coerceIn(0f, 1f), animationSpec = tween(1200), label = "prayer")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MasterSurfaceDark)
            .border(1.dp, MasterCardBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag("circular_activity_graph_card")
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.dailyRingTitle(language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MasterRedPrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "$overallPercentage% DONE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MasterRedLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Radial Graph
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val strokeWidth = 11.dp.toPx()
                    val spacing = 15.dp.toPx()

                    // Ring 1: Steps (Outer - Crimson Red)
                    val radius1 = (size.minDimension / 2f) - (strokeWidth / 2f)
                    drawCircle(
                        color = MasterRedPrimary.copy(alpha = 0.15f),
                        radius = radius1,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = MasterRedPrimary,
                        startAngle = -90f,
                        sweepAngle = animSteps * 360f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius1, center.y - radius1),
                        size = Size(radius1 * 2, radius1 * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Ring 2: Water (Cyan)
                    val radius2 = radius1 - spacing
                    drawCircle(
                        color = HealthCyan.copy(alpha = 0.15f),
                        radius = radius2,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = HealthCyan,
                        startAngle = -90f,
                        sweepAngle = animWater * 360f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius2, center.y - radius2),
                        size = Size(radius2 * 2, radius2 * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Ring 3: Calories / Activity (Gold)
                    val radius3 = radius2 - spacing
                    drawCircle(
                        color = MasterGold.copy(alpha = 0.15f),
                        radius = radius3,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = MasterGold,
                        startAngle = -90f,
                        sweepAngle = animCalorie * 360f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius3, center.y - radius3),
                        size = Size(radius3 * 2, radius3 * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Ring 4: Prayer / Routine (Green)
                    val radius4 = radius3 - spacing
                    drawCircle(
                        color = HealthGreen.copy(alpha = 0.15f),
                        radius = radius4,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = HealthGreen,
                        startAngle = -90f,
                        sweepAngle = animPrayer * 360f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius4, center.y - radius4),
                        size = Size(radius4 * 2, radius4 * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Center Score Display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$overallPercentage%",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp
                        ),
                        color = TextPrimaryDark
                    )
                    Text(
                        text = if (language == AppLanguage.BANGLA) "দৈনিক স্কোর" else "Daily Score",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Legend / Stat indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RingLegendItem(
                    color = MasterRedPrimary,
                    label = AppStrings.steps(language),
                    value = "${(stepsProgress * 100).toInt()}%"
                )
                RingLegendItem(
                    color = HealthCyan,
                    label = AppStrings.water(language),
                    value = "${(waterProgress * 100).toInt()}%"
                )
                RingLegendItem(
                    color = MasterGold,
                    label = AppStrings.calories(language),
                    value = "${(calorieProgress * 100).toInt()}%"
                )
                RingLegendItem(
                    color = HealthGreen,
                    label = AppStrings.prayerCount(language),
                    value = "${(prayerProgress * 100).toInt()}%"
                )
            }
        }
    }
}

@Composable
private fun RingLegendItem(
    color: Color,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )
        }
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextSecondaryDark
        )
    }
}
