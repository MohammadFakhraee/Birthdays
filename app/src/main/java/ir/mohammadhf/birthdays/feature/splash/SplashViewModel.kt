package ir.mohammadhf.birthdays.feature.splash

import androidx.hilt.lifecycle.ViewModelInject
import io.reactivex.Completable
import io.reactivex.subjects.BehaviorSubject
import ir.mohammadhf.birthdays.core.InitialSharedPreferences
import ir.mohammadhf.birthdays.core.bases.BaseViewModel
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.data.repo.GroupRepository

class SplashViewModel @ViewModelInject constructor(
    private val groupRepository: GroupRepository,
    private val initialSharedPreferences: InitialSharedPreferences
) : BaseViewModel() {

    val onSettingAlarmBehaveSub = BehaviorSubject.create<Boolean>()

    fun saveGroups(groups: Array<Group>): Completable =
        if (!initialSharedPreferences.isGroupSaved())
            groupRepository.saveGroups(groups)
                .doOnComplete {
                    sleep()
                    initialSharedPreferences.setGroupSaved(true)
                }
        else
            Completable.create {
                if (!it.isDisposed) {
                    sleep()
                    it.onComplete()
                }
            }

    fun settingAlarm() {
        if (!initialSharedPreferences.isAlarmSet()) {
            onSettingAlarmBehaveSub.onNext(true)
            initialSharedPreferences.setAlarmSet(true)
        }
    }

    /**
     * Simulate Loading page
     */
    private fun sleep() {
        try {
            Thread.sleep(2000)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}