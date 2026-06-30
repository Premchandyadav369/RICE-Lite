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
        return when (lang) {
            "Hindi (हिन्दी)" -> cropNameHi
            "Telugu (తెలుగు)" -> cropNameTe
            else -> cropName
        }
    }
}

object MandiPriceProvider {
    val samplePrices = listOf(
        MandiPrice("Onion", "प्याज", "ఉల్లిపాయ", "Lasalgaon APMC", "Maharashtra", 1800, 2400, 2150, "30 Jun 2026", "UP"),
        MandiPrice("Potato", "आलू", "బంగాళాదుంప", "Agra Mandi", "Uttar Pradesh", 1200, 1600, 1450, "30 Jun 2026", "UP"),
        MandiPrice("Wheat", "गेहूं", "గోధుమ", "Khanna APMC", "Punjab", 2125, 2300, 2250, "29 Jun 2026", "STABLE"),
        MandiPrice("Paddy (Rice)", "धान (चावल)", "వరి ధాన్యం", "Nalgonda Mandi", "Telangana", 2100, 2450, 2300, "30 Jun 2026", "UP"),
        MandiPrice("Cotton", "कपास", "పత్తి", "Adoni APMC", "Andhra Pradesh", 6800, 7800, 7350, "30 Jun 2026", "DOWN"),
        MandiPrice("Tomato", "टमाटर", "టమోటా", "Kolar Market", "Karnataka", 1500, 3200, 2400, "30 Jun 2026", "UP"),
        MandiPrice("Green Gram (Moong)", "मूंग", "పెసలు", "Indore Mandi", "Madhya Pradesh", 7200, 8500, 8100, "29 Jun 2026", "STABLE"),
        MandiPrice("Onion", "प्याज", "ఉల్లిపాయ", "Kurnool APMC", "Andhra Pradesh", 1600, 2200, 1950, "30 Jun 2026", "STABLE"),
        MandiPrice("Potato", "आलू", "బంగాళాదుంప", "Pune APMC", "Maharashtra", 1300, 1800, 1600, "30 Jun 2026", "UP"),
        MandiPrice("Wheat", "गेहूं", "గోధుమ", "Gondal APMC", "Gujarat", 2200, 2600, 2450, "30 Jun 2026", "UP"),
        MandiPrice("Cotton", "कपास", "పత్తి", "Warangal Mandi", "Telangana", 7000, 8100, 7600, "30 Jun 2026", "UP"),
        MandiPrice("Tomato", "टमाटर", "టమోటా", "Pimpalgaon", "Maharashtra", 1000, 2200, 1600, "30 Jun 2026", "DOWN"),
        MandiPrice("Paddy (Rice)", "धान (चावल)", "వరి ధాన్యం", "Gondia APMC", "Maharashtra", 2150, 2500, 2350, "29 Jun 2026", "STABLE")
    )
}
