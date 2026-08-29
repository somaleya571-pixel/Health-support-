package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.*

data class CityLocation(
    val nameEn: String,
    val nameBn: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffset: Double = 6.0
)

data class PrayerSchedule(
    val cityName: String,
    val dateDisplay: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val jummah: String,
    val activePrayerNameEn: String,
    val activePrayerNameBn: String,
    val nextPrayerNameEn: String,
    val nextPrayerNameBn: String,
    val timeRemainingNext: String,
    val qiblaAngle: Double
)

object PrayerTimeCalculator {

    val PRESET_CITIES = listOf(
        CityLocation("Dhaka, Bangladesh", "ঢাকা, বাংলাদেশ", 23.8103, 90.4125, 6.0),
        CityLocation("Chittagong, Bangladesh", "চট্টগ্রাম, বাংলাদেশ", 22.3569, 91.7832, 6.0),
        CityLocation("Sylhet, Bangladesh", "সিলেট, বাংলাদেশ", 24.8949, 91.8687, 6.0),
        CityLocation("Rajshahi, Bangladesh", "রাজশাহী, বাংলাদেশ", 24.3745, 88.6042, 6.0),
        CityLocation("Khulna, Bangladesh", "খুলনা, বাংলাদেশ", 22.8456, 89.5403, 6.0),
        CityLocation("Barisal, Bangladesh", "বরিশাল, বাংলাদেশ", 22.7010, 90.3535, 6.0),
        CityLocation("Rangpur, Bangladesh", "রংপুর, বাংলাদেশ", 25.7439, 89.2752, 6.0),
        CityLocation("Mymensingh, Bangladesh", "ময়মনসিংহ, বাংলাদেশ", 24.7471, 90.4203, 6.0),
        CityLocation("Makkah, Saudi Arabia", "মক্কা মুকাররমা", 21.4225, 39.8262, 3.0),
        CityLocation("Madinah, Saudi Arabia", "মদীনা মুনাওয়ারা", 24.5247, 39.5692, 3.0),
        CityLocation("London, UK", "লন্ডন, যুক্তরাজ্য", 51.5074, -0.1278, 0.0),
        CityLocation("New York, USA", "নিউইয়র্ক, যুক্তরাষ্ট্র", 40.7128, -74.0060, -5.0)
    )

