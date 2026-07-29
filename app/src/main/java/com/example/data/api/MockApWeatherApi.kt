package com.example.data.api

import kotlinx.coroutines.delay

data class HourlyForecast(
    val hour: String,
    val tempC: Int,
    val rainProbability: Int,
    val condition: String
)

data class DailyForecast(
    val dayName: String,
    val dateStr: String,
    val maxTempC: Int,
    val minTempC: Int,
    val rainProbability: Int,
    val condition: String,
    val agAdviceEn: String,
    val agAdviceTe: String
)

data class ApRegionWeatherResponse(
    val regionId: String,
    val districtName: String,
    val districtNameTe: String,
    val inferredZone: String,
    val currentTempC: Int,
    val feelsLikeC: Int,
    val weatherCondition: String,
    val rainProbability: Int,
    val humidityPercent: Int,
    val windSpeedKmh: Int,
    val uvIndex: String,
    val dewPointC: Int,
    val apAgriAlertEn: String,
    val apAgriAlertTe: String,
    val hourlyForecast: List<HourlyForecast>,
    val dailyForecast: List<DailyForecast>
)

object MockApWeatherApiService {
    
    val apRegions = listOf(
        "guntur" to "Guntur (Mirchi Belt)",
        "krishna" to "Krishna (Delta Paddy)",
        "kurnool" to "Kurnool (Rayalaseema)",
        "godavari" to "East Godavari (Aqua & Rice)",
        "vizag" to "Visakhapatnam (Coastal)",
        "anantapur" to "Anantapur (Drylands)",
        "chittoor" to "Chittoor (Horticulture)"
    )

