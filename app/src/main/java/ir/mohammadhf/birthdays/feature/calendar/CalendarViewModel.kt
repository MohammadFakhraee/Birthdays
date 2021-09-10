package ir.mohammadhf.birthdays.feature.calendar

import androidx.hilt.lifecycle.ViewModelInject
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ir.mohammadhf.birthdays.BuildConfig
import ir.mohammadhf.birthdays.core.bases.BaseViewModel
import ir.mohammadhf.birthdays.data.model.BirthdayMulti
import ir.mohammadhf.birthdays.data.model.CalendarDay
import ir.mohammadhf.birthdays.data.model.Person
import ir.mohammadhf.birthdays.data.repo.PersonRepository
import ir.mohammadhf.birthdays.utils.DateDataGenerator
import ir.mohammadhf.birthdays.utils.DateManager
import ir.mohammadhf.birthdays.utils.PersonConvertor

class CalendarViewModel @ViewModelInject constructor(
    private val dateManager: DateManager,
    private val personRepository: PersonRepository,
    private val personConvertor: PersonConvertor
) : BaseViewModel() {
    val monthTitleBS = BehaviorSubject.create<String>()
    val monthDaysListBS = BehaviorSubject.create<List<CalendarDay>>()
    val birthdaysBS = BehaviorSubject.create<List<BirthdayMulti>>()

    private var userSelectedDay = -1
    private var userSelectedMonth = -1

    private var calSelectedMonth = -1
    private var calSelectedYear = -1

    private var isFirstLoad = true

    fun loadThisMonth() {
        if (isFirstLoad) {
            val todayDate = dateManager.getTodayDateArray()
            userSelectedDay = todayDate[2]
            userSelectedMonth = todayDate[1]
            calSelectedMonth = todayDate[1]
            calSelectedYear = todayDate[0]
            loadSelectedMonthDays()
            loadBirthListFromUserSelectedDate()
            isFirstLoad = false
        }
    }

    private fun loadSelectedMonthDays() {
        compositeDisposable.add(
            personRepository.getAllPersons()
                .subscribeOn(Schedulers.io())
                .subscribe { onLoadDays(it) }
        )
    }

    private fun onLoadDays(personList: List<Person>) {
        val months = DateDataGenerator.getMonthlyDays(calSelectedYear)
        val monthDays = months[calSelectedMonth - 1]
        val prevMonthDays = months[if (calSelectedMonth - 2 < 0) 11 else calSelectedMonth - 2]

        val firstInWeek =
            dateManager.indexDayOfWeekFromSolar(calSelectedYear, calSelectedMonth, 1)

        val listOfCalendarDays = arrayListOf<CalendarDay>()
        var overSizeDays = (monthDays + firstInWeek - 1) - 35
        for (i in 1..35) {
            if (i < firstInWeek) {
                if (overSizeDays > 0) {
                    overSizeDays--
                    val dayInMonth = monthDays - overSizeDays
                    listOfCalendarDays.add(
                        CalendarDay(
                            dayInMonth,
                            calSelectedMonth,
                            hasBirthEvent(personList, calSelectedMonth, dayInMonth),
                            isInShownMonth = true,
                            isToday = isToday(calSelectedMonth, dayInMonth),
                            isSelected = isSelectedDate(calSelectedMonth, dayInMonth)
                        )
                    )
                } else
                    listOfCalendarDays.add(
                        CalendarDay(
                            prevMonthDays - firstInWeek + i + 1,
                            calSelectedMonth,
                            false,
                            isInShownMonth = false,
                            isToday = false,
                            isSelected = false
                        )
                    )
            } else {
                if (i > monthDays + firstInWeek - 1)
                    listOfCalendarDays.add(
                        CalendarDay(
                            i - monthDays - firstInWeek + 1,
                            calSelectedMonth,
                            false,
                            isInShownMonth = false,
                            isToday = false,
                            isSelected = false
                        )
                    )
                else {
                    val dayInMonth = i - firstInWeek + 1
                    listOfCalendarDays.add(
                        CalendarDay(
                            dayInMonth,
                            calSelectedMonth,
                            hasBirthEvent(personList, calSelectedMonth, dayInMonth),
                            isInShownMonth = true,
                            isToday = isToday(calSelectedMonth, dayInMonth),
                            isSelected = isSelectedDate(calSelectedMonth, dayInMonth)
                        )
                    )
                }
            }
        }
        monthTitleBS.onNext(
            dateManager.monthOfYear(calSelectedMonth, BuildConfig.CURRENT_LANG) +
                    " " +
                    calSelectedYear
        )
        monthDaysListBS.onNext(listOfCalendarDays)
    }

    fun loadNextMonth() {
        calSelectedMonth++
        if (calSelectedMonth > 12) {
            calSelectedMonth = 1
            calSelectedYear++
        }
        loadSelectedMonthDays()
    }

    fun loadPrevMonth() {
        calSelectedMonth--
        if (calSelectedMonth < 1) {
            calSelectedMonth = 12
            calSelectedYear--
        }
        loadSelectedMonthDays()
    }

    fun onUserDateSelect(calendarDay: CalendarDay) {
        userSelectedMonth = calendarDay.calMonth
        userSelectedDay = calendarDay.ofMonth
        loadBirthListFromUserSelectedDate()
    }

    private fun loadBirthListFromUserSelectedDate() {
        compositeDisposable.add(personRepository.getPersonByBirthDate(
            userSelectedMonth,
            userSelectedDay
        )
            .subscribeOn(Schedulers.io())
            .map {
                personConvertor.personToBirthMulti(ArrayList(it), dateManager)
            }
            .subscribe { birthdays, error ->
                birthdays?.let { birthdaysBS.onNext(birthdays) }
                error?.printStackTrace()
            })
    }

    private fun isToday(month: Int, day: Int): Boolean {
        val todayDate = dateManager.getTodayDateArray()
        return month == todayDate[1] && day == todayDate[2]
    }

    private fun isSelectedDate(month: Int, day: Int): Boolean =
        month == userSelectedMonth && day == userSelectedDay

    private fun hasBirthEvent(personList: List<Person>, month: Int, day: Int): Boolean {
        personList.forEach {
            if (it.birthdayMonth == month && it.birthdayDay == day) return true
        }
        return false
    }
}