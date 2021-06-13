package ir.mohammadhf.birthdays.utils

import android.content.Context
import android.content.res.Configuration
import ir.mohammadhf.birthdays.BuildConfig
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

class WrapContext @Inject constructor() {
    fun setLocale(context: Context): Context {
        return setLocale(context, language)
    }

    private fun setLocale(context: Context, locale: Locale?): Context {
        if (locale == null) return context
        Locale.setDefault(locale)
        val newConfig = Configuration()
        newConfig.setLocale(locale)
        return context.createConfigurationContext(newConfig)
    }

    private val language: Locale
        get() = Locale(BuildConfig.CURRENT_LANG)

}