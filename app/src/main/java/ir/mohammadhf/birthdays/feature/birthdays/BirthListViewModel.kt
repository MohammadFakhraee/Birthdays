package ir.mohammadhf.birthdays.feature.birthdays

import androidx.hilt.lifecycle.ViewModelInject
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ir.mohammadhf.birthdays.BuildConfig
import ir.mohammadhf.birthdays.core.bases.BaseViewModel
import ir.mohammadhf.birthdays.data.model.*
import ir.mohammadhf.birthdays.data.repo.GroupRepository
import ir.mohammadhf.birthdays.data.repo.PersonRepository
import ir.mohammadhf.birthdays.utils.DateManager

class BirthListViewModel @ViewModelInject constructor(
    private val personRepository: PersonRepository,
    private val groupRepository: GroupRepository,
    private val dateManager: DateManager
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
                // Converting person list to BirthdayMulti
                // in order to showing in BirthdayListAdapter
                val birthdayList = ArrayList<BirthdayMulti>()
                // Filtering list by chosen Groups
                val personList = ArrayList(it.filter { person ->
                    (filterIDs.isEmpty()) or (filterIDs.contains(person.groupId))
                })
                // Sorting person list by days left.
                reorderList(personList)
                val subList =
                    ArrayList(personList.subList(0, datesBeforeToday(personList)))
                personList.removeAll(subList)
                personList.addAll(subList)
                // 1. adding todayTitle to birthdayList
                // 2. change person to BirthdayPerson model and add it to birthdayList
                var lastBirthdayTitle = ""
                personList.forEach { person ->
                    val birthdayTitle = getBirthdayTitle(person)
                    // number 1
                    if (birthdayTitle != lastBirthdayTitle) {
                        birthdayList.add(BirthdayTitle(birthdayTitle))
                        lastBirthdayTitle = birthdayTitle
                    }
                    // number 2
                    birthdayList.add(
                        BirthdayPerson(
                            person.id,
                            person.name,
                            dateManager.daysLeft(person.birthdayMonth, person.birthdayDay),
                            person.avatarPath,
                            person.getAgeOnBirthday(dateManager.getTodayDateArray())
                        )
                    )
                }

                birthdayList
            }
            .observeOn(Schedulers.io())
            .subscribe {
                personListBS.onNext(it)
                loadingPersonsBS.onNext(false)
            })
    }

    fun getGroupList() {
        groupRepository.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(object : SingleObserver<List<Group>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(groupList: List<Group>) {
                    groupListBS.onNext(groupList)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    private fun reorderList(personList: ArrayList<Person>) {
        for (i in 0 until personList.size - 1) {
            for (j in i + 1 until personList.size) {
                if (personList[i].isBirthdayAfter(personList[j])) {
                    val temp = personList[i]
                    personList[i] = personList[j]
                    personList[j] = temp
                }
            }
        }
    }

    private fun datesBeforeToday(personList: ArrayList<Person>): Int {
        val todayIntDate = dateManager.getTodayDateArray()
        var length = 0
        for (it in personList) {
            if ((it.birthdayMonth < todayIntDate[1])
                or ((it.birthdayMonth == todayIntDate[1])
                        and (it.birthdayDay < todayIntDate[2]))
            )
                length++
            else
                break
        }
        return length
    }

    private fun getBirthdayTitle(person: Person): String {
        val todayIntArray = dateManager.getTodayDateArray()
        return when (person.birthdayMonth) {
            todayIntArray[1] ->
                when (person.birthdayDay - todayIntArray[2]) {
                    0 -> BuildConfig.TODAY
                    in 1..6 -> BuildConfig.THIS_WEEK
                    else -> getBirthMonthTitle(person.birthdayMonth)
                }
            else -> getBirthMonthTitle(person.birthdayMonth)
        }
    }

    private fun getBirthMonthTitle(month: Int): String =
        dateManager.monthOfYear(month, BuildConfig.CURRENT_LANG)
}
