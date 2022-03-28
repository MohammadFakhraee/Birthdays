package ir.mohammadhf.birthdays.utils

data class TimerHolder(
    var days: Int,
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
                    days--
                }
            }
        }

    }

    fun getTimeInMillis(): Long =
        (((((days.toLong()) * 24 + hours) * 60 + minutes) * 60 + seconds) * 1_000)

    override fun toString(): String =
        days.formatDay() + "  -  " +
                hours.formatDay() + " : " +
                minutes.formatDay() + " : " +
                seconds.formatDay()
}