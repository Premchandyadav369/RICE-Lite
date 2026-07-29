package com.example.ui.util

import android.content.Context
import java.util.Locale

data class LanguageOption(
    val code: String,
    val name: String,
    val nativeName: String,
    val flag: String
)

object LanguageUtils {
    val PRIMARY_LANGUAGES = listOf(
        LanguageOption("TE", "Telugu", "తెలుగు", "🏛️"),
        LanguageOption("EN", "English", "English", "🇬🇧"),
        LanguageOption("HI", "Hindi", "हिन्दी", "🇮🇳")
    )

    val SUPPORTED_LANGUAGES = listOf(
        LanguageOption("TE", "Telugu", "తెలుగు (ఆంధ్రప్రదేశ్/తెలంగాణ)", "🏛️"),
        LanguageOption("EN", "English", "English", "🇬🇧"),
        LanguageOption("HI", "Hindi", "हिन्दी", "🇮🇳"),
        LanguageOption("PA", "Punjabi", "ਪੰਜਾਬੀ", "🌾"),
        LanguageOption("MR", "Marathi", "मराठी", "🚩"),
        LanguageOption("GU", "Gujarati", "ગુજરાતી", "🦁"),
        LanguageOption("TA", "Tamil", "தமிழ்", "🛕"),
        LanguageOption("BN", "Bengali", "বাংলা", "🐅")
    )

    fun updateAppLocale(context: Context, langStr: String) {
        try {
            val opt = getLanguageOption(langStr)
            val locale = when (opt.code) {
                "TE" -> Locale("te", "IN")
                "HI" -> Locale("hi", "IN")
                else -> Locale("en", "US")
            }
            Locale.setDefault(locale)
            val config = context.resources.configuration
            config.setLocale(locale)
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        } catch (_: Exception) {}
    }

    fun getLanguageOption(langStr: String): LanguageOption {
        return SUPPORTED_LANGUAGES.find {
            langStr.contains(it.name, ignoreCase = true) ||
                    langStr.contains(it.nativeName, ignoreCase = true) ||
                    langStr.equals(it.code, ignoreCase = true)
        } ?: SUPPORTED_LANGUAGES[0]
    }

    fun getNavLabel(key: String, langStr: String): String {
        val opt = getLanguageOption(langStr)
        return when (key) {
            "home" -> when (opt.code) {
                "HI" -> "होम"
                "PA" -> "ਹੋਮ"
                "MR" -> "मुख्य"
                "GU" -> "હોમ"
                "TE" -> "హోమ్"
                "TA" -> "முகப்பு"
                "BN" -> "হোম"
                else -> "Home"
            }
            "farm" -> when (opt.code) {
                "HI" -> "कृषि"
                "PA" -> "ਖੇਤ"
                "MR" -> "शेत"
                "GU" -> "ખેતર"
                "TE" -> "పొలం"
                "TA" -> "பண்ணை"
                "BN" -> "খামার"
                else -> "Farm"
            }
            "market" -> when (opt.code) {
                "HI" -> "मंडी"
                "PA" -> "ਮੰਡੀ"
                "MR" -> "बाजार"
                "GU" -> "માર્કેટ"
                "TE" -> "మార్కెట్"
                "TA" -> "சந்தை"
                "BN" -> "বাজার"
                else -> "Market"
            }
            "finance" -> when (opt.code) {
                "HI" -> "वित्त"
                "PA" -> "ਵਿੱਤ"
                "MR" -> "वित्त"
                "GU" -> "નાણા"
                "TE" -> "ఫైనాన్స్"
                "TA" -> "நிதி"
                "BN" -> "অর্থ"
                else -> "Finance"
            }
            "profile" -> when (opt.code) {
                "HI" -> "प्रोफाइल"
                "PA" -> "ਪ੍ਰੋਫਾਈਲ"
                "MR" -> "प्रोफाईल"
                "GU" -> "પ્રોફાઇલ"
                "TE" -> "ప్రొఫైల్"
                "TA" -> "சுயவிவரம்"
                "BN" -> "প্রোফাইল"
                else -> "Profile"
            }
            "scanner" -> when (opt.code) {
                "HI" -> "एआई निदान"
                "PA" -> "AI ਨਿਦਾਨ"
                "MR" -> "AI निदान"
                "GU" -> "AI નિદાન"
                "TE" -> "AI స్కానర్"
                "TA" -> "AI சோதனை"
                "BN" -> "AI স্ক্যানার"
                else -> "AI Scanner"
            }
            else -> key
        }
    }

