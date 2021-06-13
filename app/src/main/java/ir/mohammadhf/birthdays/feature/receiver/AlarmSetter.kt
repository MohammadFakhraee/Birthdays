package ir.mohammadhf.birthdays.feature.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ir.mohammadhf.birthdays.feature.splash.SplashFragment
import java.util.*

// Notification channel constants
@JvmField
val NOTIFICATION_CHANNEL_NAME: CharSequence = "Birthday notifications"
const val NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications before person's birthday"
const val CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
const val NOTIFICATION_ID = 1001

/**
 * Use this function to make an alarm which is fired everyday in the morning
 * at 8 o'clock.
 * @param context ActivityContext used to get alarmManager and set the alarm
 */
fun setTheAlarm(context: Context) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 0)
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val alarmIntent = Intent(context, AlarmReceiver::class.java).let {
        PendingIntent.getBroadcast(context, SplashFragment.NOTIFICATION_REQUEST_CODE, it, 0)
    }

    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        alarmIntent
    )
}