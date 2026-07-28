package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import java.util.Calendar

enum class AgriSeason(
    val titleEn: String,
    val titleTe: String,
    val months: String,
    val icon: String,
    val descriptionEn: String,
    val descriptionTe: String
) {
    KHARIF(
        titleEn = "Kharif (Monsoon Season)",
        titleTe = "ఖరీఫ్ కాలం • వర్షాకాలం",
        months = "June - October",
        icon = "🌧️",
        descriptionEn = "Lush green & rain sky palette. Sowing time for Chilli, Cotton, Paddy & Turmeric in AP & TS.",
        descriptionTe = "వర్షాకాలపు హరిత వాతావరణం. మిర్చి, పత్తి, వరి మరియు పసుపు పంటల కాలం."
    ),
    RABI(
        titleEn = "Rabi (Winter Harvest Season)",
        titleTe = "రబీ కాలం • శీతాకాలం",
        months = "November - February",
        icon = "🌾",
        descriptionEn = "Golden amber & harvest gold palette. Growing Bengal Gram, Groundnut, Maize & Winter Paddy.",
        descriptionTe = "శీతాకాలపు స్వర్ణమయ పంటల కాలం. శనగ, వేరుశనగ, జొన్న మరియు రబీ పంటలు."
    ),
    YASANGI(
        titleEn = "Yasangi (Summer Crop Season)",
        titleTe = "యాసంగి • ఎండాకాలం",
        months = "March - May",
        icon = "☀️",
        descriptionEn = "Terracotta copper & sun flare palette. Focus on drip irrigation, pulses, sesame & vegetables.",
        descriptionTe = "యాసంగి వేసవి కాలం. బిందు సేద్యం, మినుములు, నువ్వులు మరియు తోటకూరలు."
    );

    companion object {
        fun detectCurrentSeason(): AgriSeason {
            val month = Calendar.getInstance().get(Calendar.MONTH)
            return when (month) {
                Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER -> KHARIF
                Calendar.NOVEMBER, Calendar.DECEMBER, Calendar.JANUARY, Calendar.FEBRUARY -> RABI
                else -> YASANGI
            }
        }
    }
}

fun getSeasonalLightColorScheme(season: AgriSeason): ColorScheme {
    return when (season) {
        AgriSeason.KHARIF -> lightColorScheme(
            primary = KharifPrimaryLight,
            secondary = KharifSecondaryLight,
            tertiary = KharifTertiaryLight,
            background = KharifBackgroundLight,
            surface = Color.White
        )
        AgriSeason.RABI -> lightColorScheme(
            primary = RabiPrimaryLight,
            secondary = RabiSecondaryLight,
            tertiary = RabiTertiaryLight,
            background = RabiBackgroundLight,
            surface = Color.White
        )
        AgriSeason.YASANGI -> lightColorScheme(
            primary = YasangiPrimaryLight,
            secondary = YasangiSecondaryLight,
            tertiary = YasangiTertiaryLight,
            background = YasangiBackgroundLight,
            surface = Color.White
        )
    }
}

fun getSeasonalDarkColorScheme(season: AgriSeason): ColorScheme {
    return when (season) {
        AgriSeason.KHARIF -> darkColorScheme(
            primary = KharifPrimaryDark,
            secondary = KharifSecondaryDark,
            tertiary = KharifTertiaryDark,
            background = KharifBackgroundDark,
            surface = Color(0xFF0D381B)
        )
        AgriSeason.RABI -> darkColorScheme(
            primary = RabiPrimaryDark,
            secondary = RabiSecondaryDark,
            tertiary = RabiTertiaryDark,
            background = RabiBackgroundDark,
            surface = Color(0xFF3B220C)
        )
        AgriSeason.YASANGI -> darkColorScheme(
            primary = YasangiPrimaryDark,
            secondary = YasangiSecondaryDark,
            tertiary = YasangiTertiaryDark,
            background = YasangiBackgroundDark,
            surface = Color(0xFF3F190D)
        )
    }
}

@Composable
fun CropDiseaseScannerTheme(
    agriSeason: AgriSeason = AgriSeason.detectCurrentSeason(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> getSeasonalDarkColorScheme(agriSeason)
        else -> getSeasonalLightColorScheme(agriSeason)
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