    fun calculatePrayerTimes(
        lat: Double,
        lng: Double,
        timezone: Double = 6.0,
        cityName: String = "Dhaka, Bangladesh",
        calendar: Calendar = Calendar.getInstance()
    ): PrayerSchedule {
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val isFriday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY

        // Approximate solar calculations
        val b = 2 * Math.PI * (dayOfYear - 81) / 365
        val eot = 9.87 * sin(2 * b) - 7.53 * cos(b) - 1.5 * sin(b) // Equation of time in minutes
        val declination = Math.toRadians(23.45 * sin(Math.toRadians(360.0 / 365 * (dayOfYear - 81))))

        val latRad = Math.toRadians(lat)

        // Solar Noon in local standard time
        val solarNoon = 12.0 + (timezone * 15 - lng) / 15.0 - (eot / 60.0)

        // Fajr angle 18 degrees below horizon
        val fajrAngle = Math.toRadians(-18.0)
        val fajrHA = calculateHourAngle(latRad, declination, fajrAngle)
        val fajrTime = solarNoon - (fajrHA / 15.0)

        // Sunrise angle -0.833 degrees
        val sunriseAngle = Math.toRadians(-0.833)
        val sunriseHA = calculateHourAngle(latRad, declination, sunriseAngle)
        val sunriseTime = solarNoon - (sunriseHA / 15.0)

        // Dhuhr
        val dhuhrTime = solarNoon + (2.0 / 60.0) // small safety margin

        // Asr (Hanafi / Shafi'i shadow ratio = 1)
        val asrAlt = atan(1.0 / (1.0 + tan(abs(latRad - declination))))
        val asrHA = calculateHourAngle(latRad, declination, asrAlt)
        val asrTime = solarNoon + (asrHA / 15.0)

        // Maghrib (Sunset -0.833 deg)
        val maghribTime = solarNoon + (sunriseHA / 15.0) + (2.0 / 60.0)

        // Isha angle 18 degrees below horizon
        val ishaTime = solarNoon + (fajrHA / 15.0)

        val fajrStr = formatDecimalTime(fajrTime)
        val sunriseStr = formatDecimalTime(sunriseTime)
        val dhuhrStr = formatDecimalTime(dhuhrTime)
        val asrStr = formatDecimalTime(asrTime)
        val maghribStr = formatDecimalTime(maghribTime)
        val ishaStr = formatDecimalTime(ishaTime)
        val jummahStr = if (isFriday) "01:15 PM" else "Every Friday 01:15 PM"

        // Determine current and next prayer
        val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
        val fajrMin = toMinutes(fajrTime)
        val sunriseMin = toMinutes(sunriseTime)
        val dhuhrMin = toMinutes(dhuhrTime)
        val asrMin = toMinutes(asrTime)
        val maghribMin = toMinutes(maghribTime)
        val ishaMin = toMinutes(ishaTime)

        var activeEn = "Isha"
        var activeBn = "এশা"
        var nextEn = "Fajr"
        var nextBn = "ফজর"
        var targetMin = fajrMin + 24 * 60

        when {
            currentMinutes < fajrMin -> {
                activeEn = "Tahajjud / Midnight"
                activeBn = "তাহাজ্জুদ / শেষ রাত"
                nextEn = "Fajr"
                nextBn = "ফজর"
                targetMin = fajrMin
            }
            currentMinutes in fajrMin until sunriseMin -> {
                activeEn = "Fajr"
                activeBn = "ফজর"
                nextEn = "Sunrise (ইশরাক)"
                nextBn = "সূর্যোদয় (ইশরাক)"
                targetMin = sunriseMin
            }
            currentMinutes in sunriseMin until dhuhrMin -> {
                activeEn = "Duha / Morning"
                activeBn = "চাশত / সকাল"
                nextEn = if (isFriday) "Jummah (জুম্মা)" else "Dhuhr (জোহর)"
                nextBn = if (isFriday) "জুম্মা নামাজ" else "জোহর নামাজ"
                targetMin = dhuhrMin
            }
            currentMinutes in dhuhrMin until asrMin -> {
                activeEn = if (isFriday) "Jummah" else "Dhuhr"
                activeBn = if (isFriday) "জুম্মা" else "জোহর"
                nextEn = "Asr"
                nextBn = "আসর"
                targetMin = asrMin
            }
            currentMinutes in asrMin until maghribMin -> {
                activeEn = "Asr"
                activeBn = "আসর"
                nextEn = "Maghrib"
                nextBn = "মাগরিব"
                targetMin = maghribMin
            }
            currentMinutes in maghribMin until ishaMin -> {
                activeEn = "Maghrib"
                activeBn = "মাগরিব"
                nextEn = "Isha"
                nextBn = "এশা"
                targetMin = ishaMin
            }
            else -> {
                activeEn = "Isha"
                activeBn = "এশা"
                nextEn = "Fajr"
                nextBn = "ফজর"
                targetMin = fajrMin + 24 * 60
            }
        }

        var diffMin = targetMin - currentMinutes
        if (diffMin < 0) diffMin += 24 * 60
        val remHours = diffMin / 60
        val remMins = diffMin % 60
        val remainingStr = "${remHours}h ${remMins}m remaining"

        // Qibla direction from Makkah (21.4225, 39.8262)
        val makkahLat = Math.toRadians(21.4225)
        val makkahLng = Math.toRadians(39.8262)
        val userLng = Math.toRadians(lng)
        val y = sin(makkahLng - userLng)
        val x = cos(latRad) * tan(makkahLat) - sin(latRad) * cos(makkahLng - userLng)
        var qibla = Math.toDegrees(atan2(y, x))
        if (qibla < 0) qibla += 360.0

        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())

        return PrayerSchedule(
            cityName = cityName,
            dateDisplay = dateFormat.format(calendar.time),
            fajr = fajrStr,
            sunrise = sunriseStr,
            dhuhr = dhuhrStr,
            asr = asrStr,
            maghrib = maghribStr,
            isha = ishaStr,
            jummah = jummahStr,
            activePrayerNameEn = activeEn,
            activePrayerNameBn = activeBn,
            nextPrayerNameEn = nextEn,
            nextPrayerNameBn = nextBn,
            timeRemainingNext = remainingStr,
            qiblaAngle = qibla
        )
    }

    private fun calculateHourAngle(lat: Double, declination: Double, altitude: Double): Double {
        val cosHA = (sin(altitude) - sin(lat) * sin(declination)) / (cos(lat) * cos(declination))
        val clamped = cosHA.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clamped))
    }

    private fun formatDecimalTime(hoursDecimal: Double): String {
        var h = (hoursDecimal.toInt() % 24 + 24) % 24
        val m = ((hoursDecimal - hoursDecimal.toInt()) * 60).roundToInt().coerceIn(0, 59)
        val amPm = if (h >= 12) "PM" else "AM"
        val displayH = when {
            h == 0 -> 12
            h > 12 -> h - 12
            else -> h
        }
        return String.format(Locale.getDefault(), "%02d:%02d %s", displayH, m, amPm)
    }

    private fun toMinutes(hoursDecimal: Double): Int {
        var h = (hoursDecimal.toInt() % 24 + 24) % 24
        val m = ((hoursDecimal - hoursDecimal.toInt()) * 60).roundToInt().coerceIn(0, 59)
        return h * 60 + m
    }
}
