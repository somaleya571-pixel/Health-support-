package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {

    // Routine Items
    @Query("SELECT * FROM daily_routine_items WHERE dateString = :date ORDER BY id ASC")
    fun getRoutinesForDate(date: String): Flow<List<RoutineItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(items: List<RoutineItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(item: RoutineItemEntity): Long

    @Update
    suspend fun updateRoutine(item: RoutineItemEntity)

    @Query("DELETE FROM daily_routine_items WHERE id = :id")
    suspend fun deleteRoutine(id: Long)

    // Daily Health Summary
    @Query("SELECT * FROM daily_health_summary WHERE dateString = :date LIMIT 1")
    fun getHealthSummaryForDate(date: String): Flow<DailyHealthSummaryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSummary(summary: DailyHealthSummaryEntity)

    // BMI records
    @Query("SELECT * FROM bmi_records ORDER BY timestamp DESC")
    fun getAllBmiRecords(): Flow<List<BmiRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBmiRecord(record: BmiRecordEntity): Long

    // Scanned foods
    @Query("SELECT * FROM scanned_food_logs ORDER BY timestamp DESC")
    fun getAllScannedFoods(): Flow<List<ScannedFoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScannedFood(food: ScannedFoodEntity): Long

    @Query("DELETE FROM scanned_food_logs WHERE id = :id")
    suspend fun deleteScannedFood(id: Long)
}

@Database(
    entities = [
        RoutineItemEntity::class,
        DailyHealthSummaryEntity::class,
        BmiRecordEntity::class,
        ScannedFoodEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun healthDao(): HealthDao
}
