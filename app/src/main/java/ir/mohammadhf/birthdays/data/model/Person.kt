package ir.mohammadhf.birthdays.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// If the Build Variant is Global, dates are Gregorian
// otherwise they're Solar.
@Entity(tableName = "persons")
data class Person(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var name: String,
    var avatarPath: String,
    var birthdayYear: Int?,
    var birthdayMonth: Int,
    var birthdayDay: Int,
    var isYearChosen: Boolean,
    var groupId: Long,
    var notifyMonth: Int,
    var notifyDay: Int
) {
    fun isBirthdayAfter(secondPerson: Person): Boolean =
        ((this.birthdayMonth > secondPerson.birthdayMonth)
                or ((this.birthdayMonth == secondPerson.birthdayMonth)
                and (this.birthdayDay > secondPerson.birthdayDay)))

    /**
     * For showing how old the person would be on his/her birthday, this method is called.
     * @param today An array of integers which contains Year, Month and Day of today.
     * @return Null if person's birth year is null (it happens when user doesn't want to
     * choose person's birth year).
     * @return Int if person's birth year is not null. If the birth month and day, is before today
     * the method adds 1 to the age which shows the age of person in next year.
     */
    fun getAgeOnBirthday(today: IntArray): Int? =
        if (birthdayYear == null) null
        else {
            var age = today[0] - birthdayYear!!
            if (isBeforeToday(today)) age++
            age
        }

    fun isAfterToday(today: IntArray): Boolean =
        birthdayMonth > today[1] || (birthdayMonth == today[1] && birthdayDay > today[2])

    private fun isBeforeToday(today: IntArray): Boolean =
        !isAfterToday(today)
}