package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BmiRecordEntity
import com.example.data.local.DailyHealthSummaryEntity
import com.example.data.local.RoutineItemEntity
import com.example.data.local.ScannedFoodEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HealthRepository(private val database: AppDatabase) {
    private val dao = database.healthDao()

    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getRoutinesForDate(date: String = getTodayDateString()): Flow<List<RoutineItemEntity>> {
        return dao.getRoutinesForDate(date)
    }

    suspend fun initializeDefaultRoutinesIfEmpty(date: String = getTodayDateString()) {
        val defaultRoutines = listOf(
            RoutineItemEntity(
                titleEn = "Morning Hydration (2 Glasses Warm Water)",
                titleBn = "সকালের আর্দ্রতা (২ গ্লাস হালকা গরম পানি)",
                category = "WATER",
                timeSlot = "06:00 AM",
                isCompleted = false,
                dateString = date
            ),
            RoutineItemEntity(
                titleEn = "Fajr Prayer & Spiritual Reflection",
                titleBn = "ফজর নামাজ ও সকালের জিকির/ধ্যান",
                category = "PRAYER",
                timeSlot = "05:00 AM",
                isCompleted = false,
                dateString = date
            ),
            RoutineItemEntity(
                titleEn = "Morning Walk / Stretch (20-30 Mins)",
                titleBn = "সকালের হাঁটাহাঁটি বা ফ্রি-হ্যান্ড ওয়ার্কআউট (২০-৩০ মিনিট)",
                category = "EXERCISE",
                timeSlot = "07:00 AM",
                isCompleted = false,
                dateString = date
            ),
            RoutineItemEntity(
                titleEn = "Nutritious Protein Breakfast",
                titleBn = "উচ্চ প্রোটিন সমৃদ্ধ পুষ্টিকর সকালের নাস্তা",
                category = "MEAL",
                timeSlot = "08:30 AM",
                isCompleted = false,
                dateString = date
            ),
            RoutineItemEntity(
                titleEn = "Mid-Day Hydration (1.5L Milestone)",
                titleBn = "দুপুরের পানি মাইলফলক (দেড় লিটার সম্পন্ন)",
                category = "WATER",
                timeSlot = "12:00 PM",
                isCompleted = false,
                dateString = date
            ),
            RoutineItemEntity(
                titleEn = "Dhuhr / Jummah Prayer & Balanced Lunch",
                titleBn = "জোহর / জুম্মা নামাজ ও ব্যালেন্সড লাঞ্চ",
                category = "PRAYER",
                timeSlot = "01:30 PM",
                isCompleted = false,
                dateString = date
            ),
            RoutineItemEntity(
                titleEn = "Asr Prayer & Evening Cardio / Gym",
                titleBn = "আসর নামাজ ও বিকালের ব্যায়াম/হাঁটা",
                category = "EXERCISE",
                timeSlot = "04:45 PM",
                isCompleted = false,
                dateString = date
            ),
            RoutineItemEntity(
                titleEn = "Maghrib Prayer & Healthy Snack",
                titleBn = "মাগরিব নামাজ ও স্বাস্থ্যকর হালকা খাবার",
                category = "PRAYER",
                timeSlot = "06:30 PM",
                isCompleted = false,
                dateString = date
            ),
            RoutineItemEntity(
                titleEn = "Light Dinner & Green Tea",
                titleBn = "হালকা ডিনার ও ভেষজ চা",
                category = "MEAL",
                timeSlot = "08:30 PM",
                isCompleted = false,
                dateString = date
            ),
            RoutineItemEntity(
                titleEn = "Isha Prayer & 8-Hour Deep Sleep Prep",
                titleBn = "এশা নামাজ ও স্ক্রিন বন্ধ করে ৮ ঘণ্টার গভীর ঘুম",
                category = "SLEEP",
                timeSlot = "10:30 PM",
                isCompleted = false,
                dateString = date
            )
        )
        dao.insertRoutines(defaultRoutines)
    }

    suspend fun addRoutine(item: RoutineItemEntity) = dao.insertRoutine(item)

    suspend fun updateRoutine(item: RoutineItemEntity) = dao.updateRoutine(item)

    suspend fun deleteRoutine(id: Long) = dao.deleteRoutine(id)

    fun getHealthSummary(date: String = getTodayDateString()): Flow<DailyHealthSummaryEntity?> {
        return dao.getHealthSummaryForDate(date)
    }

    suspend fun saveHealthSummary(summary: DailyHealthSummaryEntity) {
        dao.insertOrUpdateSummary(summary)
    }

    fun getAllBmiRecords(): Flow<List<BmiRecordEntity>> = dao.getAllBmiRecords()

    suspend fun saveBmiRecord(record: BmiRecordEntity) = dao.insertBmiRecord(record)

    fun getAllScannedFoods(): Flow<List<ScannedFoodEntity>> = dao.getAllScannedFoods()

    suspend fun saveScannedFood(food: ScannedFoodEntity) = dao.insertScannedFood(food)

    suspend fun deleteScannedFood(id: Long) = dao.deleteScannedFood(id)
}
