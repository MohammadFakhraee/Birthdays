package ir.mohammadhf.birthdays.core

import android.content.SharedPreferences
import javax.inject.Inject

class InitialSharedPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object KEYS {
        const val IS_GROUP_SAVED = "key_initiate_groups"
        const val IS_ALARM_SET = "key_initiate_alarm"
    }

    fun setGroupSaved(isGroupSaved: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(IS_GROUP_SAVED, isGroupSaved)
        editor.apply()
    }

    fun isGroupSaved(): Boolean =
        sharedPreferences.getBoolean(IS_GROUP_SAVED, false)

    fun setAlarmSet(isAlarmSet: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(IS_ALARM_SET, isAlarmSet)
        editor.apply()
    }

    fun isAlarmSet(): Boolean =
        sharedPreferences.getBoolean(IS_ALARM_SET, false)
}