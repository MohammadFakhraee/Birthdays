package ir.mohammadhf.birthdays.utils

import android.text.format.DateFormat
import ir.mohammadhf.birthdays.BuildConfig
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateManager @Inject constructor() {

    companion object {
        const val DATE_SEPARATOR: String = "/"
    }

    private var todayDateStr: String? = null
    private var todayDateArray: IntArray? = null

    fun gregorian_to_solar(gYear: Int, gMonth: Int, gDay: Int): IntArray {
        var gY = gYear
        val gDaysPerMonth =
            intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        var jy: Int
        if (gY > 1600) {
            jy = 979
            gY -= 1600
        } else {
            jy = 0
            gY -= 621
        }
        val gy2 = if (gMonth > 2) gY + 1 else gY
        var days =
            365 * gY + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400 - 80 + gDay + gDaysPerMonth[gMonth - 1]
        jy += 33 * (days / 12053)
        days %= 12053
        jy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            jy += (days - 1) / 365
            days = (days - 1) % 365
        }
        val jm = if (days < 186) 1 + days / 31 else 7 + (days - 186) / 30
        val jd = 1 + if (days < 186) days % 31 else (days - 186) % 30
        return intArrayOf(jy, jm, jd)
    }

    fun jalali_to_gregorian(jYear: Int, jMonth: Int, jDay: Int): IntArray {
        var jY = jYear
        var gy: Int
        if (jY > 979) {
            gy = 1600
            jY -= 979
        } else {
            gy = 621
        }
        var days =
            365 * jY + jY / 33 * 8 + (jY % 33 + 3) / 4 + 78 + jDay + if (jMonth < 7) (jMonth - 1) * 31 else (jMonth - 7) * 30 + 186
        gy += 400 * (days / 146097)
        days %= 146097
        if (days > 36524) {
            gy += 100 * (--days / 36524)
            days %= 36524
            if (days >= 365) days++
        }
        gy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            gy += (days - 1) / 365
            days = (days - 1) % 365
        }
        var gd = days + 1
        val salA = intArrayOf(
            0,
            31,
            if (gy % 4 == 0 && gy % 100 != 0 || gy % 400 == 0) 29 else 28,
            31,
            30,
            31,
            30,
            31,
            31,
            30,
            31,
            30,
            31
        )
        var gm = 0
        while (gm < 13) {
            val v = salA[gm]
            if (gd <= v) break
            gd -= v
            gm++
        }
        return intArrayOf(gy, gm, gd)
    }

    fun dayOfWeekFromSolar(jY: Int, jM: Int, jD: Int): Array<String> {
        val out = arrayOf("", "")
        return when (indexDayOfWeekFromSolar(jY, jM, jD)) {
            1 -> {
                out[0] = "شنبه"
                out[1] = "Saturday"
                out
            }
            2 -> {
                out[0] = "یکشنبه"
                out[1] = "Sunday"
                out
            }
            3 -> {
                out[0] = "دوشنبه"
                out[1] = "Monday"
                out
            }
            4 -> {
                out[0] = "سه شنبه"
                out[1] = "Tuesday"
                out
            }
            5 -> {
                out[0] = "چهارشنبه"
                out[1] = "Wednesday"
                out

            }
            6 -> {
                out[0] = "پنجشنبه"
                out[1] = "Thursday"
                out
            }
            else -> {
                out[0] = "جمعه"
                out[1] = "Friday"
                out
            }
        }
    }

    private fun indexDayOfWeekFromGregorian(gY: Int, gM: Int, gD: Int): Int {
        val localDate = LocalDate.of(gY, gM, gD)
        return DayOfWeek.from(localDate).value
    }

    fun indexDayOfWeekFromSolar(jY: Int, jM: Int, jD: Int): Int {
        val gregorian = jalali_to_gregorian(jY, jM, jD)
        return when (val index =
            indexDayOfWeekFromGregorian(gregorian[0], gregorian[1], gregorian[2])) {
            in 1..5 -> index.plus(2)
            else -> index % 5
        }
    }

    fun monthOfYear(month: Int, lang: String): String {
        return when (lang) {
            "en" -> when (month) {
                1 -> "January"
                2 -> "February"
                3 -> "March"
                4 -> "April"
                5 -> "May"
                6 -> "June"
                7 -> "July"
                8 -> "August"
                9 -> "September"
                10 -> "October"
                11 -> "November"
                12 -> "December"
                else -> "January"
            }
            "fa" -> when (month) {
                1 -> "فروردین"
                2 -> "اردیبهشت"
                3 -> "خرداد"
                4 -> "تیر"
                5 -> "مرداد"
                6 -> "شهریور"
                7 -> "مهر"
                8 -> "آبان"
                9 -> "آذر"
                10 -> "دی"
                11 -> "بهمن"
                12 -> "اسفند"
                else -> "فروردین"
            }
            else -> "فروردین"
        }
    }

    fun stringFormattedDate(date: IntArray): String {
        val strBuilder = StringBuilder()
        for (i in date.indices) {
            if (date[i] > 0) {
                strBuilder.append(date[i].formatDay())
                if (i != date.size - 1) strBuilder.append(DATE_SEPARATOR)
            }
        }
        return strBuilder.toString()
    }

    fun intFormattedDate(inDate: String, separator: String): IntArray {
        // 1399/06/19
        var date = inDate

        val outDate = IntArray(3)
        var index = 0
        while (date.isNotEmpty()) {
            var sepPos = date.indexOf(separator)
            outDate[index] = if (sepPos > -1)
                date.substring(0, sepPos).toInt()
            else {
                sepPos = date.length - 1
                date.substring(0).toInt()
            }

            date = date.substring(sepPos + 1)
            index++
        }
        return outDate
    }

    fun formatTime(hour: Int, minute: Int): String {
        return (String.format(Locale.ENGLISH, "%02d", hour) + ":"
                + String.format(Locale.ENGLISH, "%02d", minute))
    }

    fun getTodayDateArray(): IntArray {
        if (todayDateArray == null) {
            todayDateArray = intFormattedDate(getTodayDateStr(), DATE_SEPARATOR)
        }
        return todayDateArray!!
    }

    fun getTodayDateStr(): String {
        if (todayDateStr == null) {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.US)
            todayDateStr = sdf.format(Calendar.getInstance().time)

            if (BuildConfig.CURRENT_LANG == "fa") {
                var todayInts = intFormattedDate(todayDateStr!!, DATE_SEPARATOR)
                todayInts = gregorian_to_solar(todayInts[0], todayInts[1], todayInts[2])
                todayDateStr = stringFormattedDate(todayInts)
            }
        }
        return todayDateStr!!
    }


    fun getCurrentTime(): ArrayList<Int> {
        val intArray = ArrayList<Int>()
        var s = DateFormat.format("HH:mm:ss", Date().time).toString()
        while (s.isNotEmpty()) {
            var sepPos = s.indexOf(":")
            intArray.add(
                if (sepPos > -1)
                    s.substring(0, sepPos).toInt()
                else {
                    sepPos = s.length - 1
                    s.toInt()
                }
            )
            s = s.substring(sepPos + 1)
        }

        return intArray
    }

    fun daysLeft(month: Int, day: Int): Int {
        val personDaysPast = daysPastSinceFirstDayOfYear(getTodayDateArray()[0], month, day)
        val todayDaysPast = todayDaysPastSinceFirstDayOfYear()

        return if (todayDaysPast > personDaysPast) 356 - (todayDaysPast - personDaysPast)
        else personDaysPast - todayDaysPast
    }

    fun daysPastSinceFirstDayOfYear(year: Int, month: Int, day: Int): Int {
        val monthlyDays = DateDataGenerator.getMonthlyDays(year)

        var daysPast = day
        for (i in 0 until month - 1) {
            daysPast += monthlyDays[i]
        }

        return daysPast
    }

    fun todayDaysPastSinceFirstDayOfYear(): Int =
        daysPastSinceFirstDayOfYear(
            getTodayDateArray()[0],
            getTodayDateArray()[1],
            getTodayDateArray()[2]
        )
}