    suspend fun fetchWeatherForecast(regionId: String): ApRegionWeatherResponse {
        // Simulate network API call latency
        delay(350)
        
        return when (regionId.lowercase()) {
            "guntur" -> ApRegionWeatherResponse(
                regionId = "guntur",
                districtName = "Guntur District",
                districtNameTe = "గుంటూరు జిల్లా",
                inferredZone = "Central AP Chilli & Cotton Agro Zone",
                currentTempC = 34,
                feelsLikeC = 38,
                weatherCondition = "Partly Cloudy • Humid",
                rainProbability = 65,
                humidityPercent = 78,
                windSpeedKmh = 14,
                uvIndex = "7.2 (High)",
                dewPointC = 23,
                apAgriAlertEn = "🌧️ 65% Rain Chance: Postpone Chilli sprayings until rain passes. Keep field drainage channels clear.",
                apAgriAlertTe = "🌧️ 65% వర్ష సూచన: మిర్చి తోటల్లో మందుల పిచికారీ వాయిదా వేయండి. డ్రైనేజీ కాలువలు శుభ్రం చేయండి.",
                hourlyForecast = listOf(
                    HourlyForecast("09:00", 31, 20, "☀️ Sunny"),
                    HourlyForecast("12:00", 34, 45, "⛅ Partly Cloudy"),
                    HourlyForecast("15:00", 35, 65, "🌧️ Light Rain"),
                    HourlyForecast("18:00", 32, 70, "🌧️ Rain Showers"),
                    HourlyForecast("21:00", 29, 30, "☁️ Overcast"),
                    HourlyForecast("00:00", 27, 10, "🌙 Clear")
                ),
                dailyForecast = listOf(
                    DailyForecast("Today", "Jul 29", 35, 26, 65, "Rain Showers", "High rain chance. Hold off on pesticide sprays.", "వర్ష సూచన. మందుల పిచికారీ వాయిదా."),
                    DailyForecast("Wed", "Jul 30", 33, 25, 40, "Scattered Clouds", "Good window for weeding and fertilizer application.", "కలుపు తీతకు అనుకూల సమయం."),
                    DailyForecast("Thu", "Jul 31", 34, 26, 20, "Sunny", "Optimal day for Neem oil spray against Black Thrips.", "నల్ల తామర పురుగు మందు పిచికారీకి అనుకూలం."),
                    DailyForecast("Fri", "Aug 01", 36, 27, 15, "Hot Sunshine", "Provide light surface irrigation to prevent heat stress.", "మిర్చి పైరుకు తేలికపాటి తడి ఇవ్వండి."),
                    DailyForecast("Sat", "Aug 02", 35, 26, 50, "Thunderstorms", "Potential evening rain. Secure harvested chilli pods.", "సాయంత్రం వాన పడే అవకాశం. ఎండు మిర్చిని కప్పండి.")
                )
            )
            "krishna" -> ApRegionWeatherResponse(
                regionId = "krishna",
                districtName = "Krishna & NTR District",
                districtNameTe = "కృష్ణా & ఎన్టీఆర్ జిల్లా",
                inferredZone = "Krishna Delta Rice & Sugarcane Belt",
                currentTempC = 32,
                feelsLikeC = 37,
                weatherCondition = "Scattered Showers 🌧️",
                rainProbability = 80,
                humidityPercent = 85,
                windSpeedKmh = 18,
                uvIndex = "5.5 (Mod)",
                dewPointC = 25,
                apAgriAlertEn = "⚠️ 80% Heavy Rain Risk: Cover harvested Paddy grain heaps with tarpaulins immediately.",
                apAgriAlertTe = "⚠️ 80% వర్ష ప్రమాదం: కోత కోసిన వరి ధాన్యాన్ని వెంటనే టార్పాలిన్లతో కప్పండి.",
                hourlyForecast = listOf(
                    HourlyForecast("09:00", 29, 60, "🌧️ Rain"),
                    HourlyForecast("12:00", 31, 80, "🌧️ Heavy Rain"),
                    HourlyForecast("15:00", 32, 75, "🌧️ Showers"),
                    HourlyForecast("18:00", 30, 50, "☁️ Overcast"),
                    HourlyForecast("21:00", 28, 20, "☁️ Cloudy"),
                    HourlyForecast("00:00", 26, 15, "🌙 Clear")
                ),
                dailyForecast = listOf(
                    DailyForecast("Today", "Jul 29", 32, 25, 80, "Heavy Rain", "Cover harvested paddy and stop urea top dressing.", "ధాన్యం తడవకుండా చూసుకోండి."),
                    DailyForecast("Wed", "Jul 30", 31, 24, 60, "Moderate Rain", "Ensure standing water level is kept under 5cm.", "చేనులో 5సెం.మీ మించి నీరు ఉండకుండా చూడండి."),
                    DailyForecast("Thu", "Jul 31", 33, 25, 30, "Partly Cloudy", "Drain excess field water to avoid root suffocation.", "అదనపు నీటిని బయటకు పంపండి."),
                    DailyForecast("Fri", "Aug 01", 34, 26, 10, "Sunny", "Ideal weather for zinc sulfate application.", "జింక్ సల్ఫేట్ వాడకానికి అనుకూలం."),
                    DailyForecast("Sat", "Aug 02", 35, 26, 25, "Clear Sky", "Normal irrigation schedule can be resumed.", "సాధారణ సాగునీటి నిర్వహణ.")
                )
            )
            "kurnool" -> ApRegionWeatherResponse(
                regionId = "kurnool",
                districtName = "Kurnool District",
                districtNameTe = "కర్నూలు జిల్లా",
                inferredZone = "Rayalaseema Semi-Arid Groundnut & Onion Belt",
                currentTempC = 37,
                feelsLikeC = 41,
                weatherCondition = "Hot Dry Sunshine 🌤️",
                rainProbability = 10,
                humidityPercent = 40,
                windSpeedKmh = 16,
                uvIndex = "9.5 (Very High)",
                dewPointC = 17,
                apAgriAlertEn = "🌤️ Low 10% Rain: High evapotranspiration. Run drip irrigation early morning to protect Groundnut.",
                apAgriAlertTe = "🌤️ 10% తక్కువ వర్షం: ఎండ తీవ్రత వల్ల వేరుశనగ పంటకు ఉదయమే డ్రిప్ తడి ఇవ్వండి.",
                hourlyForecast = listOf(
                    HourlyForecast("09:00", 32, 5, "☀️ Sunny"),
                    HourlyForecast("12:00", 36, 10, "☀️ Hot Sun"),
                    HourlyForecast("15:00", 37, 10, "☀️ Peak Heat"),
                    HourlyForecast("18:00", 34, 5, "🌤️ Clear"),
                    HourlyForecast("21:00", 30, 0, "🌙 Breezy"),
                    HourlyForecast("00:00", 27, 0, "🌙 Clear")
                ),
                dailyForecast = listOf(
                    DailyForecast("Today", "Jul 29", 37, 26, 10, "Hot & Dry", "Schedule drip irrigation during cool hours.", "ఉదయమే డ్రిప్ ద్వారా తడి ఇవ్వండి."),
                    DailyForecast("Wed", "Jul 30", 38, 27, 5, "Sunny Heatwave", "Apply straw mulch to conserve soil moisture.", "మట్టిలో తేమ ఇంకిపోకుండా మల్చింగ్ చేయండి."),
                    DailyForecast("Thu", "Jul 31", 37, 26, 15, "Partly Warm", "Check groundnut foliage for tikka leaf spot.", "ఆకుమచ్చ తెగులు పర్యవేక్షణ చేయండి."),
                    DailyForecast("Fri", "Aug 01", 36, 25, 30, "Passing Clouds", "Good condition for onion weeding.", "ఉల్లి చేనులో కలుపు తీత."),
                    DailyForecast("Sat", "Aug 02", 36, 25, 20, "Sunny", "Maintain light sprinkler irrigation.", "స్ప్రింక్లర్ ద్వారా నీటి తడి.")
                )
            )
            "godavari" -> ApRegionWeatherResponse(
                regionId = "godavari",
                districtName = "East & West Godavari",
                districtNameTe = "తూర్పు & పశ్చిమ గోదావరి",
                inferredZone = "Godavari Fertile Delta & Aqua Culture Belt",
                currentTempC = 33,
                feelsLikeC = 39,
                weatherCondition = "Tropical Humidity • Rain Risk",
                rainProbability = 75,
                humidityPercent = 88,
                windSpeedKmh = 15,
                uvIndex = "6.0 (Mod)",
                dewPointC = 26,
                apAgriAlertEn = "🌧️ 75% Rain Probability: Monitor paddy fields for Bacterial Leaf Blight under high humidity.",
                apAgriAlertTe = "🌧️ 75% వర్ష సూచన: అధిక తేమ కారణంగా వరిలో బాక్టీరియా ఎండు తెగులును గమనించండి.",
                hourlyForecast = listOf(
                    HourlyForecast("09:00", 30, 40, "⛅ Humid"),
                    HourlyForecast("12:00", 33, 75, "🌧️ Rain"),
                    HourlyForecast("15:00", 33, 80, "🌧️ Heavy Rain"),
                    HourlyForecast("18:00", 31, 60, "🌧️ Rain"),
                    HourlyForecast("21:00", 29, 30, "☁️ Overcast"),
                    HourlyForecast("00:00", 27, 20, "🌙 Warm")
                ),
                dailyForecast = listOf(
                    DailyForecast("Today", "Jul 29", 33, 26, 75, "Tropical Rain", "Keep fish/shrimp pond aeration running.", "చెరువుల్లో ఏరేటర్లు నిరంతరం రన్ చేయండి."),
                    DailyForecast("Wed", "Jul 30", 32, 25, 70, "Rain Showers", "Check paddy field bunds for leaks.", "వరి గట్ల లీకేజీలను అరికట్టండి."),
                    DailyForecast("Thu", "Jul 31", 34, 26, 35, "Humid Sun", "Apply copper fungicide if blight spotted.", "ఫంగీసైడ్ చల్లడానికి అనుకూలం."),
                    DailyForecast("Fri", "Aug 01", 35, 27, 20, "Partly Cloudy", "Top dress fertilizers after rain stops.", "వర్షం తగ్గాక ఎరువులు వేయండి."),
                    DailyForecast("Sat", "Aug 02", 34, 26, 40, "Coastal Breeze", "Regular irrigation and water monitoring.", "సాధారణ నీటి యాజమాన్యం.")
                )
            )
            "vizag" -> ApRegionWeatherResponse(
                regionId = "vizag",
                districtName = "Visakhapatnam & North Coast",
                districtNameTe = "విశాఖపట్నం & ఉత్తరాంధ్ర",
                inferredZone = "North Coastal AP Sugarcane & Cashew Zone",
                currentTempC = 31,
                feelsLikeC = 36,
                weatherCondition = "Coastal Wind • Mild Rain",
                rainProbability = 50,
                humidityPercent = 82,
                windSpeedKmh = 22,
                uvIndex = "6.8 (High)",
                dewPointC = 24,
                apAgriAlertEn = "💨 22 km/h Winds & 50% Rain: Support young banana & sugarcane plants with stakes.",
                apAgriAlertTe = "💨 22 కిమీ/గంట ఈదురు గాలులు & 50% వర్షం: అరటి, చెరకు పైర్లకు కర్రల ప్రాప్ బలం ఇవ్వండి.",
                hourlyForecast = listOf(
                    HourlyForecast("09:00", 29, 30, "💨 Breezy"),
                    HourlyForecast("12:00", 31, 50, "🌧️ Passing Rain"),
                    HourlyForecast("15:00", 31, 55, "🌧️ Rain Showers"),
                    HourlyForecast("18:00", 30, 40, "⛅ Windy"),
                    HourlyForecast("21:00", 28, 20, "☁️ Cloud"),
                    HourlyForecast("00:00", 26, 10, "🌙 Sea Breeze")
                ),
                dailyForecast = listOf(
                    DailyForecast("Today", "Jul 29", 31, 25, 50, "Coastal Rain", "Stake sugarcane to prevent wind lodging.", "ఈదురు గాలుల నుండి చెరకును రక్షించండి."),
                    DailyForecast("Wed", "Jul 30", 32, 26, 35, "Windy Sun", "Clean cashew plantations of fallen debris.", "జీడిమామిడి తోటల నిర్వహణ."),
                    DailyForecast("Thu", "Jul 31", 33, 26, 25, "Sunny Coast", "Foliar spray of micronutrients.", "సూక్ష్మపోషకాల పిచికారీ."),
                    DailyForecast("Fri", "Aug 01", 33, 27, 30, "Humid", "Regular crop inspection for stem borer.", "కాండం తొలిచే పురుగు పరిశీలన."),
                    DailyForecast("Sat", "Aug 02", 32, 25, 60, "Sea Thunder", "Keep drainage open for unexpected coastal rain.", "తీరప్రాంత వర్షాల హెచ్చరిక.")
                )
            )
            else -> ApRegionWeatherResponse(
                regionId = "guntur",
                districtName = "Guntur District",
                districtNameTe = "గుంటూరు జిల్లా",
                inferredZone = "Central AP Chilli & Cotton Agro Zone",
                currentTempC = 34,
                feelsLikeC = 38,
                weatherCondition = "Partly Cloudy • Humid",
                rainProbability = 65,
                humidityPercent = 78,
                windSpeedKmh = 14,
                uvIndex = "7.2 (High)",
                dewPointC = 23,
                apAgriAlertEn = "🌧️ 65% Rain Chance: Postpone Chilli sprayings until rain passes. Keep field drainage channels clear.",
                apAgriAlertTe = "🌧️ 65% వర్ష సూచన: మిర్చి తోటల్లో మందుల పిచికారీ వాయిదా వేయండి. డ్రైనేజీ కాలువలు శుభ్రం చేయండి.",
                hourlyForecast = listOf(
                    HourlyForecast("09:00", 31, 20, "☀️ Sunny"),
                    HourlyForecast("12:00", 34, 45, "⛅ Partly Cloudy"),
                    HourlyForecast("15:00", 35, 65, "🌧️ Light Rain"),
                    HourlyForecast("18:00", 32, 70, "🌧️ Rain Showers"),
                    HourlyForecast("21:00", 29, 30, "☁️ Overcast"),
                    HourlyForecast("00:00", 27, 10, "🌙 Clear")
                ),
                dailyForecast = listOf(
                    DailyForecast("Today", "Jul 29", 35, 26, 65, "Rain Showers", "High rain chance. Hold off on pesticide sprays.", "వర్ష సూచన. మందుల పిచికారీ వాయిదా."),
                    DailyForecast("Wed", "Jul 30", 33, 25, 40, "Scattered Clouds", "Good window for weeding and fertilizer application.", "కలుపు తీతకు అనుకూల సమయం."),
                    DailyForecast("Thu", "Jul 31", 34, 26, 20, "Sunny", "Optimal day for Neem oil spray against Black Thrips.", "నల్ల తామర పురుగు మందు పిచికారీకి అనుకూలం."),
                    DailyForecast("Fri", "Aug 01", 36, 27, 15, "Hot Sunshine", "Provide light surface irrigation to prevent heat stress.", "మిర్చి పైరుకు తేలికపాటి తడి ఇవ్వండి."),
                    DailyForecast("Sat", "Aug 02", 35, 26, 50, "Thunderstorms", "Potential evening rain. Secure harvested chilli pods.", "సాయంత్రం వాన పడే అవకాశం. ఎండు మిర్చిని కప్పండి.")
                )
            )
        }
    }
}
