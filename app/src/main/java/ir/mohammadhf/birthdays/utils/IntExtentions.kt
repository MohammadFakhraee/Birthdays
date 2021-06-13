package ir.mohammadhf.birthdays.utils

import java.util.*

fun Int.formatDay() = "%02d".format(this)

fun Int.formatDay(locale: Locale) = "%02d".format(locale, this)

fun Int.formatYear() = "%04d".format(this)

fun Int.formatYear(locale: Locale) = "%04d".format(locale, this)