    fun getTitle(activeTab: Int, langStr: String): String {
        val opt = getLanguageOption(langStr)
        return when (activeTab) {
            0 -> when (opt.code) {
                "HI" -> "कृषि-दृष्टि सुपर ऐप"
                "PA" -> "ਕ੍ਰਿਸ਼ੀ ਸੁਪਰ ਐਪ"
                "MR" -> "कृषीदृष्टी सुपर ॲप"
                "GU" -> "કૃષિ સુપર એપ"
                "TE" -> "కృషి దృష్టీ సూపర్ యాప్"
                "TA" -> "கிருஷி சூப்பர் ஆப்"
                "BN" -> "কৃষি দৃষ্টি সুপার অ্যাপ"
                else -> "RICE Super App"
            }
            1 -> when (opt.code) {
                "HI" -> "सटीक खेती एवं उपग्रह"
                "PA" -> "ਖੇਤੀਬਾੜੀ ਅਤੇ ਸੈਟੇਲਾਈਟ"
                "MR" -> "अचूक शेती आणि उपग्रह"
                "GU" -> "ચોક્કસ ખેતી અને સેટેલાઇટ"
                "TE" -> "ఖచ్చితమైన వ్యవసాయం"
                "TA" -> "துல்லிய விவசாயம்"
                "BN" -> "সুনির্দিষ্ট কৃষি ও উপগ্রহ"
                else -> "Precision Farm & Satellite"
            }
            2 -> when (opt.code) {
                "HI" -> "कृषि मंडी एवं लाइव भाव"
                "PA" -> "ਕ੍ਰਿਸ਼ੀ ਮੰਡੀ ਅਤੇ ਲਾਈਵ ਰੇਟ"
                "MR" -> "कृषी बाजार आणि दर"
                "GU" -> "કૃષિ મંડી અને ભાવ"
                "TE" -> "వ్యవసాయ మార్కెట్ & ధరలు"
                "TA" -> "வேளாண் சந்தை & விலைகள்"
                "BN" -> "কৃষি মান্ডি ও দর"
                else -> "Agri Marketplace & Bids"
            }
            3 -> when (opt.code) {
                "HI" -> "वित्त, ऋण एवं डेयरी"
                "PA" -> "ਵਿੱਤ, ਕਰਜ਼ਾ ਅਤੇ ਡੇਅਰੀ"
                "MR" -> "वित्त, कर्ज आणि डेअरी"
                "GU" -> "નાણા, લોન અને ડેરી"
                "TE" -> "ఫైనాన్స్, రుణాలు & డెయిరీ"
                "TA" -> "நிதி, கடன்கள் & பால்"
                "BN" -> "অর্থ, ঋণ ও ডেইরি"
                else -> "Finance, Loans & Dairy"
            }
            4 -> when (opt.code) {
                "HI" -> "प्रोफाइल एवं ऑफ़लाइन डेटा"
                "PA" -> "ਪ੍ਰੋਫਾਈਲ ਅਤੇ ਔਫਲਾਈਨ ਡਾਟਾ"
                "MR" -> "प्रोफाईल आणि ऑफलाइन डेटा"
                "GU" -> "પ્રોફાઇલ અને ઓફલાઇન ડેટા"
                "TE" -> "ప్రొఫైల్ & ఆఫ్‌లైన్ డేటా"
                "TA" -> "சுயவிவரம் & ஆஃப்லைன்"
                "BN" -> "প্রোফাইল ও অফলাইন তথ্য"
                else -> "Profile & Offline Cache"
            }
            5 -> when (opt.code) {
                "HI" -> "एआई रोग निदान स्कैनर"
                "PA" -> "AI ਬਿਮਾਰੀ ਨਿਦਾਨ ਸਕੈਨਰ"
                "MR" -> "AI रोग निदान स्कॅनर"
                "GU" -> "AI રોગ નિદાન સ્કેનર"
                "TE" -> "AI పంట వ్యాధి నిర్ధారణ"
                "TA" -> "AI நோய் கண்டறிதல்"
                "BN" -> "AI রোগ নির্ণয় স্ক্যানার"
                else -> "AI Disease Scanner"
            }
            else -> "KrishiDrishti AI"
        }
    }
}
