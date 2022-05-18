package ir.mohammadhf.birthdays.feature.birthdays

import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ir.mohammadhf.birthdays.core.InitialSharedPreferences
import ir.mohammadhf.birthdays.core.bases.BaseViewModel
import ir.mohammadhf.birthdays.data.model.BirthdayMulti
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.data.repo.GroupRepository
import ir.mohammadhf.birthdays.data.repo.PersonRepository
import ir.mohammadhf.birthdays.utils.DateManager
import ir.mohammadhf.birthdays.utils.PersonConvertor
import javax.inject.Inject

@HiltViewModel
class BirthListViewModel @Inject constructor(
    private val personRepository: PersonRepository,
    private val groupRepository: GroupRepository,
    private val dateManager: DateManager,
    private val personConvertor: PersonConvertor,
    private val initialSharedPreferences: InitialSharedPreferences
) : BaseViewModel() {
    val personListBS = BehaviorSubject.create<ArrayList<BirthdayMulti>>()
    val groupListBS = BehaviorSubject.create<List<Group>>()
    val loadingPersonsBS = BehaviorSubject.create<Boolean>()

    private var firstLoad = true

    fun getPersonList(filterIDs: ArrayList<Long>) {
        if (firstLoad) {
            loadingPersonsBS.onNext(true)
            firstLoad = false
        }
        compositeDisposable.add(personRepository.getAllPersons()
            .subscribeOn(Schedulers.io())
            .map {
                // Filtering list by chosen Groups
                val personList = ArrayList(it.filter { person ->
                    (filterIDs.isEmpty()) or (filterIDs.contains(person.groupId))
                })
                // mapping Person model list to BirthdayMulti model list
                personConvertor.sortedPersonToBirthdayMulti(personList, dateManager)
            }
            .subscribe {
                personListBS.onNext(it)
                loadingPersonsBS.onNext(false)
            })
    }

    fun getGroupList() {
        compositeDisposable.add(groupRepository.getAll()
            .subscribeOn(Schedulers.io())
            .subscribe { groupList, error ->
                groupList?.let { groupListBS.onNext(it) }
                error?.printStackTrace()
            })
    }

    fun isInitialDone(): Boolean =
        initialSharedPreferences.isInitialDone()
}
