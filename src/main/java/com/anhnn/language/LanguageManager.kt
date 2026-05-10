package com.anhnn.language

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LanguageManager {

    enum class Language(val code: String, val displayName: String, val flagResId: Int) {
        ENGLISH("en", "English", R.drawable.eng),
        VIETNAMESE("vi", "Tiếng Việt", R.drawable.vn),
        SPANISH("es", "Español", R.drawable.es),
        FRENCH("fr", "Français", R.drawable.fr),
        GERMAN("de", "Deutsch", R.drawable.de),
        ITALIAN("it", "Italiano", R.drawable.it),
        PORTUGUESE("pt", "Português", R.drawable.pt),
        RUSSIAN("ru", "Русский", R.drawable.ru),
        JAPANESE("ja", "日本語", R.drawable.ja),
        KOREAN("ko", "한국어", R.drawable.ko),
        CHINESE_SIMPLIFIED("zh-CN", "简体中文", R.drawable.cn),
        CHINESE_TRADITIONAL("zh-TW", "繁體中文", R.drawable.tw),
        ARABIC("ar", "العربية", R.drawable.sa),
        HINDI("hi", "हिन्दी", R.drawable.`in`),
        TURKISH("tr", "Türkçe", R.drawable.tr),
        POLISH("pl", "Polski", R.drawable.pl),
        DUTCH("nl", "Nederlands", R.drawable.nl),
        INDONESIAN("id", "Bahasa Indonesia", R.drawable.id),
        THAI("th", "ไทย", R.drawable.th),
        GREEK("el", "Ελληνικά", R.drawable.gr),
        CZECH("cs", "Čeština", R.drawable.cz),
        SWEDISH("sv", "Svenska", R.drawable.se),
        NORWEGIAN("no", "Norsk", R.drawable.no),
        FINNISH("fi", "Suomi", R.drawable.fi);

        companion object {
            fun fromCode(code: String): Language? = values().find { it.code == code }
        }
    }

    fun setLanguage(context: Context, languageCode: String): Context {
        val locale = when (languageCode) {
            "zh-CN", "zh-rCN" -> Locale.SIMPLIFIED_CHINESE
            "zh-TW", "zh-rTW" -> Locale.TRADITIONAL_CHINESE
            else -> {
                val parts = languageCode.split("-")
                if (parts.size >= 2) Locale(parts[0], parts[1]) else Locale(languageCode)
            }
        }
        val config = Configuration(context.resources.configuration)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

    fun getSupportedLanguages(): List<Language> = Language.values().toList()
}
