package ir.mohammadhf.birthdays.feature.setting.groups

import androidx.hilt.lifecycle.ViewModelInject
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ir.mohammadhf.birthdays.core.bases.BaseViewModel
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.data.repo.GroupRepository

class GroupsManagerViewModel @ViewModelInject constructor(
    private val groupRepository: GroupRepository
) : BaseViewModel() {
    val groupListBS = BehaviorSubject.create<List<Group>>()

    fun getGroups() {
        compositeDisposable.add(
            groupRepository.getAll()
                .subscribeOn(Schedulers.io())
                .subscribe { groups, error ->
                    groups?.let { groupListBS.onNext(groups) }

                    error?.printStackTrace()
                }
        )
    }

    fun changeGroups(group: Group) {
        compositeDisposable.add(
            groupRepository.updateGroup(group)
                .subscribeOn(Schedulers.io())
                .subscribe { getGroups() }
        )
    }

    fun removeGroup(group: Group) {
        compositeDisposable.add(
            groupRepository.deleteGroup(group)
                .subscribeOn(Schedulers.io())
                .subscribe { getGroups() }
        )
    }

    fun addGroup(group: Group) {
        compositeDisposable.add(
            groupRepository.insertOneGroup(group)
                .subscribeOn(Schedulers.io())
                .subscribe { id, error ->
                    id?.let { getGroups() }

                    error?.printStackTrace()
                }
        )
    }
}