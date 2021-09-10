package ir.mohammadhf.birthdays.feature.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import ir.mohammadhf.birthdays.feature.splash.SplashFragment
import ir.mohammadhf.birthdays.utils.DateManager
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AlarmSetter @Inject constructor(private val dateManager: DateManager) {


}

const val ALARM_REPEAT_HOUR = 8
const val ALARM_REPEAT_MINUTE = 0

/**
 * Use this function to make an alarm which is fired in the morning at 8 o'clock.
 * @param context ActivityContext used to get alarmManager and set the alarm
 * @param forTomorrow declares that whether the alarm is better to be triggered tomorrow or not.
 * But at the end method decides to whether set the alarm for tomorrow or not:
 * e.g. 1 : (forTomorrow = false) and (HOUR_OF_DAY <= <code>ALARM_REPEAT_HOUR</code>) -> sets for today
 * e.g. 2 : (forTomorrow = false) and (HOUR_OF_DAY > <code>ALARM_REPEAT_HOUR</code>) -> sets for tomorrow
 * e.g. 3 : (forTomorrow = true) -> sets for tomorrow
 */
fun setTheAlarm(
    context: Context,
    forTomorrow: Boolean = false,
    alarmHour: Int = ALARM_REPEAT_HOUR,
    alarmMinute: Int = ALARM_REPEAT_MINUTE
) {
    val calendar = Calendar.getInstance().apply {
        if (forTomorrow || get(Calendar.HOUR_OF_DAY) > alarmHour)
            add(Calendar.DAY_OF_MONTH, 1)

        set(Calendar.HOUR_OF_DAY, alarmHour)
        set(Calendar.MINUTE, alarmMinute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val alarmPendingIntent = Intent(context, AlarmReceiver::class.java).let {
        PendingIntent.getBroadcast(context, SplashFragment.NOTIFICATION_REQUEST_CODE, it, 0)
    }

    alarmManager.setWindow(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_FIFTEEN_MINUTES,
        alarmPendingIntent
    )
    val str = SimpleDateFormat("yyyy/MM/dd  HH:mm:ss", Locale.US).format(calendar.time)
    Log.i(TAG, "setTheAlarm: --- Alarm has been set to $str")
    /**
     * The below code has an issue with AlarmManager.INTERVAL_DAY
     * and alarm is not triggered every day [couldn't find why!]
     * But it works perfectly with other time intervals.
     */
//    alarmManager.setInexactRepeating(
//        AlarmManager.RTC_WAKEUP,
//        calendar.timeInMillis,
//        AlarmManager.INTERVAL_DAY,
//        alarmPendingIntent
//    )
}