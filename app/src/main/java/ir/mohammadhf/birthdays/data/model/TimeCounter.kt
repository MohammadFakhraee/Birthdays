package ir.mohammadhf.birthdays.data.model

import ir.mohammadhf.birthdays.utils.formatDay

data class TimeCounter(
    var daysLeft: Int,
    var hours: Int,
    var minutes: Int,
    var seconds: Int
) {
    fun decrease() {
        seconds--
        if (seconds < 0) {
            seconds = 59
            minutes--
            if (minutes < 0) {
                minutes = 59
                hours--
                if (hours < 0) {
                    hours = 23
                    daysLeft--
                }
            }
        }

    }

    override fun toString(): String =
        daysLeft.formatDay() + "  -  " +
                hours.formatDay() + " : " +
                minutes.formatDay() + " : " +
                seconds.formatDay()
}