package ir.mohammadhf.birthdays.core

import android.content.SharedPreferences
import javax.inject.Inject

class InitialSharedPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object KEYS {
        const val IS_GROUP_SAVED = "key_initiate_groups"
        const val IS_ALARM_SET = "key_initiate_alarm_manager"
        const val IS_GIFT_FRAMES_SAVED = "key_gift_frames"
        const val IS_INITIAL_DONE = "key_init_done"
    }

    fun setGroupSaved(isGroupSaved: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(IS_GROUP_SAVED, isGroupSaved)
            apply()
        }
    }

    fun isGroupSaved(): Boolean =
        sharedPreferences.getBoolean(IS_GROUP_SAVED, false)

    fun setAlarmSet(isAlarmSet: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(IS_ALARM_SET, isAlarmSet)
            apply()
        }
    }

    fun isAlarmSet(): Boolean =
        sharedPreferences.getBoolean(IS_ALARM_SET, false)

    fun setGiftFrameSaved(isFramesSaved: Boolean) {
        sharedPreferences.edit().apply() {
            putBoolean(IS_GIFT_FRAMES_SAVED, isFramesSaved)
            apply()
        }
    }

    fun isFramesSaved(): Boolean =
        sharedPreferences.getBoolean(IS_GIFT_FRAMES_SAVED, false)

    fun setInitialDone(isInitialDone: Boolean) {
        sharedPreferences.edit().apply() {
            putBoolean(IS_INITIAL_DONE, isInitialDone)
            apply()
        }
    }

    fun isInitialDone(): Boolean =
        sharedPreferences.getBoolean(IS_INITIAL_DONE, false)
}