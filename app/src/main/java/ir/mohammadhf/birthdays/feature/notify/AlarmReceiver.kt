package ir.mohammadhf.birthdays.feature.notify

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ir.mohammadhf.birthdays.R
import ir.mohammadhf.birthdays.core.HiltBroadcastReceiver
import ir.mohammadhf.birthdays.data.repo.PersonRepository
import ir.mohammadhf.birthdays.utils.DateManager
import javax.inject.Inject

const val TAG = "AlarmSet"

@AndroidEntryPoint
class AlarmReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var personRepository: PersonRepository

    @Inject
    lateinit var dateManager: DateManager

    @Inject
    lateinit var notifyTrigger: NotifyTrigger

    private var disposable: Disposable? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.i(TAG, "onReceive: --- AlarmReceiver's onReceive called.")

        setTheAlarm(context!!, true)

        disposable = personRepository.getAllPersons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { persons ->
                Log.i(TAG, "onSubscribe: --- Person list loaded.")
                val today = dateManager.getTodayDateArray()
                persons.forEach { person ->
                    if (person.notifyDay == today[2] && person.notifyMonth == today[1]) {
                        Log.i(TAG, "onSubscribe: --- triggering notification for person.")
                        notifyTrigger.trigger(
                            context,
                            context.getString(R.string.notification_title, person.name),
                            context.getString(R.string.notification_description, person.name),
                            BitmapFactory.decodeFile(person.avatarPath),
                            person.id
                        )
                    }
                }
                disposable?.dispose()
            }
    }
}

