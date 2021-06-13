package ir.mohammadhf.birthdays.utils

// in Global build variant, it returns Gregorian months
object DateDataGenerator {
    val minYearValue = 1930
    val maxYearValue = 2022

    fun getMonthlyDays(): IntArray =
        intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
}