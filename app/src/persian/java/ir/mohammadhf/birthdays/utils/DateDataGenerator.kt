package ir.mohammadhf.birthdays.utils

// in Persian build variant, it returns Solar months
object DateDataGenerator {
    const val minYearValue = 1310
    const val maxYearValue = 1401

    fun getMonthlyDays(year: Int): IntArray {
        return if (isLeapYear(year)) intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 30)
        else intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
    }

    private fun isLeapYear(year: Int): Boolean {
        val leapYearsRemain = arrayListOf(1, 5, 9, 13, 17, 22, 26, 30)
        return leapYearsRemain.contains(year % 33)
    }
}

