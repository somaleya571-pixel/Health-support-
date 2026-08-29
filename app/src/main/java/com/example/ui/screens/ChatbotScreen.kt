package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.HealthViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import kotlinx.coroutines.launch

@Composable
fun ChatbotScreen(
    viewModel: HealthViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isChatLoading.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val isBn = language == AppLanguage.BANGLA

    val promptSuggestions = if (isBn) listOf(
        "🌿 ওজন কমানোর সহজ ডায়েট চার্ট দাও",
        "💪 মাসল বিল্ড করার জন্য হাই-প্রোটিন রুটিন",
        "🍎 কোন খাবারে কেমন ফাইবার ও ক্যালোরি থাকে?",
        "💧 দৈনিক কত লিটার পানি পান করা আবশ্যক?",
        "🏃 ঘরে বসে ১৫ মিনিটের কার্ডিও ওয়ার্কআউট প্ল্যান"
    ) else listOf(
        "🌿 7-Day Clean Healthy Diet Plan",
        "💪 High-Protein Muscle Building Routine",
        "🍎 Best low-calorie high-fiber foods",
        "💧 Daily hydration and metabolic guide",
        "🏃 15-Minute Home Cardio Workout"
    )

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MasterDarkBg)
            .padding(bottom = 90.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MasterSurfaceDark)
                .border(1.dp, MasterCardBorder)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
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
                            .background(
                                Brush.linearGradient(listOf(MasterRedPrimary, MasterGold))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "Gemini AI",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = AppStrings.chatTitle(language),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
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
                                text = if (isBn) "জেমিনি ৩.৫ ফ্ল্যাশ সক্রিয়" else "Gemini 3.5 Flash Active",
                                fontSize = 10.sp,
                                color = HealthGreen
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { viewModel.setSettingsSheetVisible(true) },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MasterCardBgDark)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Config", tint = MasterGold, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Suggestions Carousel
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(promptSuggestions) { suggestion ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MasterSurfaceDark)
                        .border(0.5.dp, MasterRedPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable {
                            viewModel.sendUserMessage(suggestion)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = suggestion,
                        fontSize = 11.sp,
                        color = TextPrimaryDark
                    )
                }
            }
        }

        // Chat Messages Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(chatMessages, key = { it.id }) { msg ->
                ChatBubble(message = msg)
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MasterRedPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isBn) "জেমিনি পরামর্শ তৈরি করছে..." else "Gemini is analyzing your request...",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
            }
        }

        // Bottom Chat Input Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MasterSurfaceDark)
                .border(1.dp, MasterCardBorder)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = AppStrings.askAnything(language),
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedBorderColor = MasterRedPrimary,
                        unfocusedBorderColor = MasterCardBorder
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val textToSend = inputText
                            inputText = ""
                            viewModel.sendUserMessage(textToSend)
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(MasterRedPrimary, MasterRedDark))
                        )
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MasterRedPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI",
                    tint = MasterRedPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) Brush.linearGradient(listOf(MasterRedPrimary, MasterRedDark))
                    else Brush.linearGradient(listOf(MasterSurfaceDark, MasterCardBgDark))
                )
                .border(
                    1.dp,
                    if (isUser) MasterRedPrimary else MasterCardBorder,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(14.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimaryDark,
                lineHeight = 20.sp
            )
        }
    }
}
