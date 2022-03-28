package ir.mohammadhf.birthdays.core

import android.content.SharedPreferences
import javax.inject.Inject

class SettingSharedPrefManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        const val KEY_SELECTED_IMAGE_COUNTER = "KEY_IMAGE_COUNTER"
    }

    fun getCounter(): Int =
        sharedPreferences.getInt(KEY_SELECTED_IMAGE_COUNTER, 1)

    fun updateCounter() {
        setCounter(getCounter() + 1)
    }

    private fun setCounter(counter: Int) =
        sharedPreferences.edit().apply {
            putInt(KEY_SELECTED_IMAGE_COUNTER, counter)
            apply()
        }
}