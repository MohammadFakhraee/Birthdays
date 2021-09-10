package ir.mohammadhf.birthdays.feature.notify

import android.content.Context
import android.content.Intent
import ir.mohammadhf.birthdays.core.HiltBroadcastReceiver

class RebootReceiver : HiltBroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            setTheAlarm(context!!, false)
        }
    }
}