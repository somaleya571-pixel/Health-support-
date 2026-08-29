package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.ui.components.AppNavTab
import com.example.ui.components.MasterBottomNav
import com.example.ui.components.MasterSettingsDialog
import com.example.ui.screens.*
import com.example.ui.theme.HealthConsciousTheme
import com.example.ui.theme.MasterDarkBg
import com.example.ui.viewmodel.HealthViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: HealthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthConsciousTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: HealthViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val language by viewModel.language.collectAsState()
    val showSettings by viewModel.showSettingsSheet.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MasterDarkBg)
    ) {
        // Master Card Textured Background
        Image(
            painter = painterResource(id = R.drawable.master_card_bg),
            contentDescription = "Master Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.35f
        )

        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = {
                MasterBottomNav(
                    currentTab = currentTab,
                    onSelectTab = { viewModel.setNavTab(it) },
                    language = language
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    AppNavTab.HOME -> HomeScreen(viewModel = viewModel)
                    AppNavTab.PRAYER -> PrayerScreen(viewModel = viewModel)
                    AppNavTab.SCANNER -> ScannerScreen(viewModel = viewModel)
                    AppNavTab.BMI -> BmiScreen(viewModel = viewModel)
                    AppNavTab.CHATBOT -> ChatbotScreen(viewModel = viewModel)
                }
            }
        }

        // Master Settings Sheet / Modal
        if (showSettings) {
            MasterSettingsDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.setSettingsSheetVisible(false) }
            )
        }
    }
}
