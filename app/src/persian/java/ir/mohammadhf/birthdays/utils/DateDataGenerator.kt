package ir.mohammadhf.birthdays.utils

// in Persian build variant, it returns Solar months
object DateDataGenerator {
    const val minYearValue = 1310
    const val maxYearValue = 1401

    fun getMonthlyDays(): IntArray =
        intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
}