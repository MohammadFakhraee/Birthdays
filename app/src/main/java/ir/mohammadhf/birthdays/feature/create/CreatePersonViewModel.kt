package ir.mohammadhf.birthdays.feature.create

import android.graphics.Bitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ir.mohammadhf.birthdays.core.SettingSharedPrefManager
import ir.mohammadhf.birthdays.core.bases.BaseViewModel
import ir.mohammadhf.birthdays.data.model.Avatar
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.data.model.Person
import ir.mohammadhf.birthdays.data.repo.AvatarRepository
import ir.mohammadhf.birthdays.data.repo.GroupRepository
import ir.mohammadhf.birthdays.data.repo.PersonRepository
import ir.mohammadhf.birthdays.utils.DateDataGenerator
import ir.mohammadhf.birthdays.utils.DateManager
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreatePersonViewModel @Inject constructor(
    private val personRepository: PersonRepository,
    private val groupRepository: GroupRepository,
    private val avatarRepository: AvatarRepository,
    private val settingSharedPrefManager: SettingSharedPrefManager,
    private val dateManager: DateManager
) : BaseViewModel() {
    val personBehaveSub = BehaviorSubject.create<Person>()
    val groupListBehaveSub = BehaviorSubject.create<List<Group>>()
    val avatarListBehaveSub = BehaviorSubject.create<ArrayList<Avatar>>()
    val loadingBehaveSub = BehaviorSubject.create<Boolean>()
    val saveBehaveSub = BehaviorSubject.create<Boolean>()
    val formCompleteBehaveSub = BehaviorSubject.create<Boolean>()

    fun savePerson(avatarBitmap: Bitmap, imageFile: File) {
        personBehaveSub.value?.let {
            loadingBehaveSub.onNext(true)
            changeNotifyDateInPerson()
            if (it.id <= 0) create(avatarBitmap, imageFile)
            else update(avatarBitmap, imageFile)
        }
    }

    private fun changeNotifyDateInPerson() {
        personBehaveSub.value?.let {
            val months = DateDataGenerator.getMonthlyDays(dateManager.getTodayDateArray()[0])

            it.notifyDay = it.birthdayDay - DAYS_BEFORE
            it.notifyMonth = it.birthdayMonth

            while (it.notifyDay < 1) {
                it.notifyMonth--
                if (it.notifyMonth < 1) it.notifyMonth = months.size
                it.notifyDay += months[it.notifyMonth - 1]
            }
        }
    }

    private fun create(avatarBitmap: Bitmap, imageFile: File) {
        compositeDisposable.add(personRepository.createPerson(
            personBehaveSub.value!!,
            avatarBitmap,
            imageFile
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { generatedId, _ ->
                loadingBehaveSub.onNext(false)
                saveBehaveSub.onNext(generatedId != null)
            })
    }

    private fun update(avatarBitmap: Bitmap, imageFile: File) {
        compositeDisposable.add(personRepository.changePerson(
            personBehaveSub.value!!,
            avatarBitmap,
            imageFile
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnEvent {
                loadingBehaveSub.onNext(false)
            }
            .doOnError {
                saveBehaveSub.onNext(false)
                it.printStackTrace()
            }
            .subscribe {
                saveBehaveSub.onNext(true)
            })
    }

    fun getDefValue(personId: Long) {
        compositeDisposable.add(personRepository.getPerson(personId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { person, error ->
                person?.let {
                    personBehaveSub.onNext(it)
                }

                error?.let {
                    personBehaveSub.onNext(
                        Person(
                            0L, "", "",
                            null, 0, 0,
                            isYearChosen = false, 1,
                            0, 0
                        )
                    )
                }
            })
    }

    fun getGroups() {
        compositeDisposable.add(groupRepository.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { groupList, error ->
                groupList?.let { groupListBehaveSub.onNext(it) }
                error?.printStackTrace()
            })
    }

    fun saveNewGroup(group: Group) {
        compositeDisposable.add(groupRepository.insertOneGroup(group)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { groupId, error ->
                groupId?.let { getGroups() }
                error?.printStackTrace()
            })
    }

    fun getAvatars() {
        compositeDisposable.add(
            avatarRepository.getList()
                .subscribe { avatars, error ->
                    avatars?.let {
                        avatarListBehaveSub.onNext(it)
                    }

                    error?.printStackTrace()
                }
        )
    }

    fun changeName(name: String) {
        changePerson {
            this.name = name
        }
    }

    fun changeBirthday(day: Int, month: Int, year: Int?) {
        changePerson {
            birthdayDay = day
            birthdayMonth = month
            birthdayYear = year
            isYearChosen = year != null
        }
    }

    fun changeGroup(id: Long) {
        changePerson {
            groupId = id
        }
    }

    private fun changePerson(changer: Person.() -> Unit) {
        personBehaveSub.onNext(
            personBehaveSub.value!!.apply {
                changer(this)
            }
        )

        changeFormComplete()
    }

    fun changeFormComplete() {
        var bool = false
        personBehaveSub.value?.let {
            bool = (it.name.isNotEmpty()) and
                    (it.birthdayMonth > 0) and
                    (it.birthdayDay > 0) and
                    (it.groupId > 0)
        }
        formCompleteBehaveSub.onNext(bool)
    }

    fun getTagId(): Int {
        val counter = settingSharedPrefManager.getCounter()
        settingSharedPrefManager.updateCounter()
        return counter
    }

    companion object {
        const val DAYS_BEFORE = 1
    }
}