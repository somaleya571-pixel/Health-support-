package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.api.FoodScanResult
import com.example.data.api.GeminiApiClient
import com.example.data.local.AppDatabase
import com.example.data.local.BmiRecordEntity
import com.example.data.local.DailyHealthSummaryEntity
import com.example.data.local.RoutineItemEntity
import com.example.data.local.ScannedFoodEntity
import com.example.data.repository.HealthRepository
import com.example.ui.components.AppNavTab
import com.example.util.AppLanguage
import com.example.util.BmiAnalysisResult
import com.example.util.BmiCalculator
import com.example.util.CityLocation
import com.example.util.PrayerSchedule
import com.example.util.PrayerTimeCalculator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val sender: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "health_conscious_master.db"
    ).fallbackToDestructiveMigration().build()

    private val repository = HealthRepository(db)
    private val prefs = application.getSharedPreferences("health_prefs", Context.MODE_PRIVATE)

    // Language State
    private val _language = MutableStateFlow(
        if (prefs.getString("app_lang", "bn") == "en") AppLanguage.ENGLISH else AppLanguage.BANGLA
    )
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Navigation Tab
    private val _currentTab = MutableStateFlow(AppNavTab.HOME)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    // Master API Key
    private val _customApiKey = MutableStateFlow(prefs.getString("custom_api_key", "") ?: "")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    // Routines & Summary
    val routines: StateFlow<List<RoutineItemEntity>> = repository.getRoutinesForDate()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val healthSummary: StateFlow<DailyHealthSummaryEntity> = repository.getHealthSummary()
        .map { it ?: DailyHealthSummaryEntity(dateString = repository.getTodayDateString()) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DailyHealthSummaryEntity(dateString = repository.getTodayDateString())
        )

    // Location & Prayer Times
    private val _selectedCity = MutableStateFlow(PrayerTimeCalculator.PRESET_CITIES.first())
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    private val _prayerSchedule = MutableStateFlow(
        PrayerTimeCalculator.calculatePrayerTimes(
            lat = PrayerTimeCalculator.PRESET_CITIES.first().latitude,
            lng = PrayerTimeCalculator.PRESET_CITIES.first().longitude,
            cityName = PrayerTimeCalculator.PRESET_CITIES.first().nameBn
        )
    )
    val prayerSchedule: StateFlow<PrayerSchedule> = _prayerSchedule.asStateFlow()

    // BMI State
    private val _heightCm = MutableStateFlow(172f)
    val heightCm: StateFlow<Float> = _heightCm.asStateFlow()

    private val _weightKg = MutableStateFlow(68f)
    val weightKg: StateFlow<Float> = _weightKg.asStateFlow()

    private val _age = MutableStateFlow(25)
    val age: StateFlow<Int> = _age.asStateFlow()

    private val _gender = MutableStateFlow("Male")
    val gender: StateFlow<String> = _gender.asStateFlow()

    private val _bmiResult = MutableStateFlow(BmiCalculator.calculate(172f, 68f))
    val bmiResult: StateFlow<BmiAnalysisResult> = _bmiResult.asStateFlow()

    val bmiHistory: StateFlow<List<BmiRecordEntity>> = repository.getAllBmiRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Food Scanner State
    private val _scannedBitmap = MutableStateFlow<Bitmap?>(null)
    val scannedBitmap: StateFlow<Bitmap?> = _scannedBitmap.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _foodScanResult = MutableStateFlow<FoodScanResult?>(null)
    val foodScanResult: StateFlow<FoodScanResult?> = _foodScanResult.asStateFlow()

    val foodLogs: StateFlow<List<ScannedFoodEntity>> = repository.getAllScannedFoods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chatbot State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "model",
                text = if (_language.value == AppLanguage.BANGLA)
                    "আসসালামু আলাইকুম! আমি আপনার পার্সোনাল হেল্থ ও ডায়েট কোচ জেমিনি এআই। আপনার দৈনিক রুটিন, খাবারের ক্যালোরি বা ব্যায়ামের যেকোনো প্রশ্ন করতে পারেন।"
                else
                    "Hello! I am your Health & Nutrition AI Coach powered by Gemini. Ask me anything about your daily routine, nutrition facts, or customized workout advice."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Settings sheet
    private val _showSettingsSheet = MutableStateFlow(false)
    val showSettingsSheet: StateFlow<Boolean> = _showSettingsSheet.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getRoutinesForDate().firstOrNull().let { items ->
                if (items.isNullOrEmpty()) {
                    repository.initializeDefaultRoutinesIfEmpty()
                }
            }
        }

        // Live Prayer Timer & Clock update loop
        viewModelScope.launch {
            while (true) {
                refreshPrayerTimes()
                delay(30000) // update every 30 seconds
            }
        }
    }

    fun setNavTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun toggleLanguage() {
        val newLang = if (_language.value == AppLanguage.BANGLA) AppLanguage.ENGLISH else AppLanguage.BANGLA
        _language.value = newLang
        prefs.edit().putString("app_lang", newLang.code).apply()
        refreshPrayerTimes()
    }

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
        prefs.edit().putString("custom_api_key", key).apply()
    }

    fun setSettingsSheetVisible(visible: Boolean) {
        _showSettingsSheet.value = visible
    }

    fun toggleRoutineCompleted(item: RoutineItemEntity) {
        viewModelScope.launch {
            val updated = item.copy(isCompleted = !item.isCompleted)
            repository.updateRoutine(updated)

            // Update prayer / routine count in summary
            val currentSummary = healthSummary.value
            val prayerAdd = if (item.category == "PRAYER") (if (updated.isCompleted) 1 else -1) else 0
            val newPrayers = (currentSummary.prayersCompleted + prayerAdd).coerceIn(0, 5)
            repository.saveHealthSummary(currentSummary.copy(prayersCompleted = newPrayers))
        }
    }

    fun addCustomRoutine(title: String, category: String, timeSlot: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val newItem = RoutineItemEntity(
                titleEn = title,
                titleBn = title,
                category = category,
                timeSlot = timeSlot,
                isCompleted = false,
                dateString = repository.getTodayDateString()
            )
            repository.addRoutine(newItem)
        }
    }

    fun deleteRoutine(id: Long) {
        viewModelScope.launch {
            repository.deleteRoutine(id)
        }
    }

    fun addWater(amountMl: Int = 250) {
        viewModelScope.launch {
            val cur = healthSummary.value
            repository.saveHealthSummary(cur.copy(waterMl = cur.waterMl + amountMl))
        }
    }

    fun addSteps(stepsCount: Int = 500) {
        viewModelScope.launch {
            val cur = healthSummary.value
            repository.saveHealthSummary(
                cur.copy(
                    steps = cur.steps + stepsCount,
                    caloriesBurned = cur.caloriesBurned + (stepsCount * 0.04f).toInt()
                )
            )
        }
    }

    fun addCaloriesConsumed(calories: Int) {
        viewModelScope.launch {
            val cur = healthSummary.value
            repository.saveHealthSummary(cur.copy(caloriesConsumed = cur.caloriesConsumed + calories))
        }
    }

    // Prayer & Location
    fun selectCity(city: CityLocation) {
        _selectedCity.value = city
        refreshPrayerTimes()
    }

    fun updateGpsLocation(location: Location) {
        val city = CityLocation(
            nameEn = "GPS: Lat ${String.format("%.2f", location.latitude)}, Lng ${String.format("%.2f", location.longitude)}",
            nameBn = "বর্তমান অবস্থান (GPS)",
            latitude = location.latitude,
            longitude = location.longitude,
            timezoneOffset = 6.0
        )
        _selectedCity.value = city
        refreshPrayerTimes()
    }

    private fun refreshPrayerTimes() {
        val city = _selectedCity.value
        val schedule = PrayerTimeCalculator.calculatePrayerTimes(
            lat = city.latitude,
            lng = city.longitude,
            timezone = city.timezoneOffset,
            cityName = if (_language.value == AppLanguage.BANGLA) city.nameBn else city.nameEn,
            calendar = Calendar.getInstance()
        )
        _prayerSchedule.value = schedule
    }

    // BMI
    fun updateBmiInputs(height: Float, weight: Float, ageVal: Int, genderVal: String) {
        _heightCm.value = height
        _weightKg.value = weight
        _age.value = ageVal
        _gender.value = genderVal
        _bmiResult.value = BmiCalculator.calculate(height, weight, ageVal, genderVal)
    }

    fun saveCurrentBmiRecord() {
        viewModelScope.launch {
            val res = _bmiResult.value
            val record = BmiRecordEntity(
                heightCm = _heightCm.value,
                weightKg = _weightKg.value,
                bmiValue = res.bmi,
                category = res.category.name,
                age = _age.value,
                gender = _gender.value
            )
            repository.saveBmiRecord(record)
        }
    }

    // Food Scanner
    fun setScannedBitmap(bitmap: Bitmap?) {
        _scannedBitmap.value = bitmap
        _foodScanResult.value = null
        if (bitmap != null) {
            analyzeScannedFood(bitmap)
        }
    }

    private fun analyzeScannedFood(bitmap: Bitmap) {
        _isScanning.value = true
        viewModelScope.launch {
            val result = GeminiApiClient.scanFoodImage(
                bitmap = bitmap,
                customApiKey = _customApiKey.value,
                language = _language.value.code
            )
            _isScanning.value = false
            result.onSuccess { scan ->
                _foodScanResult.value = scan
                repository.saveScannedFood(
                    ScannedFoodEntity(
                        foodNameEn = scan.foodNameEn,
                        foodNameBn = scan.foodNameBn,
                        calories = scan.calories,
                        proteinGrams = scan.proteinGrams,
                        carbsGrams = scan.carbsGrams,
                        fatGrams = scan.fatGrams,
                        fiberGrams = scan.fiberGrams,
                        healthRating = scan.healthRating,
                        healthAdviceEn = scan.healthAdviceEn,
                        healthAdviceBn = scan.healthAdviceBn,
                        ingredients = scan.ingredients
                    )
                )
            }
        }
    }

    fun deleteScannedFood(id: Long) {
        viewModelScope.launch {
            repository.deleteScannedFood(id)
        }
    }

    // Chatbot
    fun sendUserMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(sender = "user", text = text)
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            val history = _chatMessages.value.map { it.sender to it.text }
            val result = GeminiApiClient.sendChatMessage(
                prompt = text,
                chatHistory = history,
                customApiKey = _customApiKey.value
            )
            _isChatLoading.value = false
            val replyText = result.getOrElse { "Could not generate advice right now. Please try again." }
            val modelMsg = ChatMessage(sender = "model", text = replyText)
            _chatMessages.value = _chatMessages.value + modelMsg
        }
    }
}
