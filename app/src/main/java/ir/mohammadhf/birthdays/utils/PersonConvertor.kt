package ir.mohammadhf.birthdays.utils

import ir.mohammadhf.birthdays.BuildConfig
import ir.mohammadhf.birthdays.data.model.BirthdayMulti
import ir.mohammadhf.birthdays.data.model.BirthdayPerson
import ir.mohammadhf.birthdays.data.model.BirthdayTitle
import ir.mohammadhf.birthdays.data.model.Person
import javax.inject.Inject

class PersonConvertor @Inject constructor() {
    /**
     * Sorts <code>Person</code> list by days left until birthday
     * and converts the list to <code>BirthdayMulti</code>.
     *
     * @param   personList  list of person's data
     * @param   dateManager a class that manages the dates
     * @return  a list of sorted and converted persons to <code>BirthdayMulti</code> for showing in views
     */
    fun sortedPersonToBirthdayMulti(
        personList: ArrayList<Person>,
        dateManager: DateManager
    ): ArrayList<BirthdayMulti> {
        // Sorting person list by days left.
        reorderList(personList)
        val subList = ArrayList(personList.subList(0, datesBeforeToday(personList, dateManager)))
        personList.removeAll(subList)
        personList.addAll(subList)
        return personToBirthMulti(personList, dateManager)
    }

    /**
     * Converts <code>Person</code>'s to <code>BirthMulti</code> which are grouped
     * with their birth month. First adds group title to list and then adds
     * list of <code>Person</code>s which their birthday is in the group.
     *
     * @param   personList  list of person's data
     * @param   dateManager a class that manages the dates
     * @return  a list of converted person to <code>BirthdayMulti</code> for showing in views
     */
    fun personToBirthMulti(
        personList: ArrayList<Person>,
        dateManager: DateManager
    ): ArrayList<BirthdayMulti> {
        val birthdayList = arrayListOf<BirthdayMulti>()
        // 1. adding todayTitle to birthdayList
        // 2. change person to BirthdayPerson model and add it to birthdayList
        var lastBirthdayTitle = ""
        personList.forEach { person ->
            val birthdayTitle = getBirthdayTitle(person, dateManager)
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
        return birthdayList
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

    private fun datesBeforeToday(personList: ArrayList<Person>, dateManager: DateManager): Int {
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

    private fun getBirthdayTitle(person: Person, dateManager: DateManager): String {
        val todayIntArray = dateManager.getTodayDateArray()
        return when (person.birthdayMonth) {
            todayIntArray[1] ->
                when (person.birthdayDay - todayIntArray[2]) {
                    0 -> BuildConfig.TODAY
                    in 1..6 -> BuildConfig.THIS_WEEK
                    else -> getBirthMonthTitle(person.birthdayMonth, dateManager)
                }
            else -> getBirthMonthTitle(person.birthdayMonth, dateManager)
        }
    }

    private fun getBirthMonthTitle(month: Int, dateManager: DateManager): String =
        dateManager.monthOfYear(month, BuildConfig.CURRENT_LANG)
}