package com.example.util

import java.util.Locale
import kotlin.math.abs

enum class BmiCategory(val labelEn: String, val labelBn: String) {
    UNDERWEIGHT("Underweight (Low BMI)", "কম ওজন (লো বিএমআই)"),
    NORMAL("Normal Weight (Healthy)", "স্বাভাবিক ওজন (সুস্থ)"),
    OVERWEIGHT("Overweight (High BMI)", "অতিরিক্ত ওজন (হাই বিএমআই)"),
    OBESE("Obese (Very High BMI)", "স্থূলতা (খুব বেশি ওজন)")
}

data class FoodAdviceItem(
    val titleEn: String,
    val titleBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val iconEmoji: String
)

data class BmiAnalysisResult(
    val bmi: Float,
    val category: BmiCategory,
    val idealWeightMinKg: Float,
    val idealWeightMaxKg: Float,
    val diffFromIdealKg: Float,
    val foodsToEat: List<FoodAdviceItem>,
    val foodsToAvoid: List<FoodAdviceItem>,
    val fitnessTips: List<String>,
    val fitnessTipsBn: List<String>
)

object BmiCalculator {

    fun calculate(
        heightCm: Float,
        weightKg: Float,
        age: Int = 25,
        gender: String = "Male"
    ): BmiAnalysisResult {
        val heightM = heightCm / 100f
        val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 0f

        val idealMinKg = 18.5f * (heightM * heightM)
        val idealMaxKg = 24.9f * (heightM * heightM)

        val category = when {
            bmi < 18.5f -> BmiCategory.UNDERWEIGHT
            bmi < 25.0f -> BmiCategory.NORMAL
            bmi < 30.0f -> BmiCategory.OVERWEIGHT
            else -> BmiCategory.OBESE
        }

        val diffKg = when {
            bmi < 18.5f -> idealMinKg - weightKg
            bmi > 24.9f -> weightKg - idealMaxKg
            else -> 0f
        }

        val foodsToEat: List<FoodAdviceItem>
        val foodsToAvoid: List<FoodAdviceItem>
        val tipsEn: List<String>
        val tipsBn: List<String>

        when (category) {
            BmiCategory.UNDERWEIGHT -> {
                foodsToEat = listOf(
                    FoodAdviceItem(
                        "Eggs & Lean Meat / Fish",
                        "ডিম ও চর্বিহীন মাংস/মাছ",
                        "Rich in complete amino acids and bioavailable protein for healthy muscle mass building.",
                        "স্বাস্থ্যকর পেশি গঠনের জন্য উচ্চমানের প্রোটিন সরবরাহ করে।",
                        "🥚"
                    ),
                    FoodAdviceItem(
                        "Peanut Butter & Mixed Nuts",
                        "পিনাট বাটার ও মিশ্র বাদাম",
                        "Almonds, walnuts, and peanuts provide calorie-dense healthy omega fats.",
                        "কাঠবাদাম, কাজুবাদাম এবং চিনাবাদাম স্বাস্থ্যকর ক্যালোরি ও ফ্যাট দেয়।",
                        "🥜"
                    ),
                    FoodAdviceItem(
                        "Whole Milk, Yogurt & Cheese",
                        "খাঁটি দুধ, দই ও পনির",
                        "Calcium, vitamin D, and rich healthy fats to support natural weight gain.",
                        "ক্যালসিয়াম ও পুষ্টিকর ফ্যাট যা ওজন স্বাভাবিক করতে দারুণ সহায়ক।",
                        "🥛"
                    ),
                    FoodAdviceItem(
                        "Oats, Bananas & Dates Shake",
                        "ওটস, কলা ও খেজুরের স্মুদি",
                        "Energy-dense complex carbohydrates to fuel cellular stamina and weight.",
                        "কমপ্লেক্স কার্বোহাইড্রেট ও প্রাকৃতিক শর্করা দিয়ে তৈরি স্বাস্থ্যকর শেক।",
                        "🍌"
                    ),
                    FoodAdviceItem(
                        "Avocado & Olive Oil",
                        "অ্যাভোকাডো ও অলিভ অয়েল",
                        "Monounsaturated fats that boost nutrient absorption without raising bad cholesterol.",
                        "শরীরের পুষ্টি শোষণ বাড়ায় এবং খারাপ কোলেস্টেরল কম রাখে।",
                        "🥑"
                    )
                )

                foodsToAvoid = listOf(
                    FoodAdviceItem(
                        "Skipping Meals & Fasting Long Hours",
                        "খাবার বাদ দেওয়া বা দীর্ঘক্ষণ না খেয়ে থাকা",
                        "Causes muscle catabolism and metabolic slowdown.",
                        "পেশি ভেঙে যায় এবং শক্তি হ্রাস পায়। প্রতিদিন ৫-৬ বার অল্প অল্প খান।",
                        "⚠️"
                    ),
                    FoodAdviceItem(
                        "Empty Sugar Junk & Sodas",
                        "অতিরিক্ত চিনিযুক্ত মিষ্টি ও কোমল পানীয়",
                        "Causes visceral belly fat and inflammation without actual nutritional weight gain.",
                        "ক্ষতিকর চর্বি বাড়ায় কিন্তু কোনো পুষ্টিকর পেশি তৈরি করে না।",
                        "🥤"
                    ),
                    FoodAdviceItem(
                        "Excessive Cardio Without Fueling",
                        "খাবার ছাড়া অতিরিক্ত দৌড়াদৌড়ি/কার্ডিও",
                        "Burns critical muscle mass instead of building functional strength.",
                        "প্রয়োজনীয় ক্যালোরি অপচয় করে শরীরকে আরও দুর্বল করে দেয়।",
                        "🏃"
                    ),
                    FoodAdviceItem(
                        "Drinking Water Right Before Meals",
                        "খাওয়ার ঠিক আগে বেশি পানি পান",
                        "Fills the stomach prematurely and prevents taking sufficient nutrition.",
                        "পেট ভরিয়ে দেয় ফলে পর্যাপ্ত খাবার খাওয়া যায় না।",
                        "💧"
                    )
                )

                tipsEn = listOf(
                    "Eat 5-6 small, nutrient-dense meals throughout the day.",
                    "Focus on progressive resistance training (weight lifting / pushups) to build muscle.",
                    "Drink calorie-dense milk smoothies with oats, honey, and nuts between meals."
                )
                tipsBn = listOf(
                    "সারাদিনে ৩ বারের জায়গায় ৫-৬ বার পুষ্টিকর খাবার খান।",
                    "কার্ডিও কমিয়ে পুশ-আপ ও হালকা ওয়েট ট্রেনিং দিয়ে পেশি মজবুত করুন।",
                    "খাবারের মাঝে দুধ, কলা, বাদাম ও ওটসের স্মুদি পান করুন।"
                )
            }

            BmiCategory.NORMAL -> {
                foodsToEat = listOf(
                    FoodAdviceItem(
                        "Colorful Fresh Vegetables & Greens",
                        "রঙিন তাজা শাকসবজি",
                        "Full of antioxidants, potassium, and micronutrients for optimal immunity.",
                        "শরীরের রোগ প্রতিরোধ ক্ষমতা ও ভাইটালিটি বজায় রাখতে সহায়তা করে।",
                        "🥦"
                    ),
                    FoodAdviceItem(
                        "Lean Proteins (Fish, Chicken, Tofu)",
                        "লীন প্রোটিন (মাছ, মুরগি, ডাল)",
                        "Maintains lean active muscle tissue and metabolic health.",
                        "শরীরের মেটাবলিজম ও ফিটনেস ধরে রাখতে প্রতিদিন রাখুন।",
                        "🐟"
                    ),
                    FoodAdviceItem(
                        "Whole Grains (Brown Rice, Oats)",
                        "হোল গ্রেইন (লাল চাল, ওটস, লাল আটা)",
                        "Stable low-glycemic blood sugar response and lasting focus.",
                        "রক্তে শর্করার মাত্রা নিয়ন্ত্রণ করে এবং দীর্ঘস্থায়ী শক্তি দেয়।",
                        "🌾"
                    ),
                    FoodAdviceItem(
                        "Fresh Seasonal Fruits & Berries",
                        "তাজা মৌসুমি ফল",
                        "Hydration, vitamin C, and dietary fiber.",
                        "ভিটামিন সি ও প্রাকৃতিক ফাইবার সমৃদ্ধ।",
                        "🍎"
                    )
                )

                foodsToAvoid = listOf(
                    FoodAdviceItem(
                        "Ultra-Processed Snacks & Trans Fats",
                        "প্যাকেটজাত জাঙ্ক ফুড ও ট্রান্স ফ্যাট",
                        "Chips, bakery biscuits that disrupt gut microbiome and metabolic balance.",
                        "চিপস, বিস্কুট ও বেকারির আইটেম যা শরীরের মেটাবলিজম ব্যাহত করে।",
                        "🍟"
                    ),
                    FoodAdviceItem(
                        "Late Night Heavy Carb Feasts",
                        "দেরি করে রাতে ভারী খাবার গ্রহণ",
                        "Impairs deep REM sleep and insulin sensitivity.",
                        "ঘুমের ব্যাঘাত ঘটায় এবং পেটে মেদ জমায়।",
                        "🌙"
                    )
                )

                tipsEn = listOf(
                    "Maintain 7,000 - 10,000 steps daily.",
                    "Keep consistent sleep schedule (7-8 hours).",
                    "Drink 2.5 - 3 liters of pure water daily."
                )
                tipsBn = listOf(
                    "প্রতিদিন ৭,০০০ - ১০,০০০ কদম হাঁটার অভ্যাস ধরে রাখুন।",
                    "নিয়মিত ৭-৮ ঘণ্টা শান্তির ঘুম নিশ্চিত করুন।",
                    "প্রতিদিন আড়াই থেকে ৩ লিটার পরিষ্কার পানি পান করুন।"
                )
            }

            BmiCategory.OVERWEIGHT, BmiCategory.OBESE -> {
                foodsToEat = listOf(
                    FoodAdviceItem(
                        "High Dietary Fiber (Broccoli, Spinach, Cucumbers)",
                        "উচ্চ ফাইবারযুক্ত সবুজ শাকসবজি ও শসা",
                        "Very low calorie volume that fills the stomach and optimizes digestion.",
                        "কম ক্যালোরিতে দ্রুত পেট ভরায় এবং ফ্যাট বার্নিং ত্বরান্বিত করে।",
                        "🥗"
                    ),
                    FoodAdviceItem(
                        "Boiled Eggs & Steamed Chicken Breast",
                        "সিদ্ধ ডিম ও তেল ছাড়া মুরগির বুকের মাংস",
                        "High thermic effect of food (TEF) burns calories during digestion and prevents hunger.",
                        "ক্ষুধা নিয়ন্ত্রণে রাখে এবং ক্যালোরি ডেফিসিটে পেশি রক্ষা করে।",
                        "🍗"
                    ),
                    FoodAdviceItem(
                        "Chia Seeds & Lemon Water",
                        "চিয়া সিডস ও লেবুর পানি",
                        "Rich in soluble fiber and antioxidants to curb sugar cravings.",
                        "অতিরিক্ত খাওয়ার লোভ কমায় এবং হজম শক্তি বৃদ্ধি করে।",
                        "🍋"
                    ),
                    FoodAdviceItem(
                        "Green Tea / Black Coffee (No Sugar)",
                        "চিনি ছাড়া গ্রিন টি বা ব্ল্যাক কফি",
                        "EGCG catechins boost resting metabolic rate and fat oxidation.",
                        "মেটাবলিজম বাড়িয়ে চর্বি কমাতে সাহায্য করে।",
                        "🍵"
                    ),
                    FoodAdviceItem(
                        "Lentils, Chickpeas & Beans",
                        "ডাল, ছোলা ও রাজমা",
                        "Plant-based protein combined with resistant starch for sustained satiety.",
                        "পর্যাপ্ত প্রোটিন ও ফাইবার দিয়ে দীর্ঘক্ষণ পেট ভরা রাখে।",
                        "🥣"
                    )
                )

                foodsToAvoid = listOf(
                    FoodAdviceItem(
                        "Refined White Sugar & Sweet Drinks",
                        "সাদা চিনি, মিষ্টি ও মিষ্টি কোমল পানীয়",
                        "Spikes insulin immediately, locking fat stores and prompting cravings.",
                        "ইনসুলিন বাড়িয়ে শরীরে চর্বি জমায়। কোমল পানীয় ও মিষ্টি সম্পূর্ণ বর্জন করুন।",
                        "🚫"
                    ),
                    FoodAdviceItem(
                        "Deep Fried Fast Foods (Singara, Puri, Fries)",
                        "ডুবো তেলে ভাজা খাবার (সিঙ্গারা, পুরি, ফ্রাইজ)",
                        "Contains hazardous oxidized trans fats that elevate LDL and visceral belly fat.",
                        "অতিরিক্ত খারাপ চর্বি ও ক্যালোরি তৈরি করে হৃদরোগের ঝুঁকি বাড়ায়।",
                        "🍔"
                    ),
                    FoodAdviceItem(
                        "Refined Flour (White Bread, Paratha, Naan)",
                        "ময়দার খাবার (পরোটা, নান, সাদা পাউরুটি)",
                        "Converts into glucose almost instantly, storing excess as abdominal fat.",
                        "রক্তে দ্রুত সুগার বাড়িয়ে পেটে মেদ তৈরি করে।",
                        "🍞"
                    ),
                    FoodAdviceItem(
                        "High-Calorie Creamy Dressings & Mayo",
                        "মেয়োনিজ ও ভারী ক্রিম সস",
                        "Sneaky hidden calorie bombs that destroy weight loss deficits.",
                        "অতিরিক্ত গোপন ক্যালোরি যা ডায়েট ব্যর্থ করে দেয়।",
                        "🧴"
                    )
                )

                tipsEn = listOf(
                    "Maintain a moderate 300-500 kcal daily deficit with real whole foods.",
                    "Walk 30-45 minutes briskly every single day (8,000+ steps).",
                    "Do intermittent fasting (14-16 hours overnight) to boost insulin sensitivity."
                )
                tipsBn = listOf(
                    "প্রতিদিনের খাবারে ৩০০-৫০০ ক্যালোরি ঘাটতি রাখুন এবং বাইরের খাবার বর্জন করুন।",
                    "প্রতিদিন ৩০-৪৫ মিনিট দ্রুত হাঁটুন (কমপক্ষে ৮,০০০ কদম)।",
                    "রাতে তাড়াতাড়ি ডিনার সেরে সকালে দেরিতে নাস্তা করার অভ্যাস (ইন্টারমিটেন্ট ফাস্টিং) গড়ে তুলুন।"
                )
            }
        }

        return BmiAnalysisResult(
            bmi = String.format(Locale.US, "%.1f", bmi).toFloat(),
            category = category,
            idealWeightMinKg = String.format(Locale.US, "%.1f", idealMinKg).toFloat(),
            idealWeightMaxKg = String.format(Locale.US, "%.1f", idealMaxKg).toFloat(),
            diffFromIdealKg = String.format(Locale.US, "%.1f", abs(diffKg)).toFloat(),
            foodsToEat = foodsToEat,
            foodsToAvoid = foodsToAvoid,
            fitnessTips = tipsEn,
            fitnessTipsBn = tipsBn
        )
    }
}
