package ir.mohammadhf.birthdays.feature.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ir.mohammadhf.birthdays.R
import ir.mohammadhf.birthdays.core.HiltBroadcastReceiver
import ir.mohammadhf.birthdays.data.repo.PersonRepository
import ir.mohammadhf.birthdays.utils.DateManager
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : HiltBroadcastReceiver() {
    @Inject
    lateinit var personRepository: PersonRepository

    @Inject
    lateinit var dateManager: DateManager

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        val disposable = personRepository.getAllPersons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { persons ->
                val today = dateManager.getTodayDateArray()
                persons.forEach { person ->
                    if (person.notifyDay == today[2]
                        && person.notifyMonth == today[1]
                    ) {
                        triggerNotification(
                            context!!,
                            context.getString(R.string.notification_title, person.name),
                            context.getString(R.string.notification_description, person.name),
                            BitmapFactory.decodeFile(person.avatarPath),
                            person.id
                        )
                    }
                }
            }
    }

    private fun triggerNotification(
        context: Context,
        notificationTitle: String,
        notificationDesc: String,
        notificationBitmap: Bitmap,
        personId: Long
    ) {
        // Creating notification channel on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = NOTIFICATION_CHANNEL_DESCRIPTION

            // Get notificationManager from context
            val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            // Add channel
            notificationManager?.createNotificationChannel(channel)
        }

        // todo: make PendingIntent for doing action when notification pressed.

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(notificationBitmap)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDesc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID + personId.toInt(), notification)
    }
}

