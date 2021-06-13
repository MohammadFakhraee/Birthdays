package ir.mohammadhf.birthdays.core

import android.content.Context
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

    fun setCounter(counter: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_SELECTED_IMAGE_COUNTER, counter)
        editor.apply()
    }

    fun updateCounter() {
        setCounter(getCounter() + 1)
    }
}