package ir.mohammadhf.birthdays.feature.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ir.mohammadhf.birthdays.R
import javax.inject.Inject

class NotifyTrigger @Inject constructor() {
    companion object {
        // Notification channel constants
        const val NOTIFICATION_CHANNEL_NAME: String = "Birthday notifications"
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications before person's birthday"
        const val CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
        const val NOTIFICATION_ID = 1001
    }

    fun trigger(
        context: Context,
        notificationTitle: String,
        notificationDesc: String,
        notificationBitmap: Bitmap,
        personId: Long
    ) {
        // Creating notification channel on API 26+
        createChannel(context)

        // todo: make PendingIntent for doing action when notification pressed.

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setLargeIcon(notificationBitmap)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDesc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID + personId.toInt(), notification)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = NOTIFICATION_CHANNEL_DESCRIPTION }

            // Get notificationManager from context
            val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            // Add channel
            notificationManager?.let {
                if (!it.notificationChannels.contains(channel))
                    it.createNotificationChannel(channel)
            }
        }
    }
}