package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_routine_items")
data class RoutineItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titleEn: String,
    val titleBn: String,
    val category: String, // "WATER", "PRAYER", "EXERCISE", "MEAL", "SLEEP", "CUSTOM"
    val timeSlot: String,
    val isCompleted: Boolean = false,
    val targetCount: Int = 1,
    val currentCount: Int = 0,
    val dateString: String // "yyyy-MM-dd"
)

@Entity(tableName = "daily_health_summary")
data class DailyHealthSummaryEntity(
    @PrimaryKey
    val dateString: String, // "yyyy-MM-dd"
    val steps: Int = 0,
    val stepsGoal: Int = 8000,
    val waterMl: Int = 0,
    val waterGoalMl: Int = 3000,
    val caloriesBurned: Int = 0,
    val caloriesBurnedGoal: Int = 500,
    val caloriesConsumed: Int = 0,
    val caloriesConsumedGoal: Int = 2000,
    val sleepMinutes: Int = 0,
    val sleepGoalMinutes: Int = 480, // 8 hours
    val prayersCompleted: Int = 0,
    val prayersGoal: Int = 5
)

@Entity(tableName = "bmi_records")
data class BmiRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val heightCm: Float,
    val weightKg: Float,
    val bmiValue: Float,
    val category: String,
    val age: Int,
    val gender: String
)

@Entity(tableName = "scanned_food_logs")
data class ScannedFoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val foodNameEn: String,
    val foodNameBn: String,
    val calories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatGrams: Float,
    val fiberGrams: Float,
    val healthRating: String, // "EXCELLENT", "MODERATE", "AVOID"
    val healthAdviceEn: String,
    val healthAdviceBn: String,
    val ingredients: String
)
