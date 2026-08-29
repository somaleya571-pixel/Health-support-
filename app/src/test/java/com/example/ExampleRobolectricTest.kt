package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.BmiCalculator
import com.example.util.BmiCategory
import com.example.util.PrayerTimeCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun testAppNameResource() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Health Conscious", appName)
  }

  @Test
  fun testBmiCalculationNormal() {
    val result = BmiCalculator.calculate(170f, 65f)
    assertEquals(BmiCategory.NORMAL, result.category)
    assertTrue(result.foodsToEat.isNotEmpty())
  }

  @Test
  fun testBmiCalculationUnderweightDietAdvice() {
    val result = BmiCalculator.calculate(175f, 50f)
    assertEquals(BmiCategory.UNDERWEIGHT, result.category)
    assertTrue(result.foodsToEat.any { it.titleEn.contains("Protein") || it.titleEn.contains("Egg") })
    assertTrue(result.foodsToAvoid.isNotEmpty())
  }

  @Test
  fun testBmiCalculationOverweightDietAdvice() {
    val result = BmiCalculator.calculate(170f, 88f)
    assertEquals(BmiCategory.OVERWEIGHT, result.category)
    assertTrue(result.foodsToAvoid.any { it.titleEn.contains("Sugar") || it.titleBn.contains("চিনি") })
  }

  @Test
  fun testPrayerTimeCalculation() {
    val schedule = PrayerTimeCalculator.calculatePrayerTimes(
        lat = 23.8103,
        lng = 90.4125,
        timezone = 6.0,
        cityName = "Dhaka, Bangladesh"
    )
    assertNotNull(schedule.fajr)
    assertNotNull(schedule.dhuhr)
    assertNotNull(schedule.jummah)
    assertTrue(schedule.qiblaAngle > 0)
  }
}
