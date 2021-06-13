package ir.mohammadhf.birthdays.feature.profile

import androidx.hilt.lifecycle.ViewModelInject
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ir.mohammadhf.birthdays.core.bases.BaseViewModel
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.data.model.Person
import ir.mohammadhf.birthdays.data.model.TimeCounter
import ir.mohammadhf.birthdays.data.repo.GroupRepository
import ir.mohammadhf.birthdays.data.repo.PersonRepository
import ir.mohammadhf.birthdays.utils.DateManager
import java.util.*

class PersonProfileViewModel @ViewModelInject constructor(
    private val personRepository: PersonRepository,
    private val groupRepository: GroupRepository,
    private val dateManager: DateManager
) : BaseViewModel() {
    val personBehaveSub = BehaviorSubject.create<Person>()
    val groupBehaveSub = BehaviorSubject.create<Group>()
    val counterBehaveSub = BehaviorSubject.create<String>()
    val onBirthdayBehaveSub = BehaviorSubject.create<Boolean>()

    private var timer: Timer? = null

    fun getPerson(id: Long) {
        compositeDisposable.add(personRepository.getPerson(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { person, _ ->
                person?.let {
                    personBehaveSub.onNext(it)
                    getGroup(it.groupId)
                    countDays(dateManager.daysLeft(it.birthdayMonth, it.birthdayDay))
                }
            })
    }

    fun deleteUser(): Completable =
        personRepository.deletePerson(personBehaveSub.value!!)

    private fun getGroup(id: Long) {
        compositeDisposable.add(
            groupRepository.getById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { group, _ ->
                    group?.let { groupBehaveSub.onNext(it) }
                }
        )
    }

    private fun countDays(daysLeft: Int) {
        timer?.cancel()

        if (daysLeft >= 0) {
            val timeCounter = TimeCounter(daysLeft - 1, 0, 0, 0)
            setTimesLeft(timeCounter)

            timer = Timer()
            timer?.schedule(object : TimerTask() {
                override fun run() {
                    timeCounter.decrease()
                    if (timeCounter.daysLeft >= 0)
                        counterBehaveSub.onNext(timeCounter.toString())
                    else {
                        onBirthdayBehaveSub.onNext(true)
                        timer?.cancel()
                    }
                }
            }, 0, 1000)

            onBirthdayBehaveSub.onNext(false)
        } else onBirthdayBehaveSub.onNext(true)
    }

    private fun setTimesLeft(timeCounter: TimeCounter) {
        val currentTime = dateManager.getCurrentTime()
        if (currentTime[2] > 0) {
            timeCounter.seconds = 60 - currentTime[2]
            currentTime[1]++
            if (currentTime[1] == 60) {
                currentTime[1] = 0
                currentTime[0]++
            }
        }
        if (currentTime[1] > 0) {
            timeCounter.minutes = 60 - currentTime[1]
            currentTime[0]++
        }
        timeCounter.hours = 24 - currentTime[0]
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}