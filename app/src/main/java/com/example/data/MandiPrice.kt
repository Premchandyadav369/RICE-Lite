package com.example.data

data class MandiPrice(
    val cropName: String,
    val cropNameHi: String, // Hindi
    val cropNameTe: String, // Telugu
    val marketName: String,
    val state: String,
    val minPrice: Int, // Rs per quintal
    val maxPrice: Int,
    val modalPrice: Int,
    val date: String,
    val trend: String // "UP", "DOWN", "STABLE"
) {
    fun getLocalizedCropName(lang: String): String {
        return when {
            lang.contains("Hindi", ignoreCase = true) || lang.contains("हिन्दी") || lang.equals("HI", ignoreCase = true) -> cropNameHi
            lang.contains("Telugu", ignoreCase = true) || lang.contains("తెలుగు") || lang.equals("TE", ignoreCase = true) -> cropNameTe
            lang.contains("Punjabi", ignoreCase = true) || lang.contains("ਪੰਜਾਬੀ") -> cropName
            lang.contains("Marathi", ignoreCase = true) || lang.contains("मराठी") -> cropNameHi
            lang.contains("Gujarati", ignoreCase = true) || lang.contains("ગુજરાતી") -> cropNameHi
            else -> cropName
        }
    }
}

object MandiPriceProvider {
    val samplePrices = listOf(
        MandiPrice("Chilli (Red Mirchi)", "लाल मिर्च", "ఎర్ర మిరపకాయలు (గుంటూరు)", "Guntur Mirchi Yard", "Andhra Pradesh", 18500, 22800, 21200, "28 Jul 2026", "UP"),
        MandiPrice("Cotton", "कपास", "పత్తి (వరంగల్)", "Warangal APMC", "Telangana", 7200, 8450, 7900, "28 Jul 2026", "UP"),
        MandiPrice("Paddy (Nellore Sona)", "धान (चावल)", "వరి ధాన్యం (నెల్లూరు సోనా)", "Miryalaguda Mandi", "Telangana", 2250, 2680, 2450, "28 Jul 2026", "STABLE"),
        MandiPrice("Turmeric", "हल्दी", "పసుపు (నిజామాబాద్)", "Nizamabad APMC", "Telangana", 12500, 16800, 14900, "28 Jul 2026", "UP"),
        MandiPrice("Groundnut", "मूंगफली", "వేరుశనగ (కర్నూలు)", "Adoni APMC", "Andhra Pradesh", 6400, 7500, 7100, "28 Jul 2026", "STABLE"),
        MandiPrice("Onion", "प्याज", "ఉల్లిపాయ (కర్నూలు)", "Kurnool APMC", "Andhra Pradesh", 1800, 2500, 2150, "28 Jul 2026", "UP"),
        MandiPrice("Maize (Corn)", "मक्का", "మొలకజొన్న (ఖమ్మం)", "Khammam Market", "Telangana", 2050, 2400, 2220, "28 Jul 2026", "UP"),
        MandiPrice("Mango (Banganapalli)", "आम", "బంగనపల్లి మామిడి", "Vijayawada Rythu Bazar", "Andhra Pradesh", 3500, 6000, 4800, "28 Jul 2026", "STABLE"),
        MandiPrice("Sugarcane", "गन्ना", "చెరకు (ఏలూరు)", "Eluru APMC", "Andhra Pradesh", 3100, 3500, 3350, "28 Jul 2026", "STABLE"),
        MandiPrice("Tomato", "टमाटर", "టమోటా (మదన్‌పల్లె)", "Madanapalle Market", "Andhra Pradesh", 1800, 3800, 2900, "28 Jul 2026", "UP"),
        MandiPrice("Wheat", "गेहूं", "గోధుమ", "Khanna APMC", "Punjab", 2125, 2300, 2250, "28 Jul 2026", "STABLE"),
        MandiPrice("Green Gram (Moong)", "मूंग", "పెసలు (సూర్యాపేట)", "Suryapet Market", "Telangana", 7400, 8600, 8150, "28 Jul 2026", "STABLE")
    )
}
