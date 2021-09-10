package ir.mohammadhf.birthdays.data.model

data class CalendarDay(
    val ofMonth: Int,
    val calMonth: Int,
    val hasEvent: Boolean,
    val isInShownMonth: Boolean,
    val isToday: Boolean,
    var isSelected: Boolean
)