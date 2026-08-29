package com.example.util

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    BANGLA("bn", "বাংলা", "🇧🇩"),
    ENGLISH("en", "English", "🇬🇧")
}

object AppStrings {
    // Nav tabs
    fun navHome(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "হোম" else "Home"
    fun navPrayer(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "নামাজ" else "Prayer"
    fun navScanner(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "খাবার স্ক্যান" else "Food Scanner"
    fun navBmi(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "বিএমআই" else "BMI Guide"
    fun navChatbot(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "জেমিনি কোচ" else "Gemini Coach"

    // Home
    fun appTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "হেল্থ কনশিয়াস" else "Health Conscious"
    fun masterCardTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "মাস্টার হেলথ কার্ড" else "MASTER HEALTH PASS"
    fun masterCardSubtitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "দৈনিক সুস্থ জীবনের পূর্ণাঙ্গ ট্র্যাকার" else "Daily Life Routine & Wellness Engine"
    fun dailyRingTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "দৈনিক অ্যাক্টিভিটি রাউন্ড গ্রাফ" else "Daily Activity Progress Rings"
    fun dailyRoutines(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "আজকের সুস্থ রুটিন ও কাজ" else "Today's Life Routine & Tasks"
    fun addCustomTask(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "নতুন কাজ যোগ করুন" else "Add New Task"
    fun quickLog(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "কুইক লগ" else "Quick Action"
    fun healthTipOfTheDay(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "আজকের স্পেশাল হেলথ টিপস" else "Today's Golden Health Tip"

    // Units & Stats
    fun steps(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "কদম" else "Steps"
    fun water(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "পানি" else "Water"
    fun calories(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ক্যালোরি" else "Calories"
    fun sleep(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ঘুম" else "Sleep"
    fun prayerCount(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "নামাজ" else "Prayers"

    // Scanner
    fun scanTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "এআই খাবার ও ক্যালোরি স্ক্যানার" else "AI Food & Calorie Scanner"
    fun scanSubtitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ক্যামেরা দিয়ে খাবারের ছবি তুলুন বা গ্যালারি থেকে সিলেক্ট করুন" else "Snap a picture or select from gallery to analyze nutrition"
    fun capturePhoto(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ছবি তুলুন" else "Take Photo"
    fun selectGallery(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "গ্যালারি থেকে নিন" else "Choose Gallery"
    fun googleSearchNutrition(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "গুগল স্ক্যান / ক্রোম সার্চ" else "Google Scan / Chrome Lookup"
    fun addToDailyLog(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ক্যালোরি লগে যোগ করুন" else "Add to Daily Calorie Log"
    fun nutritionFacts(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "পুষ্টির উপাদান ও অনুপাত" else "Nutritional Breakdown"
    fun ingredients(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "উপাদান সমূহ" else "Key Ingredients"

    // BMI
    fun bmiTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "বিএমআই ক্যালকুলেটর ও ডায়েট গাইড" else "BMI Calculator & Diet Guide"
    fun height(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "উচ্চতা (সেমি)" else "Height (cm)"
    fun weight(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ওজন (কেজি)" else "Weight (kg)"
    fun calculateBmi(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "বিএমআই হিসাব করুন" else "Calculate BMI"
    fun whatToEat(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "যে খাবারগুলো বেশি খাওয়া উচিত" else "Recommended Foods to Eat"
    fun whatToAvoid(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "যে খাবারগুলো পরিহার (এভয়েড) করা উচিত" else "Foods You Must Avoid"
    fun idealWeight(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "আদর্শ ওজনের সীমা" else "Ideal Weight Range"

    // Prayer
    fun prayerTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "লোকেশন অনুযায়ী নামাজের সময়সূচি" else "Location Based Prayer Times"
    fun jummahSpecial(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "শুক্রবার বিশেষ জুম্মার নামাজ" else "Special Jummah Prayer Time"
    fun nextPrayer(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "পরবর্তী নামাজ" else "Next Prayer"
    fun qiblaDirection(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "ক্বিবলা দিক নির্দেশক" else "Qibla Direction"

    // Chatbot
    fun chatTitle(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "জেমিনি হেল্থ ও নিউট্রিশন চ্যাটবট" else "Gemini Health & Nutrition Chatbot"
    fun askAnything(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "খাবার, রুটিন বা স্বাস্থ্য বিষয়ক প্রশ্ন লিখুন..." else "Ask about food nutrition, routines, workouts..."
    fun send(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "পাঠান" else "Send"
    fun masterApiKey(lang: AppLanguage) = if (lang == AppLanguage.BANGLA) "মাস্টার এপিআই কি সেটিংস" else "Master API Key Settings"
}
