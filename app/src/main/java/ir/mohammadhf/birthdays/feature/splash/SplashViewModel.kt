package ir.mohammadhf.birthdays.feature.splash

import androidx.hilt.lifecycle.ViewModelInject
import io.reactivex.Completable
import ir.mohammadhf.birthdays.core.InitialSharedPreferences
import ir.mohammadhf.birthdays.core.bases.BaseViewModel
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.data.repo.GroupRepository

class SplashViewModel @ViewModelInject constructor(
    private val groupRepository: GroupRepository,
    private val initialSharedPreferences: InitialSharedPreferences
) : BaseViewModel() {

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