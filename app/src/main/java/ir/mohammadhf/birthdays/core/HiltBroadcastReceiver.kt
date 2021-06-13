package ir.mohammadhf.birthdays.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.CallSuper

/**
 * Base class used for hilt injections.
 * Hilt injects dependencies to BroadcastReceivers in onReceive 's super.
 * One work around is to call the super class's onReceive method which is abstract
 * and cannot be called.
 * So in order to call a super class's method, we made a class which can be called from children.
 */
abstract class HiltBroadcastReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context?, intent: Intent?) {
    }
}